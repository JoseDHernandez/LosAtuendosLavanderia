package com.lavanderia.validacion.lavanderia;

import com.lavanderia.validacion.ResultadoValidacion;

import java.util.Map;

/**
 * Contrato de validador para solicitudes y tandas de lavandería.
 * Proporciona métodos para validar atributos de solicitudes (como prioridad)
 * y para comprobar y normalizar cupos diarios de lavado.
 */
public interface ISolicitudLavanderiaValidator {

    /**
     * Valida los datos recibidos para una solicitud de lavandería.
     *
     * @param datos Diccionario con atributos (ej. prioridad).
     * @return {@link ResultadoValidacion} con los datos limpios o mapa de errores.
     */
    ResultadoValidacion<Map<String, String>> validar(
            Map<String, String> datos
    );

    /**
     * Valida y convierte una cadena textual representativa del cupo diario en un entero positivo.
     *
     * @param cupo Cadena de texto con el cupo.
     * @return Valor numérico entero del cupo.
     * @throws com.lavanderia.validacion.ValidacionException Si es nulo, no numérico o menor o igual a cero.
     */
    int validarCupo(String cupo);

    /**
     * Valida que un valor entero de cupo sea estrictamente positivo.
     *
     * @param cupo Entero con la cantidad de cupo.
     * @return El mismo cupo validado.
     * @throws com.lavanderia.validacion.ValidacionException Si el cupo es menor o igual a cero.
     */
    int validarCupo(int cupo);
}
