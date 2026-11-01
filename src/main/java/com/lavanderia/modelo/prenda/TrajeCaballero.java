
package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;

public class TrajeCaballero extends Prenda implements ITrajeCaballero {

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

    public TrajeCaballero(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String tipoTraje,
            String tipoComplemento) {

        super(
                idPrenda,
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

    @Override
    public String getTipoTraje() {
        return tipoTraje;
    }

    @Override
    public String getTipoComplemento() {
        return tipoComplemento;
    }
}
