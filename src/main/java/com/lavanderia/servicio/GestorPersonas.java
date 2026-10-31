package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IPersonaDAO;
import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;
import com.lavanderia.patrones.factory.IPersonaFactory;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.comun.ValidadorComun;

import java.util.Map;

public class GestorPersonas implements IGestorPersonas {

    private final IPersonaFactory personaFactory;
    private final IPersonaDAO personaDAO;

    public GestorPersonas(
            IPersonaFactory personaFactory,
            IPersonaDAO personaDAO) {

        this.personaFactory = personaFactory;
        this.personaDAO = personaDAO;
    }

    @Override
    public ICliente registrarCliente(Map<String, String> datos) {

        ICliente cliente = personaFactory.crearCliente(datos);

        verificarIdentificacionDisponible(
                cliente.getIdentificacion()
        );

        personaDAO.guardarCliente(cliente);

        return cliente;
    }

    @Override
    public IEmpleado registrarEmpleado(Map<String, String> datos) {

        IEmpleado empleado = personaFactory.crearEmpleado(datos);

        verificarIdentificacionDisponible(
                empleado.getIdentificacion()
        );

        personaDAO.guardarEmpleado(empleado);

        return empleado;
    }

    @Override
    public IPersona buscarPorIdentificacion(String identificacion) {

        String id = ValidadorComun.identificacion(identificacion);

        return personaDAO.buscarPorIdentificacion(id)
                .orElseThrow(() -> new ValidacionException(
                        "No existe una persona registrada con la identificación: "
                                + id
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