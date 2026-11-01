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

/**
 * Servicio de lógica de negocio para la gestión integral de Personas (Clientes y Empleados).
 * Implementa {@link IGestorPersonas}. Se encarga de la validación previa de datos,
 * control de unicidad de documentos mediante {@link IPersonaDAO}, creación delegada
 * en {@link IPersonaFactory} y persistencia mediante {@link IClienteDAO} y {@link IEmpleadoDAO}.
 */
public class GestorPersonas implements IGestorPersonas {

    /** Fábrica abstracta para la creación de instancias de clientes y empleados. */
    private final IPersonaFactory personaFactory;

    /** DAO transversal para consultas y verificación de unicidad de identificación. */
    private final IPersonaDAO personaDAO;

    /** DAO para operaciones específicas sobre clientes. */
    private final IClienteDAO clienteDAO;

    /** DAO para operaciones específicas sobre empleados. */
    private final IEmpleadoDAO empleadoDAO;

    /** Validador de formato y campos de clientes. */
    private final IPersonaValidator clienteValidator;

    /** Validador de formato y campos de empleados. */
    private final IPersonaValidator empleadoValidator;

    /**
     * Construye el gestor inyectando sus dependencias obligatorias.
     *
     * @param personaFactory Fábrica de personas.
     * @param personaDAO DAO transversal de personas.
     * @param clienteDAO DAO de clientes.
     * @param empleadoDAO DAO de empleados.
     * @param clienteValidator Validador para clientes.
     * @param empleadoValidator Validador para empleados.
     * @throws NullPointerException Si alguna de las dependencias es nula.
     */
    public GestorPersonas(
            IPersonaFactory personaFactory,
            IPersonaDAO personaDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO,
            IPersonaValidator clienteValidator,
            IPersonaValidator empleadoValidator) {

        // Validar inyección obligatoria de dependencias
        this.personaFactory = Objects.requireNonNull(personaFactory, "La fábrica de personas es obligatoria.");
        this.personaDAO = Objects.requireNonNull(personaDAO, "El DAO de personas es obligatorio.");
        this.clienteDAO = Objects.requireNonNull(clienteDAO, "El DAO de clientes es obligatorio.");
        this.empleadoDAO = Objects.requireNonNull(empleadoDAO, "El DAO de empleados es obligatorio.");

        this.clienteValidator = Objects.requireNonNull(clienteValidator, "El validador de clientes es obligatorio.");
        this.empleadoValidator = Objects.requireNonNull(empleadoValidator, "El validador de empleados es obligatorio.");
    }

