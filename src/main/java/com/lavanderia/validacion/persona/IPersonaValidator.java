package com.lavanderia.validacion.persona;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

/**
 * Contrato de validador para datos personales y de contacto en el sistema.
 * Establece el método principal para validar diccionarios de datos de personas.
 */
public interface IPersonaValidator {

    /**
     * Valida el conjunto de datos de una persona (identificación, nombre, dirección, teléfono, correo).
     *
     * @param datos Diccionario con las propiedades textuales de la persona.
     * @return {@link ResultadoValidacion} conteniendo los datos procesados o el mapa de errores.
     */
    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );
}
