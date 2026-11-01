package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.ILavanderiaDAO;
import com.lavanderia.modelo.enums.EstadoLote;
import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.modelo.lavanderia.LoteLavanderia;
import com.lavanderia.modelo.lavanderia.SolicitudLavanderia;
import com.lavanderia.patrones.strategy.IEstrategiaPrioridadLavanderia;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.lavanderia.ISolicitudLavanderiaValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio de lógica de negocio para la gestión de lavandería de prendas.
 * Implementa {@link IGestorLavanderia}. Controla el ciclo de vida de las solicitudes
 * de lavado, la selección de tandas aplicando la estrategia activa (patrón Strategy),
 * el despacho y confirmación de lotes de prendas y la reincorporación de prendas higienizadas.
 */
public class GestorLavanderia implements IGestorLavanderia {

    /** Repositorio de datos para solicitudes y lotes de lavandería. */
    private final ILavanderiaDAO repositorio;

    /** Validador de formato y cupos de lavandería. */
    private final ISolicitudLavanderiaValidator validador;

    /** Estrategia de selección de prendas para el despacho (FIFO o Prioridad). */
    private IEstrategiaPrioridadLavanderia estrategia;

    /**
     * Construye una nueva instancia de GestorLavanderia inyectando sus componentes.
     *
     * @param repositorio DAO de lavandería.
     * @param validador Validador de solicitudes y cupos.
     * @param estrategia Estrategia inicial de priorización ({@link IEstrategiaPrioridadLavanderia}).
     * @throws IllegalArgumentException Si alguno de los parámetros es nulo.
     */
    public GestorLavanderia(
            ILavanderiaDAO repositorio,
            ISolicitudLavanderiaValidator validador,
            IEstrategiaPrioridadLavanderia estrategia) {

        // Validar no nulidad de los argumentos inyectados
        if (repositorio == null
                || validador == null
                || estrategia == null) {

            throw new IllegalArgumentException(
                    "Los componentes del gestor no pueden ser nulos."
            );
        }

        this.repositorio = repositorio;
        this.validador = validador;
        this.estrategia = estrategia;
    }

    // --------------------------------------------------
    // REGISTRO DE SOLICITUDES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos son inválidos o ya existe solicitud para la devolución.
     */
    @Override
    public void registrarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        // 1. Validar parámetros de entrada
        validarDatosSolicitud(devolucion, prioridad, fechaRecepcion);

        // 2. Verificar que la devolución no tenga ya una solicitud asociada
        boolean existe = repositorio
                .buscarSolicitudPorDevolucion(
                        devolucion.getIdDevolucion()
                )
                .isPresent();

        if (existe) {
            throw new ValidacionException(
                    "Esta devolución ya tiene una solicitud de lavandería."
            );
        }

        // 3. Crear y persistir la solicitud
        SolicitudLavanderia solicitud = new SolicitudLavanderia(
                devolucion,
                prioridad,
                fechaRecepcion
        );

