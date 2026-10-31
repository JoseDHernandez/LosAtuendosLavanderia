package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.ILavanderiaDAO;
import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.modelo.lavanderia.SolicitudLavanderia;
import com.lavanderia.patrones.strategy.EstrategiaFIFO;
import com.lavanderia.patrones.strategy.EstrategiaLavanderia;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.lavanderia.ISolicitudLavanderiaValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contexto del patrón Strategy.
 */
public class GestorLavanderia {

    private final ILavanderiaDAO repositorio;
    private final ISolicitudLavanderiaValidator validador;

    private EstrategiaLavanderia estrategia;

    /**
     * Constructor con estrategia FIFO predeterminada.
     */
    public GestorLavanderia(
            ILavanderiaDAO repositorio,
            ISolicitudLavanderiaValidator validador) {

        this(repositorio, validador, new EstrategiaFIFO());
    }

    /**
     * Constructor con estrategia personalizada.
     */
    public GestorLavanderia(
            ILavanderiaDAO repositorio,
            ISolicitudLavanderiaValidator validador,
            EstrategiaLavanderia estrategia) {

        if (repositorio == null || validador == null
                || estrategia == null) {
            throw new IllegalArgumentException(
                    "Las dependencias de lavandería son obligatorias."
            );
        }

        this.repositorio = repositorio;
        this.validador = validador;
        this.estrategia = estrategia;
    }

    /**
     * Registra una devolución desde la interfaz gráfica.
     */
    public ISolicitudLavanderia registrarDevolucion(
            String referenciaPrenda,
            String prioridad) {

        Map<String, String> datos = new HashMap<>();

        datos.put("referenciaPrenda", referenciaPrenda);
        datos.put("prioridad", prioridad);

        Map<String, String> limpio = validador.validar(datos);

        return registrarDevolucion(
                limpio.get("referenciaPrenda"),
                Prioridad.valueOf(limpio.get("prioridad")),
                LocalDate.now()
        );
    }

    /**
     * Registra una devolución con fecha definida.
     */
    public ISolicitudLavanderia registrarDevolucion(
            String referenciaPrenda,
            Prioridad prioridad,
            LocalDate fecha) {

        Map<String, String> datos = new HashMap<>();

        datos.put("referenciaPrenda", referenciaPrenda);
        datos.put("prioridad",
                prioridad == null ? null : prioridad.name());

        Map<String, String> limpio = validador.validar(datos);

        ISolicitudLavanderia solicitud =
                new SolicitudLavanderia(
                        limpio.get("referenciaPrenda"),
                        Prioridad.valueOf(limpio.get("prioridad")),
                        fecha
                );

        repositorio.guardarSolicitud(solicitud);

        return solicitud;
    }

    /**
     * Selecciona una tanda desde el cupo ingresado en Swing.
     */
    public List<ISolicitudLavanderia> seleccionarTanda(
            String cupoDiario) {

        return seleccionarTanda(
                validador.validarCupo(cupoDiario)
        );
    }

    /**
     * Selecciona las solicitudes según la estrategia activa.
     */
    public List<ISolicitudLavanderia> seleccionarTanda(
            int cupoDiario) {

        validador.validarCupo(cupoDiario);

        return estrategia.seleccionarPrendas(
                repositorio.consultarPendientes(),
                cupoDiario
        );
    }

    /**
     * Confirma oficialmente el envío.
     */
    public void confirmarEnvio(
            List<ISolicitudLavanderia> tanda) {

        if (tanda == null || tanda.isEmpty()) {
            throw new ValidacionException(
                    "Debe seleccionar prendas para confirmar el envío."
            );
        }

        for (ISolicitudLavanderia solicitud : tanda) {

            if (solicitud == null
                    || solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {

                throw new ValidacionException(
                        "Todas las solicitudes deben estar pendientes."
                );
            }
        }

        for (ISolicitudLavanderia solicitud : tanda) {
            solicitud.setEstado(EstadoSolicitud.EN_LAVADO);
            repositorio.actualizarEstado(solicitud);
        }
    }

    /**
     * Permite cambiar la estrategia de selección.
     */
    public void cambiarEstrategia(EstrategiaLavanderia nueva) {

        if (nueva == null) {
            throw new ValidacionException(
                    "Estrategia no válida."
            );
        }

        this.estrategia = nueva;
    }
}