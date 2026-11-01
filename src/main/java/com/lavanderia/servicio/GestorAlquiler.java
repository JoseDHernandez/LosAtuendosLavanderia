package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IAlquilerDAO;
import com.lavanderia.dao.interfaces.IClienteDAO;
import com.lavanderia.dao.interfaces.IEmpleadoDAO;
import com.lavanderia.dao.interfaces.IPrendaDAO;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IPrenda;

import com.lavanderia.servicio.interfaces.IGestorAlquiler;

import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.alquiler.IAlquilerValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio de lógica de negocio para la gestión de contratos de Alquiler.
 * Implementa {@link IGestorAlquiler}. Administra el flujo integral de validación de fechas,
 * verificación de registro previo de clientes y empleados, control estricto de disponibilidad
 * física de prendas sin traslapes temporales y persistencia transaccional mediante {@link IAlquilerDAO}.
 */
public class GestorAlquiler implements IGestorAlquiler {

    /** DAO para la persistencia y consulta de alquileres. */
    private final IAlquilerDAO alquilerDAO;

    /** DAO para consultar y verificar la existencia de clientes. */
    private final IClienteDAO clienteDAO;

    /** DAO para consultar y verificar la existencia de empleados. */
    private final IEmpleadoDAO empleadoDAO;

    /** DAO para consultar y verificar la existencia e inventario de prendas. */
    private final IPrendaDAO prendaDAO;

    /** Validador de reglas de formato y lógica de fechas para alquileres. */
    private final IAlquilerValidator validator;

    /**
     * Construye una nueva instancia de GestorAlquiler inyectando sus dependencias DAOs y validador.
     *
     * @param alquilerDAO DAO de alquileres.
     * @param clienteDAO DAO de clientes.
     * @param empleadoDAO DAO de empleados.
     * @param prendaDAO DAO de prendas.
     * @param validator Validador de alquileres.
     * @throws NullPointerException Si alguno de los DAOs o el validador es nulo.
     */
    public GestorAlquiler(
            IAlquilerDAO alquilerDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO,
            IPrendaDAO prendaDAO,
            IAlquilerValidator validator) {

        // Validar no nulidad de DAOs y componentes inyectados
        this.alquilerDAO = Objects.requireNonNull(
                alquilerDAO,
                "El DAO de alquileres es obligatorio."
        );

        this.clienteDAO = Objects.requireNonNull(
                clienteDAO,
                "El DAO de clientes es obligatorio."
        );

        this.empleadoDAO = Objects.requireNonNull(
                empleadoDAO,
                "El DAO de empleados es obligatorio."
        );

        this.prendaDAO = Objects.requireNonNull(
                prendaDAO,
                "El DAO de prendas es obligatorio."
        );

        this.validator = Objects.requireNonNull(
                validator,
                "El validador de alquileres es obligatorio."
        );
    }

    // --------------------------------------------------
    // REGISTRO DE ALQUILER
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el contrato es nulo, los datos fallan validación,
     *                             el cliente o empleado no existen, o hay prendas no disponibles.
     * @throws IllegalStateException Si el DAO no genera identificadores válidos.
     */
    @Override
    public IAlquiler registrarAlquiler(IAlquiler alquiler) {

        if (alquiler == null) {
            throw new ValidacionException(
                    "El alquiler es obligatorio."
            );
        }

        // 1. Extraer y validar datos generales del alquiler (fechas e identificaciones)
        Map<String, String> datos = construirDatosValidacion(alquiler);

        ResultadoValidacion<Map<String, String>> resultado =
                validator.validar(datos);

        Map<String, String> datosValidados = resultado.obtenerValor();

        // 2. Verificar que el cliente esté registrado en la base de datos
        verificarClienteRegistrado(
                datosValidados.get("identificacionCliente")
        );

        // 3. Verificar que el empleado esté registrado en la base de datos
        verificarEmpleadoRegistrado(
                datosValidados.get("identificacionEmpleado")
        );

        // 4. Obtener y validar las prendas especificadas en los detalles
        List<Long> idsPrendas = obtenerIdsPrendas(alquiler);

        // 5. Verificar que ninguna de las prendas tenga cruce de fechas o reservas en ese periodo
        boolean disponibles = verificarDisponibilidad(
                idsPrendas,
                alquiler.getFechaInicio(),
                alquiler.getFechaFinPrevista()
        );

        if (!disponibles) {
            throw new ValidacionException(
                    "Una o varias prendas no están disponibles "
                            + "para el periodo solicitado."
            );
        }

        // 6. Persistir el alquiler y sus detalles en el repositorio
        // El DAO genera los identificadores autoincrementales y el número consecutivo
        IAlquiler alquilerPersistido = alquilerDAO.guardar(alquiler);

        if (alquilerPersistido == null
                || alquilerPersistido.getIdAlquiler() <= 0
                || alquilerPersistido.getNumeroConsecutivo() <= 0) {

            throw new IllegalStateException(
                    "No se pudo registrar correctamente el alquiler."
            );
        }

        return alquilerPersistido;
    }

