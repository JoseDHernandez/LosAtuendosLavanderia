package com.lavanderia.modelo.interfaces;

/**
 * Contrato que representa a un empleado del negocio en el modelo de dominio.
 * Extiende {@link IPersona} agregando información sobre el cargo desempeñado.
 */
public interface IEmpleado extends IPersona {

    /**
     * Obtiene el cargo o función laboral del empleado dentro de la empresa.
     *
     * @return Nombre del cargo asignado.
     */
    String getCargo();
}
