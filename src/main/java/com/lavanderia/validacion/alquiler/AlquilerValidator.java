package com.lavanderia.validacion.alquiler;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Validador concreto de las solicitudes de alquiler de vestuario.
 * Implementa {@link IAlquilerValidator}. Comprueba la obligatoriedad y el formato ISO (yyyy-MM-dd)
 * de las fechas de inicio y fin prevista, asegura que la fecha final sea posterior a la inicial
 * y verifica la longitud y dígitos de las identificaciones de cliente y empleado.
 */
public class AlquilerValidator implements IAlquilerValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // Validar si el mapa recibido es nulo o vacío
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

        // Validar formato de la fecha de inicio
        validarCampo(
                datos,
                limpio,
                errores,
                "fechaInicio",
                valor -> validarFecha(valor, "fecha de inicio")
        );

        // Validar formato de la fecha fin prevista
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

        // Validar identificación del cliente
        validarCampo(
                datos,
                limpio,
                errores,
                "identificacionCliente",
                valor -> validarIdentificacion(valor, "cliente")
        );

        // Validar identificación del empleado
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

        // Comprobar que la fecha de finalización sea posterior al inicio
        validarRangoFechas(limpio, errores);

        // --------------------------------------------------
        // RESULTADO FINAL
        // --------------------------------------------------

        // Si existen errores acumulados, retornar resultado fallido
        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        // Retornar mapa inmutable con los datos normalizados
        return ResultadoValidacion.exitoso(
                Collections.unmodifiableMap(
                        new LinkedHashMap<>(limpio)
                )
        );
    }

    // ==========================================
    // VALIDACIÓN DE FECHAS
    // ==========================================

    /**
     * Valida que una fecha sea obligatoria y se ajuste al estándar yyyy-MM-dd.
     *
     * @param valor Cadena con la fecha.
     * @param campo Nombre del campo para mensajes de error.
     * @return Cadena normalizada de la fecha.
     * @throws ValidacionException Si el formato es erróneo o el campo está vacío.
     */
    private String validarFecha(
            String valor,
            String campo) {

        String limpio = obligatorio(valor, campo);

        try {
            // Parsear para garantizar que sea una fecha válida del calendario
            return LocalDate.parse(limpio).toString();

        } catch (DateTimeParseException ex) {
            throw new ValidacionException(
                    "La " + campo + " no tiene un formato válido. "
                            + "Utilice yyyy-MM-dd."
            );
        }
    }

    /**
     * Comprueba que la fecha límite pactada sea estrictamente posterior a la fecha inicial.
     *
     * @param limpio Mapa de campos que pasaron la validación de formato individual.
     * @param errores Mapa donde se registran inconsistencias encontradas.
     */
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

    /**
     * Valida que el documento de identificación conste de 5 a 12 dígitos numéricos.
     *
     * @param valor Documento a verificar.
     * @param campo Rol ("cliente" o "empleado").
     * @return Cadena de identificación limpia.
     * @throws ValidacionException Si no cumple la expresión regular de dígitos.
     */
    private String validarIdentificacion(
            String valor,
            String campo) {

        String limpio = obligatorio(valor, campo);

        // Se valida únicamente el formato numérico de la identificación
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

    /**
     * Asegura que un valor no sea nulo ni consista únicamente de espacios en blanco.
     *
     * @param valor Valor textual a revisar.
     * @param campo Nombre del campo.
     * @return Valor recortado (trimmed).
     * @throws ValidacionException Si está vacío.
     */
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

    /**
     * Aplica una función de regla sobre un campo; si tiene éxito almacena el resultado en 'limpio',
     * y si lanza {@link ValidacionException} atrapa el mensaje y lo asienta en 'errores'.
     *
     * @param datos Diccionario original.
     * @param limpio Diccionario de datos limpios.
     * @param errores Diccionario de errores.
     * @param campo Clave del campo.
     * @param regla Función con la lógica de validación a aplicar.
     */
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
