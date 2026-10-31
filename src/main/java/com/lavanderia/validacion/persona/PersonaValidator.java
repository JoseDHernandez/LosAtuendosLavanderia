package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.comun.ValidadorComun;
import java.util.HashMap;
import java.util.Map;

public class PersonaValidator implements IPersonaValidator {

    @Override
    public Map<String, String> validar(Map<String, String> datos) {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "No se recibieron datos."
            );
        }

        Map<String, String> limpio = new HashMap<>();

        limpio.put(
                "identificacion",
                ValidadorComun.identificacion(datos.get("identificacion"))
        );

        limpio.put(
                "nombre",
                ValidadorComun.nombre(datos.get("nombre"), "nombre")
        );

        limpio.put(
                "direccion",
                ValidadorComun.direccion(datos.get("direccion"))
        );

        limpio.put(
                "telefono",
                ValidadorComun.telefono(datos.get("telefono"))
        );

        limpio.put(
                "correo",
                ValidadorComun.correo(datos.get("correo"))
        );

        return limpio;
    }
}