        repositorio.guardarSolicitud(solicitud);
    }

    // --------------------------------------------------
    // REGISTRO O ACTUALIZACIÓN DE SOLICITUDES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos no son válidos o si la solicitud ya está en proceso.
     */
    @Override
    public void registrarOActualizarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        // 1. Validar parámetros
        validarDatosSolicitud(devolucion, prioridad, fechaRecepcion);

        // 2. Buscar si existe solicitud previa
        Optional<SolicitudLavanderia> resultado =
                repositorio.buscarSolicitudPorDevolucion(
                        devolucion.getIdDevolucion()
                );

        // Si no existe, crear una nueva solicitud
        if (resultado.isEmpty()) {
            registrarSolicitud(devolucion, prioridad, fechaRecepcion);
            return;
        }

        // Si ya existe, evaluar su estado para actualizarla
        SolicitudLavanderia solicitud = resultado.get();

        switch (solicitud.getEstado()) {

            case PENDIENTE:
                // Actualizar prioridad y fecha en la solicitud pendiente
                solicitud.setPrioridad(prioridad);
                solicitud.setFechaRecepcion(fechaRecepcion);

                repositorio.actualizarSolicitud(solicitud);
                break;

            case CANCELADA:
                // Reactivar la solicitud previamente cancelada
                solicitud.setPrioridad(prioridad);
                solicitud.setFechaRecepcion(fechaRecepcion);
                solicitud.setEstado(EstadoSolicitud.PENDIENTE);

                repositorio.actualizarSolicitud(solicitud);
                break;

            case EN_LAVADO:
            case LISTA:
                // Solicitudes en curso o finalizadas no pueden modificarse
                throw new ValidacionException(
                        "La solicitud ya fue procesada y no puede modificarse."
                );
        }
    }

    // --------------------------------------------------
    // CANCELACIÓN DE SOLICITUDES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si el idDevolucion es inválido o la solicitud ya no puede cancelarse.
     */
    @Override
    public void cancelarSolicitudPorDevolucion(long idDevolucion) {

        if (idDevolucion <= 0) {
            throw new ValidacionException(
                    "El identificador de la devolución no es válido."
            );
        }

        SolicitudLavanderia solicitud = repositorio
                .buscarSolicitudPorDevolucion(idDevolucion)
                .orElse(null);

        // No existe o ya está cancelada: salir sin acción
        if (solicitud == null
                || solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            return;
        }

        // Solo solicitudes pendientes son cancelables
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ValidacionException(
                    "No se puede cancelar una solicitud que ya fue enviada "
                            + "o finalizada."
            );
        }

        repositorio.actualizarEstado(
                solicitud.getIdSolicitud(),
                EstadoSolicitud.CANCELADA
        );

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
    }

    // --------------------------------------------------
    // VALIDACIÓN DE SOLICITUDES
    // --------------------------------------------------

    /**
     * Valida la presencia de la devolución registrada, prioridad y fecha.
     *
     * @param devolucion Devolución a verificar.
     * @param prioridad Prioridad asignada.
     * @param fechaRecepcion Fecha de recepción.
     * @throws ValidacionException Si algún argumento no cumple los criterios.
     */
    private void validarDatosSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        if (devolucion == null || devolucion.getIdDevolucion() <= 0) {
            throw new ValidacionException(
                    "Debe proporcionar una devolución registrada."
            );
        }

        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
            );
        }

        if (fechaRecepcion == null) {
            throw new ValidacionException(
                    "La fecha de recepción es obligatoria."
            );
        }
    }

    // --------------------------------------------------
    // SELECCIÓN DE TANDA
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ISolicitudLavanderia> seleccionarTanda(
            String cupoDiario) {

        // Validar y convertir la cadena de texto a entero
        int cupo = validador.validarCupo(cupoDiario);

        return seleccionarTanda(cupo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ISolicitudLavanderia> seleccionarTanda(
            int cupoDiario) {

        int cupo = validador.validarCupo(cupoDiario);

        // Consultar todas las solicitudes actualmente pendientes
        List<ISolicitudLavanderia> pendientes =
                repositorio.consultarPendientes();

        if (pendientes == null || pendientes.isEmpty()) {
            return List.of();
        }

        // Delegar la selección y ordenamiento en el algoritmo de la estrategia activa
        return estrategia.seleccionarPrendas(
                pendientes,
                cupo
        );
    }

    // --------------------------------------------------
    // CONFIRMACIÓN DEL ENVÍO
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la tanda no supera las validaciones de integridad.
     * @throws IllegalStateException Si el DAO no genera un identificador válido para el lote.
     */
    @Override
    public ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda) {

        // 1. Validar que la tanda contenga solicitudes válidas y pendientes
        validarTanda(tanda);

        // 2. Crear la entidad de Lote de Lavandería
        ILoteLavanderia lote = new LoteLavanderia(
                LocalDate.now(),
                tanda.size(),
                EstadoLote.ENVIADO
        );

        /*
         * Operación transaccional:
         * 1. Guardar el lote en persistencia.
         * 2. Asignar el lote a las solicitudes.
         * 3. Cambiar sus estados a EN_LAVADO.
         */
        ILoteLavanderia lotePersistido =
                repositorio.confirmarEnvio(tanda, lote);

        if (lotePersistido == null
                || lotePersistido.getIdLote() <= 0) {

            throw new IllegalStateException(
                    "No se pudo confirmar el lote de lavandería."
            );
        }

        // 3. Sincronizar las entidades en memoria
        for (ISolicitudLavanderia solicitud : tanda) {
            solicitud.asignarLote(lotePersistido);
            solicitud.setEstado(EstadoSolicitud.EN_LAVADO);
        }

        return lotePersistido;
    }

    /**
     * Valida que una lista de solicitudes sea apta para conformar un nuevo lote.
     *
     * @param tanda Lista de solicitudes a examinar.
     * @throws ValidacionException Si la tanda está vacía, contiene duplicados o ítems no pendientes.
     */
    private void validarTanda(List<ISolicitudLavanderia> tanda) {

        if (tanda == null || tanda.isEmpty()) {
            throw new ValidacionException(
                    "Debe seleccionar al menos una solicitud."
            );
        }

        Set<Long> ids = new HashSet<>();

        for (ISolicitudLavanderia solicitud : tanda) {

            if (solicitud == null) {
                throw new ValidacionException(
                        "La tanda contiene solicitudes inválidas."
                );
            }

            if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
                throw new ValidacionException(
                        "Solo se pueden enviar solicitudes pendientes."
                );
            }

            if (solicitud.getLote() != null) {
                throw new ValidacionException(
                        "Una solicitud ya está asignada a un lote."
                );
            }

            if (solicitud.getIdSolicitud() <= 0) {
                throw new ValidacionException(
                        "La solicitud no tiene un identificador persistido."
                );
            }

            if (!ids.add(solicitud.getIdSolicitud())) {
                throw new ValidacionException(
                        "La tanda contiene solicitudes duplicadas."
                );
            }
        }
    }

    // --------------------------------------------------
    // CAMBIO DE ESTRATEGIA
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la estrategia es nula.
     */
    @Override
    public void cambiarEstrategia(
            IEstrategiaPrioridadLavanderia estrategia) {

        if (estrategia == null) {
            throw new ValidacionException(
                    "La estrategia de lavandería no puede ser nula."
            );
        }

        this.estrategia = estrategia;
    }

    // --------------------------------------------------
    // FINALIZACIÓN DE SOLICITUDES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la solicitud no está en estado EN_LAVADO o es nula.
     */
    @Override
    public void marcarSolicitudLista(
            ISolicitudLavanderia solicitud) {

        if (solicitud == null) {
            throw new ValidacionException(
                    "La solicitud es obligatoria."
            );
        }

        if (solicitud.getEstado() != EstadoSolicitud.EN_LAVADO) {
            throw new ValidacionException(
                    "Solo se pueden finalizar solicitudes que estén en lavado."
            );
        }

        // Actualizar el estado en el DAO
        repositorio.actualizarEstado(
                solicitud.getIdSolicitud(),
                EstadoSolicitud.LISTA
        );

        // Actualizar en el objeto en memoria
        solicitud.setEstado(EstadoSolicitud.LISTA);
    }
}
