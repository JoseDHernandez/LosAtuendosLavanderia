package com.lavanderia.modelo.interfaces;

/**
 * Contrato base para cualquier persona en el sistema (clientes y empleados).
 * Provee los datos de contacto e identificación esenciales.
 */
public interface IPersona {

    /**
     * Obtiene el documento o número de identificación oficial.
     *
     * @return Cédula o número de documento.
     */
    String getIdentificacion();

    /**
     * Obtiene el nombre completo de la persona.
     *
     * @return Nombre y apellidos.
     */
    String getNombre();

    /**
     * Obtiene la dirección de residencia o correspondencia.
     *
     * @return Dirección física.
     */
    String getDireccion();

    /**
     * Obtiene el número de contacto telefónico.
     *
     * @return Teléfono móvil o fijo.
     */
    String getTelefono();

    /**
     * Obtiene la dirección de correo electrónico.
     *
     * @return Correo electrónico válido.
     */
    String getCorreo();
}
