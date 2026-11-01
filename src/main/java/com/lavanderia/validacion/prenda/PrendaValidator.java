
package com.lavanderia.validacion.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PrendaValidator implements IPrendaValidator {

    private static final Pattern REFERENCIA =
            Pattern.compile("^[A-Za-z0-9_-]{1,20}$");

    // ==========================================
    // VALIDACIÓN DE VESTIDO
    // ==========================================

    @Override
    public ResultadoValidacion<Map<String, String>> validarVestido(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        validarDatosGenerales(datos, limpio, errores);

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

    @Override
    public ResultadoValidacion<Map<String, String>> validarTraje(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        validarDatosGenerales(datos, limpio, errores);

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

    @Override
    public ResultadoValidacion<Map<String, String>> validarDisfraz(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        validarDatosGenerales(datos, limpio, errores);

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

    private ResultadoValidacion<Map<String, String>> construirResultado(
            Map<String, String> limpio,
            Map<String, String> errores) {

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }
}
