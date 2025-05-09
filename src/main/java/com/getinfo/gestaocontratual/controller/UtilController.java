package com.getinfo.gestaocontratual.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getinfo.gestaocontratual.controller.dto.UtilResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.getinfo.gestaocontratual.utils.Validadores;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Utilitários", description = "Endpoints auxiliares e utilitários")
@RestController
@RequestMapping("/utils")
public class UtilController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Retorna todos os estados do brasil e seus respectivos IDs")
    @GetMapping("/estados")
    public ResponseEntity<?> getEstados() {
        try {
            String url = "http://servicodados.ibge.gov.br/api/v1/localidades/estados";
            String response = restTemplate.getForObject(url, String.class);
            List<Map<String, Object>> estados = objectMapper.readValue(response, new TypeReference<>() {
            });

            List<Map<String, Object>> estadosFiltrados = estados.stream()
                    .map(estado -> Map.of(
                            "id", estado.get("id"),
                            "nome", estado.get("nome")))
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
            List<Map<String, Object>> municipios = objectMapper.readValue(response, new TypeReference<>() {
            });

            List<Map<String, Object>> municipiosFiltrados = municipios.stream()
                    .map(municipio -> Map.of(
                            "id", municipio.get("id"),
                            "nome", municipio.get("nome")))
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
            Map<String, Object> cnpjData = objectMapper.readValue(response, new TypeReference<>() {
            });

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
                    "cidade", cidade);

            return ResponseEntity.ok(new UtilResponse("Dados recuperados com sucesso", empresaFiltrada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UtilResponse("Erro ao buscar informações do CNPJ", e.getMessage()));
        }
    }

    @Operation(summary = "Retorna as informações de um CEP. Também retorna se ele é válido ou não.")
    @GetMapping("/cep/{cep}")
    public ResponseEntity<?> getCepInfo(@PathVariable String cep) {
        if (!Validadores.ValidarCEP(cep)) {
            return ResponseEntity.badRequest().body(new UtilResponse("CEP inválido", null));
        }

        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            String response = restTemplate.getForObject(url, String.class);
            Map<String, Object> cepData = objectMapper.readValue(response, new TypeReference<>() {
            });

            // Caso nao tenha o CEP no VIACEP, tenta a API da Republica Virtual
            if (cepData.containsKey("erro")) {
                // return ResponseEntity.badRequest().body(new UtilResponse("ERRO - CEP não encontrado", null));

                url = "http://cep.republicavirtual.com.br/web_cep.php?" + cep + "&formato=json";
                response = restTemplate.getForObject(url, String.class);
                cepData = objectMapper.readValue(response, new TypeReference<>() {
                });

                if (cepData.containsKey("resultado") && "0".equals(cepData.get("resultado"))) {
                    return ResponseEntity.badRequest().body(new UtilResponse("ERRO - CEP não encontrado", null));
                } else {
                    Map<String, Object> cepFiltrado = Map.of(
                            "cep", cep,
                            "logradouro",
                            cepData.get("tipo_logradouro").toString() + cepData.get("logradouro").toString(),
                            "bairro", cepData.get("bairro"),
                            "localidade", cepData.get("cidade"),
                            "uf", cepData.get("uf"));

                    return ResponseEntity.ok(new UtilResponse("OK", cepFiltrado));
                }
            }

            Map<String, Object> cepFiltrado = Map.of(
                    "cep", cepData.get("cep"),
                    "logradouro", cepData.get("logradouro"),
                    "bairro", cepData.get("bairro"),
                    "localidade", cepData.get("localidade"),
                    "uf", cepData.get("uf"));

            return ResponseEntity.ok(new UtilResponse("OK", cepFiltrado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UtilResponse("Erro ao buscar informações do CEP", e.getMessage()));
        }
    }
}
