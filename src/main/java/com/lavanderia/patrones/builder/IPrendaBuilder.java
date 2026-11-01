package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.enums.EstadoPrenda;

/**
 * Contrato base para el patrón de diseño Builder aplicado a la construcción de prendas.
 * Utiliza genéricos recursivos (Curiously Recurring Template Pattern) para permitir
 * encadenamiento fluido de métodos (Fluent Interface) conservando el tipo de builder específico.
 *
 * @param <B> Representa el tipo concreto del builder para retornar 'this' tipado.
 * @param <P> Representa el tipo específico de prenda que se construye.
 */
public interface IPrendaBuilder<
        B extends IPrendaBuilder<B, P>,
        P extends IPrenda> {

    /**
     * Asigna la referencia alfanumérica única de la prenda.
     *
     * @param referencia Código de referencia.
     * @return La instancia del builder para encadenamiento.
     */
    B conReferencia(String referencia);

    /**
     * Asigna el color principal de la prenda.
     *
     * @param color Color de la prenda.
     * @return La instancia del builder para encadenamiento.
     */
    B conColor(String color);

    /**
     * Asigna la marca o confeccionista de la prenda.
     *
     * @param marca Fabricante o marca.
     * @return La instancia del builder para encadenamiento.
     */
    B conMarca(String marca);

    /**
     * Asigna la talla de la prenda.
     *
     * @param talla Talla comercial.
     * @return La instancia del builder para encadenamiento.
     */
    B conTalla(String talla);

    /**
     * Asigna la tarifa base de alquiler.
     *
     * @param valorAlquiler Precio por alquiler.
     * @return La instancia del builder para encadenamiento.
     */
    B conValorAlquiler(long valorAlquiler);

    /**
     * Asigna el estado inicial de disponibilidad de la prenda.
     *
     * @param estado {@link EstadoPrenda} a asignar.
     * @return La instancia del builder para encadenamiento.
     */
    B conEstado(EstadoPrenda estado);

    /**
     * Construye y retorna la instancia final de la prenda con todos los atributos configurados.
     *
     * @return Instancia concreta de tipo {@code P}.
     */
    P build();
}
