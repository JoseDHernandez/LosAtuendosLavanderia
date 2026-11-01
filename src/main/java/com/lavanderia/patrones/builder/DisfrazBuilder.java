package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.Disfraz;

/**
 * Constructor concreto (Builder) para instanciar objetos {@link Disfraz}.
 * Extiende {@link PrendaBuilder} especializándolo con el nombre o temática del disfraz.
 */
public class DisfrazBuilder
        extends PrendaBuilder<DisfrazBuilder, Disfraz> {

    /** Nombre temático o personaje representado por el disfraz. */
    private String nombre;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DisfrazBuilder self() {
        return this;
    }

    /**
     * Asigna el nombre temático o personaje del disfraz.
     *
     * @param nombre Nombre del personaje o temática.
     * @return Esta misma instancia de builder para fluidez.
     */
    public DisfrazBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    /**
     * Construye y retorna una nueva instancia inmutable de {@link Disfraz} con los datos suministrados.
     *
     * @return Nuevo {@link Disfraz}.
     */
    @Override
    public Disfraz build() {

        // Instanciar la prenda con todos los atributos recolectados por el builder
        return new Disfraz(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado,
                nombre
        );
    }
}
