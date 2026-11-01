package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;

import java.util.Objects;

public class SistemaFacade implements ISistemaFacade {

    private final IGestorPersonas gestorPersonas;
    private final IGestorPrendas gestorPrendas;
    private final IGestorAlquiler gestorAlquiler;
    private final IGestorLavanderia gestorLavanderia;

    public SistemaFacade(
            IGestorPersonas gestorPersonas,
            IGestorPrendas gestorPrendas,
            IGestorAlquiler gestorAlquiler,
            IGestorLavanderia gestorLavanderia) {

        this.gestorPersonas = Objects.requireNonNull(gestorPersonas);
        this.gestorPrendas = Objects.requireNonNull(gestorPrendas);
        this.gestorAlquiler = Objects.requireNonNull(gestorAlquiler);
        this.gestorLavanderia = Objects.requireNonNull(gestorLavanderia);
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
}