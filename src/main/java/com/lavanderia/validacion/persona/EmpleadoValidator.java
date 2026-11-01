package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;

public class EmpleadoValidator extends PersonaValidator {

    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        ResultadoValidacion<Map<String, String>> resultadoPersona =
                super.validar(datos);

        Map<String, String> errores = new LinkedHashMap<>(
                resultadoPersona.getErrores()
        );

        if (datos == null) {
            return ResultadoValidacion.fallido(errores);
        }

        Map<String, String> limpio = new LinkedHashMap<>(
                resultadoPersona.getValor().orElse(Map.of())
        );

        try {
            limpio.put(
                    "cargo",
                    validarNombre(datos.get("cargo"))
            );
        } catch (ValidacionException ex) {
            errores.put("cargo", ex.getMessage());
        }

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }
}