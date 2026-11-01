package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.Objects;

/**
 * Clase abstracta que define las propiedades y comportamientos comunes a todas las prendas.
 * Implementa el contrato {@link IPrenda}. Gestiona referencia, color, marca, talla,
 * valor del servicio de alquiler y estado actual de disponibilidad.
 */
public abstract class Prenda implements IPrenda {

    /** Identificador primario de la prenda en base de datos. */
    private long idPrenda;

    /** Referencia o código alfanumérico único en el catálogo. */
    private final String referencia;

    /** Color predominante de la prenda. */
    private final String color;

    /** Fabricante, diseñador o marca de la prenda. */
    private final String marca;

    /** Talla de confección (ej. S, M, L, etc.). */
    private final String talla;

    /** Tarifa estándar de alquiler por servicio. */
    private final long valorAlquiler;

    /** Estado físico y de disponibilidad de la prenda. */
    private EstadoPrenda estado;

    /**
     * Constructor para registrar una nueva prenda en el catálogo sin ID asignado.
     *
     * @param referencia Código alfanumérico único.
     * @param color Color de la tela.
     * @param marca Marca de confección.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Precio por alquiler.
     * @param estado Estado de disponibilidad inicial.
     */
    protected Prenda(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado) {

        this(
                0,
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );
    }

    /**
     * Constructor completo para reconstruir una prenda recuperada de la persistencia.
     *
     * @param idPrenda Identificador único en base de datos.
     * @param referencia Código de referencia único.
     * @param color Color principal.
     * @param marca Marca o fabricante.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Valor pactado de alquiler.
     * @param estado Estado actual de la prenda.
     * @throws IllegalArgumentException Si el identificador es negativo o el estado es nulo.
     */
    protected Prenda(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado) {

        // Validación de integridad del identificador
        if (idPrenda < 0) {
            throw new IllegalArgumentException(
                    "El identificador de la prenda no puede ser negativo."
            );
        }

        this.idPrenda = idPrenda;
        this.referencia = referencia;
        this.color = color;
        this.marca = marca;
        this.talla = talla;
        this.valorAlquiler = valorAlquiler;

        // Validar que el estado inicial no sea nulo
        this.estado = Objects.requireNonNull(
                estado,
                "El estado de la prenda es obligatorio."
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdPrenda() {
        return idPrenda;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencia() {
        return referencia;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMarca() {
        return marca;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTalla() {
        return talla;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getValorAlquiler() {
        return valorAlquiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstadoPrenda getEstado() {
        return estado;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException Si el nuevo estado es nulo.
     */
    @Override
    public void cambiarEstado(EstadoPrenda nuevoEstado) {

        // Validar que el nuevo estado sea válido
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado de la prenda no puede ser nulo."
            );
        }

        // Actualizar el estado
        this.estado = nuevoEstado;
    }
}
