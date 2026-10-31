package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;

import java.time.LocalDate;

public interface ISolicitudLavanderia {

    String getReferenciaPrenda();

    LocalDate getFechaRecepcion();

    Prioridad getPrioridad();

    EstadoSolicitud getEstado();

    void setEstado(EstadoSolicitud estado);
}
