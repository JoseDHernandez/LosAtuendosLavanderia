package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.Map;

/**
 * Contrato de fábrica para la instanciación de personas (patrón Abstract Factory / Factory Method).
 * Define los métodos necesarios para crear clientes y empleados a partir de un mapa de atributos.
 */
public interface IPersonaFactory {

    /**
     * Construye un cliente a partir de un mapa de datos validados.
     *
     * @param datos Mapa con las claves requeridas (identificacion, nombre, direccion, telefono, correo).
     * @return Nueva instancia de {@link ICliente}.
     */
    ICliente crearCliente(Map<String, String> datos);

    /**
     * Construye un empleado a partir de un mapa de datos validados.
     *
     * @param datos Mapa con las claves requeridas (identificacion, nombre, direccion, telefono, correo, cargo).
     * @return Nueva instancia de {@link IEmpleado}.
     */
    IEmpleado crearEmpleado(Map<String, String> datos);
}
