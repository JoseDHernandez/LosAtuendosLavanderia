package com.lavanderia.modelo.enums;

/**
 * Representa los estados del ciclo de vida de un lote de lavandería industrial.
 */
public enum EstadoLote {

    /** Lote en conformación, disponible para la asignación de solicitudes. */
    ABIERTO,

    /** Lote cerrado y despachado hacia la lavandería externa. */
    ENVIADO,

    /** Lote retornado con todas las prendas higienizadas. */
    FINALIZADO,

    /** Lote cancelado o anulado. */
    CANCELADO
}
