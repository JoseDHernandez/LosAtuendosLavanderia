
package com.lavanderia.validacion.prenda;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

public interface IPrendaValidator {

    ResultadoValidacion<Map<String, String>> validarVestido(
            Map<String, String> datos
    );

    ResultadoValidacion<Map<String, String>> validarTraje(
            Map<String, String> datos
    );

    ResultadoValidacion<Map<String, String>> validarDisfraz(
            Map<String, String> datos
    );
}
