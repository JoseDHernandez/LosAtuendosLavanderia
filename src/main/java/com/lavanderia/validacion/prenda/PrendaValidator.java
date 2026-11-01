package com.lavanderia.validacion.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.comun.ValidadorComun;

import java.util.HashMap;
import java.util.Map;

public class PrendaValidator implements IPrendaValidator {

    @Override
    public Map<String, String> validarVestido(
            Map<String, String> datos) {

        Map<String, String> limpio = validarDatosGenerales(datos);

        String pedreria = obligatorio(
                datos.get("tienePedreria"),
                "pedrería"
        );

        if (!pedreria.equalsIgnoreCase("true")
                && !pedreria.equalsIgnoreCase("false")) {
            throw new ValidacionException(
                    "El campo pedrería debe ser verdadero o falso."
            );
        }

        limpio.put("tienePedreria", pedreria.toLowerCase());

        limpio.put("largo",
                obligatorio(datos.get("largo"), "largo"));

        limpio.put("numeroPiezas",
                validarEntero(datos.get("numeroPiezas"), "número de piezas"));

        return limpio;
    }

    @Override
    public Map<String, String> validarTraje(
            Map<String, String> datos) {

        Map<String, String> limpio = validarDatosGenerales(datos);

        limpio.put("tipoTraje",
                obligatorio(datos.get("tipoTraje"), "tipo de traje"));

        limpio.put("tipoComplemento",
                obligatorio(datos.get("tipoComplemento"), "tipo de complemento"));

        return limpio;
    }

    @Override
    public Map<String, String> validarDisfraz(
            Map<String, String> datos) {

        Map<String, String> limpio = validarDatosGenerales(datos);

        limpio.put("nombre",
                obligatorio(datos.get("nombre"), "nombre del disfraz"));

        return limpio;
    }

    private Map<String, String> validarDatosGenerales(
            Map<String, String> datos) {

        if (datos == null) {
            throw new ValidacionException(
                    "No se recibieron datos de la prenda."
            );
        }

        Map<String, String> limpio = new HashMap<>();

        limpio.put("referencia",
                ValidadorComun.referencia(datos.get("referencia")));

        limpio.put("color",
                obligatorio(datos.get("color"), "color"));

        limpio.put("marca",
                obligatorio(datos.get("marca"), "marca"));

        limpio.put("talla",
                obligatorio(datos.get("talla"), "talla"));

        limpio.put("valorAlquiler",
                validarValorAlquiler(datos.get("valorAlquiler")));

        String estado = datos.get("estado");

        if (estado == null || estado.isBlank()) {
            estado = EstadoPrenda.DISPONIBLE.name();
        }

        try {
            estado = EstadoPrenda.valueOf(estado.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(
                    "El estado de la prenda no es válido."
            );
        }

        limpio.put("estado", estado);

        return limpio;
    }

    private String obligatorio(String valor, String campo) {
        return ValidadorComun.obligatorio(valor, campo);
    }

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

        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "El valor de alquiler debe ser un número entero."
            );
        }
    }

    private String validarEntero(String valor, String campo) {

        String limpio = obligatorio(valor, campo);

        try {
            return String.valueOf(Integer.parseInt(limpio));
        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "El campo " + campo + " debe ser un número entero."
            );
        }
    }
}