package com.lavanderia.dao.sqlite;

import com.lavanderia.conexion.*;
import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.alquiler.*;
import com.lavanderia.modelo.enums.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.lavanderia.*;
import com.lavanderia.modelo.prenda.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Alquiler y detalles en una transacción, con bloqueo previo a comprobar disponibilidad. */
public final class AlquilerDAOSQLite extends SQLiteDAO implements IAlquilerDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public AlquilerDAOSQLite(SQLiteConnection bd) {
    super(bd);
  }

  /** Consulta base que reúne los datos necesarios para reconstruir la entidad. */
  static final String SELECT =
      "SELECT a.*,pc.identificacion cliente_doc,pe.identificacion empleado_doc FROM ALQUILER a JOIN"
          + " PERSONA pc ON pc.id_persona=a.id_cliente JOIN PERSONA pe ON"
          + " pe.id_persona=a.id_empleado";

  /**
   * Reconstruye un objeto Java a partir de la fila actual del resultado SQL; no guarda datos.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param r resultado SQL situado en la fila que se desea convertir
   * @return objeto reconstruido desde la fila actual
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  IAlquiler mapear(Connection c, ResultSet r) throws SQLException {
    var cliente =
        new ClienteDAOSQLite(bd).buscarPorIdentificacion(r.getString("cliente_doc")).orElseThrow();
    var empleado =
        new EmpleadoDAOSQLite(bd)
            .buscarPorIdentificacion(r.getString("empleado_doc"))
            .orElseThrow();
    var detalles =
        consultar(
            c,
            "SELECT * FROM DETALLE_ALQUILER WHERE id_alquiler=? ORDER BY id_detalle",
            d -> detalle(c, d),
            r.getLong("id_alquiler"));
    return new Alquiler(
        r.getLong("id_alquiler"),
        r.getInt("numero_consecutivo"),
        LocalDate.parse(r.getString("fecha_solicitud")),
        LocalDate.parse(r.getString("fecha_inicio")),
        LocalDate.parse(r.getString("fecha_fin_prevista")),
        cliente,
        empleado,
        EstadoAlquiler.valueOf(r.getString("estado")),
        detalles);
  }

  /**
   * Reconstruye una línea del alquiler con su prenda y su importe decimal exacto.
   *
   * @param c conexión abierta que se utiliza en esta operación
   * @param d resultado SQL situado en la fila del detalle que se reconstruye
   * @return detalle con su prenda y valor acordado
   * @throws SQLException si falla el acceso SQL o una comprobación de persistencia
   */
  static IDetalleAlquiler detalle(Connection c, ResultSet d) throws SQLException {
    IPrenda p =
        consultar(
                c,
                PrendaDAOSQLite.SELECT + " WHERE p.id_prenda=?",
                PrendaDAOSQLite::mapear,
                d.getLong("id_prenda"))
            .getFirst();
    return new DetalleAlquiler(
        d.getLong("id_detalle"), p, new BigDecimal(d.getString("valor_acordado")));
  }

  /**
   * Reutiliza la consulta base, aplica un filtro SQL interno y transforma las filas en objetos.
   *
   * @param condicion filtro SQL interno que se añade a la consulta base
   * @param args valores que se asignarán a los marcadores en el mismo orden
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  List<IAlquiler> lista(String condicion, Object... args) {
    return ejecutar(c -> consultar(c, SELECT + condicion, r -> mapear(c, r), args));
  }

  /**
   * Inserta el registro y sus datos relacionados. La transacción revierte todo si ocurre un
   * error.
   *
   * @param a servicio de alquiler que se registrará
   * @return objeto persistido con los identificadores generados
   */
  @Override
  public IAlquiler guardar(IAlquiler a) {
    long id =
        transaccion(
            c -> {
              // Adquirir bloqueo de escritura antes de leer disponibilidad y consecutivo.
              cambiar(c, "UPDATE ALQUILER SET estado=estado WHERE 0");
              if (a.getFechaFinPrevista().isBefore(a.getFechaInicio()))
                throw new SQLException("Rango de fechas inválido");
              // Calcula el consecutivo bajo el bloqueo de escritura; un fallo posterior también
              // revierte el registro.
              int numero =
                  consultar(
                          c,
                          "SELECT COALESCE(MAX(numero_consecutivo),0)+1 FROM ALQUILER",
                          r -> r.getInt(1))
                      .getFirst();
              // Resuelve los documentos a claves internas. NOT NULL y FOREIGN KEY rechazan personas
              // o roles no registrados.
              long nuevo =
                  insertar(
                      c,
                      "INSERT INTO"
                          + " ALQUILER(numero_consecutivo,fecha_solicitud,fecha_inicio,fecha_fin_prevista,id_cliente,id_empleado,estado)"
                          + " VALUES (?,?,?,?,(SELECT id_persona FROM PERSONA WHERE"
                          + " identificacion=?),(SELECT id_persona FROM PERSONA WHERE"
                          + " identificacion=?),?)",
                      numero,
                      LocalDate.now().toString(),
                      a.getFechaInicio().toString(),
                      a.getFechaFinPrevista().toString(),
                      a.getCliente().getIdentificacion(),
                      a.getEmpleado().getIdentificacion(),
                      a.getEstado());
              // Cada prenda se comprueba y se registra dentro de la misma transacción que la
              // cabecera.
              for (IDetalleAlquiler d : a.getDetalles()) {
                // No basta con recibir un objeto prenda: debe existir en el inventario y estar
                // disponible.
                long prenda =
                    consultar(
                            c,
                            "SELECT id_prenda FROM PRENDA WHERE referencia=? AND"
                                + " estado='DISPONIBLE'",
                            r -> r.getLong(1),
                            d.getPrenda().getReferencia())
                        .stream()
                        .findFirst()
                        .orElseThrow(
                            () -> new SQLException("Prenda no registrada o no disponible"));
                // Hay cruce si el inicio existente no supera el fin solicitado y el fin existente
                // no precede al inicio solicitado.
                if (!consultar(
                        c,
                        "SELECT 1 FROM DETALLE_ALQUILER d JOIN ALQUILER a USING(id_alquiler) WHERE"
                            + " d.id_prenda=? AND a.estado IN ('REGISTRADO','ACTIVO') AND"
                            + " a.fecha_inicio<=? AND a.fecha_fin_prevista>=?",
                        r -> 1,
                        prenda,
                        a.getFechaFinPrevista().toString(),
                        a.getFechaInicio().toString())
                    .isEmpty())
                  throw new SQLException("La prenda ya está reservada en ese intervalo");
                // Los importes no pueden ser negativos; toPlainString conserva el decimal sin
                // convertirlo a double.
                if (d.getValorAcordado().signum() < 0)
                  throw new SQLException("Valor acordado negativo");
                insertar(
                    c,
                    "INSERT INTO DETALLE_ALQUILER(id_alquiler,id_prenda,valor_acordado) VALUES"
                        + " (?,?,?)",
                    nuevo,
                    prenda,
                    d.getValorAcordado().toPlainString());
              }
              return nuevo;
            });
    return lista(" WHERE a.id_alquiler=?", id).getFirst();
  }

  /**
   * Busca por número de servicio, que es distinto del identificador interno de la tabla.
   *
   * @param n número consecutivo del servicio de alquiler
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<IAlquiler> buscarPorConsecutivo(int n) {
    return lista(" WHERE numero_consecutivo=?", n).stream().findFirst();
  }

  /**
   * Recupera el historial ordenado por fecha. El argumento es el id interno del cliente, no su
   * documento.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IAlquiler> consultarPorCliente(long id) {
    /**
     * Ejecuta la operación descrita por su contrato.
     *
     * @param fecha_inicio dato requerido por la operación
     * @param id_alquiler" dato requerido por la operación
     * @param id clave interna del registro indicado por el nombre del método
     * @return resultado de la operación
     */
    return lista(" WHERE id_cliente=? ORDER BY fecha_inicio,id_alquiler", id);
  }

  /**
   * Filtra servicios desde hoy en estados REGISTRADO o ACTIVO y los ordena por fecha.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IAlquiler> consultarVigentesPorCliente(long id) {
    return lista(
        " WHERE id_cliente=? AND fecha_inicio>=? AND a.estado IN ('REGISTRADO','ACTIVO') ORDER BY"
            + " fecha_inicio,id_alquiler",
        id,
        LocalDate.now().toString());
  }

  /**
   * Consulta servicios cuya fecha de inicio coincide exactamente con la fecha recibida.
   *
   * @param fecha fecha de inicio de los servicios que se consultan
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IAlquiler> consultarPorFecha(LocalDate fecha) {
    return lista(" WHERE fecha_inicio=? ORDER BY id_alquiler", fecha.toString());
  }

  /**
   * Detecta reservas activas que se cruzan con el intervalo inclusivo solicitado para una prenda.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @param inicio primer día del intervalo inclusivo que se consulta
   * @param fin último día del intervalo inclusivo que se consulta
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IAlquiler> consultarConflictos(long id, LocalDate inicio, LocalDate fin) {
    return lista(
        " WHERE a.estado IN ('REGISTRADO','ACTIVO') AND fecha_inicio<=? AND fecha_fin_prevista>=?"
            + " AND EXISTS(SELECT 1 FROM DETALLE_ALQUILER d WHERE d.id_alquiler=a.id_alquiler AND"
            + " d.id_prenda=?)",
        fin.toString(),
        inicio.toString(),
        id);
  }

  /**
   * Guarda el nuevo estado por id. El servicio es responsable de validar la transición de
   * negocio.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @param estado nuevo estado que se guardará
   */
  @Override
  public void actualizarEstado(long id, EstadoAlquiler estado) {
    ejecutar(
        c -> {
          exigirUno(cambiar(c, "UPDATE ALQUILER SET estado=? WHERE id_alquiler=?", estado, id));
          return null;
        });
  }

  /**
   * Lee todos los registros de este tipo. Esta consulta no modifica la base de datos.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<IAlquiler> listarTodos() {
    /**
     * Ejecuta la operación descrita por su contrato.
     *
     * @param id_alquiler" dato requerido por la operación
     * @return resultado de la operación
     */
    return lista(" ORDER BY id_alquiler");
  }
}
