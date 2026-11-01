package com.lavanderia.modelo.enums;

/**
 * Representa el estado de una solicitud individual de lavado para una prenda devuelta.
 */
public enum EstadoSolicitud {

    /** Solicitud en espera de asignación a un lote de despacho. */
    PENDIENTE,

    /** Prenda actualmente en tratamiento de lavado. */
    EN_LAVADO,

    /** Proceso de lavado completado con éxito, lista para regresar a stock. */
    LISTA,

    /** Solicitud de lavado descartada o anulada. */
    CANCELADA
}
