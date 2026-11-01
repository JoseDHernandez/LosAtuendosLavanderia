package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.ICliente;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para la persistencia de clientes.
 * Proporciona métodos para registrar, consultar por identificación única,
 * listar y actualizar los registros de clientes en la base de datos.
 */
public interface IClienteDAO {

    /**
     * Persiste un nuevo cliente en el repositorio de datos.
     *
     * @param cliente Instancia de {@link ICliente} con los datos a almacenar.
     */
    void guardar(ICliente cliente);

    /**
     * Retorna todos los clientes registrados en el sistema.
     *
     * @return Lista de todos los clientes.
     */
    List<ICliente> listarTodos();

    /**
     * Busca un cliente por su número o documento de identificación.
     *
     * @param identificacion Documento de identidad del cliente.
     * @return Un {@link Optional} con el cliente si existe, o vacío en caso contrario.
     */
    Optional<ICliente> buscarPorIdentificacion(
            String identificacion
    );

    /**
     * Actualiza la información de un cliente existente identificado por su documento actual.
     *
     * @param identificacionActual Documento previo del cliente a modificar.
     * @param cliente Datos actualizados del cliente.
     */
    void actualizar(
            String identificacionActual,
            ICliente cliente
    );
}
