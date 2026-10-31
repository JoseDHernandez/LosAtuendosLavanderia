package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.List;

public interface ILavanderiaDAO {

    void guardarSolicitud(ISolicitudLavanderia solicitud);

    List<ISolicitudLavanderia> consultarPendientes();

    void actualizarEstado(ISolicitudLavanderia solicitud);
}
