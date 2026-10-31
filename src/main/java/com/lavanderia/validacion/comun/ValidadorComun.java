package com.lavanderia.validacion.comun;

import com.lavanderia.validacion.ValidacionException;
import java.util.regex.Pattern;

public final class ValidadorComun {

    private static final Pattern ID =
            Pattern.compile("^\\d{5,12}$");

    private static final Pattern TELEFONO =
            Pattern.compile("^\\d{7,10}$");

    private static final Pattern LETRAS =
            Pattern.compile("^\\p{L}+(?:[ '\\-]\\p{L}+)*$");

    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(?:\\.[\\w\\-]+)+$");

    private static final Pattern REFERENCIA =
            Pattern.compile("^[A-Za-z0-9_-]{1,20}$");

    private ValidadorComun() {
    }

    public static String obligatorio(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(
                    "El campo '" + campo + "' es obligatorio."
            );
        }

        return valor.trim();
    }

    public static String identificacion(String valor) {

        String limpio = obligatorio(valor, "identificacion");

        if (!ID.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "La identificación debe tener entre 5 y 12 dígitos."
            );
        }

        return limpio;
    }

    public static String telefono(String valor) {

        String limpio = obligatorio(valor, "telefono");

        if (!TELEFONO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El teléfono debe tener entre 7 y 10 dígitos."
            );
        }

        return limpio;
    }

    public static String nombre(String valor, String campo) {

        String limpio = obligatorio(valor, campo);

        if (limpio.length() < 2
                || limpio.length() > 60
                || !LETRAS.matcher(limpio).matches()) {

            throw new ValidacionException(
                    campo + " solo admite letras y espacios (2 a 60 caracteres)."
            );
        }

        return limpio;
    }

    public static String correo(String valor) {

        String limpio = obligatorio(valor, "correo");

        if (!CORREO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El correo no tiene un formato válido."
            );
        }

        return limpio;
    }

    public static String direccion(String valor) {

        String limpio = obligatorio(valor, "direccion");

        if (limpio.length() > 100) {
            throw new ValidacionException(
                    "La dirección es demasiado larga (máx. 100)."
            );
        }

        return limpio;
    }

    public static String referencia(String valor) {

        String limpio = obligatorio(valor, "referencia de la prenda");

        if (!REFERENCIA.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "La referencia solo admite letras, números y guiones (máx. 20)."
            );
        }

        return limpio;
    }
}