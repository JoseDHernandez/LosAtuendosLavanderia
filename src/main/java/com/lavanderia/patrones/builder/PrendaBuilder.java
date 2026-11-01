package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.prenda.Prenda;

/**
 * Implementación abstracta base del patrón Builder para entidades derivadas de {@link Prenda}.
 * Centraliza la asignación de atributos comunes (referencia, color, marca, talla, valor y estado)
 * y delega en el método {@link #self()} el retorno del tipo especializado del builder.
 *
 * @param <B> Subtipo concreto de {@link PrendaBuilder}.
 * @param <P> Subtipo concreto de {@link Prenda}.
 */
public abstract class PrendaBuilder<
        B extends PrendaBuilder<B, P>,
        P extends Prenda>
        implements IPrendaBuilder<B, P> {

    /** Referencia alfanumérica única. */
    protected String referencia;

    /** Color de la prenda. */
    protected String color;

    /** Marca o diseñador. */
    protected String marca;

    /** Talla comercial de la prenda. */
    protected String talla;

    /** Tarifa estándar de alquiler. */
    protected long valorAlquiler;

    /** Estado inicial, predeterminado en {@link EstadoPrenda#DISPONIBLE}. */
    protected EstadoPrenda estado = EstadoPrenda.DISPONIBLE;

    /**
     * Retorna la referencia a la instancia concreta actual ('this' tipado como B).
     *
     * @return Instancia concreta del builder.
     */
    protected abstract B self();

    /**
     * {@inheritDoc}
     */
    @Override
    public B conReferencia(String referencia) {
        this.referencia = referencia;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B conColor(String color) {
        this.color = color;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B conMarca(String marca) {
        this.marca = marca;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B conTalla(String talla) {
        this.talla = talla;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B conValorAlquiler(long valorAlquiler) {
        this.valorAlquiler = valorAlquiler;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B conEstado(EstadoPrenda estado) {
        this.estado = estado;
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract P build();
}
