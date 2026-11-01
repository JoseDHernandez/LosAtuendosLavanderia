package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

public class ClienteValidator extends PersonaValidator {

    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        return super.validar(datos);
    }
}