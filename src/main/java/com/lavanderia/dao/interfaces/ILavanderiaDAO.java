package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.enums.EstadoSolicitud;
import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.modelo.lavanderia.SolicitudLavanderia;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para el flujo de lavandería industrial/externa.
 * Administra el registro de solicitudes de lavado para prendas con novedades o sucias,
 * la consulta de solicitudes pendientes, la actualización de estados y el despacho de lotes.
 */
public interface ILavanderiaDAO {

    /**
     * Guarda una nueva solicitud de servicio de lavandería para una prenda retornada.
     *
     * @param solicitud Instancia de {@link ISolicitudLavanderia} a persistir.
     */
    void guardarSolicitud(ISolicitudLavanderia solicitud);

    /**
     * Consulta todas las solicitudes de lavado que se encuentran pendientes de asignación a un lote.
     *
     * @return Lista de solicitudes pendientes.
     */
    List<ISolicitudLavanderia> consultarPendientes();

    /**
     * Modifica el estado de una solicitud de lavandería específica.
     *
     * @param idSolicitud Identificador de la solicitud.
     * @param nuevoEstado Nuevo {@link EstadoSolicitud} (ej. EN_LAVADO, LISTA, CANCELADA).
     */
    void actualizarEstado(
            long idSolicitud,
            EstadoSolicitud nuevoEstado
    );

    /**
     * Confirma el despacho y envío de una tanda o lote de solicitudes a la lavandería.
     * Persiste el lote y asocia las solicitudes seleccionadas.
     *
     * @param solicitudes Lista de solicitudes incluidas en el lote.
     * @param lote Datos del lote a despachar.
     * @return Instancia persistida de {@link ILoteLavanderia} con identificador asignado.
     */
    ILoteLavanderia confirmarEnvio(
            List<ISolicitudLavanderia> solicitudes,
            ILoteLavanderia lote
    );

    /**
     * Busca la solicitud de lavandería vinculada a una devolución determinada.
     *
     * @param idDevolucion Identificador único de la devolución.
     * @return Un {@link Optional} con la solicitud de lavandería encontrada o vacío si no existe.
     */
    Optional<SolicitudLavanderia> buscarSolicitudPorDevolucion(
            long idDevolucion
    );

    /**
     * Actualiza la información y estado de una solicitud de lavandería ya existente.
     *
     * @param solicitud Objeto {@link SolicitudLavanderia} con datos actualizados.
     */
    void actualizarSolicitud(SolicitudLavanderia solicitud);

    /**
     * Consulta todas las solicitudes registradas.
     */
    List<ISolicitudLavanderia> listarSolicitudes();

    /**
     * Consulta todos los lotes de lavandería registrados.
     */
    List<ILoteLavanderia> listarLotes();
}
