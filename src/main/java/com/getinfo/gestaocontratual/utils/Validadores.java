package com.getinfo.gestaocontratual.utils;

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
        if (base64.startsWith("data:")) {
            String mimeType = base64.split(";")[0].split(":")[1];

            switch (mimeType) {
                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                case "image/png":
                case "image/jpeg": // JPG
                case "application/pdf":
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }
}