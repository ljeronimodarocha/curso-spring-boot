package curso.spring.controller;

import curso.spring.domain.entity.Cliente;
import curso.spring.domain.repository.Clientes;
import curso.spring.dto.ClienteDTO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
        return this.repository.findById(id).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cliente save(@RequestBody @Valid ClienteDTO cliente) {
        return repository.save(
                Cliente.builder()
                        .nome(cliente.getNome())
                        .cpf(cliente.getCpf())
                        .build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        repository.findById(id).map(cliente -> {
            repository.delete(cliente);
            return Void.TYPE;
        }).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @PutMapping("{id}")
    public void update(@PathVariable Integer id, @RequestBody @Valid ClienteDTO dto) {

        repository.findById(id).map(clienteEncontrado -> {
            parseCliente(clienteEncontrado, dto);
            repository.save(clienteEncontrado);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
    public List<Cliente> find(ClienteDTO filtro) {
        Cliente cliente = new Cliente();
        parseCliente(cliente, filtro);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Cliente> example = Example.of(cliente, matcher);
        return repository.findAll(example);
    }

    private void parseCliente(Cliente cliente, ClienteDTO dto) {
        if (dto.getNome() != null && !dto.getNome().isEmpty())
            cliente.setNome(dto.getNome());
        if (dto.getCpf() != null && !dto.getCpf().isEmpty())
            cliente.setCpf(dto.getCpf());
    }
}
