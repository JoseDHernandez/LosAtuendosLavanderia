
package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;

public class VestidoDama extends Prenda implements IVestidoDama {

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

    public VestidoDama(
            long idPrenda,
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
                idPrenda,
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

    @Override
    public boolean isTienePedreria() {
        return tienePedreria;
    }

    @Override
    public String getLargo() {
        return largo;
    }

    @Override
    public int getNumeroPiezas() {
        return numeroPiezas;
    }
}
