package com.getinfo.gestaocontratual.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getinfo.gestaocontratual.controller.dto.UtilResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.getinfo.gestaocontratual.utils.Validadores;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/util")
public class UtilController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Retorna todos os estados do brasil e seus respectivos IDs")
    @GetMapping("/estados")
    public ResponseEntity<?> getEstados() {
        try {
            String url = "http://servicodados.ibge.gov.br/api/v1/localidades/estados";
            String response = restTemplate.getForObject(url, String.class);
            List<Map<String, Object>> estados = objectMapper.readValue(response, new TypeReference<>() {});

            List<Map<String, Object>> estadosFiltrados = estados.stream()
                    .map(estado -> Map.of(
                            "id", estado.get("id"),
                            "nome", estado.get("nome")
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new UtilResponse("Dados recuperados com sucesso", estadosFiltrados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UtilResponse("Erro ao buscar estados", e.getMessage()));
        }
    }

    @Operation(summary = "Retorna todos os municipios de um estado, recebe apenas o ID do estado")
    @GetMapping("/estados/{idEstado}/municipios")
    public ResponseEntity<?> getMunicipios(@PathVariable String idEstado) {
        try {
            String url = "http://servicodados.ibge.gov.br/api/v1/localidades/estados/" + idEstado + "/municipios";
            String response = restTemplate.getForObject(url, String.class);
            List<Map<String, Object>> municipios = objectMapper.readValue(response, new TypeReference<>() {});

            List<Map<String, Object>> municipiosFiltrados = municipios.stream()
                    .map(municipio -> Map.of(
                            "id", municipio.get("id"),
                            "nome", municipio.get("nome")
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new UtilResponse("Dados recuperados com sucesso", municipiosFiltrados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UtilResponse("Erro ao buscar municípios", e.getMessage()));
        }
    }

    @Operation(summary = "Retorna as informações de um cnpj. Também retorna se ele é válido ou não.")
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<?> getCnpjInfo(@PathVariable String cnpj) {
        if (!Validadores.isCnpjValido(cnpj)) {
            return ResponseEntity.badRequest().body(new UtilResponse("CNPJ inválido", null));
        }

        try {
            String url = "https://publica.cnpj.ws/cnpj/" + cnpj;
            String response = restTemplate.getForObject(url, String.class);
            Map<String, Object> cnpjData = objectMapper.readValue(response, new TypeReference<>() {});

            Map<String, Object> estabelecimento = (Map<String, Object>) cnpjData.get("estabelecimento");
            Map<String, Object> estado = (Map<String, Object>) estabelecimento.get("estado");
            Map<String, Object> cidade = (Map<String, Object>) estabelecimento.get("cidade");

            Map<String, Object> empresaFiltrada = Map.of(
                    "cnpj", estabelecimento.get("cnpj"),
                    "razao_social", cnpjData.get("razao_social"),
                    "capital_social", cnpjData.get("capital_social"),
                    "situacao_cadastral", estabelecimento.get("situacao_cadastral"),
                    "data_inicio_atividade", estabelecimento.get("data_inicio_atividade"),
                    "atividade_principal", estabelecimento.get("atividade_principal"),
                    "estado", estado,
                    "cidade", cidade
            );

            return ResponseEntity.ok(new UtilResponse("Dados recuperados com sucesso", empresaFiltrada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UtilResponse("Erro ao buscar informações do CNPJ", e.getMessage()));
        }
    }
}
