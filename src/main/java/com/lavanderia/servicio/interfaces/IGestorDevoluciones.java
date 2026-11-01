package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.IDevolucion;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de servicio para la gestión de devoluciones e inspecciones de prendas alquiladas.
 * Permite registrar la recepción física de prendas, inspeccionar su estado (novedades/manchas/daños),
 * coordinar automáticamente con lavandería en caso de anomalías y consultar devoluciones.
 */
public interface IGestorDevoluciones {

    /**
     * Registra el retorno físico de una prenda de un alquiler, validando los datos y
     * disparando la solicitud de lavandería si la prenda presenta novedades.
     *
     * @param devolucion Datos de la devolución e inspección.
     * @return Instancia persistida de {@link IDevolucion}.
     */
    IDevolucion registrarDevolucion(IDevolucion devolucion);

    /**
     * Busca una devolución por su identificador único.
     *
     * @param idDevolucion ID numérico de la devolución.
     * @return {@link Optional} con la devolución encontrada.
     */
    Optional<IDevolucion> consultarPorId(long idDevolucion);

    /**
     * Busca la devolución correspondiente a una línea de detalle de alquiler.
     *
     * @param idDetalle Identificador del detalle de alquiler.
     * @return {@link Optional} con la devolución asociada.
     */
    Optional<IDevolucion> consultarPorDetalle(long idDetalle);

    /**
     * Lista todas las devoluciones registradas en el sistema.
     *
     * @return Lista completa de devoluciones.
     */
    List<IDevolucion> listarTodas();

    /**
     * Actualiza la información y dictamen de una devolución existente, sincronizando
     * las solicitudes de lavandería si el estado de revisión cambia.
     *
     * @param idDevolucion ID de la devolución a actualizar.
     * @param devolucion Datos actualizados.
     * @return Devolución actualizada.
     */
    IDevolucion actualizarDevolucion(
            long idDevolucion,
            IDevolucion devolucion
    );
}
