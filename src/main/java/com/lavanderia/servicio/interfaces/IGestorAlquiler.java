package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de servicio para la gestión de contratos de alquiler de prendas.
 * Proporciona métodos para registrar alquileres, consultar por consecutivo, cliente o fecha,
 * validar disponibilidad de prendas para un rango de fechas y gestionar transiciones de estado.
 */
public interface IGestorAlquiler {

    /**
     * Registra un nuevo contrato de alquiler en el sistema validando cliente, empleado, prendas y fechas.
     *
     * @param alquiler Objeto con los datos del alquiler y detalles de prendas.
     * @return Alquiler registrado y persistido con número consecutivo generado.
     */
    IAlquiler registrarAlquiler(IAlquiler alquiler);

    /**
     * Consulta un alquiler mediante su número de folio o consecutivo.
     *
     * @param consecutivo Número consecutivo único del servicio.
     * @return {@link Optional} con el alquiler si fue encontrado.
     */
    Optional<IAlquiler> consultarPorConsecutivo(int consecutivo);

    /**
     * Consulta el historial de alquileres tramitados por un cliente específico.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de contratos de alquiler asociados al cliente.
     */
    List<IAlquiler> consultarPorCliente(long idCliente);

    /**
     * Consulta los alquileres que inician en una fecha determinada.
     *
     * @param fecha Fecha de inicio del servicio.
     * @return Lista de alquileres programados para esa fecha.
     */
    List<IAlquiler> consultarPorFecha(LocalDate fecha);

    /**
     * Lista la totalidad de contratos de alquiler registrados en el sistema.
     *
     * @return Lista completa de alquileres.
     */
    List<IAlquiler> listarTodos();

    /**
     * Verifica si una lista de prendas se encuentra libre y sin conflictos de fechas
     * para el intervalo comprendido entre fechaInicio y fechaFin.
     *
     * @param idsPrendas Lista de identificadores de prendas a comprobar.
     * @param fechaInicio Inicio del período de reserva.
     * @param fechaFin Fin del período de reserva.
     * @return {@code true} si todas las prendas están disponibles, {@code false} si hay conflicto.
     */
    boolean verificarDisponibilidad(
            List<Long> idsPrendas,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Cambia el estado del ciclo de vida de un alquiler (ej. a ACTIVO, FINALIZADO o CANCELADO).
     *
     * @param idAlquiler Identificador del alquiler a modificar.
     * @param estado Nuevo estado a asignar ({@link EstadoAlquiler}).
     */
    void cambiarEstado(long idAlquiler, EstadoAlquiler estado);
}
