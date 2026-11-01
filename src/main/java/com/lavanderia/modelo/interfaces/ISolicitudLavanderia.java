package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato para solicitudes individuales de lavado generadas a partir de una devolución con novedad.
 */
public interface ISolicitudLavanderia {

    /**
     * Obtiene el identificador único de la solicitud de lavandería.
     *
     * @return ID de la solicitud.
     */
    long getIdSolicitud();

    /**
     * Obtiene la devolución asociada que originó la solicitud de lavado.
     *
     * @return Instancia de {@link IDevolucion}.
     */
    IDevolucion getDevolucion();

    /**
     * Obtiene el lote al cual fue asignada la solicitud, si ya fue empaquetada.
     *
     * @return {@link ILoteLavanderia} asignado o null si sigue pendiente.
     */
    ILoteLavanderia getLote();

    /**
     * Obtiene la fecha en que se recibió la prenda en la lavandería o en recepción.
     *
     * @return Fecha de recepción.
     */
    LocalDate getFechaRecepcion();

    /**
     * Obtiene la prioridad asignada para el procesamiento en lavandería.
     *
     * @return Nivel de {@link Prioridad}.
     */
    Prioridad getPrioridad();

    /**
     * Obtiene el estado de avance de la solicitud.
     *
     * @return {@link EstadoSolicitud}.
     */
    EstadoSolicitud getEstado();

    /**
     * Modifica la fecha de recepción de la solicitud.
     *
     * @param fechaRecepcion Nueva fecha.
     */
    void setFechaRecepcion(LocalDate fechaRecepcion);

    /**
     * Establece o cambia la prioridad asignada a la solicitud.
     *
     * @param prioridad Nivel de prioridad.
     */
    void setPrioridad(Prioridad prioridad);

    /**
     * Actualiza el estado de procesamiento de la solicitud.
     *
     * @param estado Nuevo estado.
     */
    void setEstado(EstadoSolicitud estado);

    /**
     * Asocia la solicitud a un lote específico para su despacho.
     *
     * @param lote Lote contenedor.
     */
    void asignarLote(ILoteLavanderia lote);
}
