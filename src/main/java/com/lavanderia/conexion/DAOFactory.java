package com.lavanderia.conexion;

import com.lavanderia.dao.interfaces.*;

/**
 * Fábrica abstracta (patrón Abstract Factory) para la creación de DAOs (Data Access Objects).
 * Desacopla la capa de servicios de la tecnología de persistencia subyacente (por ejemplo, SQLite).
 * Permite intercambiar fácilmente el motor de base de datos sin afectar la lógica de negocio.
 */
public interface DAOFactory {

  /**
   * Obtiene el DAO para operaciones generales sobre personas.
   *
   * @return Implementación de {@link IPersonaDAO}.
   */
  IPersonaDAO getPersonaDAO();

  /**
   * Obtiene el DAO para operaciones relacionadas con clientes.
   *
   * @return Implementación de {@link IClienteDAO}.
   */
  IClienteDAO getClienteDAO();

  /**
   * Obtiene el DAO para operaciones relacionadas con empleados.
   *
   * @return Implementación de {@link IEmpleadoDAO}.
   */
  IEmpleadoDAO getEmpleadoDAO();

  /**
   * Obtiene el DAO para la persistencia e inventario de prendas.
   *
   * @return Implementación de {@link IPrendaDAO}.
   */
  IPrendaDAO getPrendaDAO();

  /**
   * Obtiene el DAO para el registro y consulta de devoluciones.
   *
   * @return Implementación de {@link IDevolucionDAO}.
   */
  IDevolucionDAO getDevolucionDAO();

  /**
   * Obtiene el DAO para la gestión y seguimiento de contratos de alquiler.
   *
   * @return Implementación de {@link IAlquilerDAO}.
   */
  IAlquilerDAO getAlquilerDAO();

  /**
   * Obtiene el DAO para solicitudes y lotes de lavandería externa.
   *
   * @return Implementación de {@link ILavanderiaDAO}.
   */
  ILavanderiaDAO getLavanderiaDAO();
}
