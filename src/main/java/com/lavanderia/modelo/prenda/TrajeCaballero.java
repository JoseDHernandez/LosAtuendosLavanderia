package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;

public class TrajeCaballero extends Prenda {

    private final String tipoTraje;
    private final String tipoComplemento;

    public TrajeCaballero(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String tipoTraje,
            String tipoComplemento) {

        super(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.tipoTraje = tipoTraje;
        this.tipoComplemento = tipoComplemento;
    }

    public String getTipoTraje() {
        return tipoTraje;
    }

    public String getTipoComplemento() {
        return tipoComplemento;
    }
}