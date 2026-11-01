package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;

public abstract class Prenda implements IPrenda {

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

        this.referencia = referencia;
        this.color = color;
        this.marca = marca;
        this.talla = talla;
        this.valorAlquiler = valorAlquiler;
        this.estado = estado;
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
    public void cambiarEstado(EstadoPrenda nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado de la prenda no puede ser nulo."
            );
        }

        this.estado = nuevoEstado;
    }
    @Override
    public EstadoPrenda getEstado() {
        return estado;
    }
}