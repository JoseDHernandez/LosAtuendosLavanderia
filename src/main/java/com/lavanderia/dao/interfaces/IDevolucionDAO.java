package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.lavanderia.Devolucion;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para el registro de devoluciones de prendas.
 * Permite registrar inspecciones post-alquiler, verificar si un detalle de alquiler
 * ya fue retornado, consultar y actualizar el estado de revisión.
 */
public interface IDevolucionDAO {

    /**
     * Persiste una nueva devolución en el sistema.
     *
     * @param devolucion Objeto con los datos de inspección y detalle de alquiler.
     * @return Instancia de {@link Devolucion} guardada, con identificador autogenerado.
     */
    Devolucion guardar(Devolucion devolucion);

    /**
     * Comprueba si un ítem específico de un contrato de alquiler ya cuenta con una devolución registrada.
     *
     * @param idDetalle Identificador del detalle de alquiler.
     * @return {@code true} si ya existe devolución para ese detalle, {@code false} de lo contrario.
     */
    boolean existePorIdDetalle(long idDetalle);

    /**
     * Busca una devolución por su identificador primario único.
     *
     * @param idDevolucion Identificador numérico de la devolución.
     * @return Un {@link Optional} con la devolución encontrada, o vacío si no existe.
     */
    Optional<Devolucion> buscarPorId(long idDevolucion);

    /**
     * Busca la devolución vinculada a una línea de detalle de alquiler específica.
     *
     * @param idDetalle Identificador de la línea de detalle de alquiler.
     * @return Un {@link Optional} con la devolución vinculada si existe.
     */
    Optional<Devolucion> buscarPorIdDetalle(long idDetalle);

    /**
     * Lista todas las devoluciones registradas en el sistema.
     *
     * @return Lista completa de devoluciones.
     */
    List<Devolucion> listarDevoluciones();

    /**
     * Actualiza la información de una devolución ya existente (ej. cambio en observaciones o estado).
     *
     * @param devolucion Datos actualizados de la devolución.
     */
    void actualizar(Devolucion devolucion);
}
