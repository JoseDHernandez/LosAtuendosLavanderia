package com.lavanderia.validacion.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Validador concreto polimórfico para prendas de vestir (vestidos, trajes y disfraces).
 * Implementa {@link IPrendaValidator}. Aplica validaciones sobre atributos generales
 * (referencia con patrón alfanumérico, color, marca, talla, valor entero no negativo y estado válido)
 * así como sobre atributos específicos por tipología de prenda.
 */
public class PrendaValidator implements IPrendaValidator {

    /** Patrón para referencia: alfanumérico, guiones y guiones bajos (1 a 20 caracteres). */
    private static final Pattern REFERENCIA =
            Pattern.compile("^[A-Za-z0-9_-]{1,20}$");

    // ==========================================
    // VALIDACIÓN DE VESTIDO
    // ==========================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validarVestido(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // 1. Validar atributos comunes de cualquier prenda
        validarDatosGenerales(datos, limpio, errores);

        // 2. Validar atributos específicos de vestidos de dama
        if (datos != null) {

            validarCampo(datos, limpio, errores,
                    "tienePedreria", this::validarPedreria);

            validarCampo(datos, limpio, errores,
                    "largo", valor -> obligatorio(valor, "largo"));

            validarCampo(datos, limpio, errores,
                    "numeroPiezas",
                    valor -> validarEntero(valor, "número de piezas"));
        }

