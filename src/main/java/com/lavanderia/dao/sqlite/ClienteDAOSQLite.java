package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.persona.*;
import java.sql.*;
import java.util.*;

/** Persistencia transaccional de persona y rol cliente. */
public final class ClienteDAOSQLite extends SQLiteDAO implements IClienteDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public ClienteDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /**
   * Reconstruye un objeto Java a partir de la fila actual del resultado SQL; no guarda datos.
   *
   * @param r resultado SQL situado en la fila que se desea convertir
   * @return objeto reconstruido desde la fila actual
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  static ICliente mapear(ResultSet r) throws SQLException {
    return new Cliente(
        r.getString("identificacion"),
        r.getString("nombre"),
        r.getString("direccion"),
        r.getString("telefono"),
        r.getString("correo"));
  }

  /**
   * Inserta el registro y sus datos relacionados. La transacción revierte todo si ocurre un
   * error.
   *
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void guardar(ICliente p) {
    transaccion(
        c -> {
          long id =
              insertar(
                  c,
                  "INSERT INTO PERSONA(identificacion,nombre,direccion,telefono,correo) VALUES"
                      + " (?,?,?,?,?)",
                  p.getIdentificacion(),
                  p.getNombre(),
                  p.getDireccion(),
                  p.getTelefono(),
                  p.getCorreo());
          cambiar(c, "INSERT INTO CLIENTE VALUES (?)", id);
          return null;
        });
  }

  /** Consulta base que reúne los datos necesarios para reconstruir la entidad. */
  static final String SELECT = "SELECT * FROM PERSONA JOIN CLIENTE USING(id_persona)";

  /**
   * Lee todos los registros de este tipo. Esta consulta no modifica la base de datos.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<ICliente> listarTodos() {
    return ejecutar(
        c -> consultar(c, SELECT + " ORDER BY nombre,identificacion", ClienteDAOSQLite::mapear));
  }

  /**
   * Busca por documento; devuelve Optional.empty() si no encuentra el registro.
   *
   * @param id documento de identificación de la persona
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<ICliente> buscarPorIdentificacion(String id) {
    return ejecutar(
        c ->
            consultar(c, SELECT + " WHERE identificacion=?", ClienteDAOSQLite::mapear, id).stream()
                .findFirst());
  }

  /**
   * Actualiza los datos del registro existente, sin crear un registro nuevo.
   *
   * @param anterior documento actual que identifica al registro antes de editarlo
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void actualizar(String anterior, ICliente p) {
    transaccion(
        c -> {
          exigirUno(
              cambiar(
                  c,
                  "UPDATE PERSONA SET identificacion=?,nombre=?,direccion=?,telefono=?,correo=?"
                      + " WHERE identificacion=? AND id_persona IN (SELECT id_persona FROM"
                      + " CLIENTE)",
                  p.getIdentificacion(),
                  p.getNombre(),
                  p.getDireccion(),
                  p.getTelefono(),
                  p.getCorreo(),
                  anterior));

          return null;
        });
  }
}
