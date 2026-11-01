package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.prenda.Prenda;

public abstract class PrendaBuilder<
        B extends PrendaBuilder<B, P>,
        P extends Prenda>
        implements IPrendaBuilder<B, P> {

    protected String referencia;
    protected String color;
    protected String marca;
    protected String talla;
    protected long valorAlquiler;
    protected EstadoPrenda estado = EstadoPrenda.DISPONIBLE;

    protected abstract B self();

    @Override
    public B conReferencia(String referencia) {
        this.referencia = referencia;
        return self();
    }

    @Override
    public B conColor(String color) {
        this.color = color;
        return self();
    }

    @Override
    public B conMarca(String marca) {
        this.marca = marca;
        return self();
    }

    @Override
    public B conTalla(String talla) {
        this.talla = talla;
        return self();
    }

    @Override
    public B conValorAlquiler(long valorAlquiler) {
        this.valorAlquiler = valorAlquiler;
        return self();
    }

    @Override
    public B conEstado(EstadoPrenda estado) {
        this.estado = estado;
        return self();
    }

    @Override
    public abstract P build();
}