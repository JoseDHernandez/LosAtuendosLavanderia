package com.lavanderia.patrones.factory;

import com.lavanderia.modelo.interfaces.IPersona;
import java.util.Map;

/**
 * Creador abstracto (Creator) del patrón Factory Method para la instanciación de personas.
 * Define la operación fábrica abstracta {@link #crearPersona(Map)} que es implementada
 * por las subclases especializadas ({@link ClienteCreator} y {@link EmpleadoCreator}).
 */
public abstract class PersonaCreator {

    /**
     * Crea una instancia de persona utilizando el diccionario de datos suministrado.
     *
     * <p>Claves esperadas por convención:
     * {@code identificacion}, {@code nombre}, {@code direccion}, {@code telefono}, {@code correo}.
     * En el caso de empleados, se requiere adicionalmente la clave {@code cargo}.</p>
     *
     * @param datos Mapa con los atributos textuales necesarios para construir la persona.
     * @return Instancia concreta de {@link IPersona} construida.
     */
    public abstract IPersona crearPersona(Map<String, String> datos);
}
