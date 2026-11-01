package com.lavanderia.validacion.lavanderia;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.validacion.ValidacionException;

import java.util.HashMap;
import java.util.Map;

public class SolicitudLavanderiaValidator
        implements ISolicitudLavanderiaValidator {

    @Override
    public Map<String, String> validar(Map<String, String> datos) {

        if (datos == null || datos.isEmpty()) {
            throw new ValidacionException(
                    "No se recibieron datos para validar."
            );
        }

        Map<String, String> resultado = new HashMap<>(datos);

        String prioridad = resultado.get("prioridad");

        if (prioridad == null || prioridad.isBlank()) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

        prioridad = prioridad.trim().toUpperCase();

        try {
            Prioridad.valueOf(prioridad);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(
                    "La prioridad indicada no es válida."
            );
        }

        resultado.put("prioridad", prioridad);

        return resultado;
    }

    @Override
    public int validarCupo(String cupo) {

        if (cupo == null || cupo.isBlank()) {
            throw new ValidacionException(
                    "El cupo es obligatorio."
            );
        }

        try {
            return validarCupo(Integer.parseInt(cupo.trim()));
        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "El cupo debe ser un número entero."
            );
        }
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