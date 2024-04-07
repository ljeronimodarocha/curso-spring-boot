package curso.spring.dto;

import curso.spring.domain.entity.enums.StatusPedido;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizacaoStatusPedidoDTO {
    private StatusPedido novoStatus;
}
