package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.lavanderia.Devolucion;

import java.util.List;
import java.util.Optional;

public interface IDevolucionDAO {

    void guardar(Devolucion devolucion);

    boolean existePorIdDetalle(long idDetalle);

    Optional<Devolucion> buscarPorId(long idDevolucion);

    Optional<Devolucion> buscarPorIdDetalle(long idDetalle);

    List<Devolucion> listarDevoluciones();

    void actualizar(Devolucion devolucion);
}