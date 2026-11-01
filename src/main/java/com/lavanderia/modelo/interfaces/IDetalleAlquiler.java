package com.lavanderia.modelo.interfaces;

import java.math.BigDecimal;

/**
 * Contrato que modela una línea de detalle dentro de un contrato de alquiler.
 * Vincula una prenda específica con el precio acordado para la transacción.
 */
public interface IDetalleAlquiler {

    /**
     * Obtiene el identificador numérico de la línea de detalle.
     *
     * @return ID único del detalle de alquiler.
     */
    long getIdDetalle();

    /**
     * Obtiene la prenda asociada a esta línea del contrato.
     *
     * @return Instancia de {@link IPrenda}.
     */
    IPrenda getPrenda();

    /**
     * Obtiene el valor monetario acordado para el alquiler de esta prenda.
     *
     * @return Monto pactado como {@link BigDecimal}.
     */
    BigDecimal getValorAcordado();
}
