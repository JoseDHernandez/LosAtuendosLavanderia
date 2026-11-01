package com.lavanderia.modelo.interfaces;

/**
 * Contrato especializado para prendas de tipo Disfraz.
 * Extiende las propiedades generales de {@link IPrenda}.
 */
public interface IDisfraz extends IPrenda {

    /**
     * Obtiene el nombre temático o personaje al que representa el disfraz.
     *
     * @return Nombre del personaje o disfraz.
     */
    String getNombre();
}
