package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.VestidoDama;

public class VestidoBuilder
        extends PrendaBuilder<VestidoBuilder, VestidoDama> {

    private boolean tienePedreria;
    private String largo;
    private int numeroPiezas;

    @Override
    protected VestidoBuilder self() {
        return this;
    }

    public VestidoBuilder conPedreria(boolean tienePedreria) {
        this.tienePedreria = tienePedreria;
        return this;
    }

    public VestidoBuilder conLargo(String largo) {
        this.largo = largo;
        return this;
    }

    public VestidoBuilder conNumeroPiezas(int numeroPiezas) {
        this.numeroPiezas = numeroPiezas;
        return this;
    }

    @Override
    public VestidoDama build() {

        return new VestidoDama(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado,
                tienePedreria,
                largo,
                numeroPiezas
        );
    }
}