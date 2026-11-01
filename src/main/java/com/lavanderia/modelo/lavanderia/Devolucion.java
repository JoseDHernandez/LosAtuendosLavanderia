package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoRevision;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad concreta que modela la Devolución de una prenda previamente alquilada.
 * Implementa {@link IDevolucion}. Registra la fecha de retorno, la línea de alquiler
 * correspondiente, observaciones del estado del vestuario y el dictamen de revisión técnica.
 */
public class Devolucion implements IDevolucion {

    /** Identificador primario de la devolución en base de datos. */
    private final long idDevolucion;

    /** Detalle de alquiler al cual pertenece la prenda que se devuelve. */
    private final IDetalleAlquiler detalleAlquiler;

    /** Fecha efectiva en que se realiza la entrega de la prenda. */
    private final LocalDate fechaDevolucion;

    /** Observaciones, comentarios o anotaciones sobre desperfectos encontrados. */
    private final String observaciones;

    /** Dictamen de la revisión técnica (PENDIENTE, SIN_NOVEDAD, CON_NOVEDAD). */
    private final EstadoRevision estadoRevision;

    /**
     * Constructor para registrar una nueva devolución antes de ser persistida.
     * Inicializa el ID en 0.
     *
     * @param detalleAlquiler Detalle de alquiler asociado a la prenda devuelta.
     * @param fechaDevolucion Fecha de entrega.
     * @param observaciones Comentarios u observaciones.
     * @param estadoRevision Dictamen de revisión física.
     */
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

    /**
     * Constructor completo para reconstruir una devolución recuperada de la persistencia.
     *
     * @param idDevolucion Identificador único en base de datos.
     * @param detalleAlquiler Línea de detalle de alquiler.
     * @param fechaDevolucion Fecha de entrega (si es nula se toma la fecha actual).
     * @param observaciones Notas u observaciones (se limpian espacios en blanco).
     * @param estadoRevision Dictamen del estado de revisión de la prenda.
     * @throws ValidacionException Si el estado de revisión es nulo.
     * @throws NullPointerException Si el detalle del alquiler es nulo.
     */
    public Devolucion(
            long idDevolucion,
            IDetalleAlquiler detalleAlquiler,
            LocalDate fechaDevolucion,
            String observaciones,
            EstadoRevision estadoRevision) {

        this.idDevolucion = idDevolucion;

        // Validar que el detalle de alquiler esté presente
        this.detalleAlquiler = Objects.requireNonNull(
                detalleAlquiler,
                "El detalle del alquiler es obligatorio."
        );

        // Si la fecha no se especifica, asignar la fecha del sistema
        this.fechaDevolucion = fechaDevolucion == null
                ? LocalDate.now()
                : fechaDevolucion;

        // Normalizar observaciones a cadena vacía si es nulo
        this.observaciones = observaciones == null
                ? ""
                : observaciones.trim();

        // Validar que el estado de revisión no sea nulo
        if (estadoRevision == null) {
            throw new ValidacionException(
                    "El estado de revisión es obligatorio."
            );
        }

        this.estadoRevision = estadoRevision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdDevolucion() {
        return idDevolucion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDetalleAlquiler getDetalleAlquiler() {
        return detalleAlquiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstadoRevision getEstadoRevision() {
        return estadoRevision;
    }
}
