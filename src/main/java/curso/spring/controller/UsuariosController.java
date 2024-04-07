package curso.spring.controller;

import curso.spring.domain.entity.Usuario;
import curso.spring.dto.TokenDTO;
import curso.spring.dto.UsuarioDTO;
import curso.spring.dto.UsuarioResponseDTO;
import curso.spring.exception.UsuarioOuSenhaInvalidaException;
import curso.spring.security.jwt.Jwtservice;
import curso.spring.services.impl.UsuarioServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Api(value = "Api Usuarios", tags = "Api Usuarios")
public class UsuariosController {

    private final UsuarioServiceImpl service;

    private final Jwtservice jwtservice;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(("Salva um novo usuario"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "usuario salvo com sucesso", response = UsuarioResponseDTO.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
    })
    public UsuarioResponseDTO salvar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        Usuario usuario = Usuario.builder().login(usuarioDTO.getLogin()).senha(usuarioDTO.getSenha()).build();
        service.salvar(usuario);
        return UsuarioResponseDTO.builder().login(usuario.getLogin()).build();
    }

    @PostMapping("/auth")
    @ResponseStatus(OK)
    @ApiOperation(("Efetua login com o usuario"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuario encontrado com sucesso", response = TokenDTO.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao encontrado"),
    })
    public TokenDTO autenticar(@RequestBody UsuarioDTO credencias, HttpServletResponse response) {
        try {
            Usuario usuario = Usuario.builder().login(credencias.getLogin()).senha(credencias.getSenha()).build();
            service.autenticar(usuario);
            String token = String.format("Bearer %s", jwtservice.gerarToken(usuario));
            response.addHeader("Authorization", token);
            return new TokenDTO(usuario.getLogin(), token);
        } catch (UsernameNotFoundException | UsuarioOuSenhaInvalidaException e) {
            throw new UsuarioOuSenhaInvalidaException();
        }
    }
}
