package com.lavanderia.validacion.alquiler;

import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.comun.ValidadorComun;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class AlquilerValidator implements IAlquilerValidator {

    @Override
    public Map<String, String> validar(Map<String, String> datos) {

        if (datos == null) {
            throw new ValidacionException(
                    "No se recibieron datos del alquiler."
            );
        }

        Map<String, String> limpio = new HashMap<>();

        LocalDate fechaInicio = validarFecha(
                datos.get("fechaInicio"), "fecha de inicio"
        );

        LocalDate fechaFin = validarFecha(
                datos.get("fechaFinPrevista"), "fecha de finalización"
        );

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ValidacionException(
                    "La fecha de finalización debe ser posterior "
                            + "a la fecha de inicio."
            );
        }

        limpio.put("fechaInicio", fechaInicio.toString());
        limpio.put("fechaFinPrevista", fechaFin.toString());

        limpio.put(
                "idCliente",
                validarIdentificador(datos.get("idCliente"), "cliente")
        );

        limpio.put(
                "idEmpleado",
                validarIdentificador(datos.get("idEmpleado"), "empleado")
        );

        return limpio;
    }

    private LocalDate validarFecha(String valor, String campo) {

        try {
            return LocalDate.parse(
                    ValidadorComun.obligatorio(valor, campo)
            );

        } catch (DateTimeParseException e) {
            throw new ValidacionException(
                    "La " + campo + " no tiene un formato válido."
            );
        }
    }

    private String validarIdentificador(String valor, String campo) {

        String limpio = ValidadorComun.obligatorio(valor, campo);

        try {
            long id = Long.parseLong(limpio);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return String.valueOf(id);

        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "El identificador del " + campo + " no es válido."
            );
        }
    }
}