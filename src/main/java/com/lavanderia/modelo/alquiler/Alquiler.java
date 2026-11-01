package com.lavanderia.modelo.alquiler;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Entidad principal que modela un contrato o servicio de Alquiler de vestuario.
 * Implementa {@link IAlquiler}. Administra el cliente, empleado que atiende,
 * fechas programadas (solicitud, inicio y fin prevista), estado del ciclo de vida
 * y la lista de prendas asociadas mediante {@link IDetalleAlquiler}.
 */
public class Alquiler implements IAlquiler {

    /** Identificador primario del alquiler en base de datos. */
    private final long idAlquiler;

    /** Número consecutivo legible y correlativo del servicio. */
    private final int numeroConsecutivo;

    /** Fecha en que se procesó el registro inicial de la solicitud. */
    private final LocalDate fechaSolicitud;

    /** Fecha de inicio pactada para el alquiler (entrega de prendas). */
    private final LocalDate fechaInicio;

    /** Fecha límite pactada para la devolución de las prendas alquiladas. */
    private final LocalDate fechaFinPrevista;

    /** Cliente que suscribe el contrato de alquiler. */
    private final ICliente cliente;

    /** Empleado responsable que registra y atiende la transacción. */
    private final IEmpleado empleado;

    /** Estado actual del servicio de alquiler. */
    private EstadoAlquiler estado;

    /** Lista inmutable de los detalles (prendas y tarifas) que componen el alquiler. */
    private final List<IDetalleAlquiler> detalles;

    /**
     * Constructor para registrar un nuevo alquiler en el sistema.
     * Asigna ID y consecutivo en 0, fecha de solicitud como la fecha actual
     * y estado inicial en {@link EstadoAlquiler#REGISTRADO}.
     *
     * @param fechaInicio Fecha de inicio del alquiler.
     * @param fechaFinPrevista Fecha máxima programada para la devolución.
     * @param cliente Cliente solicitante.
     * @param empleado Empleado que efectúa el registro.
     * @param detalles Lista de prendas alquiladas con sus respectivos importes.
     */
    public Alquiler(
            LocalDate fechaInicio,
            LocalDate fechaFinPrevista,
            ICliente cliente,
            IEmpleado empleado,
            List<IDetalleAlquiler> detalles) {

        // Delegar en el constructor completo con valores iniciales por defecto
        this(
                0,
                0,
                LocalDate.now(),
                fechaInicio,
                fechaFinPrevista,
                cliente,
                empleado,
                EstadoAlquiler.REGISTRADO,
                detalles
        );
    }

    /**
     * Constructor completo para reconstruir una instancia de alquiler ya persistida.
     *
     * @param idAlquiler Identificador único en la base de datos.
     * @param numeroConsecutivo Número correlativo oficial del comprobante.
     * @param fechaSolicitud Fecha de creación del registro.
     * @param fechaInicio Fecha de inicio pactada.
     * @param fechaFinPrevista Fecha límite prevista para la devolución.
     * @param cliente Instancia del cliente titular.
     * @param empleado Empleado que gestionó el alquiler.
     * @param estado Estado del alquiler.
     * @param detalles Lista de líneas de detalle asociadas.
     * @throws NullPointerException Si alguno de los objetos o fechas obligatorios es nulo.
     * @throws IllegalArgumentException Si la lista de detalles está vacía o es nula.
     */
    public Alquiler(
            long idAlquiler,
            int numeroConsecutivo,
            LocalDate fechaSolicitud,
            LocalDate fechaInicio,
            LocalDate fechaFinPrevista,
            ICliente cliente,
            IEmpleado empleado,
            EstadoAlquiler estado,
            List<IDetalleAlquiler> detalles) {

        // Asignación de identificadores
        this.idAlquiler = idAlquiler;
        this.numeroConsecutivo = numeroConsecutivo;

        // Validaciones de no nulidad en atributos obligatorios
        this.fechaSolicitud = Objects.requireNonNull(fechaSolicitud, "La fecha de solicitud es obligatoria.");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFinPrevista = Objects.requireNonNull(fechaFinPrevista, "La fecha fin prevista es obligatoria.");
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado es obligatorio.");
        this.estado = Objects.requireNonNull(estado, "El estado del alquiler es obligatorio.");

        // Validar que el contrato contenga al menos una prenda en arriendo
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El alquiler debe contener al menos una prenda."
            );
        }

        // Crear una copia inmutable de la lista de detalles para proteger el encapsulamiento
        this.detalles = List.copyOf(detalles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdAlquiler() {
        return idAlquiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaFinPrevista() {
        return fechaFinPrevista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICliente getCliente() {
        return cliente;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEmpleado getEmpleado() {
        return empleado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstadoAlquiler getEstado() {
        return estado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IDetalleAlquiler> getDetalles() {
        return detalles;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException Si el nuevo estado es nulo.
     */
    @Override
    public void cambiarEstado(EstadoAlquiler nuevoEstado) {
        // Asignar el nuevo estado asegurando que no sea nulo
        this.estado = Objects.requireNonNull(nuevoEstado, "El nuevo estado no puede ser nulo.");
    }
}
