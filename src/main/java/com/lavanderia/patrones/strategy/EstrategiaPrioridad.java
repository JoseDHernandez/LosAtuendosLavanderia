package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia concreta que despacha solicitudes de lavandería considerando el nivel de urgencia/prioridad.
 * Ordena primero por el nivel numérico de la prioridad (descendente: ESPECIAL > MANCHADA > DELICADA > NORMAL),
 * y en caso de empate desempata cronológicamente por la fecha de recepción (ascendente).
 */
public class EstrategiaPrioridad implements IEstrategiaPrioridadLavanderia {

    /**
     * Selecciona las solicitudes más prioritarias para completar el cupo del lote.
     *
     * @param pendientes Lista de solicitudes pendientes de lavado.
     * @param cupo Capacidad máxima de prendas permitidas en el lote.
     * @return Sublista de solicitudes seleccionadas ordenadas por prioridad.
     */
    @Override
    public List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> pendientes,
            int cupo) {

        // Validar lista vacía o cupo menor o igual a cero
        if (pendientes == null || pendientes.isEmpty() || cupo <= 0) {
            return new ArrayList<>();
        }

        // Crear una copia mutable para realizar la ordenación
        List<ISolicitudLavanderia> copia =
                new ArrayList<>(pendientes);

        // Ordenar primero por nivel de prioridad en orden descendente (mayor nivel primero)
        // y como criterio secundario por fecha de recepción ascendente (más antiguas primero)
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

        // Extraer las solicitudes que quepan dentro del cupo establecido
        return new ArrayList<>(
                copia.subList(0, Math.min(cupo, copia.size()))
        );
    }
}
