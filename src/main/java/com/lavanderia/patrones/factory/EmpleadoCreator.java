package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;
import com.lavanderia.modelo.persona.Empleado;
import com.lavanderia.validacion.persona.IPersonaValidator;

import java.util.Map;

/**
 * Creador concreto (ConcreteCreator) para empleados dentro del patrón Factory Method.
 * Extiende {@link PersonaCreator} implementando la creación de objetos {@link Empleado}.
 */
public class EmpleadoCreator extends PersonaCreator {

    /** Validador de reglas de formato y negocio para datos de empleados. */
    private final IPersonaValidator validator;

    /**
     * Construye una instancia de EmpleadoCreator inyectando el validador requerido.
     *
     * @param validator Validador de persona ({@link IPersonaValidator}).
     */
    public EmpleadoCreator(IPersonaValidator validator) {
        this.validator = validator;
    }

    /**
     * Valida los datos recibidos y construye una nueva instancia de {@link Empleado}.
     *
     * @param datos Mapa con los atributos textuales del empleado (incluyendo 'cargo').
     * @return Nueva instancia de {@link IEmpleado}.
     */
    @Override
    public IEmpleado crearPersona(Map<String, String> datos) {

        // ERROR DE COMPILACIÓN DETECTADO:
        // 'validator.validar(datos)' retorna 'ResultadoValidacion<Map<String, String>>',
        // no un 'Map<String, String>' directamente.
        // Solución requerida: validator.validar(datos).obtenerValor();
        Map<String, String> d = validator.validar(datos).obtenerValor();

        // Construir la instancia de Empleado con los datos extraídos
        return new Empleado(
                d.get("identificacion"),
                d.get("nombre"),
                d.get("direccion"),
                d.get("telefono"),
                d.get("correo"),
                d.get("cargo")
        );
    }
}
