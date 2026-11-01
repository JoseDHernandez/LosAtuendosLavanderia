package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.strategy.IEstrategiaPrioridadLavanderia;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato de servicio para el control de lavandería de prendas del negocio.
 * Administra solicitudes de lavado, conformación de tandas o lotes de envío,
 * despacho a lavanderías externas, cambio de estrategias de priorización (Strategy)
 * y reingreso de prendas limpias a disponibilidad.
 */
public interface IGestorLavanderia {

    /**
     * Registra una nueva solicitud de servicio de lavado para una prenda con novedades.
     *
     * @param devolucion Devolución que originó la novedad.
     * @param prioridad Prioridad asignada (NORMAL, DELICADA, MANCHADA, ESPECIAL).
     * @param fechaRecepcion Fecha de entrada a lavandería.
     */
    void registrarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion
    );

    /**
     * Registra una nueva solicitud de lavado o actualiza la existente si ya fue registrada.
     *
     * @param devolucion Devolución asociada.
     * @param prioridad Nivel de prioridad.
     * @param fechaRecepcion Fecha de recepción.
     */
    void registrarOActualizarSolicitud(
            IDevolucion devolucion,
            Prioridad prioridad,
            LocalDate fechaRecepcion
    );

    /**
     * Cancela la solicitud de lavandería vinculada a una devolución (por ejemplo si se reclasifica sin novedad).
     *
     * @param idDevolucion Identificador de la devolución.
     */
    void cancelarSolicitudPorDevolucion(long idDevolucion);

    /**
     * Selecciona una tanda de solicitudes pendientes para despacho según el cupo provisto en formato texto.
     *
     * @param cupoDiario Capacidad diaria en representación textual (será validada).
     * @return Lista de solicitudes seleccionadas según la estrategia activa.
     */
    List<ISolicitudLavanderia> seleccionarTanda(String cupoDiario);

    /**
     * Selecciona una tanda de solicitudes pendientes según el cupo entero provisto.
     *
     * @param cupoDiario Cantidad máxima de prendas a enviar.
     * @return Lista de solicitudes seleccionadas.
     */
    List<ISolicitudLavanderia> seleccionarTanda(int cupoDiario);

    /**
     * Consulta todas las solicitudes de lavandería,
     * independientemente de su estado.
     */
    List<ISolicitudLavanderia> listarSolicitudes();

    /**
     * Consulta los lotes de lavandería registrados.
     */
    List<ILoteLavanderia> listarLotes();

    /**
     * Confirma y formaliza el envío de una tanda de solicitudes conformando un nuevo lote de lavandería.
     *
     * @param tanda Lista de solicitudes seleccionadas a despachar.
     * @return Lote de lavandería persistido.
     */
    ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> tanda
    );

    /**
     * Modifica en tiempo de ejecución la estrategia de selección de prendas (patrón Strategy).
     *
     * @param estrategia Nueva implementación de {@link IEstrategiaPrioridadLavanderia}.
     */
    void cambiarEstrategia(
            IEstrategiaPrioridadLavanderia estrategia
    );

    /**
     * Marca una solicitud de lavandería como completada/lista, permitiendo que la prenda retorne al inventario.
     *
     * @param solicitud Solicitud que finalizó su proceso de lavado.
     */
    void marcarSolicitudLista(ISolicitudLavanderia solicitud);
}
