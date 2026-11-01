
package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoRevision;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.util.Objects;

public class Devolucion implements IDevolucion {

    private final long idDevolucion;
    private final IDetalleAlquiler detalleAlquiler;
    private final LocalDate fechaDevolucion;
    private final String observaciones;
    private final EstadoRevision estadoRevision;

    public Devolucion(
            IDetalleAlquiler detalleAlquiler,
            LocalDate fechaDevolucion,
            String observaciones,
            EstadoRevision estadoRevision) {

        this(
                0,
                detalleAlquiler,
                fechaDevolucion,
                observaciones,
                estadoRevision
        );
    }

    public Devolucion(
            long idDevolucion,
            IDetalleAlquiler detalleAlquiler,
            LocalDate fechaDevolucion,
            String observaciones,
            EstadoRevision estadoRevision) {

        this.idDevolucion = idDevolucion;
        this.detalleAlquiler = Objects.requireNonNull(
                detalleAlquiler,
                "El detalle del alquiler es obligatorio."
        );

        this.fechaDevolucion = fechaDevolucion == null
                ? LocalDate.now()
                : fechaDevolucion;

        this.observaciones = observaciones == null
                ? ""
                : observaciones.trim();

        if (estadoRevision == null) {
            throw new ValidacionException(
                    "El estado de revisión es obligatorio."
            );
        }

        this.estadoRevision = estadoRevision;
    }

    @Override
    public long getIdDevolucion() {
        return idDevolucion;
    }

    @Override
    public IDetalleAlquiler getDetalleAlquiler() {
        return detalleAlquiler;
    }

    @Override
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    @Override
    public String getObservaciones() {
        return observaciones;
    }

    @Override
    public EstadoRevision getEstadoRevision() {
        return estadoRevision;
    }
}
