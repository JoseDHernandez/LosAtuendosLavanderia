package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;
import com.lavanderia.modelo.persona.Empleado;
import com.lavanderia.validacion.persona.IPersonaValidator;

import java.util.Map;

// Importar Validador desde su paquete correspondiente.

public class EmpleadoCreator extends PersonaCreator {

    private final IPersonaValidator validator;

    public EmpleadoCreator(IPersonaValidator validator) {
        this.validator = validator;
    }

    @Override
    public IEmpleado crearPersona(Map<String, String> datos) {

        Map<String, String> d = validator.validar(datos);

        return new Empleado(
                d.get("identificacion"),
                d.get("nombre"),
                d.get("direccion"),
                d.get("telefono"),
                d.get("correo"),
                d.get("cargo")
        );

    }

}