package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;

import java.time.LocalDate;

public interface ISolicitudLavanderia {

    long getIdSolicitud();

    IDevolucion getDevolucion();

    ILoteLavanderia getLote();

    LocalDate getFechaRecepcion();

    Prioridad getPrioridad();

    EstadoSolicitud getEstado();

    void setFechaRecepcion(LocalDate fechaRecepcion);

    void setPrioridad(Prioridad prioridad);

    void setEstado(EstadoSolicitud estado);

    void asignarLote(ILoteLavanderia lote);
}