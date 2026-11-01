
package com.lavanderia.validacion.alquiler;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

public interface IAlquilerValidator {

    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );
}
