package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;

public class SolicitudLavanderia implements ISolicitudLavanderia {

    private final String referenciaPrenda;
    private final LocalDate fechaRecepcion;
    private final Prioridad prioridad;

    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    public SolicitudLavanderia(
            String referenciaPrenda,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

        this.referenciaPrenda = validarReferencia(referenciaPrenda);
        this.prioridad = prioridad;
        this.fechaRecepcion = fechaRecepcion == null
                ? LocalDate.now()
                : fechaRecepcion;
    }
    private String validarReferencia(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "La referencia de la prenda es obligatoria."
            );
        }

        return referencia.trim();
    }
    @Override
    public String getReferenciaPrenda() {
        return referenciaPrenda;
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
    public String toString() {
        return "Solicitud(" + referenciaPrenda + ", "
                + prioridad + ", "
                + fechaRecepcion + ", "
                + estado + ")";
    }
}
