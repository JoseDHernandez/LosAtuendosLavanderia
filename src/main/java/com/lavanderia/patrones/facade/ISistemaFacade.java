package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.*;

/**
 * Contrato de la Fachada del Sistema (patrón Facade).
 * Proporciona un punto de acceso unificado hacia los cinco gestores de servicio principales
 * del aplicativo, simplificando la interacción desde las capas de presentación (vistas Swing).
 */
public interface ISistemaFacade {

    /**
     * Obtiene el gestor de servicios para el registro y consulta de personas (clientes y empleados).
     *
     * @return Instancia de {@link IGestorPersonas}.
     */
    IGestorPersonas getGestorPersonas();

    /**
     * Obtiene el gestor de servicios para el catálogo e inventario de prendas de vestuario.
     *
     * @return Instancia de {@link IGestorPrendas}.
     */
    IGestorPrendas getGestorPrendas();

    /**
     * Obtiene el gestor de servicios para los procesos de alquiler y reservas.
     *
     * @return Instancia de {@link IGestorAlquiler}.
     */
    IGestorAlquiler getGestorAlquiler();

    /**
     * Obtiene el gestor de servicios para el flujo de solicitudes y lotes de lavandería.
     *
     * @return Instancia de {@link IGestorLavanderia}.
     */
    IGestorLavanderia getGestorLavanderia();

    /**
     * Obtiene el gestor de servicios para el registro y revisión de devoluciones de prendas.
     *
     * @return Instancia de {@link IGestorDevoluciones}.
     */
    IGestorDevoluciones getGestorDevoluciones();
}
