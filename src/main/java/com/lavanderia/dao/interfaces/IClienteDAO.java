package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;

import java.util.List;
import java.util.Optional;


public interface IClienteDAO {

    void guardar(ICliente cliente);

    List<ICliente> listarTodos();

    Optional<ICliente> buscarPorIdentificacion(
            String identificacion
    );

    void actualizar(
            String identificacionActual,
            ICliente cliente
    );
}
