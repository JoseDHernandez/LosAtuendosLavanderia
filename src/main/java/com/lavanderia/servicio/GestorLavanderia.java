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
import com.lavanderia.patrones.strategy.EstrategiaLavanderia;
import com.lavanderia.patrones.strategy.EstrategiaPrioridad;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.lavanderia.ISolicitudLavanderiaValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GestorLavanderia {

    private final ILavanderiaDAO repositorio;
    private final ISolicitudLavanderiaValidator validador;
    private EstrategiaLavanderia estrategia;

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
            EstrategiaLavanderia estrategia) {

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

    public void registrarDevolucion(
            IDevolucion devolucion,
            String prioridad) {

        Map<String, String> datos = new HashMap<>();
        datos.put("prioridad", prioridad);

        Map<String, String> datosValidados =
                validador.validar(datos);

        String prioridadValidada =
                datosValidados.get("prioridad");

        if (prioridadValidada == null) {
            throw new ValidacionException(
                    "No se pudo validar la prioridad."
            );
        }

        Prioridad prioridadEnum;

        try {
            prioridadEnum = Prioridad.valueOf(
                    prioridadValidada.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(
                    "La prioridad indicada no es válida."
            );
        }

        registrarDevolucion(
                devolucion,
                prioridadEnum,
                LocalDate.now()
        );
    }

    public void registrarDevolucion(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion) {

        if (devolucion == null) {
            throw new ValidacionException(
                    "La devolución es obligatoria."
            );
        }

        if (prioridad == null) {
            throw new ValidacionException(
                    "La prioridad es obligatoria."
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
    // SELECCIÓN DE TANDA
    // --------------------------------------------------

    public List<ISolicitudLavanderia> seleccionarTanda(
            String cupoDiario) {

        int cupo = validador.validarCupo(cupoDiario);

        return seleccionarTanda(cupo);
    }

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

    public ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda) {

        validarTanda(tanda);

        ILoteLavanderia lote = new LoteLavanderia(
                LocalDate.now(),
                tanda.size(),
                EstadoLote.ENVIADO
        );

        ILoteLavanderia lotePersistido =
                repositorio.confirmarEnvio(tanda, lote);

        if (lotePersistido == null ||
                lotePersistido.getIdLote() <= 0) {

            throw new IllegalStateException(
                    "No se pudo confirmar el lote de lavandería."
            );
        }

        for (ISolicitudLavanderia solicitud : tanda) {
            solicitud.asignarLote(lotePersistido);
            solicitud.setEstado(EstadoSolicitud.EN_LAVADO);
        }

        return lotePersistido;
    }

    private void validarTanda(
            List<ISolicitudLavanderia> tanda) {

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

    public void cambiarEstrategia(EstrategiaLavanderia estrategia) {

        if (estrategia == null) {
            throw new ValidacionException(
                    "La estrategia de lavandería no puede ser nula."
            );
        }

        this.estrategia = estrategia;
    }

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