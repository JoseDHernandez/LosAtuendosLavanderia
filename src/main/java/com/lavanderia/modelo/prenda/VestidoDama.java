package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;

public class VestidoDama extends Prenda {

    private final boolean tienePedreria;
    private final String largo;
    private final int numeroPiezas;

    public VestidoDama(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            boolean tienePedreria,
            String largo,
            int numeroPiezas) {

        super(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.tienePedreria = tienePedreria;
        this.largo = largo;
        this.numeroPiezas = numeroPiezas;
    }

    public boolean isTienePedreria() {
        return tienePedreria;
    }

    public String getLargo() {
        return largo;
    }

    public int getNumeroPiezas() {
        return numeroPiezas;
    }


}