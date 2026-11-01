package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IGestorDevoluciones {

    IDevolucion registrarDevolucion(IDevolucion devolucion);

    Optional<IDevolucion> consultarPorId(long idDevolucion);

    Optional<IDevolucion> consultarPorDetalle(long idDetalle);

    List<IDevolucion> listarTodas();

    IDevolucion actualizarDevolucion(
            long idDevolucion,
            IDevolucion devolucion
    );

}