        return construirResultado(limpio, errores);
    }

    // ==========================================
    // VALIDACIÓN DE TRAJE
    // ==========================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validarTraje(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // 1. Validar atributos comunes
        validarDatosGenerales(datos, limpio, errores);

        // 2. Validar atributos específicos de traje de caballero
        if (datos != null) {

            validarCampo(datos, limpio, errores,
                    "tipoTraje",
                    valor -> obligatorio(valor, "tipo de traje"));

            validarCampo(datos, limpio, errores,
                    "tipoComplemento",
                    valor -> obligatorio(valor, "tipo de complemento"));
        }

        return construirResultado(limpio, errores);
    }

    // ==========================================
    // VALIDACIÓN DE DISFRAZ
    // ==========================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validarDisfraz(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        // 1. Validar atributos comunes
        validarDatosGenerales(datos, limpio, errores);

        // 2. Validar nombre o personaje del disfraz
        if (datos != null) {

            validarCampo(datos, limpio, errores,
                    "nombre",
                    valor -> obligatorio(valor, "nombre del disfraz"));
        }

        return construirResultado(limpio, errores);
    }

    // ==========================================
    // DATOS GENERALES DE PRENDA
    // ==========================================

    /**
     * Valida los campos comunes presentes en cualquier categoría de prenda.
     *
     * @param datos Diccionario de datos crudos.
     * @param limpio Diccionario para depositar campos depurados.
     * @param errores Diccionario para registrar fallos.
     */
    private void validarDatosGenerales(
            Map<String, String> datos,
            Map<String, String> limpio,
            Map<String, String> errores) {

        if (datos == null) {
            errores.put("general", "No se recibieron datos de la prenda.");
            return;
        }

        validarCampo(datos, limpio, errores,
                "referencia", this::validarReferencia);

        validarCampo(datos, limpio, errores,
                "color", valor -> obligatorio(valor, "color"));

        validarCampo(datos, limpio, errores,
                "marca", valor -> obligatorio(valor, "marca"));

        validarCampo(datos, limpio, errores,
                "talla", valor -> obligatorio(valor, "talla"));

        validarCampo(datos, limpio, errores,
                "valorAlquiler", this::validarValorAlquiler);

        validarCampo(datos, limpio, errores,
                "estado", this::validarEstado);
    }

    // ==========================================
    // REGLAS ESPECÍFICAS
    // ==========================================

    /**
     * Valida que la referencia cumpla el formato alfanumérico reglamentario.
     *
     * @param valor Referencia a evaluar.
     * @return Referencia limpia.
     * @throws ValidacionException Si no cumple la expresión regular.
     */
    private String validarReferencia(String valor) {

        String limpio = obligatorio(valor, "referencia");

        if (!REFERENCIA.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "La referencia solo admite letras, números y guiones "
                            + "(máximo 20 caracteres)."
            );
        }

        return limpio;
    }

    /**
     * Valida que el campo pedrería contenga "true" o "false".
     *
     * @param valor Valor booleano textual.
     * @return "true" o "false".
     * @throws ValidacionException Si es un texto distinto.
     */
    private String validarPedreria(String valor) {

        String limpio = obligatorio(valor, "pedrería");

        if (!limpio.equalsIgnoreCase("true")
                && !limpio.equalsIgnoreCase("false")) {

            throw new ValidacionException(
                    "El campo pedrería debe ser verdadero o falso."
            );
        }

        return limpio.toLowerCase();
    }

    /**
     * Valida el estado de la prenda; si no se suministra, asume {@link EstadoPrenda#DISPONIBLE}.
     *
     * @param valor Estado en formato texto.
     * @return Nombre de la constante de {@link EstadoPrenda}.
     * @throws ValidacionException Si no existe en el enum.
     */
    private String validarEstado(String valor) {

        String limpio = valor;

        if (limpio == null || limpio.isBlank()) {
            limpio = EstadoPrenda.DISPONIBLE.name();
        }

        try {
            return EstadoPrenda.valueOf(
                    limpio.trim().toUpperCase()
            ).name();

        } catch (IllegalArgumentException ex) {

            throw new ValidacionException(
                    "El estado de la prenda no es válido."
            );
        }
    }

    // ==========================================
    // VALIDACIONES NUMÉRICAS
    // ==========================================

    /**
     * Valida que el valor de alquiler sea un número entero no negativo.
     *
     * @param valor Valor de alquiler textual.
     * @return Cadena representativa del valor entero.
     * @throws ValidacionException Si no es numérico o es negativo.
     */
    private String validarValorAlquiler(String valor) {

        String limpio = obligatorio(valor, "valor de alquiler");

        try {

            long resultado = Long.parseLong(limpio);

            if (resultado < 0) {
                throw new ValidacionException(
                        "El valor de alquiler no puede ser negativo."
                );
            }

            return String.valueOf(resultado);

        } catch (NumberFormatException ex) {

            throw new ValidacionException(
                    "El valor de alquiler debe ser un número entero."
            );
        }
    }

    /**
     * Valida que un campo contenga un número entero válido.
     *
     * @param valor Texto numérico.
     * @param campo Nombre del campo para el error.
     * @return Cadena normalizada del entero.
     * @throws ValidacionException Si no es entero válido.
     */
    private String validarEntero(String valor, String campo) {

        String limpio = obligatorio(valor, campo);

        try {
            return String.valueOf(Integer.parseInt(limpio));

        } catch (NumberFormatException ex) {

            throw new ValidacionException(
                    "El campo " + campo + " debe ser un número entero."
            );
        }
    }

    // ==========================================
    // VALIDACIONES GENERALES
    // ==========================================

    /**
     * Comprueba obligatoriedad de un campo textual.
     *
     * @param valor Valor a evaluar.
     * @param campo Nombre del campo.
     * @return Valor recortado.
     * @throws ValidacionException Si está en blanco o nulo.
     */
    private String obligatorio(String valor, String campo) {

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
     * Ejecuta una regla de validación sobre un campo y captura errores en el mapa.
     *
     * @param datos Diccionario original.
     * @param limpio Diccionario de campos válidos.
     * @param errores Diccionario de errores.
     * @param campo Nombre del campo.
     * @param regla Regla funcional a ejecutar.
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

    /**
     * Construye un {@link ResultadoValidacion} exitoso o fallido según si el mapa de errores contiene elementos.
     *
     * @param limpio Datos limpios.
     * @param errores Mapa de errores.
     * @return Instancia de {@link ResultadoValidacion}.
     */
    private ResultadoValidacion<Map<String, String>> construirResultado(
            Map<String, String> limpio,
            Map<String, String> errores) {

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }
}
