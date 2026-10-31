package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.IPersona;

public abstract class Persona implements IPersona {

    private final String identificacion;
    private final String nombre;
    private final String direccion;
    private final String telefono;
    private final String correo;

    protected Persona(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo) {

        this.identificacion = identificacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    @Override
    public String getIdentificacion() {
        return identificacion;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getDireccion() {
        return direccion;
    }

    @Override
    public String getTelefono() {
        return telefono;
    }

    @Override
    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "(" + identificacion + ", " + nombre + ")";
    }
}