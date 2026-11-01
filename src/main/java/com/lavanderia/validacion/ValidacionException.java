package com.lavanderia.validacion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excepción personalizada no verificada (unchecked) lanzada cuando ocurren fallos en las reglas de validación.
 * Permite transportar tanto mensajes generales como un mapa detallado de errores mapeados por campo.
 */
public class ValidacionException extends RuntimeException {

    /** Mapa inmutable que asocia cada campo inválido con su mensaje descriptivo de error. */
    private final Map<String, String> errores;

    /**
     * Constructor para errores generales que no corresponden a un campo en particular.
     *
     * @param mensaje Mensaje explicativo del fallo.
     */
    public ValidacionException(String mensaje) {
        super(mensaje);
        // Inicializar mapa de errores como vacío
        this.errores = Map.of();
    }

    /**
     * Constructor para errores asociados a campos específicos de formularios o entidades.
     *
     * @param errores Mapa con los errores detectados (clave: nombre del campo, valor: mensaje).
     */
    public ValidacionException(Map<String, String> errores) {
        super("Se encontraron errores de validación.");

        // Guardar copia inmutable de los errores preservando el orden
        this.errores = Collections.unmodifiableMap(
                new LinkedHashMap<>(errores)
        );
    }

    /**
     * Obtiene el mapa con el desglose de errores por campo.
     *
     * @return Mapa de errores (campo -> mensaje).
     */
    public Map<String, String> getErrores() {
        return errores;
    }

    /**
     * Determina si la excepción contiene un desglose de errores específicos por campo.
     *
     * @return {@code true} si hay errores por campo, {@code false} si es un mensaje global.
     */
    public boolean tieneErroresPorCampo() {
        return !errores.isEmpty();
    }
}
