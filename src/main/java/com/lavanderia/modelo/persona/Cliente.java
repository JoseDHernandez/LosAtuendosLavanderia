package com.lavanderia.modelo.persona;

import com.lavanderia.modelo.interfaces.ICliente;

/**
 * Entidad de dominio que representa a un Cliente en el sistema de alquiler de vestuario.
 * Hereda todos los atributos personales y de contacto de {@link Persona} e implementa {@link ICliente}.
 */
public class Cliente extends Persona implements ICliente {

    /**
     * Construye una nueva instancia de Cliente con sus datos personales.
     *
     * @param identificacion Documento de identificación único del cliente.
     * @param nombre Nombre y apellidos del cliente.
     * @param direccion Dirección residencial.
     * @param telefono Teléfono de contacto.
     * @param correo Correo electrónico.
     */
    public Cliente(
            String identificacion,
            String nombre,
            String direccion,
            String telefono,
            String correo) {

        // Invocar el constructor de la superclase Persona
        super(
                identificacion,
                nombre,
                direccion,
                telefono,
                correo
        );
    }
}
