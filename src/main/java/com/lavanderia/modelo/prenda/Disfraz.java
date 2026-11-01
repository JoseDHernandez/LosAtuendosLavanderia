package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IDisfraz;

/**
 * Entidad concreta que representa un Disfraz en el catálogo de prendas.
 * Extiende {@link Prenda} e implementa {@link IDisfraz}, añadiendo el nombre o temática del disfraz.
 */
public class Disfraz extends Prenda implements IDisfraz {

    /** Nombre temático o personaje representado por el disfraz. */
    private final String nombre;

    /**
     * Constructor para registrar un nuevo disfraz sin ID asignado.
     *
     * @param referencia Código de referencia único.
     * @param color Color del disfraz.
     * @param marca Fabricante o diseñador.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Tarifa base de alquiler.
     * @param estado Estado de disponibilidad inicial.
     * @param nombre Nombre o temática del disfraz.
     */
    public Disfraz(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String nombre) {

        super(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.nombre = nombre;
    }

    /**
     * Constructor completo para reconstruir un disfraz persistido.
     *
     * @param idPrenda Identificador único en base de datos.
     * @param referencia Código de referencia único.
     * @param color Color del disfraz.
     * @param marca Fabricante o marca.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Tarifa de alquiler.
     * @param estado Estado actual de la prenda.
     * @param nombre Nombre temático del disfraz.
     */
    public Disfraz(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String nombre) {

        super(
                idPrenda,
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.nombre = nombre;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return nombre;
    }
}
