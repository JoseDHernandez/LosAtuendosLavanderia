package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.List;
import java.util.Optional;


public interface IEmpleadoDAO {

    void guardar(IEmpleado empleado);

    List<IEmpleado> listarTodos();

    Optional<IEmpleado> buscarPorIdentificacion(
            String identificacion
    );

    void actualizar(
            String identificacionActual,
            IEmpleado empleado
    );
}
