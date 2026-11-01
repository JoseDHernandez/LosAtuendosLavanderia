package com.lavanderia.modelo.alquiler;

import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IPrenda;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad concreta que representa un elemento o línea de detalle dentro de un contrato de alquiler.
 * Implementa {@link IDetalleAlquiler}. Vincula una prenda específica con el precio acordado para la renta.
 */
public class DetalleAlquiler implements IDetalleAlquiler {

    /** Identificador primario de la línea de detalle en base de datos. */
    private final long idDetalle;

    /** Prenda objeto del alquiler. */
    private final IPrenda prenda;

    /** Monto o importe monetario acordado para el arriendo de esta prenda. */
    private final BigDecimal valorAcordado;

    /**
     * Constructor completo para inicializar un detalle de alquiler existente o con ID asignado.
     *
     * @param idDetalle Identificador único de la línea de detalle.
     * @param prenda Instancia de la prenda alquilada.
     * @param valorAcordado Importe monetario acordado.
     * @throws NullPointerException Si la prenda o el valor acordado son nulos.
     */
    public DetalleAlquiler(
            long idDetalle,
            IPrenda prenda,
            BigDecimal valorAcordado) {

        this.idDetalle = idDetalle;
        // Validar que la prenda asociada no sea nula
        this.prenda = Objects.requireNonNull(prenda, "La prenda del detalle es obligatoria.");
        // Validar que el valor acordado no sea nulo
        this.valorAcordado = Objects.requireNonNull(valorAcordado, "El valor acordado es obligatorio.");
    }

    /**
     * Constructor de conveniencia para registrar un nuevo detalle antes de su persistencia.
     * Inicializa el ID en 0.
     *
     * @param prenda Prenda a incluir en el contrato de alquiler.
     * @param valorAcordado Monto monetario pactado.
     */
    public DetalleAlquiler(
            IPrenda prenda,
            BigDecimal valorAcordado) {

        this(0, prenda, valorAcordado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdDetalle() {
        return idDetalle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPrenda getPrenda() {
        return prenda;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getValorAcordado() {
        return valorAcordado;
    }
}
