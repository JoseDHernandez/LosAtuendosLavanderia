package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.ICliente;

public class Cliente extends Persona implements ICliente {

    public Cliente(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo) {

        super(
                identificacion,
                nombre,
                direccion,
                telefono,
                correo
        );
    }
}