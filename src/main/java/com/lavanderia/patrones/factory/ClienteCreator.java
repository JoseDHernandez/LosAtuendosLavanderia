package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.persona.Cliente;
import com.lavanderia.validacion.persona.IPersonaValidator;

import java.util.Map;

public class ClienteCreator extends PersonaCreator {

    private final IPersonaValidator validator;

    public ClienteCreator(IPersonaValidator validator) {
        this.validator = validator;
    }

    @Override
    public ICliente crearPersona(Map<String, String> datos) {

        Map<String, String> d = validator.validar(datos);

        return new Cliente(
                d.get("identificacion"),
                d.get("nombre"),
                d.get("direccion"),
                d.get("telefono"),
                d.get("correo")
        );
    }
}