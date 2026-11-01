package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoDAO extends IPersonaDAO {

    void guardar(IEmpleado empleado);

    Optional<IEmpleado> buscarEmpleadoPorIdentificacion(
            String identificacion
    );

    List<IEmpleado> listarEmpleados();
}