package curso.spring.controller;

import curso.spring.domain.entity.Usuario;
import curso.spring.dto.CredenciasDTO;
import curso.spring.dto.TokenDTO;
import curso.spring.exception.UsuarioOuSenhaInvalidaException;
import curso.spring.security.jwt.Jwtservice;
import curso.spring.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl service;

    private final Jwtservice jwtservice;

    @PostMapping
    @ResponseStatus(CREATED)
    public Usuario salvar(@RequestBody @Valid Usuario usuario) {
        return service.salvar(usuario);
    }

    @PostMapping("/auth")
    public TokenDTO autenticar(@RequestBody CredenciasDTO credencias, HttpServletResponse response) {
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
