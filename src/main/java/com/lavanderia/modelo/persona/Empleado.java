package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.IEmpleado;

public class Empleado extends Persona implements IEmpleado {

    private final String cargo;

    public Empleado(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo,
            String cargo) {

        super(
                identificacion,
                nombre,
                direccion,
                telefono,
                correo
        );

        this.cargo = cargo;
    }

    @Override
    public String getCargo() {
        return cargo;
    }
}