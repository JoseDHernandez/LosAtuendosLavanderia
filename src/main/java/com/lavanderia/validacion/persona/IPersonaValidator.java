package com.lavanderia.validacion.persona;

import java.util.Map;

public interface IPersonaValidator {

    Map<String, String> validar(Map<String, String> datos);
}