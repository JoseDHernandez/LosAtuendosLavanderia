
package com.lavanderia.validacion.alquiler;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class AlquilerValidator implements IAlquilerValidator {

    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        if (datos == null || datos.isEmpty()) {
            errores.put(
                    "general",
                    "No se recibieron datos del alquiler."
            );

            return ResultadoValidacion.fallido(errores);
        }

        // --------------------------------------------------
        // VALIDACIÓN DE FECHAS
        // --------------------------------------------------

        validarCampo(
                datos,
                limpio,
                errores,
                "fechaInicio",
                valor -> validarFecha(valor, "fecha de inicio")
        );

        validarCampo(
                datos,
                limpio,
                errores,
                "fechaFinPrevista",
                valor -> validarFecha(valor, "fecha de finalización")
        );

        // --------------------------------------------------
        // VALIDACIÓN DE IDENTIFICACIONES
        // --------------------------------------------------

        validarCampo(
                datos,
                limpio,
                errores,
                "identificacionCliente",
                valor -> validarIdentificacion(valor, "cliente")
        );

        validarCampo(
                datos,
                limpio,
                errores,
                "identificacionEmpleado",
                valor -> validarIdentificacion(valor, "empleado")
        );

        // --------------------------------------------------
        // VALIDACIÓN DE RELACIÓN ENTRE FECHAS
        // --------------------------------------------------

        validarRangoFechas(limpio, errores);

        // --------------------------------------------------
        // RESULTADO
        // --------------------------------------------------

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(
                Collections.unmodifiableMap(
                        new LinkedHashMap<>(limpio)
                )
        );
    }

    // ==========================================
    // VALIDACIÓN DE FECHAS
    // ==========================================

    private String validarFecha(
            String valor,
            String campo) {

        String limpio = obligatorio(valor, campo);

        try {
            return LocalDate.parse(limpio).toString();

        } catch (DateTimeParseException ex) {
            throw new ValidacionException(
                    "La " + campo + " no tiene un formato válido. "
                            + "Utilice yyyy-MM-dd."
            );
        }
    }

    private void validarRangoFechas(
            Map<String, String> limpio,
            Map<String, String> errores) {

        if (!limpio.containsKey("fechaInicio")
                || !limpio.containsKey("fechaFinPrevista")) {
            return;
        }

        LocalDate fechaInicio = LocalDate.parse(
                limpio.get("fechaInicio")
        );

        LocalDate fechaFin = LocalDate.parse(
                limpio.get("fechaFinPrevista")
        );

        if (!fechaFin.isAfter(fechaInicio)) {
            errores.put(
                    "fechaFinPrevista",
                    "La fecha de finalización debe ser posterior "
                            + "a la fecha de inicio."
            );
        }
    }

    // ==========================================
    // VALIDACIÓN DE IDENTIFICACIONES
    // ==========================================

    private String validarIdentificacion(
            String valor,
            String campo) {

        String limpio = obligatorio(valor, campo);

        // Se valida el formato de la identificación.
        // La existencia de la persona se comprueba en GestorAlquiler.

        if (!limpio.matches("\\d{5,12}")) {
            throw new ValidacionException(
                    "La identificación del " + campo
                            + " debe contener entre 5 y 12 dígitos."
            );
        }

        return limpio;
    }

    // ==========================================
    // VALIDACIÓN DE CAMPOS OBLIGATORIOS
    // ==========================================

    private String obligatorio(
            String valor,
            String campo) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(
                    "El campo '" + campo + "' es obligatorio."
            );
        }

        return valor.trim();
    }

    // ==========================================
    // ACUMULACIÓN DE ERRORES
    // ==========================================

    private void validarCampo(
            Map<String, String> datos,
            Map<String, String> limpio,
            Map<String, String> errores,
            String campo,
            Function<String, String> regla) {

        try {
            limpio.put(
                    campo,
                    regla.apply(datos.get(campo))
            );

        } catch (ValidacionException ex) {
            errores.put(campo, ex.getMessage());
        }
    }
}
