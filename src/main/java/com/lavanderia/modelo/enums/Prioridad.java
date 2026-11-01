package com.lavanderia.modelo.enums;

/**
 * Nivel de urgencia y tratamiento asignado a una solicitud de lavado.
 */
public enum Prioridad {

    /** Tratamiento convencional (prioridad base). */
    NORMAL(1),

    /** Tratamiento para tejidos frágiles o pedrería delicada. */
    DELICADA(2),

    /** Tratamiento químico intensivo para manchas difíciles. */
    MANCHADA(3),

    /** Máxima urgencia por agenda de entrega o prendas de alta gama. */
    ESPECIAL(4);

    /** Nivel numérico de prelación (mayor valor indica mayor prioridad en la cola). */
    private final int nivel;

    /**
     * Constructor del enum asignando el peso o nivel jerárquico.
     *
     * @param nivel Ponderación numérica de prioridad.
     */
    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene el nivel numérico de prelación para ordenamiento.
     *
     * @return Entero con el nivel de prioridad.
     */
    public int getNivel() {
        return nivel;
    }
}
