package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.*;

import java.util.Objects;

public class SistemaFacade implements ISistemaFacade {

    private final IGestorPersonas gestorPersonas;
    private final IGestorPrendas gestorPrendas;
    private final IGestorAlquiler gestorAlquiler;
    private final IGestorLavanderia gestorLavanderia;
    private  final IGestorDevoluciones gestorDevoluciones;
    public SistemaFacade(
            IGestorPersonas gestorPersonas,
            IGestorPrendas gestorPrendas,
            IGestorAlquiler gestorAlquiler,
            IGestorLavanderia gestorLavanderia,
            IGestorDevoluciones gestorDevoluciones) {

        this.gestorPersonas = Objects.requireNonNull(gestorPersonas);
        this.gestorPrendas = Objects.requireNonNull(gestorPrendas);
        this.gestorAlquiler = Objects.requireNonNull(gestorAlquiler);
        this.gestorLavanderia = Objects.requireNonNull(gestorLavanderia);
        this.gestorDevoluciones = Objects.requireNonNull(gestorDevoluciones);
    }

    @Override
    public IGestorPersonas getGestorPersonas() {
        return gestorPersonas;
    }

    @Override
    public IGestorPrendas getGestorPrendas() {
        return gestorPrendas;
    }

    @Override
    public IGestorAlquiler getGestorAlquiler() {
        return gestorAlquiler;
    }

    @Override
    public IGestorLavanderia getGestorLavanderia() {
        return gestorLavanderia;
    }

    @Override
    public IGestorDevoluciones getGestorDevoluciones(){return gestorDevoluciones;}
}