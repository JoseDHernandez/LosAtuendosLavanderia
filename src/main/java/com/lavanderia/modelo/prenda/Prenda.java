
package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.Objects;

public abstract class Prenda implements IPrenda {

    private long idPrenda;

    private final String referencia;
    private final String color;
    private final String marca;
    private final String talla;
    private final long valorAlquiler;

    private EstadoPrenda estado;

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

    protected Prenda(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado) {

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
        this.estado = Objects.requireNonNull(
                estado,
                "El estado de la prenda es obligatorio."
        );
    }

    @Override
    public long getIdPrenda() {
        return idPrenda;
    }

    @Override
    public String getReferencia() {
        return referencia;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getMarca() {
        return marca;
    }

    @Override
    public String getTalla() {
        return talla;
    }

    @Override
    public long getValorAlquiler() {
        return valorAlquiler;
    }

    @Override
    public EstadoPrenda getEstado() {
        return estado;
    }

    @Override
    public void cambiarEstado(EstadoPrenda nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado de la prenda no puede ser nulo."
            );
        }

        this.estado = nuevoEstado;
    }
}
