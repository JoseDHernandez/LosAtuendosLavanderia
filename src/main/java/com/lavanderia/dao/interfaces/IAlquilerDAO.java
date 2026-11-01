package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAlquilerDAO {

    /**
     * Registra el alquiler y sus detalles.
     * El consecutivo y los identificadores son generados
     * durante la persistencia.
     *
     * @param alquiler alquiler que se desea registrar
     * @return alquiler persistido con sus identificadores
     */
    IAlquiler guardar(IAlquiler alquiler);

    /**
     * Busca un alquiler por su número consecutivo.
     *
     * @param numeroConsecutivo número del servicio
     * @return alquiler encontrado, si existe
     */
    Optional<IAlquiler> buscarPorConsecutivo(
            int numeroConsecutivo
    );

    /**
     * Consulta todos los alquileres asociados a un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de alquileres
     */
    List<IAlquiler> consultarPorCliente(long idCliente);

    /**
     * Consulta los alquileres vigentes de un cliente.
     *
     * Se consideran vigentes los que están registrados
     * o activos, según el estado del alquiler.
     *
     * @param idCliente identificador del cliente
     * @return lista de alquileres vigentes
     */
    List<IAlquiler> consultarVigentesPorCliente(
            long idCliente
    );

    /**
     * Consulta los alquileres cuya fecha de inicio
     * corresponde a la fecha indicada.
     *
     * @param fecha fecha de alquiler
     * @return lista de alquileres
     */
    List<IAlquiler> consultarPorFecha(LocalDate fecha);

    /**
     * Consulta los alquileres que se cruzan con el período
     * solicitado para una prenda determinada.
     *
     * @param idPrenda identificador de la prenda
     * @param fechaInicio inicio del período solicitado
     * @param fechaFin fin del período solicitado
     * @return alquileres que presentan cruces de fechas
     */
    List<IAlquiler> consultarConflictos(
            long idPrenda,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Actualiza el estado de un alquiler.
     *
     * @param idAlquiler identificador del alquiler
     * @param nuevoEstado nuevo estado
     */
    void actualizarEstado(
            long idAlquiler,
            EstadoAlquiler nuevoEstado
    );
}