
package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PersonaValidator implements IPersonaValidator {

    private static final Pattern IDENTIFICACION =
            Pattern.compile("^\\d{5,12}$");

    private static final Pattern TELEFONO =
            Pattern.compile("^\\d{7,10}$");

    private static final Pattern NOMBRE =
            Pattern.compile("^\\p{L}+(?:[ '\\-]\\p{L}+)*$");

    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(?:\\.[\\w\\-]+)+$");

    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        Map<String, String> limpio = new LinkedHashMap<>();
        Map<String, String> errores = new LinkedHashMap<>();

        if (datos == null) {
            errores.put("general", "No se recibieron datos.");
            return ResultadoValidacion.fallido(errores);
        }

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

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }

    protected String validarIdentificacion(String valor) {

        String limpio = obligatorio(valor, "identificacion");

        if (!IDENTIFICACION.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "La identificación debe tener entre 5 y 12 dígitos."
            );
        }

        return limpio;
    }

    protected String validarTelefono(String valor) {

        String limpio = obligatorio(valor, "telefono");

        if (!TELEFONO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El teléfono debe tener entre 7 y 10 dígitos."
            );
        }

        return limpio;
    }

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

    protected String validarCorreo(String valor) {

        String limpio = obligatorio(valor, "correo");

        if (!CORREO.matcher(limpio).matches()) {
            throw new ValidacionException(
                    "El correo no tiene un formato válido."
            );
        }

        return limpio;
    }

    protected String validarDireccion(String valor) {

        String limpio = obligatorio(valor, "direccion");

        if (limpio.length() > 100) {
            throw new ValidacionException(
                    "La dirección es demasiado larga (máx. 100)."
            );
        }

        return limpio;
    }

    protected String obligatorio(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(
                    "El campo '" + campo + "' es obligatorio."
            );
        }

        return valor.trim();
    }

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
