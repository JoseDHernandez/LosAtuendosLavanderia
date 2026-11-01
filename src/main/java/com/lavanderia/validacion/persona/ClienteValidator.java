package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

/**
 * Validador especializado para la entidad Cliente.
 * Extiende {@link PersonaValidator} aplicando las reglas comunes de persona.
 */
public class ClienteValidator extends PersonaValidator {

    /**
     * Valida los datos del cliente delegando en la lógica de la superclase {@link PersonaValidator}.
     *
     * @param datos Diccionario con atributos del cliente.
     * @return {@link ResultadoValidacion} con datos validados o errores.
     */
    @Override
    public ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos) {

        // Delegar en la validación estándar de persona
        return super.validar(datos);
    }
}
