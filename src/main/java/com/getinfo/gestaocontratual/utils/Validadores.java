package com.getinfo.gestaocontratual.utils;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

public class Validadores {

    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }

        try {
            CNPJValidator validator = new CNPJValidator();
            validator.assertValid(cnpj);
            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }

    public static String sanitizeFileName(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.replaceAll("[^a-zA-Z0-9._-]", "");
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        try {
            CPFValidator validator = new CPFValidator();
            validator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }

    public static boolean isBase64Valido(String base64) {
        try {
            Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public static boolean validarData(String data, String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        sdf.setLenient(false); 
        try {
            Date date = sdf.parse(data);
            return true;
        } catch (Exception e) {
            return false; 
        }
    }

    public static boolean isEmailValido(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public static boolean ValidarCEP(String cep) {
        if (cep == null || cep.isBlank()) {
            return false;
        }

        String regex = "^[0-9]{5}-?[0-9]{3}$";
        return cep.matches(regex);
    }

}