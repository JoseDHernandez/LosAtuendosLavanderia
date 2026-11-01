package com.lavanderia.validacion.devolucion;

import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.validacion.ResultadoValidacion;

import java.util.HashMap;
import java.util.Map;

/**
 * Validador concreto para objetos de devolución de prendas.
 * Implementa {@link IDevolucionValidator}. Comprueba la no nulidad de la devolución,
 * la presencia obligatoria de la línea de detalle de alquiler asociada, la fecha de retorno
 * y el dictamen de revisión técnica (PENDIENTE, SIN_NOVEDAD, CON_NOVEDAD).
 */
public class DevolucionValidator implements IDevolucionValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultadoValidacion<IDevolucion> validar(IDevolucion devolucion) {

        Map<String, String> errores = new HashMap<>();

        // 1. Validar que el objeto devolución no sea nulo
        if (devolucion == null) {
            errores.put("devolucion", "La devolución es obligatoria.");
            return ResultadoValidacion.fallido(errores);
        }

        // 2. Validar que se haya especificado el detalle de alquiler correspondiente
        if (devolucion.getDetalleAlquiler() == null) {
            errores.put("detalleAlquiler", "Debe seleccionar un detalle de alquiler.");
        }

        // 3. Validar la fecha de entrega física
        if (devolucion.getFechaDevolucion() == null) {
            errores.put("fechaDevolucion", "La fecha de devolución es obligatoria.");
        }

        // 4. Validar el estado técnico de revisión
        if (devolucion.getEstadoRevision() == null) {
            errores.put("estadoRevision", "Debe seleccionar un estado de revisión.");
        }

        // 5. Retornar fallido si se acumuló algún error
        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        // Retornar exitoso con la instancia validada
        return ResultadoValidacion.exitoso(devolucion);
    }
}
