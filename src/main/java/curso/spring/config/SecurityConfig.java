package curso.spring.config;

import curso.spring.security.jwt.JwtAuthFilter;
import curso.spring.security.jwt.Jwtservice;
import curso.spring.services.impl.UsuarioServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.springframework.http.HttpMethod.POST;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private UsuarioServiceImpl usuarioService;

    private Jwtservice jwtservice;

    SecurityConfig(@Lazy UsuarioServiceImpl usuarioService, Jwtservice jwtservice) {
        this.usuarioService = usuarioService;
        this.jwtservice = jwtservice;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usuarioService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthFilter(jwtservice, usuarioService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/clientes/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .antMatchers("/api/pedidos/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .antMatchers("/api/produtos/**").hasRole(ROLE_ADMIN)
                .antMatchers(POST, "/api/usuarios/**").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
