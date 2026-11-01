package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.TrajeCaballero;

public class TrajeBuilder
        extends PrendaBuilder<TrajeBuilder, TrajeCaballero> {

    private String tipoTraje;
    private String tipoComplemento;

    @Override
    protected TrajeBuilder self() {
        return this;
    }

    public TrajeBuilder conTipoTraje(String tipoTraje) {
        this.tipoTraje = tipoTraje;
        return this;
    }

    public TrajeBuilder conTipoComplemento(String tipoComplemento) {
        this.tipoComplemento = tipoComplemento;
        return this;
    }

    @Override
    public TrajeCaballero build() {

        return new TrajeCaballero(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado,
                tipoTraje,
                tipoComplemento
        );
    }
}