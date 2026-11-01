package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.Disfraz;

public class DisfrazBuilder
        extends PrendaBuilder<DisfrazBuilder, Disfraz> {

    private String nombre;

    @Override
    protected DisfrazBuilder self() {
        return this;
    }

    public DisfrazBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    @Override
    public Disfraz build() {

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