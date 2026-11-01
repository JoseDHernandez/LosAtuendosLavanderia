package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import java.util.List;
import java.util.Map;

public interface IGestorPersonas {

    // Registro
    ICliente registrarCliente(Map<String, String> datos);

    IEmpleado registrarEmpleado(Map<String, String> datos);

    // Consultas
    IPersona buscarPorIdentificacion(String identificacion);

    List<ICliente> listarClientes();

    List<IEmpleado> listarEmpleados();

    // Actualización
    ICliente actualizarCliente(
            String identificacion,
            Map<String, String> datos
    );

    IEmpleado actualizarEmpleado(
            String identificacion,
            Map<String, String> datos
    );
}