package com.lavanderia.patrones.builder;

import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.enums.EstadoPrenda;
/**
 * Interfaz de PrendaBuilder. Ejemplo: IPrendaBuilder<VestidoBuilder, VestidoDama>
 *
 * @param <B> Representa el tipo concreto del builder
 * @param <P> Representa el tipo de prenda que construye
 *
 * */
public interface IPrendaBuilder<
        //B representa el tipo concreto del builder
        B extends IPrendaBuilder<B, P>,
        //P representa el tipo de prenda que construye
        P extends IPrenda> {

    B conReferencia(String referencia);

    B conColor(String color);

    B conMarca(String marca);

    B conTalla(String talla);

    B conValorAlquiler(long valorAlquiler);

    B conEstado(EstadoPrenda estado);

    P build();
}