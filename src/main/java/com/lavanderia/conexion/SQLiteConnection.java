package com.lavanderia.conexion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;

/** Proveedor sin conexión global compartida. Cada operación cierra sus recursos. */
public final class SQLiteConnection {
  /** Ruta absoluta del archivo donde SQLite conserva la información. */
  private final Path archivo;

  /**
   * Define el archivo SQLite; la ruta absoluta evita depender de cambios posteriores del
   * directorio.
   */
  public SQLiteConnection() {
    this(Path.of("database/database.db"));
  }

  /**
   * Define el archivo SQLite; la ruta absoluta evita depender de cambios posteriores del
   * directorio.
   *
   * @param archivo ruta del archivo SQLite que se utilizará
   */
  public SQLiteConnection(Path archivo) {
    this.archivo = archivo.toAbsolutePath();
  }

  /**
   * Abre una conexión JDBC nueva. El código que la recibe debe cerrarla con try-with-resources.
   *
   * @return conexión nueva que debe cerrar quien la utiliza
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  public Connection getConnection() throws SQLException {
    try {
      Files.createDirectories(archivo.getParent());
    } catch (IOException e) {
      throw new SQLException("No se pudo crear el directorio de datos", e);
    }
    Connection c = DriverManager.getConnection("jdbc:sqlite:" + archivo);
    try (Statement st = c.createStatement()) {
      // Activar las relaciones entre tablas en cada conexión y esperar hasta 5 segundos si SQLite
      // está ocupado.
      st.execute("PRAGMA foreign_keys = ON");
      st.execute("PRAGMA busy_timeout = 5000");
    } catch (SQLException e) {
      c.close();
      throw e;
    }
    return c;
  }

  /**
   * Crea tablas en una base nueva; no elimina ni migra tablas existentes.
   *
   * @throws PersistenciaException si no es posible crear o inicializar la base de datos
   */
  public void inicializar() {
    try (Connection c = getConnection();
        InputStream in = getClass().getResourceAsStream("/database/schema.sql")) {
      if (in == null) throw new IOException("Falta database/schema.sql en los recursos");
      // Desactivar la confirmación automática permite deshacer todas las escrituras de esta
      // operación.
      c.setAutoCommit(false);
      try (Statement st = c.createStatement()) {
        for (String sql : new String(in.readAllBytes(), StandardCharsets.UTF_8).split(";"))
          if (!sql.isBlank()) st.execute(sql);
        c.commit();
      } catch (SQLException e) {
        c.rollback();
        throw e;
      }
    } catch (SQLException | IOException e) {
      throw new PersistenciaException("No se pudo inicializar SQLite", e);
    }
  }
}
