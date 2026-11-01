
package com.lavanderia.modelo.alquiler;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Alquiler implements IAlquiler {

    private final long idAlquiler;
    private final int numeroConsecutivo;
    private final LocalDate fechaSolicitud;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFinPrevista;
    private final ICliente cliente;
    private final IEmpleado empleado;
    private EstadoAlquiler estado;
    private final List<IDetalleAlquiler> detalles;

    // Constructor para registrar un nuevo alquiler
    public Alquiler(
            LocalDate fechaInicio,
            LocalDate fechaFinPrevista,
            ICliente cliente,
            IEmpleado empleado,
            List<IDetalleAlquiler> detalles) {

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

    // Constructor para reconstruir un alquiler existente
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

        this.idAlquiler = idAlquiler;
        this.numeroConsecutivo = numeroConsecutivo;
        this.fechaSolicitud = Objects.requireNonNull(fechaSolicitud);
        this.fechaInicio = Objects.requireNonNull(fechaInicio);
        this.fechaFinPrevista = Objects.requireNonNull(fechaFinPrevista);
        this.cliente = Objects.requireNonNull(cliente);
        this.empleado = Objects.requireNonNull(empleado);
        this.estado = Objects.requireNonNull(estado);

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El alquiler debe contener al menos una prenda."
            );
        }

        this.detalles = List.copyOf(detalles);
    }

    @Override
    public long getIdAlquiler() {
        return idAlquiler;
    }

    @Override
    public int getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    @Override
    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    @Override
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    @Override
    public LocalDate getFechaFinPrevista() {
        return fechaFinPrevista;
    }

    @Override
    public ICliente getCliente() {
        return cliente;
    }

    @Override
    public IEmpleado getEmpleado() {
        return empleado;
    }

    @Override
    public EstadoAlquiler getEstado() {
        return estado;
    }

    @Override
    public List<IDetalleAlquiler> getDetalles() {
        return detalles;
    }

    @Override
    public void cambiarEstado(EstadoAlquiler nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }
}
