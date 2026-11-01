package com.lavanderia.patrones.facade;

import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;

public interface ISistemaFacade {

    IGestorPersonas getGestorPersonas();

    IGestorPrendas getGestorPrendas();

    IGestorAlquiler getGestorAlquiler();

    IGestorLavanderia getGestorLavanderia();
}