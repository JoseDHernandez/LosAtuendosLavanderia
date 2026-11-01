package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Validador especializado para la entidad Empleado.
 * Extiende {@link PersonaValidator} incorporando la comprobación obligatoria
 * y de formato del atributo laboral 'cargo'.
 */
public class EmpleadoValidator extends PersonaValidator {

    /**
     * Valida los datos del empleado ejecutando primero la validación base de {@link PersonaValidator}
     * y comprobando posteriormente que el campo 'cargo' cumpla el formato de nombre válido.
     *
     * @param datos Diccionario con atributos del empleado (incluyendo cargo).
     * @return {@link ResultadoValidacion} con datos validados o errores.
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        // 1. Ejecutar validación base de datos personales
        ResultadoValidacion<Map<String, String>> resultadoPersona =
                super.validar(datos);

        Map<String, String> errores = new LinkedHashMap<>(
                resultadoPersona.getErrores()
        );

        // Si los datos son nulos, retornar inmediatamente el fallo
        if (datos == null) {
            return ResultadoValidacion.fallido(errores);
        }

        Map<String, String> limpio = new LinkedHashMap<>(
                resultadoPersona.getValor().orElse(Map.of())
        );

        // 2. Validar el campo obligatorio de 'cargo' usando la regla de nombre alfabético
        try {
            limpio.put(
                    "cargo",
                    validarNombre(datos.get("cargo"))
            );
        } catch (ValidacionException ex) {
            errores.put("cargo", ex.getMessage());
        }

        // Si hay errores en datos de persona o en el cargo, retornar fallido
        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        // Retornar exitoso con la copia inmutable
        return ResultadoValidacion.exitoso(Map.copyOf(limpio));
    }
}
