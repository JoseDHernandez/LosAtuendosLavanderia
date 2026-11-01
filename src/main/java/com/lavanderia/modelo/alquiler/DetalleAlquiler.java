
package com.lavanderia.modelo.alquiler;

import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.prenda.Prenda;

import java.math.BigDecimal;
import java.util.Objects;

public class DetalleAlquiler implements IDetalleAlquiler {

    private final long idDetalle;
    private final Prenda prenda;
    private final BigDecimal valorAcordado;

    public DetalleAlquiler(
            long idDetalle,
            Prenda prenda,
            BigDecimal valorAcordado) {

        this.idDetalle = idDetalle;
        this.prenda = Objects.requireNonNull(prenda);
        this.valorAcordado = Objects.requireNonNull(valorAcordado);
    }

    public DetalleAlquiler(
            Prenda prenda,
            BigDecimal valorAcordado) {

        this(0, prenda, valorAcordado);
    }

    @Override
    public long getIdDetalle() {
        return idDetalle;
    }

    @Override
    public Prenda getPrenda() {
        return prenda;
    }

    @Override
    public BigDecimal getValorAcordado() {
        return valorAcordado;
    }
}
