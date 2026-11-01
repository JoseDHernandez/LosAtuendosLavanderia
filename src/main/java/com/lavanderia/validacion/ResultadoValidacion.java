
package com.lavanderia.validacion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ResultadoValidacion<T> {

    private final T valor;
    private final Map<String, String> errores;

    private ResultadoValidacion(
            T valor,
            Map<String, String> errores) {

        this.valor = valor;

        this.errores = Collections.unmodifiableMap(
                new LinkedHashMap<>(errores)
        );
    }

    // ==========================================
    // CONSTRUCTORES DE RESULTADOS
    // ==========================================

    public static <T> ResultadoValidacion<T> exitoso(T valor) {

        if (valor == null) {
            throw new IllegalArgumentException(
                    "El valor de una validación exitosa no puede ser nulo."
            );
        }

        return new ResultadoValidacion<>(
                valor,
                Map.of()
        );
    }

    public static <T> ResultadoValidacion<T> fallido(
            Map<String, String> errores) {

        if (errores == null || errores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Una validación fallida debe contener al menos un error."
            );
        }

        return new ResultadoValidacion<>(
                null,
                errores
        );
    }

    // ==========================================
    // CONSULTA DEL RESULTADO
    // ==========================================

    public boolean esValido() {
        return errores.isEmpty();
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    public Optional<T> getValor() {
        return Optional.ofNullable(valor);
    }

    public T obtenerValor() {

        if (!esValido()) {
            throw new ValidacionException(errores);
        }

        return valor;
    }

    // ==========================================
    // CONSULTA DE ERRORES
    // ==========================================

    public boolean tieneError(String campo) {
        return errores.containsKey(campo);
    }

    public String getError(String campo) {
        return errores.get(campo);
    }
}
