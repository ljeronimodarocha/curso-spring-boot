package curso.spring.services.impl;

import curso.spring.domain.entity.Usuario;
import curso.spring.domain.repository.UsuarioRepository;
import curso.spring.exception.UsuarioOuSenhaInvalidaException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UserDetailsService {

    private PasswordEncoder encoder;

    private UsuarioRepository repository;

    public UserDetails autenticar(Usuario usuario) {
        UserDetails user = loadUserByUsername(usuario.getLogin());
        boolean senhaValida = encoder.matches(usuario.getSenha(), user.getPassword());
        if (senhaValida)
            return user;

        throw new UsuarioOuSenhaInvalidaException();
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
        String[] roles = usuario.isAdmin() ? new String[]{"ADMIN", "USER"} : new String[]{"USER"};
        return User
                .builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(roles)
                .build();
    }

    public Usuario salvar(Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }
}
