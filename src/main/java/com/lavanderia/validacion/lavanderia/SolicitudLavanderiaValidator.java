package com.lavanderia.validacion.lavanderia;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.comun.ValidadorComun;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SolicitudLavanderiaValidator
        implements ISolicitudLavanderiaValidator {

    @Override
    public Map<String, String> validar(Map<String, String> datos) {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "No se recibieron datos."
            );
        }

        Map<String, String> limpio = new HashMap<>();

        limpio.put(
                "referenciaPrenda",
                ValidadorComun.referencia(datos.get("referenciaPrenda"))
        );

        limpio.put(
                "prioridad",
                validarPrioridad(datos.get("prioridad")).name()
        );

        return limpio;
    }

    private Prioridad validarPrioridad(String prioridad) {

        String limpio = ValidadorComun.obligatorio(
                prioridad,
                "prioridad"
        );

        try {
            return Prioridad.valueOf(
                    limpio.toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(
                    "La prioridad seleccionada no es válida."
            );
        }
    }

    @Override
    public int validarCupo(String cupo) {

        int valor;

        try {
            valor = Integer.parseInt(
                    ValidadorComun.obligatorio(cupo, "cupo")
            );
        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "El cupo debe ser un número entero."
            );
        }

        return validarCupo(valor);
    }

    @Override
    public int validarCupo(int cupo) {

        if (cupo <= 0) {
            throw new ValidacionException(
                    "El cupo debe ser mayor que cero."
            );
        }

        return cupo;
    }
}
