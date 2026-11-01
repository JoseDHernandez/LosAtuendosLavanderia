package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;

import java.util.List;
import java.util.Optional;

public interface IClienteDAO extends IPersonaDAO {

    void guardar(ICliente cliente);

    Optional<ICliente> buscarClientePorIdentificacion(
            String identificacion
    );

    List<ICliente> listarClientes();
}