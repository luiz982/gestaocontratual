package com.getinfo.gestaocontratual.controller;

import com.getinfo.gestaocontratual.entities.Contrato;
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

        Map<String, Long> resposta = new LinkedHashMap<>();
        for (Object[] row : resultado) {
            Integer mes = (Integer) row[0];
            Long total = (Long) row[1];
            resposta.put(Month.of(mes).getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")), total);
        }

        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Retorna os totais para os KPIs do dashboard")
    @GetMapping("/KPIs")
    public ResponseEntity<?> getKpisDashboard() {
        long totalPublicos = contratoRepository.countByTipoContratoIgnoreCase("publico");
        long totalPrivados = contratoRepository.countByTipoContratoIgnoreCase("privado");
        long entregaveisConcluidos = entregaveisRepository.countByStatusTrue();
        long entregaveisPendentes = entregaveisRepository.countByStatusFalse();

        return ResponseEntity.ok(Map.of(
                "ContratosPublicos", totalPublicos,
                "ContratosPrivados", totalPrivados,
                "EntregaveisConcluidos", entregaveisConcluidos,
                "EntregaveisPendentes", entregaveisPendentes
        ));
    }

    @Operation(summary = "Retorna os contratos que estão a dois meses ou menos de terminar")
    @GetMapping("/ContratosPertoDeAcabar")
    public ResponseEntity<List<Contrato>> contratosPertoDeAcabar() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusMonths(2);

        List<Contrato> proximos = contratoRepository.findByDtFimBetween(hoje, limite);
        return ResponseEntity.ok(proximos);
    }


}