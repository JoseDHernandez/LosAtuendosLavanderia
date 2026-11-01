package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad que modela una Solicitud individual de Lavandería para una prenda retornada con novedades.
 * Implementa {@link ISolicitudLavanderia}. Controla la prioridad asignada, la fecha de recepción,
 * el estado del servicio y la asociación al lote de despacho correspondiente.
 */
public class SolicitudLavanderia implements ISolicitudLavanderia {

    /** Identificador único de la solicitud en base de datos. */
    private final long idSolicitud;

    /** Devolución origen que motivó el envío de la prenda a lavandería. */
    private final IDevolucion devolucion;

    /** Fecha en la que la prenda fue recepcionada para el proceso de lavado. */
    private LocalDate fechaRecepcion;

    /** Nivel de prioridad asignado según la delicadeza o suciedad de la prenda. */
    private Prioridad prioridad;

    /** Lote al que ha sido asignada la solicitud (null mientras esté pendiente). */
    private ILoteLavanderia lote;

    /** Estado de la solicitud (PENDIENTE, EN_LAVADO, LISTA, CANCELADA). */
    private EstadoSolicitud estado;

    /**
     * Constructor para registrar una nueva solicitud en estado {@link EstadoSolicitud#PENDIENTE}.
     *
     * @param devolucion Devolución que originó la solicitud.
     * @param prioridad Prioridad asignada para el turno de lavado.
     * @param fechaRecepcion Fecha de recepción.
     */
    public SolicitudLavanderia(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        this(
                0,
                devolucion,
                null,
                prioridad,
                fechaRecepcion,
                EstadoSolicitud.PENDIENTE
        );
    }

    /**
     * Constructor completo para reconstruir una solicitud desde la persistencia.
     *
     * @param idSolicitud Identificador primario en base de datos.
     * @param devolucion Devolución asociada (obligatoria).
     * @param lote Lote contenedor (puede ser nulo si aún no está asignada).
     * @param prioridad Nivel de prioridad (obligatorio).
     * @param fechaRecepcion Fecha de recepción.
     * @param estado Estado de la solicitud (obligatorio).
     * @throws ValidacionException Si algún parámetro obligatorio es inválido o nulo.
     */
    public SolicitudLavanderia(
            long idSolicitud,
            IDevolucion devolucion,
            ILoteLavanderia lote,
            Prioridad prioridad,
            LocalDate fechaRecepcion,
            EstadoSolicitud estado) {

        // Validar que el identificador no sea negativo
        if (idSolicitud < 0) {
            throw new ValidacionException(
                    "El identificador de la solicitud no puede ser negativo."
            );
        }

        // Validar que la devolución asociada exista
        if (devolucion == null) {
            throw new ValidacionException(
                    "La devolución asociada es obligatoria."
            );
        }

        // Validar la prioridad
        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

        // Validar el estado
        if (estado == null) {
            throw new ValidacionException(
                    "El estado de la solicitud es obligatorio."
            );
        }

        this.idSolicitud = idSolicitud;
        this.devolucion = devolucion;
        this.lote = lote;
        this.prioridad = prioridad;
        this.fechaRecepcion = fechaRecepcion == null
                ? LocalDate.now()
                : fechaRecepcion;
        this.estado = estado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDevolucion getDevolucion() {
        return devolucion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILoteLavanderia getLote() {
        return lote;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaRecepcion() {
        return fechaRecepcion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la fecha es nula o si la solicitud no está PENDIENTE.
     */
    @Override
    public void setFechaRecepcion(LocalDate fechaRecepcion) {

        // Validar que la fecha no sea nula
        if (fechaRecepcion == null) {
            throw new ValidacionException(
                    "La fecha de recepción es obligatoria."
            );
        }

        // Solo se permite reprogramar fecha si la solicitud no ha sido procesada aún
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ValidacionException(
                    "Solo se puede modificar la fecha de una solicitud pendiente."
            );
        }

        this.fechaRecepcion = fechaRecepcion;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la prioridad es nula o si la solicitud no está PENDIENTE.
     */
    @Override
    public void setPrioridad(Prioridad prioridad) {

        // Validar que la prioridad no sea nula
        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

        // Solo se permite cambiar prioridad si la solicitud sigue pendiente
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ValidacionException(
                    "Solo se puede modificar la prioridad de una solicitud pendiente."
            );
        }

        this.prioridad = prioridad;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el estado es nulo.
     */
    @Override
    public void setEstado(EstadoSolicitud estado) {

        // Validar no nulidad
        if (estado == null) {
            throw new ValidacionException(
                    "El estado de la solicitud es obligatorio."
            );
        }

        this.estado = estado;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el lote es nulo, si ya tenía lote o si no está PENDIENTE.
     */
    @Override
    public void asignarLote(ILoteLavanderia lote) {

        // Validar que el lote no sea nulo
        if (lote == null) {
            throw new ValidacionException(
                    "El lote de lavandería es obligatorio."
            );
        }

        // Evitar reasignación si ya cuenta con un lote
        if (this.lote != null) {
            throw new ValidacionException(
                    "La solicitud ya tiene un lote asignado."
            );
        }

        // Solo se pueden empaquetar solicitudes pendientes
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ValidacionException(
                    "Solo se pueden asignar al lote solicitudes pendientes."
            );
        }

        this.lote = lote;
    }

    /**
     * Representación textual de la solicitud con fines de depuración.
     *
     * @return Cadena con los atributos clave de la solicitud.
     */
    @Override
    public String toString() {
        return "SolicitudLavanderia{" +
                "idSolicitud=" + idSolicitud +
                ", devolucion=" + devolucion.getIdDevolucion() +
                ", fechaRecepcion=" + fechaRecepcion +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                '}';
    }
}