    // ==========================================
    // REGISTRO
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos no superan la validación o si la identificación ya existe.
     */
    @Override
    public ICliente registrarCliente(Map<String, String> datos) {

        // 1. Validar reglas de formato en los datos suministrados
        Map<String, String> datosValidados =
                validarDatos(clienteValidator, datos);

        // 2. Construir la entidad mediante la fábrica
        ICliente cliente = personaFactory.crearCliente(datosValidados);

        // 3. Verificar que el documento de identificación no esté ya en uso
        verificarIdentificacionDisponible(
                cliente.getIdentificacion()
        );

        // 4. Persistir el nuevo cliente en la base de datos
        clienteDAO.guardar(cliente);

        return cliente;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos fallan la validación o la identificación ya existe.
     */
    @Override
    public IEmpleado registrarEmpleado(Map<String, String> datos) {

        // 1. Validar reglas de negocio y campos del empleado (incluye cargo)
        Map<String, String> datosValidados =
                validarDatos(empleadoValidator, datos);

        // 2. Construir la entidad de empleado a través de la fábrica
        IEmpleado empleado = personaFactory.crearEmpleado(datosValidados);

        // 3. Asegurar que la identificación esté libre en el sistema
        verificarIdentificacionDisponible(
                empleado.getIdentificacion()
        );

        // 4. Guardar en la persistencia
        empleadoDAO.guardar(empleado);

        return empleado;
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la identificación es inválida o no se encuentra la persona.
     */
    @Override
    public IPersona buscarPorIdentificacion(String identificacion) {

        // Normalizar y limpiar espacios en blanco de la identificación
        String identificacionNormalizada =
                normalizarIdentificacion(identificacion);

        // Buscar en el repositorio y arrojar excepción si no existe
        return personaDAO.buscarPorIdentificacion(
                identificacionNormalizada
        ).orElseThrow(() -> new ValidacionException(
                "No existe una persona registrada con la identificación: "
                        + identificacionNormalizada
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ICliente> listarClientes() {
        // Consultar y retornar la lista completa de clientes desde el DAO
        return clienteDAO.listarTodos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEmpleado> listarEmpleados() {
        // Consultar y retornar la lista completa de empleados desde el DAO
        return empleadoDAO.listarTodos();
    }

    // ==========================================
    // ACTUALIZACIÓN
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el cliente no existe, los datos son inválidos o la nueva cédula está en uso.
     */
    @Override
    public ICliente actualizarCliente(
            String identificacionActual,
            Map<String, String> nuevosDatos) {

        // 1. Normalizar documento original
        String identificacionOriginal =
                normalizarIdentificacion(identificacionActual);

        // 2. Comprobar existencia previa del cliente a editar
        clienteDAO.buscarPorIdentificacion(identificacionOriginal)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un cliente con la identificación: "
                                + identificacionOriginal
                ));

        // 3. Validar los nuevos datos proporcionados
        Map<String, String> datosValidados =
                validarDatos(clienteValidator, nuevosDatos);

        // 4. Instanciar el cliente actualizado con la fábrica
        ICliente clienteActualizado =
                personaFactory.crearCliente(datosValidados);

        // 5. Verificar disponibilidad de la identificación (excluyendo la original)
        verificarIdentificacionDisponible(
                clienteActualizado.getIdentificacion(),
                identificacionOriginal
        );

        // 6. Ejecutar la actualización en el repositorio
        clienteDAO.actualizar(
                identificacionOriginal,
                clienteActualizado
        );

        return clienteActualizado;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el empleado no existe, datos son inválidos o la cédula está ocupada.
     */
    @Override
    public IEmpleado actualizarEmpleado(
            String identificacionActual,
            Map<String, String> nuevosDatos) {

        // 1. Normalizar documento original
        String identificacionOriginal =
                normalizarIdentificacion(identificacionActual);

        // 2. Comprobar existencia previa del empleado
        empleadoDAO.buscarPorIdentificacion(identificacionOriginal)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un empleado con la identificación: "
                                + identificacionOriginal
                ));

        // 3. Validar los datos actualizados
        Map<String, String> datosValidados =
                validarDatos(empleadoValidator, nuevosDatos);

        // 4. Construir la entidad mediante la fábrica
        IEmpleado empleadoActualizado =
                personaFactory.crearEmpleado(datosValidados);

        // 5. Validar que la nueva identificación no colisione con otra persona
        verificarIdentificacionDisponible(
                empleadoActualizado.getIdentificacion(),
                identificacionOriginal
        );

        // 6. Actualizar en el DAO
        empleadoDAO.actualizar(
                identificacionOriginal,
                empleadoActualizado
        );

        return empleadoActualizado;
    }

    // ==========================================
    // VALIDACIONES INTERNAS
    // ==========================================

    /**
     * Ejecuta el validador especificado sobre el mapa de datos y extrae el valor procesado.
     *
     * @param validador Validador de persona.
     * @param datos Mapa con los atributos a examinar.
     * @return Mapa de datos validados.
     * @throws ValidacionException Si se detectan inconsistencias.
     */
    private Map<String, String> validarDatos(
            IPersonaValidator validador,
            Map<String, String> datos) {

        // Ejecutar validación
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validar(datos);

        // Extraer valor o disparar ValidacionException si hay errores
        return resultado.obtenerValor();
    }

    /**
     * Comprueba que la identificación suministrada no pertenezca a ningún registro existente.
     *
     * @param identificacion Documento a verificar.
     * @throws ValidacionException Si ya existe una persona registrada con ese documento.
     */
    private void verificarIdentificacionDisponible(
            String identificacion) {

        if (personaDAO.existeIdentificacion(identificacion)) {
            throw new ValidacionException(
                    "Ya existe una persona registrada con la identificación: "
                            + identificacion
            );
        }
    }

    /**
     * Comprueba que la identificación no esté en uso por otra persona distinta a la que se actualiza.
     *
     * @param identificacion Nuevo documento.
     * @param identificacionExcluida Documento de la persona que se está modificando.
     * @throws ValidacionException Si otra persona ya posee el documento.
     */
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

    /**
     * Normaliza una cadena de identificación asegurando que no sea nula ni vacía y eliminando espacios.
     *
     * @param identificacion Cadena a normalizar.
     * @return Cadena recortada.
     * @throws ValidacionException Si la identificación es nula o solo contiene espacios.
     */
    private String normalizarIdentificacion(String identificacion) {

        if (identificacion == null || identificacion.isBlank()) {
            throw new ValidacionException(
                    "La identificación es obligatoria."
            );
        }

        return identificacion.trim();
    }
}
