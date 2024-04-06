package curso.spring.controller;

import curso.spring.domain.entity.Produto;
import curso.spring.domain.repository.Produtos;
import curso.spring.dto.ProdutoDTO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private static final String CLIENTE_NAO_ENCONTRADO = "Produto nao encontrado";
    private Produtos repository;

    public ProdutoController(Produtos repository) {
        this.repository = repository;
    }

    @GetMapping(value = "{id}")
    public Produto getProdutoById(@PathVariable("id") Integer id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(CREATED)
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
    public void delete(@PathVariable("id") Integer id) {
        repository.findById(id).map(cliente -> {
                    repository.delete(cliente);
                    return Void.TYPE;
                })
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @PutMapping("{id}")
    public void update(@PathVariable Integer id, @RequestBody @Valid ProdutoDTO dto) {
        repository.findById(id).map(produtoEncontrado -> {
            parseProduto(produtoEncontrado, dto);
            repository.save(produtoEncontrado);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
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
