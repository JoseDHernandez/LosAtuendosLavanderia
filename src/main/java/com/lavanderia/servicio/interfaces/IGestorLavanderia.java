package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.strategy.IEstrategiaPrioridadLavanderia;

import java.time.LocalDate;
import java.util.List;

public interface IGestorLavanderia {

    // Gestión de solicitudes
    void registrarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion
    );

    void registrarOActualizarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion
    );

    void cancelarSolicitudPorDevolucion(long idDevolucion);

    // Gestión de tandas
    List<ISolicitudLavanderia> seleccionarTanda(String cupoDiario);

    List<ISolicitudLavanderia> seleccionarTanda(int cupoDiario);

    ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda
    );

    // Estrategia de prioridad
    void cambiarEstrategia(
            IEstrategiaPrioridadLavanderia estrategia
    );

    // Finalización de solicitudes
    void marcarSolicitudLista(ISolicitudLavanderia solicitud);
}