package com.lavanderia.validacion.alquiler;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

/**
 * Contrato para la validación de datos de entrada en la solicitud de alquileres.
 * Verifica formato de fechas (inicio y fin prevista), coherencia del rango y cédulas.
 */
public interface IAlquilerValidator {

    /**
     * Valida los datos textuales de un alquiler y retorna el resultado estructurado.
     *
     * @param datos Diccionario con claves: fechaInicio, fechaFinPrevista, identificacionCliente, identificacionEmpleado.
     * @return {@link ResultadoValidacion} con mapa de campos limpios o errores detectados.
     */
    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );
}
