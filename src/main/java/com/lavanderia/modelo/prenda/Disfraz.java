package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;

public class Disfraz extends Prenda {

    private final String nombre;

    public Disfraz(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String nombre) {

        super(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}