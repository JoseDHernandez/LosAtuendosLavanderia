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

/** El despacho confirma lote, solicitudes y prendas en una única transacción. */
public final class LavanderiaDAOSQLite extends SQLiteDAO implements ILavanderiaDAO {
  /**
   * Recibe el proveedor de conexiones por constructor, para usar la configuración de esta familia
   * SQLite.
   *
   * @param bd proveedor de conexiones de la familia SQLite
   */
  public LavanderiaDAOSQLite(SQLiteConnection bd) {
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
  SolicitudLavanderia mapear(Connection c, ResultSet r) throws SQLException {
    var d = new DevolucionDAOSQLite(bd).buscarPorId(r.getLong("id_devolucion")).orElseThrow();
    ILoteLavanderia lote = null;
    if (r.getObject("id_lote") != null)
      lote =
          consultar(
                  c,
                  "SELECT * FROM LOTE_LAVANDERIA WHERE id_lote=?",
                  x ->
                      new LoteLavanderia(
                          x.getLong("id_lote"),
                          LocalDate.parse(x.getString("fecha_envio")),
                          x.getInt("cupo"),
                          EstadoLote.valueOf(x.getString("estado"))),
                  r.getLong("id_lote"))
              .getFirst();
    return new SolicitudLavanderia(
        r.getLong("id_solicitud"),
        d,
        lote,
        Prioridad.valueOf(r.getString("prioridad")),
        LocalDate.parse(r.getString("fecha_recepcion")),
        EstadoSolicitud.valueOf(r.getString("estado")));
  }

  /**
   * Registra una solicitud pendiente y sin lote, vinculada a una devolución ya guardada.
   *
   * @param s solicitud de lavandería con los datos de la operación
   */
  @Override
  public void guardarSolicitud(ISolicitudLavanderia s) {
    if (s.getEstado() != EstadoSolicitud.PENDIENTE || s.getLote() != null)
      throw new IllegalArgumentException("La nueva solicitud debe estar pendiente y sin lote");
    ejecutar(
        c -> {
          insertar(
              c,
              "INSERT INTO SOLICITUD_LAVANDERIA(id_devolucion,fecha_recepcion,prioridad,estado)"
                  + " VALUES (?,?,?,?)",
              s.getDevolucion().getIdDevolucion(),
              s.getFechaRecepcion().toString(),
              s.getPrioridad(),
              s.getEstado());
          return null;
        });
  }

  /**
   * Lee solicitudes pendientes sin lote, ordenadas por fecha e id. Visualizar no equivale a
   * enviarlas.
   *
   * @return registros que cumplen la consulta, o una lista vacía si no hay coincidencias
   */
  @Override
  public List<ISolicitudLavanderia> consultarPendientes() {
    return ejecutar(
        c ->
            consultar(
                c,
                "SELECT * FROM SOLICITUD_LAVANDERIA WHERE estado='PENDIENTE' AND id_lote IS NULL"
                    + " ORDER BY fecha_recepcion,id_solicitud",
                r -> (ISolicitudLavanderia) mapear(c, r)));
  }

  /**
   * Guarda el nuevo estado por id. El servicio es responsable de validar la transición de
   * negocio.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @param e nuevo estado de la solicitud
   */
  @Override
  public void actualizarEstado(long id, EstadoSolicitud e) {
    ejecutar(
        c -> {
          exigirUno(
              cambiar(c, "UPDATE SOLICITUD_LAVANDERIA SET estado=? WHERE id_solicitud=?", e, id));
          return null;
        });
  }

  /**
   * Valida el cupo y confirma lote, solicitudes y prendas en una sola transacción. No selecciona
   * prioridades.
   *
   * @param solicitudes solicitudes seleccionadas para confirmar su envío
   * @param lote fecha y capacidad de la tanda que se enviará
   * @return lote persistido en estado enviado
   */
  @Override
  public ILoteLavanderia confirmarEnvio(
      List<ISolicitudLavanderia> solicitudes, ILoteLavanderia lote) {
    if (solicitudes == null || solicitudes.isEmpty() || solicitudes.size() > lote.getCupo())
      throw new IllegalArgumentException("Cantidad de prendas incompatible con el cupo");
    long id =
        transaccion(
            c -> {
              long nuevo =
                  insertar(
                      c,
                      "INSERT INTO LOTE_LAVANDERIA(fecha_envio,cupo,estado) VALUES (?,?,'ENVIADO')",
                      lote.getFechaEnvio().toString(),
                      lote.getCupo());
              // La condición del UPDATE impide enviar una solicitud ya procesada; cualquier fallo
              // revierte el lote completo.
              for (var s : solicitudes) {
                exigirUno(
                    cambiar(
                        c,
                        "UPDATE SOLICITUD_LAVANDERIA SET id_lote=?,estado='EN_LAVADO' WHERE"
                            + " id_solicitud=? AND estado='PENDIENTE' AND id_lote IS NULL",
                        nuevo,
                        s.getIdSolicitud()));
                cambiar(
                    c,
                    "UPDATE PRENDA SET estado='EN_LAVANDERIA' WHERE id_prenda=(SELECT d.id_prenda"
                        + " FROM DETALLE_ALQUILER d JOIN DEVOLUCION v USING(id_detalle) JOIN"
                        + " SOLICITUD_LAVANDERIA s USING(id_devolucion) WHERE s.id_solicitud=?)",
                    s.getIdSolicitud());
              }
              return nuevo;
            });
    return new LoteLavanderia(id, lote.getFechaEnvio(), lote.getCupo(), EstadoLote.ENVIADO);
  }

  /**
   * Localiza la solicitud de lavado de una devolución; permite evitar registros duplicados.
   *
   * @param id clave interna del registro indicado por el nombre del método
   * @return registro encontrado, o un Optional vacío si no existe
   */
  @Override
  public Optional<SolicitudLavanderia> buscarSolicitudPorDevolucion(long id) {
    return ejecutar(
        c ->
            consultar(
                    c,
                    "SELECT * FROM SOLICITUD_LAVANDERIA WHERE id_devolucion=?",
                    r -> mapear(c, r),
                    id)
                .stream()
                .findFirst());
  }

  /**
   * Modifica fecha y prioridad solo mientras la solicitud permanezca pendiente y sin lote.
   *
   * @param s solicitud de lavandería con los datos de la operación
   */
  @Override
  public void actualizarSolicitud(SolicitudLavanderia s) {
    ejecutar(
        c -> {
          exigirUno(
              cambiar(
                  c,
                  "UPDATE SOLICITUD_LAVANDERIA SET fecha_recepcion=?,prioridad=? WHERE"
                      + " id_solicitud=? AND estado='PENDIENTE' AND id_lote IS NULL",
                  s.getFechaRecepcion().toString(),
                  s.getPrioridad(),
                  s.getIdSolicitud()));
          return null;
        });
  }
}
