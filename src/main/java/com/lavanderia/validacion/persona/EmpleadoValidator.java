package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.comun.ValidadorComun;
import java.util.Map;
import java.util.HashMap;

public class EmpleadoValidator implements IPersonaValidator {

    private final IPersonaValidator personaValidator;

    public EmpleadoValidator(IPersonaValidator personaValidator) {
        this.personaValidator = personaValidator;
    }

    @Override
    public Map<String, String> validar(Map<String, String> datos) {

        Map<String, String> limpio =
                new HashMap<>(personaValidator.validar(datos));

        limpio.put(
                "cargo",
                ValidadorComun.nombre(datos.get("cargo"), "cargo")
        );

        return limpio;
    }
}