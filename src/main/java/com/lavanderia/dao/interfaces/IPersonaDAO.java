package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import java.util.Optional;

public interface IPersonaDAO {

    boolean existeIdentificacion(String identificacion);

    void guardarCliente(ICliente cliente);

    void guardarEmpleado(IEmpleado empleado);

    Optional<IPersona> buscarPorIdentificacion(String identificacion);
}