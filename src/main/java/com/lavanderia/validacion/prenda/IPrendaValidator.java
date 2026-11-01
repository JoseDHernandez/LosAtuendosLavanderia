package com.lavanderia.validacion.prenda;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

/**
 * Contrato de validador para prendas de vestir según su tipología específica.
 * Proporciona métodos de validación para Vestidos de Dama, Trajes de Caballero y Disfraces.
 */
public interface IPrendaValidator {

    /**
     * Valida los datos recibidos para un Vestido de Dama (incluye pedrería, largo y número de piezas).
     *
     * @param datos Diccionario con especificaciones del vestido.
     * @return {@link ResultadoValidacion} con datos normalizados o errores.
     */
    ResultadoValidacion<Map<String, String>> validarVestido(
            Map<String, String> datos
    );

    /**
     * Valida los datos recibidos para un Traje de Caballero (incluye tipo de traje y complemento).
     *
     * @param datos Diccionario con especificaciones del traje.
     * @return {@link ResultadoValidacion} con datos normalizados o errores.
     */
    ResultadoValidacion<Map<String, String>> validarTraje(
            Map<String, String> datos
    );

    /**
     * Valida los datos recibidos para un Disfraz (incluye nombre o temática).
     *
     * @param datos Diccionario con especificaciones del disfraz.
     * @return {@link ResultadoValidacion} con datos normalizados o errores.
     */
    ResultadoValidacion<Map<String, String>> validarDisfraz(
            Map<String, String> datos
    );
}
