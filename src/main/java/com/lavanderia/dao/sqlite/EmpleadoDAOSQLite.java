package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.persona.*;
import java.sql.*;
import java.util.*;

/** Persistencia transaccional de persona y rol empleado. */
public final class EmpleadoDAOSQLite extends SQLiteDAO implements IEmpleadoDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public EmpleadoDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /**
   * Reconstruye un objeto Java a partir de la fila actual del resultado SQL; no guarda datos.
   *
   * @param r resultado SQL situado en la fila que se desea convertir
   * @return objeto reconstruido desde la fila actual
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  static IEmpleado mapear(ResultSet r) throws SQLException {
    return new Empleado(
        r.getString("identificacion"),
        r.getString("nombre"),
        r.getString("direccion"),
        r.getString("telefono"),
        r.getString("correo"),
        r.getString("cargo"));
  }

  /**
   * Inserta el registro y sus datos relacionados. La transacción revierte todo si ocurre un
   * error.
   *
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void guardar(IEmpleado p) {

      Objects.requireNonNull(p, "El empleado no puede ser nulo.");

      transaccion(c -> {

          long id = insertar(
                  c,
                  """
                  INSERT INTO PERSONA (
                      identificacion,
                      nombre,
                      direccion,
                      telefono,
                      correo
                  )
                  VALUES (?, ?, ?, ?, ?)
                  """,
                  p.getIdentificacion(),
                  p.getNombre(),
                  p.getDireccion(),
                  p.getTelefono(),
                  p.getCorreo()
          );

          int filas = cambiar(
                  c,
                  """
                  INSERT INTO EMPLEADO (id_persona, cargo)
                  VALUES (?, ?)
                  """,
                  id,
                  p.getCargo()
          );

          exigirUno(filas);

          return null;
      });
  }

  /** Consulta base que reúne los datos necesarios para reconstruir la entidad. */
  static final String SELECT = "SELECT * FROM PERSONA JOIN EMPLEADO USING(id_persona)";

  /**
   * Lee todos los registros de este tipo. Esta consulta no modifica la base de datos.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IEmpleado> listarTodos() {
    return ejecutar(
        c -> consultar(c, SELECT + " ORDER BY nombre,identificacion", EmpleadoDAOSQLite::mapear));
  }

  /**
   * Busca por documento; devuelve Optional.empty() si no encuentra el registro.
   *
   * @param id documento de identificación de la persona
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<IEmpleado> buscarPorIdentificacion(String id) {
    return ejecutar(
        c ->
            consultar(c, SELECT + " WHERE identificacion=?", EmpleadoDAOSQLite::mapear, id).stream()
                .findFirst());
  }

  /**
   * Actualiza los datos del registro existente, sin crear un registro nuevo.
   *
   * @param anterior documento actual que identifica al registro antes de editarlo
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void actualizar(String anterior, IEmpleado p) {
    transaccion(
        c -> {
          exigirUno(
              cambiar(
                  c,
                  "UPDATE PERSONA SET identificacion=?,nombre=?,direccion=?,telefono=?,correo=?"
                      + " WHERE identificacion=? AND id_persona IN (SELECT id_persona FROM"
                      + " EMPLEADO)",
                  p.getIdentificacion(),
                  p.getNombre(),
                  p.getDireccion(),
                  p.getTelefono(),
                  p.getCorreo(),
                  anterior));
          cambiar(
              c,
              "UPDATE EMPLEADO SET cargo=? WHERE id_persona=(SELECT id_persona FROM PERSONA WHERE"
                  + " identificacion=?)",
              p.getCargo(),
              p.getIdentificacion());
          return null;
        });
  }
}
