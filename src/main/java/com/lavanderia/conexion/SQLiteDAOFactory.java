package com.lavanderia.conexion;

import com.lavanderia.dao.interfaces.*;
import com.lavanderia.dao.sqlite.*;
import java.nio.file.Path;

/** Abstract Factory concreta: entrega una familia de siete DAO sobre el mismo archivo SQLite. */
public final class SQLiteDAOFactory implements DAOFactory {
  /** Proveedor compartido por los productos de esta fábrica. */
  private final SQLiteConnection bd;

  /**
   * Configura la familia SQLite e inicializa su esquema; la ruta predeterminada es
   * database/database.db.
   *
   * @throws PersistenciaException si no es posible crear o inicializar la base de datos
   */
  public SQLiteDAOFactory() {
    this(Path.of("database/database.db"));
  }

  /**
   * Configura la familia SQLite con el archivo indicado e inicializa su esquema.
   *
   * @param archivo ruta del archivo SQLite que se utilizará
   * @throws PersistenciaException si no es posible crear o inicializar la base de datos
   */
  public SQLiteDAOFactory(Path archivo) {
    bd = new SQLiteConnection(archivo);
    bd.inicializar();
  }

  /**
   * Entrega el producto abstracto IPersonaDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IPersonaDAO
   */
  @Override
  public IPersonaDAO getPersonaDAO() {
    return new PersonaDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto IClienteDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IClienteDAO
   */
  @Override
  public IClienteDAO getClienteDAO() {
    return new ClienteDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto IEmpleadoDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IEmpleadoDAO
   */
  @Override
  public IEmpleadoDAO getEmpleadoDAO() {
    return new EmpleadoDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto IPrendaDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IPrendaDAO
   */
  @Override
  public IPrendaDAO getPrendaDAO() {
    return new PrendaDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto IDevolucionDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IDevolucionDAO
   */
  @Override
  public IDevolucionDAO getDevolucionDAO() {
    return new DevolucionDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto IAlquilerDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato IAlquilerDAO
   */
  @Override
  public IAlquilerDAO getAlquilerDAO() {
    return new AlquilerDAOSQLite(bd);
  }

  /**
   * Entrega el producto abstracto ILavanderiaDAO mediante su implementación SQLite; comparte
   * proveedor, no una conexión abierta.
   *
   * @return nueva implementación SQLite del contrato ILavanderiaDAO
   */
  @Override
  public ILavanderiaDAO getLavanderiaDAO() {
    return new LavanderiaDAOSQLite(bd);
  }
}
