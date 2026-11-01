package com.lavanderia.modelo.enums;

/**
 * Representa el resultado del peritaje o revisión técnica de una prenda devuelta.
 */
public enum EstadoRevision {

    /** Prenda recibida pero pendiente de inspección física. */
    PENDIENTE,

    /** Prenda revisada en perfecto estado, apta para reingreso inmediato. */
    SIN_NOVEDAD,

    /** Prenda devuelta con manchas, rasgaduras o desperfectos (requiere lavandería o arreglo). */
    CON_NOVEDAD
}
