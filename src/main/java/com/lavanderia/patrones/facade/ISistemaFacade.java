package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.*;

public interface ISistemaFacade {

    IGestorPersonas getGestorPersonas();

    IGestorPrendas getGestorPrendas();

    IGestorAlquiler getGestorAlquiler();

    IGestorLavanderia getGestorLavanderia();

    IGestorDevoluciones getGestorDevoluciones();
}