package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.IPersona;
import java.util.Map;

/**
 * Creator del patrón Factory Method.
 * Define el contrato para la creación de personas.
 */
public abstract class PersonaCreator {

    /**
     * Crea una persona utilizando los datos proporcionados.
     *
     * Claves esperadas:
     * identificacion, nombre, direccion, telefono, correo.
     * Para empleados también se requiere: cargo.
     *
     * @param datos información necesaria para construir la persona.
     * @return persona creada.
     */
    public abstract IPersona crearPersona(Map<String, String> datos);
}