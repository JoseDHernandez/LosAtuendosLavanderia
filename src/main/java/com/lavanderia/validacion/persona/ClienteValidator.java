package com.lavanderia.validacion.persona;

import java.util.Map;

public class ClienteValidator implements IPersonaValidator {

    private final IPersonaValidator personaValidator;

    public ClienteValidator(IPersonaValidator personaValidator) {
        this.personaValidator = personaValidator;
    }

    @Override
    public Map<String, String> validar(Map<String, String> datos) {
        return personaValidator.validar(datos);
    }
}