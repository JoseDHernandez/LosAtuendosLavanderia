package com.lavanderia.validacion.devolucion;

import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.validacion.ResultadoValidacion;

import java.util.HashMap;
import java.util.Map;

public class DevolucionValidator implements IDevolucionValidator {

    @Override
    public ResultadoValidacion<IDevolucion> validar(IDevolucion devolucion) {

        Map<String, String> errores = new HashMap<>();

        if (devolucion == null) {
            errores.put("devolucion", "La devolución es obligatoria.");
            return ResultadoValidacion.fallido(errores);
        }

        if (devolucion.getDetalleAlquiler() == null) {
            errores.put("detalleAlquiler", "Debe seleccionar un detalle de alquiler.");
        }

        if (devolucion.getFechaDevolucion() == null) {
            errores.put("fechaDevolucion", "La fecha de devolución es obligatoria.");
        }

        if (devolucion.getEstadoRevision() == null) {
            errores.put("estadoRevision", "Debe seleccionar un estado de revisión.");
        }

        if (!errores.isEmpty()) {
            return ResultadoValidacion.fallido(errores);
        }

        return ResultadoValidacion.exitoso(devolucion);
    }
}