package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IPersonaDAO;
import com.lavanderia.dao.interfaces.IClienteDAO;
import com.lavanderia.dao.interfaces.IEmpleadoDAO;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import com.lavanderia.patrones.factory.IPersonaFactory;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.ValidacionException;

import java.util.Map;

public class GestorPersonas implements IGestorPersonas {

    private final IPersonaFactory personaFactory;

    private final IPersonaDAO personaDAO;
    private final IClienteDAO clienteDAO;
    private final IEmpleadoDAO empleadoDAO;

    public GestorPersonas(
            IPersonaFactory personaFactory,
            IPersonaDAO personaDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO) {

        this.personaFactory = personaFactory;
        this.personaDAO = personaDAO;
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
    }

    @Override
    public ICliente registrarCliente(Map<String, String> datos) {

        ICliente cliente = personaFactory.crearCliente(datos);

        verificarIdentificacionDisponible(
                cliente.getIdentificacion()
        );

        clienteDAO.guardar(cliente);

        return cliente;
    }

    @Override
    public IEmpleado registrarEmpleado(Map<String, String> datos) {

        IEmpleado empleado = personaFactory.crearEmpleado(datos);

        verificarIdentificacionDisponible(
                empleado.getIdentificacion()
        );

        empleadoDAO.guardar(empleado);

        return empleado;
    }

    @Override
    public IPersona buscarPorIdentificacion(String identificacion) {

        return personaDAO.buscarPorIdentificacion(identificacion)
                .orElseThrow(() -> new ValidacionException(
                        "No existe una persona registrada con la identificación: "
                                + identificacion
                ));
    }

    private void verificarIdentificacionDisponible(String identificacion) {

        if (personaDAO.existeIdentificacion(identificacion)) {
            throw new ValidacionException(
                    "Ya existe una persona registrada con la identificación: "
                            + identificacion
            );
        }
    }
}