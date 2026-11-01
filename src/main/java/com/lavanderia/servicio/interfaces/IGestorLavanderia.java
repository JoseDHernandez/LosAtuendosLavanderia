package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.strategy.EstrategiaLavanderia;

import java.time.LocalDate;
import java.util.List;

public interface IGestorLavanderia {

    // REGISTRO DE SOLICITUDES

    void registrarDevolucion(
            IDevolucion devolucion,
            String prioridad
    );

    void registrarDevolucion(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion
    );

    // SELECCIÓN DE TANDA

    List<ISolicitudLavanderia> seleccionarTanda(
            String cupoDiario
    );

    List<ISolicitudLavanderia> seleccionarTanda(
            int cupoDiario
    );

    // CONFIRMACIÓN DEL ENVÍO

    ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda
    );

    // GESTIÓN DE ESTRATEGIAS

    void cambiarEstrategia(
            EstrategiaLavanderia estrategia
    );

    // ACTUALIZACIÓN DE SOLICITUDES

    void marcarSolicitudLista(
            ISolicitudLavanderia solicitud
    );
}