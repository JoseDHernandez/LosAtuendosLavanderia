
package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.util.Objects;

public class SolicitudLavanderia implements ISolicitudLavanderia {

    private final long idSolicitud;
    private final IDevolucion devolucion;
    private final LocalDate fechaRecepcion;
    private final Prioridad prioridad;

    private ILoteLavanderia lote;
    private EstadoSolicitud estado;

    // Constructor para registrar una nueva solicitud
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

    // Constructor para recuperar una solicitud existente
    public SolicitudLavanderia(
            long idSolicitud,
            IDevolucion devolucion,
            ILoteLavanderia lote,
            Prioridad prioridad,
            LocalDate fechaRecepcion,
            EstadoSolicitud estado) {

        if (devolucion == null) {
            throw new ValidacionException(
                    "La devolución asociada es obligatoria."
            );
        }

        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

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

    @Override
    public long getIdSolicitud() {
        return idSolicitud;
    }

    @Override
    public IDevolucion getDevolucion() {
        return devolucion;
    }

    @Override
    public ILoteLavanderia getLote() {
        return lote;
    }

    @Override
    public LocalDate getFechaRecepcion() {
        return fechaRecepcion;
    }

    @Override
    public Prioridad getPrioridad() {
        return prioridad;
    }

    @Override
    public EstadoSolicitud getEstado() {
        return estado;
    }

    @Override
    public void setEstado(EstadoSolicitud estado) {

        if (estado == null) {
            throw new ValidacionException(
                    "El estado de la solicitud es obligatorio."
            );
        }

        this.estado = estado;
    }

    @Override
    public void asignarLote(ILoteLavanderia lote) {

        if (lote == null) {
            throw new ValidacionException(
                    "El lote de lavandería es obligatorio."
            );
        }

        if (this.lote != null) {
            throw new ValidacionException(
                    "La solicitud ya tiene un lote asignado."
            );
        }

        this.lote = lote;
    }

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
