package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.alquiler.*;
import com.lavanderia.modelo.enums.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.lavanderia.*;
import com.lavanderia.modelo.prenda.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Persistencia de devolución con unicidad por detalle. */
public final class DevolucionDAOSQLite extends SQLiteDAO implements IDevolucionDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public DevolucionDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /**
   * Reconstruye un objeto Java a partir de la fila actual del resultado SQL; no guarda datos.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param r resultado SQL situado en la fila que se desea convertir
   * @return objeto reconstruido desde la fila actual
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  Devolucion mapear(Connection c, ResultSet r) throws SQLException {
    IDetalleAlquiler d =
        consultar(
                c,
                "SELECT * FROM DETALLE_ALQUILER WHERE id_detalle=?",
                x -> AlquilerDAOSQLite.detalle(c, x),
                r.getLong("id_detalle"))
            .getFirst();
    return new Devolucion(
        r.getLong("id_devolucion"),
        d,
        LocalDate.parse(r.getString("fecha_devolucion")),
        r.getString("observaciones"),
        EstadoRevision.valueOf(r.getString("estado_revision")));
  }

  /**
   * Reutiliza la consulta base, aplica un filtro SQL interno y transforma las filas en objetos.
   *
   * @param where filtro SQL interno que se añade a la consulta de devoluciones
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  List<Devolucion> lista(String where, Object... args) {
    return ejecutar(c -> consultar(c, "SELECT * FROM DEVOLUCION" + where, r -> mapear(c, r), args));
  }

  /**
   * Inserta el registro y sus datos relacionados. La transacción revierte todo si ocurre un
   * error.
   *
   * @param d devolución con los datos que se desean guardar o actualizar
   * @return objeto persistido con los identificadores generados
   */
  @Override
  public Devolucion guardar(Devolucion d) {
    long id =
        transaccion(
            c ->
                insertar(
                    c,
                    "INSERT INTO"
                        + " DEVOLUCION(id_detalle,fecha_devolucion,observaciones,estado_revision)"
                        + " VALUES (?,?,?,?)",
                    d.getDetalleAlquiler().getIdDetalle(),
                    d.getFechaDevolucion().toString(),
                    d.getObservaciones(),
                    d.getEstadoRevision()));
    return buscarPorId(id).orElseThrow();
  }

  /**
   * Comprueba si la línea de alquiler ya tiene una devolución registrada.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return true si existe el registro consultado; false en caso contrario
   */
  @Override
  public boolean existePorIdDetalle(long id) {
    return buscarPorIdDetalle(id).isPresent();
  }

  /**
   * Recupera la devolución mediante su clave primaria.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<Devolucion> buscarPorId(long id) {
    return lista(" WHERE id_devolucion=?", id).stream().findFirst();
  }

  /**
   * Recupera la devolución asociada a una línea concreta del alquiler.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<Devolucion> buscarPorIdDetalle(long id) {
    return lista(" WHERE id_detalle=?", id).stream().findFirst();
  }

  /**
   * Lee las devoluciones en orden de registro, sin alterar sus estados.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<Devolucion> listarDevoluciones() {
    /**
     * Ejecuta la operación descrita por su contrato.
     *
     * @param id_devolucion" dato requerido por la operación
     * @return resultado de la operación
     */
    return lista(" ORDER BY id_devolucion");
  }

  /**
   * Actualiza los datos del registro existente, sin crear un registro nuevo.
   *
   * @param d devolución con los datos que se desean guardar o actualizar
   */
  @Override
  public void actualizar(Devolucion d) {
    ejecutar(
        c -> {
          exigirUno(
              cambiar(
                  c,
                  "UPDATE DEVOLUCION SET fecha_devolucion=?,observaciones=?,estado_revision=? WHERE"
                      + " id_devolucion=?",
                  d.getFechaDevolucion().toString(),
                  d.getObservaciones(),
                  d.getEstadoRevision(),
                  d.getIdDevolucion()));
          return null;
        });
  }
}
