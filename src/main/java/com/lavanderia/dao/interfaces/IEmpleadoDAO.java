package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IEmpleado;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para la gestión de empleados del negocio.
 * Permite registrar empleados, consultar su información por documento de identidad,
 * listar la plantilla de personal y actualizar sus datos laborales.
 */
public interface IEmpleadoDAO {

    /**
     * Guarda un nuevo empleado en la base de datos.
     *
     * @param empleado Instancia de {@link IEmpleado} con los datos a persistir.
     */
    void guardar(IEmpleado empleado);

    /**
     * Retorna la lista con todos los empleados registrados.
     *
     * @return Lista de empleados.
     */
    List<IEmpleado> listarTodos();

    /**
     * Busca un empleado específico mediante su número de identificación.
     *
     * @param identificacion Documento de identidad del empleado.
     * @return Un {@link Optional} con el empleado encontrado o vacío si no existe.
     */
    Optional<IEmpleado> buscarPorIdentificacion(
            String identificacion
    );

    /**
     * Actualiza la información de un empleado registrado.
     *
     * @param identificacionActual Identificación previa del empleado a modificar.
     * @param empleado Datos actualizados del empleado.
     */
    void actualizar(
            String identificacionActual,
            IEmpleado empleado
    );
}
