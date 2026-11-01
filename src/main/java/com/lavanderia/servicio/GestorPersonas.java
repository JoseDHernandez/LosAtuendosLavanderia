
package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IClienteDAO;
import com.lavanderia.dao.interfaces.IEmpleadoDAO;
import com.lavanderia.dao.interfaces.IPersonaDAO;
import com.lavanderia.modelo.interfaces.IPersona;
import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.patrones.factory.IPersonaFactory;
import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.servicio.interfaces.IGestorPersonas;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GestorPersonas implements IGestorPersonas {

    private final IPersonaFactory personaFactory;

    private final IPersonaDAO personaDAO;
    private final IClienteDAO clienteDAO;
    private final IEmpleadoDAO empleadoDAO;

    private final IPersonaValidator clienteValidator;
    private final IPersonaValidator empleadoValidator;

    public GestorPersonas(
            IPersonaFactory personaFactory,
            IPersonaDAO personaDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO,
            IPersonaValidator clienteValidator,
            IPersonaValidator empleadoValidator) {

        this.personaFactory = Objects.requireNonNull(personaFactory);
        this.personaDAO = Objects.requireNonNull(personaDAO);
        this.clienteDAO = Objects.requireNonNull(clienteDAO);
        this.empleadoDAO = Objects.requireNonNull(empleadoDAO);

        this.clienteValidator = Objects.requireNonNull(clienteValidator);
        this.empleadoValidator = Objects.requireNonNull(empleadoValidator);
    }

    // ==========================================
    // REGISTRO
    // ==========================================

    @Override
    public ICliente registrarCliente(Map<String, String> datos) {

        Map<String, String> datosValidados =
                validarDatos(clienteValidator, datos);

        ICliente cliente = personaFactory.crearCliente(datosValidados);

        verificarIdentificacionDisponible(
                cliente.getIdentificacion()
        );

        clienteDAO.guardar(cliente);

        return cliente;
    }

    @Override
    public IEmpleado registrarEmpleado(Map<String, String> datos) {

        Map<String, String> datosValidados =
                validarDatos(empleadoValidator, datos);

        IEmpleado empleado = personaFactory.crearEmpleado(datosValidados);

        verificarIdentificacionDisponible(
                empleado.getIdentificacion()
        );

        empleadoDAO.guardar(empleado);

        return empleado;
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    @Override
    public IPersona buscarPorIdentificacion(String identificacion) {

        String identificacionNormalizada =
                normalizarIdentificacion(identificacion);

        return personaDAO.buscarPorIdentificacion(
                identificacionNormalizada
        ).orElseThrow(() -> new ValidacionException(
                "No existe una persona registrada con la identificación: "
                        + identificacionNormalizada
        ));
    }

    @Override
    public List<ICliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    @Override
    public List<IEmpleado> listarEmpleados() {
        return empleadoDAO.listarTodos();
    }

    // ==========================================
    // ACTUALIZACIÓN
    // ==========================================

    @Override
    public ICliente actualizarCliente(
            String identificacionActual,
            Map<String, String> nuevosDatos) {

        String identificacionOriginal =
                normalizarIdentificacion(identificacionActual);

        // Comprobar que el cliente original exista.
        clienteDAO.buscarPorIdentificacion(identificacionOriginal)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un cliente con la identificación: "
                                + identificacionOriginal
                ));

        // Validar los datos nuevos.
        Map<String, String> datosValidados =
                validarDatos(clienteValidator, nuevosDatos);

        ICliente clienteActualizado =
                personaFactory.crearCliente(datosValidados);

        // Permitir conservar la identificación original,
        // pero impedir utilizar la identificación de otra persona.
        verificarIdentificacionDisponible(
                clienteActualizado.getIdentificacion(),
                identificacionOriginal
        );

        clienteDAO.actualizar(
                identificacionOriginal,
                clienteActualizado
        );

        return clienteActualizado;
    }

    @Override
    public IEmpleado actualizarEmpleado(
            String identificacionActual,
            Map<String, String> nuevosDatos) {

        String identificacionOriginal =
                normalizarIdentificacion(identificacionActual);

        // Comprobar que el empleado original exista.
        empleadoDAO.buscarPorIdentificacion(identificacionOriginal)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un empleado con la identificación: "
                                + identificacionOriginal
                ));

        // Validar los datos nuevos.
        Map<String, String> datosValidados =
                validarDatos(empleadoValidator, nuevosDatos);

        IEmpleado empleadoActualizado =
                personaFactory.crearEmpleado(datosValidados);

        verificarIdentificacionDisponible(
                empleadoActualizado.getIdentificacion(),
                identificacionOriginal
        );

        empleadoDAO.actualizar(
                identificacionOriginal,
                empleadoActualizado
        );

        return empleadoActualizado;
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private Map<String, String> validarDatos(
            IPersonaValidator validador,
            Map<String, String> datos) {

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validar(datos);

        return resultado.obtenerValor();
    }

    private void verificarIdentificacionDisponible(
            String identificacion) {

        if (personaDAO.existeIdentificacion(identificacion)) {
            throw new ValidacionException(
                    "Ya existe una persona registrada con la identificación: "
                            + identificacion
            );
        }
    }

    private void verificarIdentificacionDisponible(
            String identificacion,
            String identificacionExcluida) {

        if (personaDAO.existeIdentificacion(
                identificacion,
                identificacionExcluida)) {

            throw new ValidacionException(
                    "Ya existe otra persona registrada con la identificación: "
                            + identificacion
            );
        }
    }

    private String normalizarIdentificacion(String identificacion) {

        if (identificacion == null || identificacion.isBlank()) {
            throw new ValidacionException(
                    "La identificación es obligatoria."
            );
        }

        return identificacion.trim();
    }
}
