package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.prenda.VestidoDama;

/**
 * Constructor concreto (Builder) para instanciar objetos {@link VestidoDama}.
 * Extiende {@link PrendaBuilder} especializándolo con presencia de pedrería,
 * largo de falda y número de piezas del conjunto.
 */
public class VestidoBuilder
        extends PrendaBuilder<VestidoBuilder, VestidoDama> {

    /** Indica si incluye pedrería. */
    private boolean tienePedreria;

    /** Longitud o estilo de falda (ej. Corto, Gala, Coctel). */
    private String largo;

    /** Cantidad de piezas individuales. */
    private int numeroPiezas;

    /**
     * {@inheritDoc}
     */
    @Override
    protected VestidoBuilder self() {
        return this;
    }

    /**
     * Define si el vestido tiene apliques o incrustaciones de pedrería.
     *
     * @param tienePedreria {@code true} si incluye pedrería, {@code false} de lo contrario.
     * @return Esta misma instancia de builder.
     */
    public VestidoBuilder conPedreria(boolean tienePedreria) {
        this.tienePedreria = tienePedreria;
        return this;
    }

    /**
     * Define el estilo o longitud del vestido.
     *
     * @param largo Descripción del largo del vestido.
     * @return Esta misma instancia de builder.
     */
    public VestidoBuilder conLargo(String largo) {
        this.largo = largo;
        return this;
    }

    /**
     * Define el número de piezas que conforman la prenda.
     *
     * @param numeroPiezas Cantidad de piezas.
     * @return Esta misma instancia de builder.
     */
    public VestidoBuilder conNumeroPiezas(int numeroPiezas) {
        this.numeroPiezas = numeroPiezas;
        return this;
    }

    /**
     * Construye y retorna la instancia final de {@link VestidoDama}.
     *
     * @return Nuevo {@link VestidoDama}.
     */
    @Override
    public VestidoDama build() {

        // Instanciar el vestido de dama con los atributos configurados
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
