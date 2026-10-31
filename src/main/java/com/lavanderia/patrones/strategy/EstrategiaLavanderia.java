package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.List;

public interface EstrategiaLavanderia {
    List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> pendientes,
            int cupo
    );
}