    // --------------------------------------------------
    // CONSTRUCCIÓN DE DATOS PARA VALIDACIÓN
    // --------------------------------------------------

    /**
     * Extrae un mapa de cadenas de texto a partir del objeto {@link IAlquiler} para someterlo al validador.
     *
     * @param alquiler Objeto de alquiler.
     * @return Diccionario con fechas y documentos en formato de texto.
     */
    private Map<String, String> construirDatosValidacion(
            IAlquiler alquiler) {

        Map<String, String> datos = new LinkedHashMap<>();

        datos.put(
                "fechaInicio",
                alquiler.getFechaInicio() == null
                        ? null
                        : alquiler.getFechaInicio().toString()
        );

        datos.put(
                "fechaFinPrevista",
                alquiler.getFechaFinPrevista() == null
                        ? null
                        : alquiler.getFechaFinPrevista().toString()
        );

        datos.put(
                "identificacionCliente",
                alquiler.getCliente() == null
                        ? null
                        : alquiler.getCliente().getIdentificacion()
        );

        datos.put(
                "identificacionEmpleado",
                alquiler.getEmpleado() == null
                        ? null
                        : alquiler.getEmpleado().getIdentificacion()
        );

        return datos;
    }

    // --------------------------------------------------
    // VERIFICACIÓN DE PERSONAS REGISTRADAS
    // --------------------------------------------------

    /**
     * Valida que exista un cliente con el documento dado en la base de datos.
     *
     * @param identificacion Cédula a buscar.
     * @throws ValidacionException Si el cliente no existe.
     */
    private void verificarClienteRegistrado(String identificacion) {

        boolean existe = clienteDAO
                .buscarPorIdentificacion(identificacion)
                .isPresent();

        if (!existe) {
            throw new ValidacionException(
                    "El cliente indicado no se encuentra registrado."
            );
        }
    }

    /**
     * Valida que exista un empleado con el documento dado en la base de datos.
     *
     * @param identificacion Cédula a buscar.
     * @throws ValidacionException Si el empleado no existe.
     */
    private void verificarEmpleadoRegistrado(String identificacion) {

        boolean existe = empleadoDAO
                .buscarPorIdentificacion(identificacion)
                .isPresent();

        if (!existe) {
            throw new ValidacionException(
                    "El empleado indicado no se encuentra registrado."
            );
        }
    }

    // --------------------------------------------------
    // VALIDACIÓN DE DETALLES Y OBTENCIÓN DE PRENDAS
    // --------------------------------------------------

    /**
     * Examina cada línea de detalle del contrato, verificando existencia, tarifas válidas
     * y ausencia de prendas duplicadas dentro del mismo contrato.
     *
     * @param alquiler Objeto de alquiler.
     * @return Lista de IDs numéricos de las prendas incluidas.
     * @throws ValidacionException Si algún detalle o prenda es nulo, duplicado o tiene valor negativo.
     * @throws IllegalStateException Si la prenda registrada no posee un ID válido.
     */
    private List<Long> obtenerIdsPrendas(IAlquiler alquiler) {

        List<IDetalleAlquiler> detalles = alquiler.getDetalles();

        if (detalles == null || detalles.isEmpty()) {
            throw new ValidacionException(
                    "El alquiler debe contener al menos una prenda."
            );
        }

        List<Long> idsPrendas = new ArrayList<>();
        Set<Long> idsRegistrados = new HashSet<>();

        for (IDetalleAlquiler detalle : detalles) {

            if (detalle == null || detalle.getPrenda() == null) {
                throw new ValidacionException(
                    "El alquiler contiene un detalle o una prenda inválida."
                );
            }

            IPrenda prenda = detalle.getPrenda();

            if (prenda.getReferencia() == null
                    || prenda.getReferencia().isBlank()) {

                throw new ValidacionException(
                        "Una de las prendas no tiene una referencia válida."
                );
            }

            // Comprobar que la prenda realmente esté registrada en el catálogo.
            IPrenda prendaRegistrada = prendaDAO
                    .buscarPorReferencia(prenda.getReferencia())
                    .orElseThrow(() -> new ValidacionException(
                            "La prenda con referencia "
                                    + prenda.getReferencia()
                                    + " no se encuentra registrada."
                    ));

            long idPrenda = prendaRegistrada.getIdPrenda();

            if (idPrenda <= 0) {
                throw new IllegalStateException(
                        "La prenda registrada no tiene un identificador válido."
                );
            }

            // Evitar que se agregue dos veces la misma prenda en el mismo alquiler.
            if (!idsRegistrados.add(idPrenda)) {
                throw new ValidacionException(
                        "El alquiler contiene una prenda duplicada: "
                                + prenda.getReferencia()
                );
            }

            // Validar que el valor acordado no sea nulo ni negativo.
            BigDecimal valorAcordado = detalle.getValorAcordado();

            if (valorAcordado == null
                    || valorAcordado.compareTo(BigDecimal.ZERO) < 0) {

                throw new ValidacionException(
                        "El valor acordado de la prenda "
                                + prenda.getReferencia()
                                + " no es válido."
                );
            }

            idsPrendas.add(idPrenda);
        }

        return idsPrendas;
    }

