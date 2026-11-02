package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.alquiler.*;
import com.lavanderia.modelo.enums.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.lavanderia.*;
import com.lavanderia.modelo.prenda.*;
import java.sql.*;
import java.util.*;

/** Catálogo con tabla base y especialización; inserciones y cambios atómicos. */
public final class PrendaDAOSQLite extends SQLiteDAO implements IPrendaDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public PrendaDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /** Consulta base que reúne los datos necesarios para reconstruir la entidad. */
  static final String SELECT =
      "SELECT p.*,v.tiene_pedreria,v.largo,v.numero_piezas,t.tipo_traje,t.tipo_complemento,d.nombre"
          + " FROM PRENDA p LEFT JOIN VESTIDO_DAMA v USING(id_prenda) LEFT JOIN TRAJE_CABALLERO t"
          + " USING(id_prenda) LEFT JOIN DISFRAZ d USING(id_prenda)";

  /**
   * Reconstruye un objeto Java a partir de la fila actual del resultado SQL; no guarda datos.
   *
   * @param r resultado SQL situado en la fila que se desea convertir
   * @return objeto reconstruido desde la fila actual
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  static IPrenda mapear(ResultSet r) throws SQLException {
    long id = r.getLong("id_prenda"), valor = r.getLong("valor_alquiler");
    String ref = r.getString("referencia"),
        co = r.getString("color"),
        ma = r.getString("marca"),
        ta = r.getString("talla");
    EstadoPrenda e = EstadoPrenda.valueOf(r.getString("estado"));
    // Los LEFT JOIN dejan null las columnas ajenas al subtipo; se identifica así la clase que debe
    // reconstruirse.
    if (r.getObject("numero_piezas") != null)
      return new VestidoDama(
          id,
          ref,
          co,
          ma,
          ta,
          valor,
          e,
          r.getBoolean("tiene_pedreria"),
          r.getString("largo"),
          r.getInt("numero_piezas"));
    if (r.getString("tipo_traje") != null)
      return new TrajeCaballero(
          id,
          ref,
          co,
          ma,
          ta,
          valor,
          e,
          r.getString("tipo_traje"),
          r.getString("tipo_complemento"));
    if (r.getString("nombre") != null)
      return new Disfraz(id, ref, co, ma, ta, valor, e, r.getString("nombre"));
    throw new SQLException("Prenda sin especialización");
  }

  /**
   * Inserta los atributos del subtipo usando la misma conexión e id de PRENDA.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param id identificador interno o documento indicado en la descripción del método
   * @param p objeto con los datos que se desean persistir
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  static void especializar(Connection c, long id, IPrenda p) throws SQLException {
    if (p instanceof IVestidoDama v)
      cambiar(
          c,
          "INSERT INTO VESTIDO_DAMA VALUES (?,?,?,?)",
          id,
          v.isTienePedreria() ? 1 : 0,
          v.getLargo(),
          v.getNumeroPiezas());
    else if (p instanceof ITrajeCaballero t)
      cambiar(
          c,
          "INSERT INTO TRAJE_CABALLERO VALUES (?,?,?)",
          id,
          t.getTipoTraje(),
          t.getTipoComplemento());
    else if (p instanceof IDisfraz d)
      cambiar(c, "INSERT INTO DISFRAZ VALUES (?,?)", id, d.getNombre());
    else throw new SQLException("Tipo de prenda no soportado");
  }

  /**
   * Inserta el registro y sus datos relacionados. La transacción revierte todo si ocurre un
   * error.
   *
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void guardar(IPrenda p) {
    transaccion(
        c -> {
          long id =
              insertar(
                  c,
                  "INSERT INTO PRENDA(referencia,color,marca,talla,valor_alquiler,estado) VALUES"
                      + " (?,?,?,?,?,?)",
                  p.getReferencia(),
                  p.getColor(),
                  p.getMarca(),
                  p.getTalla(),
                  p.getValorAlquiler(),
                  p.getEstado());
          especializar(c, id, p);
          return null;
        });
  }

  /**
   * Indica si ya existe una prenda con esa referencia para evitar duplicados.
   *
   * @param ref referencia única de la prenda
   * @return true si existe el registro consultado; false en caso contrario
   */
  @Override
  public boolean existeReferencia(String ref) {
    return buscarPorReferencia(ref).isPresent();
  }

  /**
   * Recupera la prenda completa por su referencia única, incluyendo su subtipo.
   *
   * @param ref referencia única de la prenda
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<IPrenda> buscarPorReferencia(String ref) {
    return ejecutar(
        c ->
            consultar(c, SELECT + " WHERE referencia=?", PrendaDAOSQLite::mapear, ref).stream()
                .findFirst());
  }

  /**
   * Recupera el catálogo completo ordenado por referencia.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IPrenda> listarPrendas() {
    return ejecutar(c -> consultar(c, SELECT + " ORDER BY referencia", PrendaDAOSQLite::mapear));
  }

  /**
   * Filtra por talla; los objetos conservan su subtipo para que la vista pueda agruparlos.
   *
   * @param talla talla por la que se filtrará el catálogo
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IPrenda> buscarPorTalla(String talla) {
    return ejecutar(
        c ->
            consultar(
                c, SELECT + " WHERE talla=? ORDER BY referencia", PrendaDAOSQLite::mapear, talla));
  }

  /**
   * Actualiza los datos del registro existente, sin crear un registro nuevo.
   *
   * @param ref referencia única de la prenda
   * @param p objeto con los datos que se desean persistir
   */
  @Override
  public void actualizar(String ref, IPrenda p) {
    transaccion(
        c -> {
          long id =
              consultar(
                      c, "SELECT id_prenda FROM PRENDA WHERE referencia=?", r -> r.getLong(1), ref)
                  .stream()
                  .findFirst()
                  .orElseThrow(() -> new SQLException("Prenda inexistente"));
          exigirUno(
              cambiar(
                  c,
                  "UPDATE PRENDA SET referencia=?,color=?,marca=?,talla=?,valor_alquiler=?,estado=?"
                      + " WHERE id_prenda=?",
                  p.getReferencia(),
                  p.getColor(),
                  p.getMarca(),
                  p.getTalla(),
                  p.getValorAlquiler(),
                  p.getEstado(),
                  id));
          // Reemplaza solo la especialización; conserva el id base para mantener los vínculos de
          // los alquileres.
          for (String tabla : List.of("VESTIDO_DAMA", "TRAJE_CABALLERO", "DISFRAZ"))
            cambiar(c, "DELETE FROM " + tabla + " WHERE id_prenda=?", id);
          especializar(c, id, p);
          return null;
        });
  }
}
