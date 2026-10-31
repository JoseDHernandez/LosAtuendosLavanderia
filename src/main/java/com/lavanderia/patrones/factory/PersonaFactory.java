package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.Map;

public class PersonaFactory implements IPersonaFactory {

    private final ClienteCreator clienteCreator;
    private final EmpleadoCreator empleadoCreator;

    public PersonaFactory(
            ClienteCreator clienteCreator,
            EmpleadoCreator empleadoCreator) {

        this.clienteCreator = clienteCreator;
        this.empleadoCreator = empleadoCreator;
    }

    @Override
    public ICliente crearCliente(Map<String, String> datos) {
        return clienteCreator.crearPersona(datos);
    }

    @Override
    public IEmpleado crearEmpleado(Map<String, String> datos) {
        return empleadoCreator.crearPersona(datos);
    }
}