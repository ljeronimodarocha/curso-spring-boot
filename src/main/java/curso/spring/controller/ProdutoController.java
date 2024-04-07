package curso.spring.controller;

import curso.spring.domain.entity.Produto;
import curso.spring.domain.repository.Produtos;
import curso.spring.dto.ProdutoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/produtos")
@Api(value = "Api Produtos", tags = "Api Produtos")
public class ProdutoController {

    private static final String CLIENTE_NAO_ENCONTRADO = "Produto nao encontrado";
    private Produtos repository;

    public ProdutoController(Produtos repository) {
        this.repository = repository;
    }

    @GetMapping(value = "{id}")
    @ApiOperation(("Obter detalhes de um produto"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto encontrado", response = Produto.class),
            @ApiResponse(code = 404, message = "Produto nao encontrado"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")

    })
    public Produto getProdutoById(@PathVariable("id") Integer id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(("Salva um novo produto"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "Produto salvo com sucesso", response = Produto.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")
    })
    public Produto save(@RequestBody @Valid ProdutoDTO produto) {
        return repository
                .save(
                        Produto.builder()
                                .descricao(produto.getDescricao())
                                .preco(produto.getPreco())
                                .build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(("Deleta um produto"))
    @ApiResponses({
            @ApiResponse(code = 204, message = "Produto deletado com sucesso", response = Void.class),
            @ApiResponse(code = 404, message = "Produto com ID informado nao encontrado"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")

    })
    public void delete(@PathVariable("id") Integer id) {
        repository.findById(id).map(cliente -> {
                    repository.delete(cliente);
                    return Void.TYPE;
                })
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @PutMapping("{id}")
    @ApiOperation(("Atualiza um produto que ja existe"))
    @ApiResponses({
            @ApiResponse(code = 204, message = "Produto atualizado com sucesso", response = Void.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado"),
            @ApiResponse(code = 404, message = "Produto com ID informado nao encontrado")
    })
    @ResponseStatus(NO_CONTENT)
    public void update(@PathVariable Integer id, @RequestBody @Valid ProdutoDTO dto) {
        repository.findById(id).map(produtoEncontrado -> {
            parseProduto(produtoEncontrado, dto);
            repository.save(produtoEncontrado);
            return produtoEncontrado;
        }).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
    @ApiOperation(("Busca produtos"))
    @ApiResponse(code = 401, message = "Usuario nao autorizado")
    public List<Produto> find(ProdutoDTO filtro) {
        Produto produto = Produto.builder()
                .build();
        parseProduto(produto, filtro);
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Produto> example = Example.of(produto, matcher);
        return repository.findAll(example);
    }

    private void parseProduto(Produto produto, ProdutoDTO dto) {
        if (dto.getPreco() != null && dto.getPreco().compareTo(BigDecimal.ZERO) >= 0)
            produto.setPreco(dto.getPreco());
        if (dto.getDescricao() != null && !dto.getDescricao().isEmpty())
            produto.setDescricao(dto.getDescricao());
    }


}
