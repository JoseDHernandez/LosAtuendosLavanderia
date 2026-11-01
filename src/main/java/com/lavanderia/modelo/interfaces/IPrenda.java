package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoPrenda;

/**
 * Contrato general para cualquier prenda gestionada en el inventario de vestuario.
 */
public interface IPrenda {

    /**
     * Obtiene el identificador numérico interno de la prenda.
     *
     * @return ID de la prenda.
     */
    long getIdPrenda();

    /**
     * Obtiene la referencia comercial o código alfanumérico único.
     *
     * @return Código de referencia.
     */
    String getReferencia();

    /**
     * Obtiene el color principal de la prenda.
     *
     * @return Color de la prenda.
     */
    String getColor();

    /**
     * Obtiene la marca o fabricante de la confección.
     *
     * @return Marca de la prenda.
     */
    String getMarca();

    /**
     * Obtiene la talla estandarizada (ej. S, M, L, 38, etc.).
     *
     * @return Talla de la prenda.
     */
    String getTalla();

    /**
     * Obtiene el valor base de alquiler de la prenda por servicio.
     *
     * @return Precio base en centavos o unidades enteras.
     */
    long getValorAlquiler();

    /**
     * Obtiene el estado actual de disponibilidad física de la prenda.
     *
     * @return {@link EstadoPrenda} de la prenda.
     */
    EstadoPrenda getEstado();

    /**
     * Cambia el estado de disponibilidad física de la prenda.
     *
     * @param nuevoEstado Nuevo {@link EstadoPrenda} asignado.
     */
    void cambiarEstado(EstadoPrenda nuevoEstado);
}
