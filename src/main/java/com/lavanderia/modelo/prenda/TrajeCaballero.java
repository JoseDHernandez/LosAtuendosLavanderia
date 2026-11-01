package com.lavanderia.modelo.prenda;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;

/**
 * Entidad concreta que representa un Traje de Caballero en el inventario.
 * Extiende {@link Prenda} e implementa {@link ITrajeCaballero}, incorporando
 * el corte o tipo de traje y los complementos que lo acompañan.
 */
public class TrajeCaballero extends Prenda implements ITrajeCaballero {

    /** Estilo o tipo de confección del traje (ej. Esmoquin, Clásico, Chaqué). */
    private final String tipoTraje;

    /** Accesorios o complementos suministrados con el traje (ej. Corbatín, Chaleco, Faja). */
    private final String tipoComplemento;

    /**
     * Constructor para registrar un nuevo traje de caballero sin ID persistido.
     *
     * @param referencia Código de referencia único.
     * @param color Color del traje.
     * @param marca Fabricante o sastre.
     * @param talla Talla del traje.
     * @param valorAlquiler Precio del servicio de alquiler.
     * @param estado Estado de disponibilidad.
     * @param tipoTraje Estilo o corte del traje.
     * @param tipoComplemento Accesorios incluidos.
     */
    public TrajeCaballero(
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String tipoTraje,
            String tipoComplemento) {

        super(
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.tipoTraje = tipoTraje;
        this.tipoComplemento = tipoComplemento;
    }

    /**
     * Constructor completo para reconstruir un traje desde la persistencia.
     *
     * @param idPrenda Identificador único en la base de datos.
     * @param referencia Código de referencia único.
     * @param color Color de la tela.
     * @param marca Marca o confección.
     * @param talla Talla de la prenda.
     * @param valorAlquiler Valor pactado de alquiler.
     * @param estado Estado actual del traje.
     * @param tipoTraje Estilo o tipo de traje.
     * @param tipoComplemento Accesorios incluidos.
     */
    public TrajeCaballero(
            long idPrenda,
            String referencia,
            String color,
            String marca,
            String talla,
            long valorAlquiler,
            EstadoPrenda estado,
            String tipoTraje,
            String tipoComplemento) {

        super(
                idPrenda,
                referencia,
                color,
                marca,
                talla,
                valorAlquiler,
                estado
        );

        this.tipoTraje = tipoTraje;
        this.tipoComplemento = tipoComplemento;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTipoTraje() {
        return tipoTraje;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTipoComplemento() {
        return tipoComplemento;
    }
}
