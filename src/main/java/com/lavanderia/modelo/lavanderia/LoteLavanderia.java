package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoLote;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;

/**
 * Entidad concreta que representa un Lote de Lavandería (tanda de prendas agrupadas).
 * Implementa {@link ILoteLavanderia}. Controla la fecha de despacho programada,
 * el cupo máximo de prendas admitidas y el estado del lote en su ciclo de vida.
 */
public class LoteLavanderia implements ILoteLavanderia {

    /** Identificador primario del lote en base de datos. */
    private final long idLote;

    /** Fecha de envío o despacho del lote a la lavandería. */
    private final LocalDate fechaEnvio;

    /** Capacidad o cupo máximo de prendas permitido en este lote. */
    private final int cupo;

    /** Estado actual del lote (ABIERTO, ENVIADO, FINALIZADO, CANCELADO). */
    private final EstadoLote estado;

    /**
     * Constructor para registrar un nuevo lote de lavandería sin ID previo.
     *
     * @param fechaEnvio Fecha prevista de despacho.
     * @param cupo Capacidad máxima de prendas.
     * @param estado Estado inicial del lote.
     */
    public LoteLavanderia(
            LocalDate fechaEnvio,
            int cupo,
            EstadoLote estado) {

        this(
                0,
                fechaEnvio,
                cupo,
                estado
        );
    }

    /**
     * Constructor completo para reconstruir un lote existente desde la base de datos.
     *
     * @param idLote Identificador numérico del lote.
     * @param fechaEnvio Fecha de despacho (si es nula se toma la fecha actual).
     * @param cupo Capacidad máxima (debe ser mayor a 0).
     * @param estado Estado del lote (obligatorio).
     * @throws ValidacionException Si el cupo es menor o igual a cero, o si el estado es nulo.
     */
    public LoteLavanderia(
            long idLote,
            LocalDate fechaEnvio,
            int cupo,
            EstadoLote estado) {

        // Validar que el cupo sea positivo
        if (cupo <= 0) {
            throw new ValidacionException(
                    "El cupo del lote debe ser mayor que cero."
            );
        }

        // Validar que el estado del lote sea suministrado
        if (estado == null) {
            throw new ValidacionException(
                    "El estado del lote es obligatorio."
            );
        }

        this.idLote = idLote;

        // Por defecto asignar fecha de hoy si no se especifica
        this.fechaEnvio = fechaEnvio == null
                ? LocalDate.now()
                : fechaEnvio;

        this.cupo = cupo;
        this.estado = estado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdLote() {
        return idLote;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCupo() {
        return cupo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstadoLote getEstado() {
        return estado;
    }
}
