package curso.spring.controller;


import curso.spring.domain.entity.ItemPedido;
import curso.spring.domain.entity.Pedido;
import curso.spring.domain.entity.enums.StatusPedido;
import curso.spring.dto.AtualizacaoStatusPedidoDTO;
import curso.spring.dto.InformacaoItemPedidoDTO;
import curso.spring.dto.InformacoesPedidoDTO;
import curso.spring.dto.PedidoDTO;
import curso.spring.services.PedidoServices;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoServices services;

    public PedidoController(PedidoServices services) {
        this.services = services;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO pedido) {
        return this.services.salvar(pedido).getId();
    }

    @GetMapping("{id}")
    @ResponseStatus(OK)
    public InformacoesPedidoDTO getById(@PathVariable Integer id) {
        return this.services.obterPedidoCompleto(id).map(this::converter)
                .orElseThrow(() -> new EntityNotFoundException("Pedido nao encontrado"));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void updateStatus(@PathVariable Integer id, @RequestBody AtualizacaoStatusPedidoDTO pedidoDTO) {
        this.services.atualizaStatus(id, StatusPedido.valueOf(pedidoDTO.getNovoStatus()));
    }

    private InformacoesPedidoDTO converter(Pedido pedido) {
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .items(converter(pedido.getItens()))
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
        if (CollectionUtils.isEmpty(itens))
            return Collections.emptyList();

        return itens.stream().map(item -> InformacaoItemPedidoDTO
                        .builder()
                        .descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco())
                        .quantidade(item.getQuantidade())
                        .build())
                .collect(Collectors.toList());
    }
}
