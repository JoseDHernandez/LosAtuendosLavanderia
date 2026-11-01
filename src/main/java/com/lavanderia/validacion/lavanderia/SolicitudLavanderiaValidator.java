package com.lavanderia.validacion.lavanderia;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Validador concreto para las operaciones y cupos del flujo de lavandería industrial.
 * Implementa {@link ISolicitudLavanderiaValidator}. Comprueba que las prioridades
 * pertenezcan a los valores válidos del enum {@link Prioridad} y asegura que los
 * cupos especificados para lotes sean enteros positivos.
 */
public class SolicitudLavanderiaValidator
        implements ISolicitudLavanderiaValidator {

    // ==========================================
    // VALIDACIÓN DE SOLICITUD
    // ==========================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // Validar recepción de datos
        if (datos == null || datos.isEmpty()) {

            errores.put(
                    "general",
                    "No se recibieron datos para validar."
            );

            return ResultadoValidacion.fallido(errores);
        }

        // Validar el campo de prioridad
        validarCampoPrioridad(datos, limpio, errores);

        // Si se encontraron errores, retornar resultado fallido
        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        // Retornar exitoso con copia inmutable
        return ResultadoValidacion.exitoso(
                Map.copyOf(limpio)
        );
    }

    // ==========================================
    // VALIDACIÓN DE PRIORIDAD
    // ==========================================

    /**
     * Comprueba que la prioridad sea obligatoria y coincida con una constante de {@link Prioridad}.
     *
     * @param datos Diccionario de entrada.
     * @param limpio Diccionario de campos válidos.
     * @param errores Diccionario donde reportar fallos.
     */
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

        // Normalizar texto en mayúsculas
        prioridad = prioridad.trim().toUpperCase(Locale.ROOT);

        try {

            // Verificar si corresponde a un valor válido del enum
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

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si es nulo, no numérico o menor/igual a cero.
     */
    @Override
    public int validarCupo(String cupo) {

        if (cupo == null || cupo.isBlank()) {
            throw new ValidacionException(
                    "El cupo es obligatorio."
            );
        }

        try {

            // Parsear a entero y validar rango
            return validarCupo(
                    Integer.parseInt(cupo.trim())
            );

        } catch (NumberFormatException ex) {

            throw new ValidacionException(
                    "El cupo debe ser un número entero."
            );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el cupo es menor o igual a cero.
     */
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
