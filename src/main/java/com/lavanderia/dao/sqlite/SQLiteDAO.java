package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import java.sql.*;
import java.util.*;

/** Utilidades JDBC internas: parámetros, cierre de recursos y transacciones. */
abstract class SQLiteDAO {
  /** Proveedor de conexiones recibido por inyección de dependencias. */
  protected final SQLiteConnection bd;

  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  protected SQLiteDAO(SQLiteConnection bd) {
    this.bd = Objects.requireNonNull(bd);
  }

  /**
   * Operación que utiliza una conexión.
   *
   * @param <T> tipo del resultado del trabajo
   */
  @FunctionalInterface
  protected interface Trabajo<T> {
    /**
     * Ejecuta la operación descrita por su contrato.
     *
     * @param c conexión abierta que se utiliza en esta operación
     * @return resultado producido por el trabajo
     * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
     */
    T ejecutar(Connection c) throws SQLException;
  }

  /**
   * Conversión de una fila SQL a un objeto.
   *
   * @param <T> tipo del objeto que se reconstruye
   */
  @FunctionalInterface
  protected interface Mapeador<T> {
    /**
     * Ejecuta la operación descrita por su contrato.
     *
     * @param r resultado SQL situado en la fila que se desea convertir
     * @return objeto reconstruido desde la fila actual
     * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
     */
    T leer(ResultSet r) throws SQLException;
  }

  /**
   * Abre una conexión para el trabajo recibido y la cierra automáticamente. Conserva la causa de
   * los errores SQL.
   *
   * @param <T> tipo del resultado de la operación
   * @param t trabajo que se ejecutará utilizando una conexión
   * @return resultado producido por el trabajo
   */
  protected <T> T ejecutar(Trabajo<T> t) {
    try (Connection c = bd.getConnection()) {
      return t.ejecutar(c);
    } catch (SQLException e) {
      throw new PersistenciaException("No se pudo completar la operación de datos", e);
    }
  }

  /**
   * Ejecuta un conjunto de operaciones como unidad: commit confirma y rollback deshace. Retorna
   * el resultado del trabajo.
   *
   * @param <T> tipo del resultado de la operación
   * @param t trabajo que se ejecutará utilizando una conexión
   * @return resultado del trabajo confirmado
   */
  protected <T> T transaccion(Trabajo<T> t) {
    return ejecutar(
        c -> {
          c.setAutoCommit(false);
          try {
            T valor = t.ejecutar(c);
            c.commit();
            return valor;
          } catch (SQLException | RuntimeException e) {
            try {
              c.rollback();
            } catch (SQLException x) {
              e.addSuppressed(x);
            }
            throw e;
          }
        });
  }

  /**
   * Prepara SQL con marcadores ?. Asigna los valores por separado y convierte los enum a su
   * nombre.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param sql sentencia SQL con marcadores para los parámetros
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return sentencia preparada que deberá cerrarse tras su uso
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  protected static PreparedStatement preparar(Connection c, String sql, Object... args)
      throws SQLException {
    PreparedStatement p = c.prepareStatement(sql);
    try {
      for (int i = 0; i < args.length; i++)
        p.setObject(i + 1, args[i] instanceof Enum<?> ? ((Enum<?>) args[i]).name() : args[i]);
      return p;
    } catch (SQLException e) {
      p.close();
      throw e;
    }
  }

  /**
   * Ejecuta INSERT, UPDATE o DELETE y devuelve el número de filas afectadas.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param sql sentencia SQL con marcadores para los parámetros
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return cantidad de filas afectadas
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  protected static int cambiar(Connection c, String sql, Object... args) throws SQLException {
    try (PreparedStatement p = preparar(c, sql, args)) {
      return p.executeUpdate();
    }
  }

  /**
   * Ejecuta una inserción y obtiene su id generado en esta misma conexión; no busca el mayor id
   * global.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param sql sentencia SQL con marcadores para los parámetros
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return identificador generado en esta conexión
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  protected static long insertar(Connection c, String sql, Object... args) throws SQLException {
    cambiar(c, sql, args);
    try (Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
      rs.next();
      return rs.getLong(1);
    }
  }

  /**
   * Recorre el ResultSet y aplica el mapeador a cada fila; cierra consulta y resultado al
   * terminar.
   *
   * @param <T> tipo del resultado de la operación
   * @param c conexión abierta que se utiliza en esta operación
   * @param sql sentencia SQL con marcadores para los parámetros
   * @param m función que convierte la fila actual en un objeto
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  protected static <T> List<T> consultar(Connection c, String sql, Mapeador<T> m, Object... args)
      throws SQLException {
    try (PreparedStatement p = preparar(c, sql, args);
        ResultSet rs = p.executeQuery()) {
      List<T> lista = new ArrayList<>();
      while (rs.next()) lista.add(m.leer(rs));
      return lista;
    }
  }

  /**
   * Exige una fila afectada para detectar registros inexistentes, repetidos o con estado
   * incompatible.
   *
   * @param n cantidad de filas afectadas por la sentencia
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  protected static void exigirUno(int n) throws SQLException {
    if (n != 1) throw new SQLException("Registro inexistente o estado incompatible");
  }
}
