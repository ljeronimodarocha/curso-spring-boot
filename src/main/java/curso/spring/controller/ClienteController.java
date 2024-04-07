package curso.spring.controller;

import curso.spring.domain.entity.Cliente;
import curso.spring.domain.repository.Clientes;
import curso.spring.dto.ClienteDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/clientes")
@Api(value = "Api Clientes", tags = "Api Clientes")
public class ClienteController {

    private static final String CLIENTE_NAO_ENCONTRADO = "Cliente nao encontrado";
    private Clientes repository;

    public ClienteController(Clientes repository) {
        this.repository = repository;
    }

    @GetMapping(value = "{id}")
    @ApiOperation(("Obter detalhes de um cliente"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado", response = Cliente.class),
            @ApiResponse(code = 404, message = "Cliente nao encontrado"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")

    })
    public Cliente getClienteById(@PathVariable("id") @ApiParam("Id do cliente") Integer id) {
        return this.repository.findById(id).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(("Salva um novo cliente"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente salvo com sucesso", response = Cliente.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")
    })
    public Cliente save(@RequestBody @Valid ClienteDTO cliente) {
        return repository.save(
                Cliente.builder()
                        .nome(cliente.getNome())
                        .cpf(cliente.getCpf())
                        .build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(("Deleta um cliente"))
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cliente deletado com sucesso", response = Void.class),
            @ApiResponse(code = 404, message = "Cliente com ID informado nao encontrado"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")

    })
    public void delete(@PathVariable("id") @ApiParam("Id do Cliente") Integer id) {
        repository.findById(id).map(cliente -> {
            repository.delete(cliente);
            return Void.TYPE;
        }).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(("Atualiza um cliente que ja existe"))
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cliente atualizado com sucesso", response = Void.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado"),
            @ApiResponse(code = 404, message = "Cliente com ID informado nao encontrado")
    })
    public void update(@PathVariable Integer id, @RequestBody @Valid ClienteDTO dto) {
        repository.findById(id).map(clienteEncontrado -> {
            parseCliente(clienteEncontrado, dto);
            repository.save(clienteEncontrado);
            return clienteEncontrado;
        }).orElseThrow(() -> new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));
    }

    @GetMapping
    @ApiOperation(("Busca clientes"))
    @ApiResponses({@ApiResponse(code = 401, message = "Usuario nao autorizado")})
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
