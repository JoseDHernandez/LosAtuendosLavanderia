package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para la entidad Alquiler.
 * Define las operaciones CRUD y consultas especializadas de disponibilidad y estados
 * sobre las transacciones de alquiler de prendas.
 */
public interface IAlquilerDAO {

    /**
     * Registra un nuevo alquiler y sus líneas de detalle asociadas.
     * El número consecutivo y los identificadores únicos son generados durante la persistencia.
     *
     * @param alquiler Objeto de alquiler con los datos a persistir.
     * @return Alquiler persistido con sus identificadores asignados.
     */
    IAlquiler guardar(IAlquiler alquiler);

    /**
     * Busca un alquiler por su número consecutivo único de servicio.
     *
     * @param numeroConsecutivo Número correlativo único asignado al alquiler.
     * @return Un {@link Optional} con el alquiler encontrado, o vacío si no existe.
     */
    Optional<IAlquiler> buscarPorConsecutivo(
            int numeroConsecutivo
    );

    /**
     * Consulta el histórico de alquileres asociados a un cliente específico.
     *
     * @param idCliente Identificador único de la persona cliente.
     * @return Lista de alquileres asociados al cliente.
     */
    List<IAlquiler> consultarPorCliente(long idCliente);

    /**
     * Consulta los alquileres vigentes o activos de un cliente.
     * Se consideran vigentes aquellos en estado REGISTRADO o ACTIVO.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de alquileres actualmente en curso para el cliente.
     */
    List<IAlquiler> consultarVigentesPorCliente(
            long idCliente
    );

    /**
     * Consulta todos los alquileres cuya fecha de inicio coincida con la fecha indicada.
     *
     * @param fecha Fecha de inicio programada del alquiler.
     * @return Lista de alquileres programados para esa fecha.
     */
    List<IAlquiler> consultarPorFecha(LocalDate fecha);

    /**
     * Consulta si existen conflictos o solapamientos de fechas para una prenda determinada.
     * Permite verificar que una prenda no sea alquilada simultáneamente en rangos coincidentes.
     *
     * @param idPrenda Identificador único de la prenda.
     * @param fechaInicio Inicio del período a consultar.
     * @param fechaFin Fin del período a consultar.
     * @return Lista de alquileres que generan conflicto de fechas con la prenda.
     */
    List<IAlquiler> consultarConflictos(
            long idPrenda,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Actualiza el estado administrativo o de ciclo de vida de un alquiler.
     *
     * @param idAlquiler Identificador del alquiler a modificar.
     * @param nuevoEstado Nuevo {@link EstadoAlquiler} (ej. ACTIVO, FINALIZADO, CANCELADO).
     */
    void actualizarEstado(
            long idAlquiler,
            EstadoAlquiler nuevoEstado
    );

    /**
     * Lista todos los alquileres registrados en el sistema de persistencia.
     *
     * @return Lista completa de alquileres.
     */
    List<IAlquiler> listarTodos();
}
