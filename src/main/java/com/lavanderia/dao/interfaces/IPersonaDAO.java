package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import java.util.List;
import java.util.Optional;

public interface IPersonaDAO {

    boolean existeIdentificacion(String identificacion);

    List<IPersona> listarPersonas();

    Optional<IPersona> buscarPorIdentificacion(String identificacion);
}