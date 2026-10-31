package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import java.util.Map;

public interface IGestorPersonas {

    ICliente registrarCliente(Map<String, String> datos);

    IEmpleado registrarEmpleado(Map<String, String> datos);

    IPersona buscarPorIdentificacion(String identificacion);
}