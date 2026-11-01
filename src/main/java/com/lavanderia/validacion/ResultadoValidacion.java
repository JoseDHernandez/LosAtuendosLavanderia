package com.lavanderia.validacion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contenedor inmutable que encapsula el resultado de un proceso de validación.
 * Puede representar un estado exitoso (con el valor resultante de tipo {@code T})
 * o un estado fallido (con un mapa de errores clasificados por nombre de campo).
 *
 * @param <T> Tipo del objeto o estructura de datos validada.
 */
public final class ResultadoValidacion<T> {

    /** Objeto resultante tras superar la validación (null si falló). */
    private final T valor;

    /** Diccionario inmutable de errores donde la clave es el campo y el valor el mensaje de error. */
    private final Map<String, String> errores;

    /**
     * Constructor privado para forzar el uso de los métodos de fábrica estáticos.
     *
     * @param valor Valor resultante si fue exitoso.
     * @param errores Mapa de mensajes de error asociados a campos.
     */
    private ResultadoValidacion(
            T valor,
            Map<String, String> errores) {

        this.valor = valor;

        // Proteger el mapa de errores asegurando orden de inserción e inmutabilidad
        this.errores = Collections.unmodifiableMap(
                new LinkedHashMap<>(errores)
        );
    }

    // ==========================================
    // CONSTRUCTORES DE RESULTADOS
    // ==========================================

    /**
     * Construye un resultado de validación exitoso con el valor transformado o limpio.
     *
     * @param <T> Tipo del valor.
     * @param valor Instancia no nula del valor validado.
     * @return {@link ResultadoValidacion} en estado exitoso.
     * @throws IllegalArgumentException Si el valor suministrado es nulo.
     */
    public static <T> ResultadoValidacion<T> exitoso(T valor) {

        if (valor == null) {
            throw new IllegalArgumentException(
                    "El valor de una validación exitosa no puede ser nulo."
            );
        }

        // Crear instancia sin errores
        return new ResultadoValidacion<>(
                valor,
                Map.of()
        );
    }

    /**
     * Construye un resultado de validación fallido a partir de un mapa de errores por campo.
     *
     * @param <T> Tipo esperado del valor.
     * @param errores Mapa con al menos un error por campo.
     * @return {@link ResultadoValidacion} en estado fallido.
     * @throws IllegalArgumentException Si el mapa es nulo o está vacío.
     */
    public static <T> ResultadoValidacion<T> fallido(
            Map<String, String> errores) {

        if (errores == null || errores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Una validación fallida debe contener al menos un error."
            );
        }

        // Crear instancia con valor nulo y la colección de errores
        return new ResultadoValidacion<>(
                null,
                errores
        );
    }

    // ==========================================
    // CONSULTA DEL RESULTADO
    // ==========================================

    /**
     * Indica si el proceso de validación fue exitoso (es decir, no contiene errores).
     *
     * @return {@code true} si es válido, {@code false} si hay anomalías.
     */
    public boolean esValido() {
        return errores.isEmpty();
    }

    /**
     * Retorna el mapa inmutable de errores encontrados.
     *
     * @return Mapa de errores (campo -> mensaje).
     */
    public Map<String, String> getErrores() {
        return errores;
    }

    /**
     * Retorna el valor encapsulado envuelto en un {@link Optional}.
     *
     * @return {@link Optional} con el valor si fue exitoso, o vacío.
     */
    public Optional<T> getValor() {
        return Optional.ofNullable(valor);
    }

    /**
     * Retorna directamente el valor validado si el resultado es válido,
     * o arroja {@link ValidacionException} con el mapa de errores en caso de fallo.
     *
     * @return El valor validado de tipo {@code T}.
     * @throws ValidacionException Si el resultado contiene errores.
     */
    public T obtenerValor() {

        if (!esValido()) {
            // Disparar excepción que transporta el detalle de inconsistencias
            throw new ValidacionException(errores);
        }

        return valor;
    }

    // ==========================================
    // CONSULTA DE ERRORES
    // ==========================================

    /**
     * Verifica si existe un error específico reportado para el campo indicado.
     *
     * @param campo Nombre del campo o propiedad evaluada.
     * @return {@code true} si el campo tiene error registrado.
     */
    public boolean tieneError(String campo) {
        return errores.containsKey(campo);
    }

    /**
     * Obtiene el mensaje descriptivo del error asociado a un campo determinado.
     *
     * @param campo Nombre del campo evaluado.
     * @return Mensaje de error, o null si el campo no presentó fallo.
     */
    public String getError(String campo) {
        return errores.get(campo);
    }
}
