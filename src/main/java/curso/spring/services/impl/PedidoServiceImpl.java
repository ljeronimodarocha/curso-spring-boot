package curso.spring.services.impl;

import curso.spring.domain.entity.ItemPedido;
import curso.spring.domain.entity.Pedido;
import curso.spring.domain.entity.enums.StatusPedido;
import curso.spring.domain.repository.Clientes;
import curso.spring.domain.repository.ItemsPedido;
import curso.spring.domain.repository.Pedidos;
import curso.spring.domain.repository.Produtos;
import curso.spring.dto.ItemPedidoDTO;
import curso.spring.dto.PedidoDTO;
import curso.spring.exception.RegraNegocioException;
import curso.spring.services.PedidoServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PedidoServiceImpl implements PedidoServices {

    private static final String CLIENTE_NAO_ENCONTRADO = "Cliente nao encontrado";

    private final Pedidos pedidosRepository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItemsPedido itemsPedidoRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(clientesRepository.findById(dto.getCliente())
                .orElseThrow(() -> new RegraNegocioException(CLIENTE_NAO_ENCONTRADO)));
        pedido.setItens(converterItens(pedido, dto.getItems()));
        pedido.setStatus(StatusPedido.REALIZADO);

        pedidosRepository.save(pedido);
        itemsPedidoRepository.saveAll(pedido.getItens());
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return this.pedidosRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        this.pedidosRepository.findById(id).map(pedido -> {
            pedido.setStatus(statusPedido);
            return pedidosRepository.save(pedido);
        }).orElseThrow(() -> new EntityNotFoundException("Pedido nao encontrado"));
    }

    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens) {
        if (itens.isEmpty()) {
            throw new RegraNegocioException("Nao e possivel realizar um pedido sem itens");
        }
        return itens.stream().map(dto -> {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setQuantidade(dto.getQuantidade());
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produtosRepository
                    .findById(dto.getProduto())
                    .orElseThrow(() -> new RegraNegocioException("Codigo produto invalido")));
            return itemPedido;
        }).collect(Collectors.toList());
    }
}
