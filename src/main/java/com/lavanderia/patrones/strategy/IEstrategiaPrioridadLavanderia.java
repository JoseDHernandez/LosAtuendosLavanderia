package com.lavanderia.patrones.strategy;

import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import java.util.List;

/**
 * Contrato de Estrategia (patrón Strategy) para la priorización y selección de prendas en lavandería.
 * Permite intercambiar dinámicamente los algoritmos de despacho de tandas según el criterio del negocio
 * (por ejemplo, orden de llegada FIFO o nivel de urgencia/prioridad).
 */
public interface IEstrategiaPrioridadLavanderia {

    /**
     * Selecciona un subconjunto de solicitudes de lavado para conformar un lote, según el cupo disponible.
     *
     * @param solicitudes Lista completa de solicitudes pendientes de lavado.
     * @param cupo Capacidad máxima de prendas que admite el lote a enviar.
     * @return Lista con las solicitudes seleccionadas bajo el criterio del algoritmo implementado.
     */
    List<ISolicitudLavanderia> seleccionarPrendas(
            List<ISolicitudLavanderia> solicitudes,
            int cupo
    );
}
