package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IGestorAlquiler {

    IAlquiler registrarAlquiler(IAlquiler alquiler);

    Optional<IAlquiler> consultarPorConsecutivo(int consecutivo);

    List<IAlquiler> consultarPorCliente(long idCliente);

    List<IAlquiler> consultarPorFecha(LocalDate fecha);

    boolean verificarDisponibilidad(
            List<Long> idsPrendas,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    void cambiarEstado(
            long idAlquiler,
            EstadoAlquiler estado
    );
}