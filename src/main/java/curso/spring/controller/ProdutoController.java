package curso.spring.controller;

import curso.spring.domain.entity.Produto;
import curso.spring.domain.repository.Produtos;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
    public Produto save(@RequestBody @Valid Produto produto) {
        return repository.save(produto);
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
    public void update(@PathVariable Integer id, @RequestBody @Valid Produto produto) {
        repository.findById(id).map(produtoEncontrado -> {
            produto.setId(produtoEncontrado.getId());
            repository.save(produto);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
    public List<Produto> find(Produto filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Produto> example = Example.of(filtro, matcher);
        return repository.findAll(example);
    }


}
