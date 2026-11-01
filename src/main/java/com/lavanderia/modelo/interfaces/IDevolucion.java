package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoRevision;
import java.time.LocalDate;

/**
 * Contrato que representa el registro de retorno físico de una prenda alquilada.
 * Incluye la fecha de retorno, observaciones del estado y dictamen de revisión.
 */
public interface IDevolucion {

    /**
     * Obtiene el identificador primario de la devolución.
     *
     * @return ID de la devolución.
     */
    long getIdDevolucion();

    /**
     * Obtiene el detalle de alquiler al que corresponde la prenda devuelta.
     *
     * @return Instancia de {@link IDetalleAlquiler}.
     */
    IDetalleAlquiler getDetalleAlquiler();

    /**
     * Obtiene la fecha exacta en la cual el cliente realizó la devolución.
     *
     * @return Fecha de devolución.
     */
    LocalDate getFechaDevolucion();

    /**
     * Obtiene las notas u observaciones técnicas asentadas al recibir la prenda.
     *
     * @return Cadena con comentarios u observaciones de revisión.
     */
    String getObservaciones();

    /**
     * Obtiene el dictamen de la inspección técnica de la prenda.
     *
     * @return {@link EstadoRevision} (PENDIENTE, SIN_NOVEDAD, CON_NOVEDAD).
     */
    EstadoRevision getEstadoRevision();
}
