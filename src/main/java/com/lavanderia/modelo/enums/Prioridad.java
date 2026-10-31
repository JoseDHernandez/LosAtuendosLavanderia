package com.lavanderia.modelo.enums;

import com.lavanderia.validacion.ValidacionException;

public enum Prioridad {

    NORMAL(1),
    DELICADA(2),
    MANCHADA(3),
    ESPECIAL(4);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

}
