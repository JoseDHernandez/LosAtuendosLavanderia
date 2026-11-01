package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;

/**
 * Entidad concreta que representa un Vestido de Dama en el inventario.
 * Extiende {@link Prenda} e implementa {@link IVestidoDama}, con atributos
 * específicos sobre presencia de pedrería, largo de falda y número de piezas.
 */
public class VestidoDama extends Prenda implements IVestidoDama {

    /** Indica si el vestido incluye adornos o incrustaciones de pedrería. */
    private final boolean tienePedreria;

    /** Longitud o estilo de falda (ej. Corto, Coctel, Largo, Cola). */
    private final String largo;

    /** Cantidad de componentes individuales que conforman el conjunto (ej. 1, 2, 3 piezas). */
    private final int numeroPiezas;

    /**
     * Constructor para registrar un nuevo vestido de dama sin ID persistido.
     *
     * @param referencia Código de referencia único.
     * @param color Color del vestido.
     * @param marca Fabricante o diseñador.
     * @param talla Talla de confección.
     * @param valorAlquiler Tarifa por alquiler.
     * @param estado Estado de disponibilidad inicial.
     * @param tienePedreria {@code true} si tiene pedrería, {@code false} de lo contrario.
     * @param largo Descripción del largo o caída.
     * @param numeroPiezas Cantidad de piezas del vestido.
     */
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

    /**
     * Constructor completo para reconstruir un vestido de dama desde la base de datos.
     *
     * @param idPrenda Identificador único en el repositorio.
     * @param referencia Código de referencia único.
     * @param color Color del vestido.
     * @param marca Fabricante o diseñador.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Tarifa por alquiler.
     * @param estado Estado actual del vestido.
     * @param tienePedreria {@code true} si tiene pedrería, {@code false} de lo contrario.
     * @param largo Descripción del largo.
     * @param numeroPiezas Número de piezas que lo componen.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTienePedreria() {
        return tienePedreria;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLargo() {
        return largo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumeroPiezas() {
        return numeroPiezas;
    }
}
