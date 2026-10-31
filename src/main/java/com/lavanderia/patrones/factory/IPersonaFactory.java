package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.Map;

public interface IPersonaFactory {

    ICliente crearCliente(Map<String, String> datos);

    IEmpleado crearEmpleado(Map<String, String> datos);
}