package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoLote;
import java.time.LocalDate;

/**
 * Contrato que representa un lote o tanda de prendas agrupadas para su despacho a lavandería.
 */
public interface ILoteLavanderia {

    /**
     * Obtiene el identificador numérico único del lote.
     *
     * @return ID del lote.
     */
    long getIdLote();

    /**
     * Obtiene la fecha de despacho o envío del lote.
     *
     * @return Fecha de envío.
     */
    LocalDate getFechaEnvio();

    /**
     * Obtiene la capacidad máxima de prendas o cupo configurado para este lote.
     *
     * @return Cupo máximo del lote.
     */
    int getCupo();

    /**
     * Obtiene el estado actual del lote (ABIERTO, ENVIADO, FINALIZADO, CANCELADO).
     *
     * @return {@link EstadoLote} actual.
     */
    EstadoLote getEstado();
}
