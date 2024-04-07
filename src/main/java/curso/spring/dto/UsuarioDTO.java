package curso.spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {

    private String login;
    private String senha;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSenha() {
        return senha;
    }
}
