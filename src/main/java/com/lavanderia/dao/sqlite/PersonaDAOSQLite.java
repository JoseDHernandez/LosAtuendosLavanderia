package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.persona.*;
import java.sql.*;
import java.util.*;

/** Consultas transversales sin duplicar la persistencia de cada rol. */
public final class PersonaDAOSQLite extends SQLiteDAO implements IPersonaDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public PersonaDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /**
   * Comprueba si el documento está ocupado; la sobrecarga permite excluir el documento que se
   * edita.
   *
   * @param id documento de identificación de la persona
   * @return true si existe el registro consultado; false en caso contrario
   */
  @Override
  public boolean existeIdentificacion(String id) {
    return existeIdentificacion(id, "");
  }

  /**
   * Comprueba si el documento está ocupado; la sobrecarga permite excluir el documento que se
   * edita.
   *
   * @param id documento de identificación de la persona
   * @param excluir documento que se excluye de la comprobación de duplicados
   * @return true si existe el registro consultado; false en caso contrario
   */
  @Override
  public boolean existeIdentificacion(String id, String excluir) {
    return ejecutar(
        c ->
            !consultar(
                    c,
                    "SELECT 1 FROM PERSONA WHERE identificacion=? AND identificacion<>?",
                    r -> 1,
                    id,
                    excluir)
                .isEmpty());
  }

  /**
   * Busca por documento; devuelve Optional.empty() si no encuentra el registro.
   *
   * @param id documento de identificación de la persona
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<IPersona> buscarPorIdentificacion(String id) {
    // Busca primero el rol cliente; si no existe, intenta empleado. No crea personas nuevas.
    Optional<ICliente> cliente = new ClienteDAOSQLite(bd).buscarPorIdentificacion(id);
    if (cliente.isPresent()) return Optional.of(cliente.get());
    return new EmpleadoDAOSQLite(bd).buscarPorIdentificacion(id).map(p -> (IPersona) p);
  }
}
