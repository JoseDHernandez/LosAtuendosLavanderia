package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.persona.Cliente;
import com.lavanderia.validacion.persona.IPersonaValidator;

import java.util.Map;

/**
 * Creador concreto (ConcreteCreator) para clientes dentro del patrón Factory Method.
 * Extiende {@link PersonaCreator} implementando la creación de objetos {@link Cliente}.
 */
public class ClienteCreator extends PersonaCreator {

    /** Validador de reglas de negocio para los datos de la persona. */
    private final IPersonaValidator validator;

    /**
     * Construye una instancia de ClienteCreator inyectando el validador correspondiente.
     *
     * @param validator Validador de persona ({@link IPersonaValidator}).
     */
    public ClienteCreator(IPersonaValidator validator) {
        this.validator = validator;
    }

    /**
     * Valida los datos recibidos y construye una nueva instancia de {@link Cliente}.
     *
     * @param datos Mapa con los atributos textuales del cliente.
     * @return Nueva instancia de {@link ICliente}.
     */
    @Override
    public ICliente crearPersona(Map<String, String> datos) {

        Map<String, String> d = validator.validar(datos).obtenerValor();

        // Construir la instancia de Cliente con los datos extraídos
        return new Cliente(
                d.get("identificacion"),
                d.get("nombre"),
                d.get("direccion"),
                d.get("telefono"),
                d.get("correo")
        );
    }
}
