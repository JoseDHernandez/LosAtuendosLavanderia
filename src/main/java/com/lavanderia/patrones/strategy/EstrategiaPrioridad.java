package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EstrategiaPrioridad implements EstrategiaLavanderia {

    @Override
    public List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> pendientes,
            int cupo) {

        List<ISolicitudLavanderia> copia =
                new ArrayList<>(pendientes);

        copia.sort(
                Comparator
                        .comparingInt(
                                (ISolicitudLavanderia s) ->
                                        s.getPrioridad().getNivel()
                        )
                        .reversed()
                        .thenComparing(
                                ISolicitudLavanderia::getFechaRecepcion
                        )
        );

        return new ArrayList<>(
                copia.subList(0, Math.min(cupo, copia.size()))
        );
    }
}
