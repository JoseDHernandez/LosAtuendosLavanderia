package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.IPersona;

/**
 * Clase base abstracta que representa a una persona en el sistema.
 * Proporciona los atributos de identificación y contacto compartidos
 * por clientes y empleados, implementando el contrato {@link IPersona}.
 */
public abstract class Persona implements IPersona {

    /** Documento único de identificación oficial. */
    private final String identificacion;

    /** Nombre completo de la persona. */
    private final String nombre;

    /** Dirección física de contacto. */
    private final String direccion;

    /** Número telefónico de contacto. */
    private final String telefono;

    /** Correo electrónico de notificación y contacto. */
    private final String correo;

    /**
     * Constructor protegido para inicializar los datos fundamentales de una persona.
     *
     * @param identificacion Documento de identidad de la persona.
     * @param nombre Nombre completo.
     * @param direccion Dirección domiciliaria.
     * @param telefono Teléfono de contacto.
     * @param correo Dirección de correo electrónico.
     */
    protected Persona(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo) {

        // Asignación de atributos inmutables de contacto e identificación
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDireccion() {
        return direccion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTelefono() {
        return telefono;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCorreo() {
        return correo;
    }

    /**
     * Representación textual legible de la persona con fines de registro o depuración.
     *
     * @return Cadena que incluye el tipo, documento y nombre.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "(" + identificacion + ", " + nombre + ")";
    }
}
