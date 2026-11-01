package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validador base para atributos personales comunes a clientes y empleados.
 * Implementa {@link IPersonaValidator}. Aplica expresiones regulares y reglas de longitud para:
 * <ul>
 *   <li>Identificación (5 a 12 dígitos)</li>
 *   <li>Teléfono (7 a 10 dígitos)</li>
 *   <li>Nombre (2 a 60 caracteres alfabéticos y espacios)</li>
 *   <li>Correo electrónico (formato estándar RFC)</li>
 *   <li>Dirección (máximo 100 caracteres)</li>
 * </ul>
 */
public class PersonaValidator implements IPersonaValidator {

    /** Patrón para identificación: entre 5 y 12 dígitos numéricos. */
    private static final Pattern IDENTIFICACION =
            Pattern.compile("^\\d{5,12}$");

    /** Patrón para teléfono: entre 7 y 10 dígitos. */
    private static final Pattern TELEFONO =
            Pattern.compile("^\\d{7,10}$");

    /** Patrón para nombre: letras unicode, espacios, comillas simples o guiones. */
    private static final Pattern NOMBRE =
            Pattern.compile("^\\p{L}+(?:[ '\\-]\\p{L}+)*$");

    /** Patrón para formato de correo electrónico. */
    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(?:\\.[\\w\\-]+)+$");

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // Validar no nulidad del mapa
        if (datos == null) {
            errores.put("general", "No se recibieron datos.");
            return ResultadoValidacion.fallido(errores);
        }

        // Validar cada campo aplicando su método específico
        validarCampo(datos, limpio, errores,
                "identificacion", this::validarIdentificacion);

        validarCampo(datos, limpio, errores,
                "nombre", this::validarNombre);

        validarCampo(datos, limpio, errores,
                "direccion", this::validarDireccion);

        validarCampo(datos, limpio, errores,
                "telefono", this::validarTelefono);

        validarCampo(datos, limpio, errores,
                "correo", this::validarCorreo);

        // Si existen errores, retornar resultado fallido
        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        // Retornar resultado exitoso con copia inmutable
        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }

    /**
     * Valida que la identificación sea obligatoria y tenga entre 5 y 12 dígitos.
     *
     * @param valor Cadena con el documento.
     * @return Identificación normalizada.
     * @throws ValidacionException Si no cumple el patrón numérico.
     */
    protected String validarIdentificacion(String valor) {

        String limpio = obligatorio(valor, "identificacion");

        if (!IDENTIFICACION.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "La identificación debe tener entre 5 y 12 dígitos."
            );
        }

        return limpio;
    }

    /**
     * Valida que el teléfono sea obligatorio y tenga entre 7 y 10 dígitos.
     *
     * @param valor Cadena con el teléfono.
     * @return Teléfono limpio.
     * @throws ValidacionException Si no cumple el formato.
     */
    protected String validarTelefono(String valor) {

        String limpio = obligatorio(valor, "telefono");

        if (!TELEFONO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El teléfono debe tener entre 7 y 10 dígitos."
            );
        }

        return limpio;
    }

    /**
     * Valida que el nombre sea obligatorio, de 2 a 60 caracteres y con caracteres alfabéticos válidos.
     *
     * @param valor Nombre a evaluar.
     * @return Nombre normalizado.
     * @throws ValidacionException Si la longitud o el formato no son válidos.
     */
    protected String validarNombre(String valor) {

        String limpio = obligatorio(valor, "nombre");

        if (limpio.length() < 2
                || limpio.length() > 60
                || !NOMBRE.matcher(limpio).matches()) {

            throw new ValidacionException(
                    "El nombre solo admite letras y espacios (2 a 60 caracteres)."
            );
        }

        return limpio;
    }

    /**
     * Valida que el correo electrónico no esté vacío y cumpla el estándar de email.
     *
     * @param valor Correo a evaluar.
     * @return Correo limpio.
     * @throws ValidacionException Si el correo no es válido.
     */
    protected String validarCorreo(String valor) {

        String limpio = obligatorio(valor, "correo");

        if (!CORREO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El correo no tiene un formato válido."
            );
        }

        return limpio;
    }

    /**
     * Valida que la dirección no esté vacía y no supere los 100 caracteres.
     *
     * @param valor Dirección a evaluar.
     * @return Dirección limpia.
     * @throws ValidacionException Si supera el límite de longitud.
     */
    protected String validarDireccion(String valor) {

        String limpio = obligatorio(valor, "direccion");

        if (limpio.length() > 100) {
            throw new ValidacionException(
                    "La dirección es demasiado larga (máx. 100)."
            );
        }

        return limpio;
    }

    /**
     * Asegura que un campo no sea nulo ni esté en blanco.
     *
     * @param valor Valor del campo.
     * @param campo Nombre del campo para el mensaje.
     * @return Valor recortado.
     * @throws ValidacionException Si está en blanco o es nulo.
     */
    protected String obligatorio(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(
                    "El campo '" + campo + "' es obligatorio."
            );
        }

        return valor.trim();
    }

    /**
     * Helper para ejecutar una regla funcional sobre un campo y atrapar excepciones de validación.
     *
     * @param datos Diccionario original.
     * @param limpio Diccionario de campos válidos.
     * @param errores Diccionario de errores acumulados.
     * @param campo Nombre del campo.
     * @param regla Regla funcional de validación.
     */
    private void validarCampo(
            Map<String, String> datos,
            Map<String, String> limpio,
            Map<String, String> errores,
            String campo,
            java.util.function.Function<String, String> regla) {

        try {
            limpio.put(campo, regla.apply(datos.get(campo)));
        } catch (ValidacionException ex) {
            errores.put(campo, ex.getMessage());
        }
    }
}
