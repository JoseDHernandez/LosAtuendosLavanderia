package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia concreta que despacha solicitudes de lavandería siguiendo el principio FIFO
 * (First In, First Out / Primero en llegar, primero en ser atendido).
 * Ordena las solicitudes por su fecha de recepción de forma cronológica ascendente.
 */
public class EstrategiaFIFO implements IEstrategiaPrioridadLavanderia {

    /**
     * Selecciona las solicitudes más antiguas por fecha de recepción hasta agotar el cupo.
     *
     * @param pendientes Lista de solicitudes de lavado pendientes.
     * @param cupo Capacidad máxima de prendas del lote.
     * @return Sublista de solicitudes seleccionadas en orden cronológico.
     */
    @Override
    public List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> pendientes,
            int cupo) {

        // Validar si la lista está vacía o si el cupo es inválido
        if (pendientes == null || pendientes.isEmpty() || cupo <= 0) {
            return new ArrayList<>();
        }

        // Crear una copia de la lista para evitar efectos secundarios sobre la colección original
        List<ISolicitudLavanderia> copia =
                new ArrayList<>(pendientes);

        // Ordenar ascendentemente por fecha de recepción (las más antiguas primero)
        copia.sort(
                Comparator
                        .comparing(ISolicitudLavanderia::getFechaRecepcion)
                        .thenComparingLong(ISolicitudLavanderia::getIdSolicitud)
        );

        // Tomar las primeras N solicitudes según el cupo disponible
        return new ArrayList<>(
                copia.subList(0, Math.min(cupo, copia.size()))
        );
    }
}
