package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

public interface IPersonaValidator {

    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );
}