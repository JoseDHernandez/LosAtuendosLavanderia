package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrato que define el comportamiento y estructura de un contrato de Alquiler.
 * Incluye datos de cabecera, clientes, empleados que atienden y detalles de prendas alquiladas.
 */
public interface IAlquiler {

    /**
     * Obtiene el identificador único del alquiler en el sistema.
     *
     * @return ID numérico del alquiler.
     */
    long getIdAlquiler();

    /**
     * Obtiene el número correlativo o folio del comprobante de alquiler.
     *
     * @return Número consecutivo único.
     */
    int getNumeroConsecutivo();

    /**
     * Obtiene la fecha en que se registró la solicitud del alquiler.
     *
     * @return Fecha de registro o solicitud.
     */
    LocalDate getFechaSolicitud();

    /**
     * Obtiene la fecha pactada de inicio del alquiler (entrega de prendas).
     *
     * @return Fecha de inicio del alquiler.
     */
    LocalDate getFechaInicio();

    /**
     * Obtiene la fecha prevista límite para la devolución de las prendas.
     *
     * @return Fecha fin pactada.
     */
    LocalDate getFechaFinPrevista();

    /**
     * Obtiene el cliente titular del contrato de alquiler.
     *
     * @return Instancia de {@link ICliente}.
     */
    ICliente getCliente();

    /**
     * Obtiene el empleado que atendió y registró la operación.
     *
     * @return Instancia de {@link IEmpleado}.
     */
    IEmpleado getEmpleado();

    /**
     * Obtiene el estado actual del alquiler.
     *
     * @return Estado del alquiler ({@link EstadoAlquiler}).
     */
    EstadoAlquiler getEstado();

    /**
     * Obtiene la lista inmutable o colección de detalles (prendas y tarifas acordadas).
     *
     * @return Lista de {@link IDetalleAlquiler}.
     */
    List<IDetalleAlquiler> getDetalles();

    /**
     * Actualiza el estado del alquiler según las transiciones del ciclo de vida.
     *
     * @param nuevoEstado Nuevo {@link EstadoAlquiler} a asignar.
     */
    void cambiarEstado(EstadoAlquiler nuevoEstado);
}
