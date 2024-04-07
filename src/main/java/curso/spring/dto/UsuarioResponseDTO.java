package curso.spring.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UsuarioResponseDTO {

    @NotNull
    @NotEmpty(message = "{campo.login.obrigatorio}")
    private String login;
}
