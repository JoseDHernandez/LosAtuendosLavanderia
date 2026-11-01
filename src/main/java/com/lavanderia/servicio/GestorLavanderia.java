
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
import com.lavanderia.patrones.strategy.EstrategiaPrioridad;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.lavanderia.ISolicitudLavanderiaValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GestorLavanderia implements IGestorLavanderia {

    private final ILavanderiaDAO repositorio;
    private final ISolicitudLavanderiaValidator validador;
    private IEstrategiaPrioridadLavanderia estrategia;

    public GestorLavanderia(
            ILavanderiaDAO repositorio,
            ISolicitudLavanderiaValidator validador) {

        this(
                repositorio,
                validador,
                new EstrategiaPrioridad()
        );
    }

    public GestorLavanderia(
            ILavanderiaDAO repositorio,
            ISolicitudLavanderiaValidator validador,
            IEstrategiaPrioridadLavanderia estrategia) {

        if (repositorio == null || validador == null || estrategia == null) {
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

    @Override
    public void registrarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        validarDatosSolicitud(devolucion, prioridad, fechaRecepcion);

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

    @Override
    public void registrarOActualizarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        validarDatosSolicitud(devolucion, prioridad, fechaRecepcion);

        Optional<SolicitudLavanderia> resultado =
                repositorio.buscarSolicitudPorDevolucion(
                        devolucion.getIdDevolucion()
                );

        // No existe: crear solicitud.
        if (resultado.isEmpty()) {
            registrarSolicitud(devolucion, prioridad, fechaRecepcion);
            return;
        }

        SolicitudLavanderia solicitud = resultado.get();

        switch (solicitud.getEstado()) {

            case PENDIENTE:

                solicitud.setPrioridad(prioridad);
                solicitud.setFechaRecepcion(fechaRecepcion);

                repositorio.actualizarSolicitud(solicitud);
                break;

            case CANCELADA:

                solicitud.setPrioridad(prioridad);
                solicitud.setFechaRecepcion(fechaRecepcion);
                solicitud.setEstado(EstadoSolicitud.PENDIENTE);

                repositorio.actualizarSolicitud(solicitud);
                break;

            case EN_LAVADO:
            case LISTA:

                throw new ValidacionException(
                        "La solicitud ya fue procesada y no puede modificarse."
                );
        }
    }

    // --------------------------------------------------
    // CANCELACIÓN DE SOLICITUDES
    // --------------------------------------------------

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

        // No existe o ya está cancelada.
        if (solicitud == null
                || solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            return;
        }

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

    @Override
    public List<ISolicitudLavanderia> seleccionarTanda(
            String cupoDiario) {

        int cupo = validador.validarCupo(cupoDiario);

        return seleccionarTanda(cupo);
    }

    @Override
    public List<ISolicitudLavanderia> seleccionarTanda(
            int cupoDiario) {

        int cupo = validador.validarCupo(cupoDiario);

        List<ISolicitudLavanderia> pendientes =
                repositorio.consultarPendientes();

        if (pendientes == null || pendientes.isEmpty()) {
            return List.of();
        }

        return estrategia.seleccionarPrendas(
                pendientes,
                cupo
        );
    }

    // --------------------------------------------------
    // CONFIRMACIÓN DEL ENVÍO
    // --------------------------------------------------

    @Override
    public ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda) {

        validarTanda(tanda);

        ILoteLavanderia lote = new LoteLavanderia(
                LocalDate.now(),
                tanda.size(),
                EstadoLote.ENVIADO
        );

        /*
         * Esta operación debe ejecutarse en una transacción:
         * 1. Guardar el lote.
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

        // Actualizar los objetos en memoria después de persistir.
        for (ISolicitudLavanderia solicitud : tanda) {
            solicitud.asignarLote(lotePersistido);
            solicitud.setEstado(EstadoSolicitud.EN_LAVADO);
        }

        return lotePersistido;
    }

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

        repositorio.actualizarEstado(
                solicitud.getIdSolicitud(),
                EstadoSolicitud.LISTA
        );

        solicitud.setEstado(EstadoSolicitud.LISTA);
    }
}
