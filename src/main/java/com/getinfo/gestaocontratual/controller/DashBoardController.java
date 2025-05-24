package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.controller.dto.ContratoDataTable;
import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.entities.StatusEntregavel;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import com.getinfo.gestaocontratual.repository.EntregaveisRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Dashboard", description = "Dados para painéis e estatísticas")
@RestController
@RequestMapping("/dashboard")
public class DashBoardController {
    private final ContratoRepository contratoRepository;
    private final EntregaveisRepository entregaveisRepository;

    public DashBoardController(ContratoRepository contratoRepository, EntregaveisRepository entregaveisRepository) {

        this.contratoRepository = contratoRepository;
        this.entregaveisRepository = entregaveisRepository;
    }

    @Operation(summary = "Quantidade de contratos cadastrados por mês em um ano")
    @GetMapping("/contratosPorMes")
    public ResponseEntity<Map<String, Long>> contratosPorMes(@RequestParam(defaultValue = "2025") int ano) {
        List<Object[]> resultado = contratoRepository.countContratosPorMes(ano);

        Map<Integer, Long> totaisPorMes = new HashMap<>();
        for (Object[] row : resultado) {
            Integer mes = (Integer) row[0];
            Long total = (Long) row[1];
            totaisPorMes.put(mes, total);
        }

        Map<String, Long> resposta = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            String nomeMes = Month.of(i).getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            Long total = totaisPorMes.getOrDefault(i, 0L);
            resposta.put(nomeMes, total);
        }

        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Retorna os totais para os KPIs do dashboard")
    @GetMapping("/KPIs")
    public ResponseEntity<?> getKpisDashboard() {
        long contratosArquivados = contratoRepository.countByStatus_NomeIgnoreCase("arquivado");
        long contratosAtivos = contratoRepository.countContratosAtivos();
        long entregaveisConcluidos = entregaveisRepository.findByStatus(StatusEntregavel.FEITO).size();
        long entregaveisPendentes = entregaveisRepository.findByStatus(StatusEntregavel.A_FAZER).size() + entregaveisRepository.findByStatus(StatusEntregavel.FAZENDO).size();

        Map<String, Object> resposta = Map.of(
                "ContratosArquivados", contratosArquivados,
                "ContratosAtivos", contratosAtivos,
                "Entregaveis", Map.of(
                        "EntregaveisConcluidos", entregaveisConcluidos,
                        "EntregaveisPendentes", entregaveisPendentes
                )
        );

        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Retorna os contratos que estão a dois meses ou menos de terminar")
    @GetMapping("/ContratosPertoDeAcabar")
    public ResponseEntity<List<ContratoDataTable>> contratosPertoDeAcabar() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusMonths(2);

        List<Contrato> proximos = contratoRepository.findByDtFimBetween(hoje, limite);

        List<ContratoDataTable> dtos = proximos.stream().map(c -> new ContratoDataTable(
                c.getIdContrato(),
                c.getNumContrato(),
                c.getDtInicio(),
                c.getDtFim(),
                c.getDtAlteracao(),
                c.getIdContratante() != null ? c.getIdContratante().getIdContratante() : null,
                c.getStatus() != null ? c.getStatus().getNome() : null,
                c.getTipoServico(),
                c.getResponsavel(),
                c.getIdContratante()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/dashboard/contratos-por-regiao")
    public ResponseEntity<List<Map<String, Object>>> contratosPorRegiao() {
        List<Object[]> resultados = contratoRepository.countContratosPorRegiao();

        Map<String, Long> mapaResultados = new HashMap<>();

        for (Object[] obj : resultados) {
            if (obj[0] != null) {
                String regiao = obj[0].toString();
                Long total = obj[1] != null ? ((Number) obj[1]).longValue() : 0L;
                mapaResultados.put(regiao, total);
            }
        }

        List<String> regioes = List.of("Norte", "Nordeste", "Centro-Oeste", "Sudeste", "Sul");

        List<Map<String, Object>> lista = new ArrayList<>();

        for (String regiao : regioes) {
            Map<String, Object> map = new HashMap<>();
            map.put("regiao", regiao);
            map.put("total", mapaResultados.getOrDefault(regiao, 0L));
            lista.add(map);
        }

        return ResponseEntity.ok(lista);
    }


}