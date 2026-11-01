
package com.lavanderia.validacion.lavanderia;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

public interface ISolicitudLavanderiaValidator {

    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );

    int validarCupo(String cupo);

    int validarCupo(int cupo);
}
