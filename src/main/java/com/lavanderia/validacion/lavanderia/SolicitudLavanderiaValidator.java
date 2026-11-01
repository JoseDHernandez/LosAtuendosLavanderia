
package com.lavanderia.validacion.lavanderia;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SolicitudLavanderiaValidator
        implements ISolicitudLavanderiaValidator {

    // ==========================================
    // VALIDACIÓN DE SOLICITUD
    // ==========================================

    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        if (datos == null || datos.isEmpty()) {

            errores.put(
                    "general",
                    "No se recibieron datos para validar."
            );

            return ResultadoValidacion.fallido(errores);
        }

        validarCampoPrioridad(datos, limpio, errores);

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(
                Map.copyOf(limpio)
        );
    }

    // ==========================================
    // VALIDACIÓN DE PRIORIDAD
    // ==========================================

    private void validarCampoPrioridad(
            Map<String, String> datos,
            Map<String, String> limpio,
            Map<String, String> errores) {

        String prioridad = datos.get("prioridad");

        if (prioridad == null || prioridad.isBlank()) {

            errores.put(
                    "prioridad",
                    "La prioridad es obligatoria."
            );

            return;
        }

        prioridad = prioridad.trim().toUpperCase(Locale.ROOT);

        try {

            Prioridad.valueOf(prioridad);

            limpio.put("prioridad", prioridad);

        } catch (IllegalArgumentException ex) {

            errores.put(
                    "prioridad",
                    "La prioridad indicada no es válida."
            );
        }
    }

    // ==========================================
    // VALIDACIÓN DE CUPO
    // ==========================================

    @Override
    public int validarCupo(String cupo) {

        if (cupo == null || cupo.isBlank()) {
            throw new ValidacionException(
                    "El cupo es obligatorio."
            );
        }

        try {

            return validarCupo(
                    Integer.parseInt(cupo.trim())
            );

        } catch (NumberFormatException ex) {

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
