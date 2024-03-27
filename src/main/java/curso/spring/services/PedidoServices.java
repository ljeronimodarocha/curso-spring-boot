package curso.spring.services;

import curso.spring.domain.entity.Pedido;
import curso.spring.domain.entity.enums.StatusPedido;
import curso.spring.dto.InformacaoItemPedidoDTO;
import curso.spring.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoServices {
    Pedido salvar(PedidoDTO pedidoDTO);
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);
}
