
package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.List;

public interface IEstrategiaPrioridadLavanderia {

    List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> solicitudes,
            int cupo
    );
}
