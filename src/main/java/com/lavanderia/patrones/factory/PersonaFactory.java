package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.Map;

/**
 * Fábrica concreta que implementa {@link IPersonaFactory}.
 * Utiliza instancias delegadas de {@link ClienteCreator} y {@link EmpleadoCreator}
 * para centralizar la instanciación de personas en el sistema.
 */
public class PersonaFactory implements IPersonaFactory {

    /** Creador especializado de clientes. */
    private final ClienteCreator clienteCreator;

    /** Creador especializado de empleados. */
    private final EmpleadoCreator empleadoCreator;

    /**
     * Construye la fábrica inyectando los creadores específicos de cliente y empleado.
     *
     * @param clienteCreator Creador de tipo {@link ClienteCreator}.
     * @param empleadoCreator Creador de tipo {@link EmpleadoCreator}.
     */
    public PersonaFactory(
            ClienteCreator clienteCreator,
            EmpleadoCreator empleadoCreator) {

        // Asignar referencias a los creadores de fábrica
        this.clienteCreator = clienteCreator;
        this.empleadoCreator = empleadoCreator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICliente crearCliente(Map<String, String> datos) {
        // Delegar la construcción del cliente en ClienteCreator
        return clienteCreator.crearPersona(datos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEmpleado crearEmpleado(Map<String, String> datos) {
        // Delegar la construcción del empleado en EmpleadoCreator
        return empleadoCreator.crearPersona(datos);
    }
}
