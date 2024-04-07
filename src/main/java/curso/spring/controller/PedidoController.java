package curso.spring.controller;


import curso.spring.domain.entity.ItemPedido;
import curso.spring.domain.entity.Pedido;
import curso.spring.dto.AtualizacaoStatusPedidoDTO;
import curso.spring.dto.InformacaoItemPedidoDTO;
import curso.spring.dto.InformacoesPedidoDTO;
import curso.spring.dto.PedidoDTO;
import curso.spring.services.PedidoServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Api Pedidos", tags = "Api Pedidos")
public class PedidoController {

    private PedidoServices services;

    public PedidoController(PedidoServices services) {
        this.services = services;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(("Salva um novo pedido"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "Pedido salvo com sucesso", response = Integer.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")
    })
    public Integer save(@RequestBody @Valid PedidoDTO pedido) {
        return this.services.salvar(pedido).getId();
    }

    @GetMapping("{id}")
    @ResponseStatus(OK)
    @ApiOperation(("Obter detalhes de um pedido"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pedido encontrado", response = Pedido.class),
            @ApiResponse(code = 404, message = "Pedido nao encontrado"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado")

    })
    public InformacoesPedidoDTO getById(@PathVariable Integer id) {
        return this.services.obterPedidoCompleto(id).map(this::converter)
                .orElseThrow(() -> new EntityNotFoundException("Pedido nao encontrado"));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(("Atualiza o status de um pedido"))
    @ApiResponses({
            @ApiResponse(code = 204, message = "Pedido atualizado com sucesso", response = Void.class),
            @ApiResponse(code = 400, message = "Erro de validacao"),
            @ApiResponse(code = 401, message = "Usuario nao autorizado"),
            @ApiResponse(code = 404, message = "Pedido com ID informado nao encontrado")
    })
    public void updateStatus(@PathVariable Integer id, @RequestBody AtualizacaoStatusPedidoDTO pedidoDTO) {
        this.services.atualizaStatus(id, pedidoDTO.getNovoStatus());
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
