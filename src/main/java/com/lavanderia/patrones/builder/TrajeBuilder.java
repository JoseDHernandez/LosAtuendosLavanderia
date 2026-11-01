package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.TrajeCaballero;

/**
 * Constructor concreto (Builder) para instanciar objetos {@link TrajeCaballero}.
 * Extiende {@link PrendaBuilder} especializándolo con tipo de traje y complemento.
 */
public class TrajeBuilder
        extends PrendaBuilder<TrajeBuilder, TrajeCaballero> {

    /** Estilo o corte del traje (ej. Esmoquin, Clásico, Chaqué). */
    private String tipoTraje;

    /** Accesorios o complementos adicionales (ej. Corbata, Chaleco, Fajín). */
    private String tipoComplemento;

    /**
     * {@inheritDoc}
     */
    @Override
    protected TrajeBuilder self() {
        return this;
    }

    /**
     * Asigna el tipo o estilo del traje.
     *
     * @param tipoTraje Estilo del traje.
     * @return Esta misma instancia de builder para encadenamiento.
     */
    public TrajeBuilder conTipoTraje(String tipoTraje) {
        this.tipoTraje = tipoTraje;
        return this;
    }

    /**
     * Asigna los accesorios complementarios del traje.
     *
     * @param tipoComplemento Accesorios incluidos.
     * @return Esta misma instancia de builder para encadenamiento.
     */
    public TrajeBuilder conTipoComplemento(String tipoComplemento) {
        this.tipoComplemento = tipoComplemento;
        return this;
    }

    /**
     * Construye y retorna una nueva instancia de {@link TrajeCaballero}.
     *
     * @return Nuevo {@link TrajeCaballero}.
     */
    @Override
    public TrajeCaballero build() {

        // Instanciar el traje con los parámetros configurados
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
