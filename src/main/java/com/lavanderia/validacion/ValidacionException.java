
package com.lavanderia.validacion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidacionException extends RuntimeException {

    private final Map<String, String> errores;

    // Error general
    public ValidacionException(String mensaje) {
        super(mensaje);
        this.errores = Map.of();
    }

    // Errores asociados a campos
    public ValidacionException(Map<String, String> errores) {
        super("Se encontraron errores de validación.");

        this.errores = Collections.unmodifiableMap(
                new LinkedHashMap<>(errores)
        );
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    public boolean tieneErroresPorCampo() {
        return !errores.isEmpty();
    }
}
