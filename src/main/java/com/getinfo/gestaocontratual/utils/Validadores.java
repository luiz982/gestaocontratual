package com.getinfo.gestaocontratual.utils;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import br.com.caelum.stella.validation.CNPJValidator;
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
}