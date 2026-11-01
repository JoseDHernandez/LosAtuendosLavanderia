
package com.lavanderia.modelo.lavanderia;

import com.lavanderia.modelo.enums.EstadoLote;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.validacion.ValidacionException;

import java.time.LocalDate;
import java.util.Objects;

public class LoteLavanderia implements ILoteLavanderia {

    private final long idLote;
    private final LocalDate fechaEnvio;
    private final int cupo;
    private final EstadoLote estado;

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

    public LoteLavanderia(
            long idLote,
            LocalDate fechaEnvio,
            int cupo,
            EstadoLote estado) {

        if (cupo <= 0) {
            throw new ValidacionException(
                    "El cupo del lote debe ser mayor que cero."
            );
        }

        if (estado == null) {
            throw new ValidacionException(
                    "El estado del lote es obligatorio."
            );
        }

        this.idLote = idLote;

        this.fechaEnvio = fechaEnvio == null
                ? LocalDate.now()
                : fechaEnvio;

        this.cupo = cupo;
        this.estado = estado;
    }

    @Override
    public long getIdLote() {
        return idLote;
    }

    @Override
    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    @Override
    public int getCupo() {
        return cupo;
    }

    @Override
    public EstadoLote getEstado() {
        return estado;
    }
}
