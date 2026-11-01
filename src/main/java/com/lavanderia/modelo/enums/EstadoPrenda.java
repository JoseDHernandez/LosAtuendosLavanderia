package com.lavanderia.modelo.enums;

/**
 * Representa los posibles estados de disponibilidad y ubicación de una prenda.
 */
public enum EstadoPrenda {

    /** Prenda disponible en bodega/tienda para ser alquilada. */
    DISPONIBLE,

    /** Prenda actualmente en posesión de un cliente por alquiler vigente. */
    ALQUILADA,

    /** Prenda en proceso de limpieza en lavandería externa o interna. */
    EN_LAVANDERIA,

    /** Prenda retirada temporalmente para compostura o mantenimiento preventivo/correctivo. */
    EN_MANTENIMIENTO
}
