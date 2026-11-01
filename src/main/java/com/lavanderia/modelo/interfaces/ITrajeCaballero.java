package com.lavanderia.modelo.interfaces;

/**
 * Contrato para trajes de caballero dentro del catálogo de vestuario.
 * Extiende {@link IPrenda} agregando tipo de traje y complementos.
 */
public interface ITrajeCaballero extends IPrenda {

    /**
     * Obtiene el estilo o corte del traje (ej. Clásico, Esmoquin, Chaqué, Frac).
     *
     * @return Tipo de traje.
     */
    String getTipoTraje();

    /**
     * Obtiene el tipo de accesorio complementario incluido (ej. Corbata, Corbatín, Chaleco).
     *
     * @return Tipo de complemento.
     */
    String getTipoComplemento();
}
