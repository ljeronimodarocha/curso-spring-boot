package curso.spring.controller;

import curso.spring.domain.entity.Cliente;
import curso.spring.domain.repository.Clientes;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final String CLIENTE_NAO_ENCONTRADO = "Cliente nao encontrado";
    private Clientes repository;


    public ClienteController(Clientes repository) {
        this.repository = repository;
    }

    @GetMapping(value = "{id}")
    public Cliente getClienteById(@PathVariable("id") Integer id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cliente save(@RequestBody Cliente cliente) {
        return repository.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        repository.findById(id).map(cliente -> {
                    repository.delete(cliente);
                    return Void.TYPE;
                })
                .orElseThrow(() -> new EntityNotFoundException( CLIENTE_NAO_ENCONTRADO));
    }

    @PutMapping("{id}")
    public void update(@PathVariable Integer id, @RequestBody Cliente cliente) {
        repository.findById(id).map(clienteEncontrado -> {
            clienteEncontrado.setNome(cliente.getNome());
            cliente.setId(clienteEncontrado.getId());
            repository.save(cliente);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new EntityNotFoundException( CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
    public List<Cliente> find(Cliente filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Cliente> example = Example.of(filtro, matcher);
        return repository.findAll(example);
    }


}
