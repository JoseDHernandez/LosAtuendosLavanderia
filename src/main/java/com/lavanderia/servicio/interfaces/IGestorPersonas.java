package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;

import java.util.List;
import java.util.Map;

/**
 * Contrato de servicio para la administración de personas (clientes y empleados).
 * Centraliza validaciones de identificación, registro mediante fábricas,
 * consultas y actualización de datos de contacto y cargos.
 */
public interface IGestorPersonas {

    /**
     * Valida y registra un nuevo cliente en el sistema.
     *
     * @param datos Diccionario de datos personales del cliente.
     * @return Instancia de {@link ICliente} creada y persistida.
     */
    ICliente registrarCliente(Map<String, String> datos);

    /**
     * Valida y registra un nuevo empleado en el sistema.
     *
     * @param datos Diccionario con datos personales y laborales (incluyendo cargo).
     * @return Instancia de {@link IEmpleado} creada y persistida.
     */
    IEmpleado registrarEmpleado(Map<String, String> datos);

    /**
     * Busca cualquier persona registrada mediante su número de identificación.
     *
     * @param identificacion Cédula o documento a consultar.
     * @return Instancia de {@link IPersona} encontrada.
     */
    IPersona buscarPorIdentificacion(String identificacion);

    /**
     * Lista todos los clientes registrados en la base de datos.
     *
     * @return Lista completa de clientes.
     */
    List<ICliente> listarClientes();

    /**
     * Lista todos los empleados registrados en la base de datos.
     *
     * @return Lista completa de empleados.
     */
    List<IEmpleado> listarEmpleados();

    /**
     * Actualiza la información de un cliente existente.
     *
     * @param identificacion Identificación actual del cliente.
     * @param datos Nuevos datos del cliente a actualizar.
     * @return Instancia de {@link ICliente} actualizada.
     */
    ICliente actualizarCliente(
            String identificacion,
            Map<String, String> datos
    );

    /**
     * Actualiza la información de un empleado existente.
     *
     * @param identificacion Identificación actual del empleado.
     * @param datos Nuevos datos del empleado a actualizar.
     * @return Instancia de {@link IEmpleado} actualizada.
     */
    IEmpleado actualizarEmpleado(
            String identificacion,
            Map<String, String> datos
    );
}
