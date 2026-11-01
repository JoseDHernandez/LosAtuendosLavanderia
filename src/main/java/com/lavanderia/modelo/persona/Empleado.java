package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.IEmpleado;

/**
 * Entidad de dominio que representa a un Empleado dentro de la organización.
 * Extiende {@link Persona} agregando la responsabilidad o cargo laboral.
 */
public class Empleado extends Persona implements IEmpleado {

    /** Cargo o función asignada al empleado (ej. Vendedor, Administrador, Revisor). */
    private final String cargo;

    /**
     * Construye un nuevo Empleado con sus datos personales y laborales.
     *
     * @param identificacion Documento de identificación único.
     * @param nombre Nombre completo del empleado.
     * @param direccion Dirección de domicilio.
     * @param telefono Teléfono de contacto.
     * @param correo Correo institucional o personal.
     * @param cargo Puesto o cargo ocupado en la empresa.
     */
    public Empleado(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo,
            String cargo) {

        // Inicializar datos base de la persona
        super(
                identificacion,
                nombre,
                direccion,
                telefono,
                correo
        );

        // Asignar el cargo laboral específico
        this.cargo = cargo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCargo() {
        return cargo;
    }
}
