package com.lavanderia.modelo.enums;

/**
 * Representa los estados del ciclo de vida de un alquiler de prendas.
 */
public enum EstadoAlquiler {

    /** El alquiler ha sido registrado inicialmente en el sistema. */
    REGISTRADO,

    /** Las prendas han sido entregadas al cliente y el alquiler está en curso. */
    ACTIVO,

    /** Las prendas han sido devueltas y el contrato concluyó satisfactoriamente. */
    FINALIZADO,

    /** El alquiler fue anulado o cancelado antes de su culminación. */
    CANCELADO
}