    // --------------------------------------------------
    // CONSULTAS
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el número consecutivo es menor o igual a cero.
     */
    @Override
    public Optional<IAlquiler> consultarPorConsecutivo(
            int consecutivo) {

        if (consecutivo <= 0) {
            throw new ValidacionException(
                    "El número consecutivo debe ser mayor que cero."
            );
        }

        return alquilerDAO.buscarPorConsecutivo(consecutivo);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el idCliente no es válido.
     */
    @Override
    public List<IAlquiler> consultarPorCliente(long idCliente) {

        if (idCliente <= 0) {
            throw new ValidacionException(
                    "El identificador del cliente no es válido."
            );
        }

        return alquilerDAO.consultarPorCliente(idCliente);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la fecha es nula.
     */
    @Override
    public List<IAlquiler> consultarPorFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new ValidacionException(
                    "La fecha de consulta es obligatoria."
            );
        }

        return alquilerDAO.consultarPorFecha(fecha);
    }

    // --------------------------------------------------
    // VERIFICACIÓN DE DISPONIBILIDAD
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los parámetros de fechas son inconsistentes o hay duplicados en la lista.
     * @throws IllegalStateException Si la respuesta del DAO es nula.
     */
    @Override
    public boolean verificarDisponibilidad(
            List<Long> idsPrendas,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        // Validaciones iniciales de fechas y colecciones
        if (idsPrendas == null || idsPrendas.isEmpty()
                || fechaInicio == null || fechaFin == null
                || !fechaFin.isAfter(fechaInicio)) {

            throw new ValidacionException(
                    "Los datos para verificar la disponibilidad no son válidos."
            );
        }

        Set<Long> idsUnicos = new HashSet<>();

        // Iterar y comprobar conflictos para cada prenda en el rango de fechas
        for (Long idPrenda : idsPrendas) {

            if (idPrenda == null || idPrenda <= 0) {
                throw new ValidacionException(
                        "La lista contiene identificadores de prendas inválidos."
                );
            }

            if (!idsUnicos.add(idPrenda)) {
                throw new ValidacionException(
                        "La lista contiene prendas duplicadas."
                );
            }

            // Consultar solapamientos de alquileres activos o registrados
            List<IAlquiler> conflictos = alquilerDAO.consultarConflictos(
                    idPrenda,
                    fechaInicio,
                    fechaFin
            );

            if (conflictos == null) {
                throw new IllegalStateException(
                        "El DAO devolvió una respuesta inválida al consultar conflictos."
                );
            }

            // Si hay al menos un conflicto de fechas, la prenda no está disponible
            if (!conflictos.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // --------------------------------------------------
    // CAMBIO DE ESTADO
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el id o el estado son inválidos.
     */
    @Override
    public void cambiarEstado(
            long idAlquiler,
            EstadoAlquiler estado) {

        if (idAlquiler <= 0) {
            throw new ValidacionException(
                    "El identificador del alquiler no es válido."
            );
        }

        if (estado == null) {
            throw new ValidacionException(
                    "El nuevo estado del alquiler es obligatorio."
            );
        }

        alquilerDAO.actualizarEstado(
                idAlquiler,
                estado
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAlquiler> listarTodos() {
        return alquilerDAO.listarTodos();
    }
}
