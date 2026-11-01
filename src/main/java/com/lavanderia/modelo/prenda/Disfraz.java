
package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IDisfraz;

public class Disfraz extends Prenda implements IDisfraz {

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

    public Disfraz(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String nombre) {

        super(
                idPrenda,
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return nombre;
    }
}
