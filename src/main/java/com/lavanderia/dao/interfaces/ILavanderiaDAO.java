package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.modelo.lavanderia.SolicitudLavanderia;

import java.util.List;
import java.util.Optional;

public interface ILavanderiaDAO {

    void guardarSolicitud(ISolicitudLavanderia solicitud);

    List<ISolicitudLavanderia> consultarPendientes();

    void actualizarEstado(
            long idSolicitud,
            EstadoSolicitud nuevoEstado
    );

    ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> solicitudes,
            ILoteLavanderia lote
    );

    Optional<SolicitudLavanderia> buscarSolicitudPorDevolucion(
            long idDevolucion
    );


    void actualizarSolicitud(SolicitudLavanderia solicitud);


}