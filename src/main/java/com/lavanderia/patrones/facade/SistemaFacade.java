package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.*;

import java.util.Objects;

/**
 * Implementación de la Fachada del Sistema (patrón Facade).
 * Centraliza la provisión y el desacoplamiento de los gestores del sistema de lavandería.
 *
 * <p><b>Observación arquitectónica:</b> Funciona principalmente como un Service Locator /
 * Contenedor de Servicios para la capa de presentación (GUI), reuniendo los gestores
 * {@link IGestorPersonas}, {@link IGestorPrendas}, {@link IGestorAlquiler},
 * {@link IGestorLavanderia} e {@link IGestorDevoluciones}.</p>
 */
public class SistemaFacade implements ISistemaFacade {

    /** Gestor de lógica de negocio para personas (clientes y empleados). */
    private final IGestorPersonas gestorPersonas;

    /** Gestor de lógica de negocio para prendas e inventario. */
    private final IGestorPrendas gestorPrendas;

    /** Gestor de lógica de negocio para alquileres. */
    private final IGestorAlquiler gestorAlquiler;

    /** Gestor de lógica de negocio para solicitudes y tandas de lavandería. */
    private final IGestorLavanderia gestorLavanderia;

    /** Gestor de lógica de negocio para devoluciones y peritajes. */
    private final IGestorDevoluciones gestorDevoluciones;

    /**
     * Construye la fachada del sistema inyectando las cinco dependencias de gestión de negocio.
     *
     * @param gestorPersonas Gestor de personas.
     * @param gestorPrendas Gestor de prendas.
     * @param gestorAlquiler Gestor de alquileres.
     * @param gestorLavanderia Gestor de lavandería.
     * @param gestorDevoluciones Gestor de devoluciones.
     * @throws NullPointerException Si alguno de los gestores es nulo.
     */
    public SistemaFacade(
            IGestorPersonas gestorPersonas,
            IGestorPrendas gestorPrendas,
            IGestorAlquiler gestorAlquiler,
            IGestorLavanderia gestorLavanderia,
            IGestorDevoluciones gestorDevoluciones) {

        // Validación estricta de no nulidad en las dependencias requeridas
        this.gestorPersonas = Objects.requireNonNull(gestorPersonas, "El gestor de personas es obligatorio.");
        this.gestorPrendas = Objects.requireNonNull(gestorPrendas, "El gestor de prendas es obligatorio.");
        this.gestorAlquiler = Objects.requireNonNull(gestorAlquiler, "El gestor de alquileres es obligatorio.");
        this.gestorLavanderia = Objects.requireNonNull(gestorLavanderia, "El gestor de lavandería es obligatorio.");
        this.gestorDevoluciones = Objects.requireNonNull(gestorDevoluciones, "El gestor de devoluciones es obligatorio.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGestorPersonas getGestorPersonas() {
        return gestorPersonas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGestorPrendas getGestorPrendas() {
        return gestorPrendas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGestorAlquiler getGestorAlquiler() {
        return gestorAlquiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGestorLavanderia getGestorLavanderia() {
        return gestorLavanderia;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGestorDevoluciones getGestorDevoluciones() {
        return gestorDevoluciones;
    }
}
