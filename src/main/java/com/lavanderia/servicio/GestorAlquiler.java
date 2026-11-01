
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

public class GestorAlquiler implements IGestorAlquiler {

    private final IAlquilerDAO alquilerDAO;
    private final IClienteDAO clienteDAO;
    private final IEmpleadoDAO empleadoDAO;
    private final IPrendaDAO prendaDAO;
    private final IAlquilerValidator validator;

    public GestorAlquiler(
            IAlquilerDAO alquilerDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO,
            IPrendaDAO prendaDAO,
            IAlquilerValidator validator) {

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

    @Override
    public IAlquiler registrarAlquiler(IAlquiler alquiler) {

        if (alquiler == null) {
            throw new ValidacionException(
                    "El alquiler es obligatorio."
            );
        }

        // 1. Validar datos generales del alquiler.
        Map<String, String> datos = construirDatosValidacion(alquiler);

        ResultadoValidacion<Map<String, String>> resultado =
                validator.validar(datos);

        Map<String, String> datosValidados = resultado.obtenerValor();

        // 2. Verificar existencia del cliente.
        verificarClienteRegistrado(
                datosValidados.get("identificacionCliente")
        );

        // 3. Verificar existencia del empleado.
        verificarEmpleadoRegistrado(
                datosValidados.get("identificacionEmpleado")
        );

        // 4. Obtener y validar las prendas de los detalles.
        List<Long> idsPrendas = obtenerIdsPrendas(alquiler);

        // 5. Verificar disponibilidad durante el periodo solicitado.
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

        // 6. Persistir el alquiler y sus detalles.
        // El DAO genera los identificadores y el consecutivo.
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

            // Comprobar que la prenda realmente esté registrada.
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

            // Evitar que se agregue dos veces la misma prenda.
            if (!idsRegistrados.add(idPrenda)) {
                throw new ValidacionException(
                        "El alquiler contiene una prenda duplicada: "
                                + prenda.getReferencia()
                );
            }

            // Validar el valor acordado.
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

    @Override
    public List<IAlquiler> consultarPorCliente(long idCliente) {

        if (idCliente <= 0) {
            throw new ValidacionException(
                    "El identificador del cliente no es válido."
            );
        }

        return alquilerDAO.consultarPorCliente(idCliente);
    }

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

    @Override
    public boolean verificarDisponibilidad(
            List<Long> idsPrendas,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (idsPrendas == null || idsPrendas.isEmpty()
                || fechaInicio == null || fechaFin == null
                || !fechaFin.isAfter(fechaInicio)) {

            throw new ValidacionException(
                    "Los datos para verificar la disponibilidad no son válidos."
            );
        }

        Set<Long> idsUnicos = new HashSet<>();

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

            if (!conflictos.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // --------------------------------------------------
    // CAMBIO DE ESTADO
    // --------------------------------------------------

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

    @Override
    public List<IAlquiler> listarTodos() {
        return alquilerDAO.listarTodos();
    }
}
