package com.lavanderia.modelo.interfaces;

/**
 * Contrato para vestidos de dama dentro del catálogo de vestuario.
 * Extiende {@link IPrenda} con especificaciones de pedrería, largo y número de piezas.
 */
public interface IVestidoDama extends IPrenda {

    /**
     * Indica si el vestido cuenta con apliques o incrustaciones de pedrería.
     *
     * @return {@code true} si tiene pedrería, {@code false} de lo contrario.
     */
    boolean isTienePedreria();

    /**
     * Obtiene el largo o caída del vestido (ej. Corto, Midi, Largo, Gala).
     *
     * @return Largo del vestido.
     */
    String getLargo();

    /**
     * Obtiene la cantidad de piezas independientes que componen el vestido (ej. vestido + estola).
     *
     * @return Número de piezas.
     */
    int getNumeroPiezas();
}
