package com.lavanderia.conexion;

import static org.junit.jupiter.api.Assertions.*;

import com.lavanderia.modelo.alquiler.*;
import com.lavanderia.modelo.enums.*;
import com.lavanderia.modelo.interfaces.*;
import com.lavanderia.modelo.lavanderia.*;
import com.lavanderia.modelo.persona.*;
import com.lavanderia.modelo.prenda.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class SQLiteDAOFactoryTest {
  @TempDir Path dir;
  DAOFactory f;
  final LocalDate hoy = LocalDate.now();

  @BeforeEach
  void crear() {
    f = new SQLiteDAOFactory(dir.resolve("prueba.db"));
  }

  ICliente cliente() {
    return new Cliente("101", "Ana", "Calle 1", "3001234567", "ana@example.com");
  }

  IEmpleado empleado() {
    return new Empleado("202", "Luis", "Calle 2", "3001234568", "luis@example.com", "Asesor");
  }

  IPrenda prenda() {
    return new Disfraz("D1", "Rojo", "Marca", "M", 10000, EstadoPrenda.DISPONIBLE, "Pirata");
  }

  void datos() {
    f.getClienteDAO().guardar(cliente());
    f.getEmpleadoDAO().guardar(empleado());
    f.getPrendaDAO().guardar(prenda());
  }

  IAlquiler alquiler(LocalDate fecha) {
    return new Alquiler(
        fecha,
        fecha,
        cliente(),
        empleado(),
        List.of(new DetalleAlquiler(prenda(), new BigDecimal("10000.50"))));
  }

  /**
   * Verifica los siete productos y que los datos sobrevivan a crear otra fábrica sobre el mismo
   * archivo.
   */
  @Test
  void familiaYReapertura() {
    assertNotNull(f.getPersonaDAO());
    assertNotNull(f.getClienteDAO());
    assertNotNull(f.getEmpleadoDAO());
    assertNotNull(f.getPrendaDAO());
    assertNotNull(f.getAlquilerDAO());
    assertNotNull(f.getDevolucionDAO());
    assertNotNull(f.getLavanderiaDAO());
    f.getClienteDAO().guardar(cliente());
    DAOFactory otra = new SQLiteDAOFactory(dir.resolve("prueba.db"));
    assertEquals(
        "Ana", otra.getClienteDAO().buscarPorIdentificacion("101").orElseThrow().getNombre());
  }

  /**
   * Verifica unicidad, actualización y ausencia de una PERSONA huérfana si falla el rol empleado.
   */
  @Test
  void personasDuplicadosYRollbackDeEspecializacion() {
    f.getClienteDAO().guardar(cliente());
    assertThrows(PersistenciaException.class, () -> f.getClienteDAO().guardar(cliente()));
    assertEquals(1, f.getClienteDAO().listarTodos().size());
    assertThrows(
        PersistenciaException.class,
        () ->
            f.getEmpleadoDAO()
                .guardar(new Empleado("303", "X", "Dir", "300", "x@example.com", null)));
    assertFalse(f.getPersonaDAO().existeIdentificacion("303"));
    f.getClienteDAO()
        .actualizar("101", new Cliente("104", "Ana María", "Dir", "300", "a@example.com"));
    assertFalse(f.getPersonaDAO().existeIdentificacion("101"));
    assertTrue(f.getPersonaDAO().existeIdentificacion("104"));
  }

  /**
   * Comprueba que cada prenda vuelve a Java con su subtipo y que se rechazan referencias repetidas.
   */
  @Test
  void subtiposPrendas() {
    f.getPrendaDAO().guardar(prenda());
    f.getPrendaDAO()
        .guardar(
            new VestidoDama(
                "V1", "Azul", "Marca", "M", 20000, EstadoPrenda.DISPONIBLE, true, "Largo", 2));
    f.getPrendaDAO()
        .guardar(
            new TrajeCaballero(
                "T1", "Negro", "Marca", "L", 30000, EstadoPrenda.DISPONIBLE, "Frac", "Corbatín"));
    assertInstanceOf(IVestidoDama.class, f.getPrendaDAO().buscarPorReferencia("V1").orElseThrow());
    assertInstanceOf(
        ITrajeCaballero.class, f.getPrendaDAO().buscarPorReferencia("T1").orElseThrow());
    assertEquals(2, f.getPrendaDAO().buscarPorTalla("M").size());
    assertThrows(PersistenciaException.class, () -> f.getPrendaDAO().guardar(prenda()));
  }

  /** Comprueba consecutivos, valores decimales y rechazo de reservas en fechas cruzadas. */
  @Test
  void alquilerDisponibilidadConsultasYConsecutivo() {
    datos();
    var a = f.getAlquilerDAO().guardar(alquiler(hoy));
    assertEquals(1, a.getNumeroConsecutivo());
    assertTrue(a.getDetalles().getFirst().getIdDetalle() > 0);
    assertEquals(new BigDecimal("10000.50"), a.getDetalles().getFirst().getValorAcordado());
    assertThrows(PersistenciaException.class, () -> f.getAlquilerDAO().guardar(alquiler(hoy)));
    assertEquals(1, f.getAlquilerDAO().listarTodos().size());
    var b = f.getAlquilerDAO().guardar(alquiler(hoy.plusDays(1)));
    assertEquals(2, b.getNumeroConsecutivo());
    assertEquals(1, f.getAlquilerDAO().consultarPorFecha(hoy).size());
    assertEquals(2, f.getAlquilerDAO().consultarVigentesPorCliente(1).size());
    assertEquals(
        1,
        f.getAlquilerDAO()
            .consultarConflictos(a.getDetalles().getFirst().getPrenda().getIdPrenda(), hoy, hoy)
            .size());
  }

  /**
   * Comprueba que un detalle inválido deshace la cabecera y los detalles insertados previamente.
   */
  @Test
  void rechazoDeReferenciasYRollbackDeDetalles() {
    assertThrows(PersistenciaException.class, () -> f.getAlquilerDAO().guardar(alquiler(hoy)));
    datos();
    var noRegistrada = new Disfraz("NO", "Rojo", "M", "M", 100, EstadoPrenda.DISPONIBLE, "X");
    var a =
        new Alquiler(
            hoy,
            hoy,
            cliente(),
            empleado(),
            List.of(
                new DetalleAlquiler(prenda(), BigDecimal.TEN),
                new DetalleAlquiler(noRegistrada, BigDecimal.TEN)));
    assertThrows(PersistenciaException.class, () -> f.getAlquilerDAO().guardar(a));
    assertTrue(f.getAlquilerDAO().listarTodos().isEmpty());
  }

  /**
   * Comprueba que consultar no envía y que un despacho fallido revierte solicitudes, lote y estado
   * de prendas.
   */
  @Test
  void devolucionLavanderiaLecturaYEnvioAtomico() {
    datos();
    var a = f.getAlquilerDAO().guardar(alquiler(hoy));
    var d =
        f.getDevolucionDAO()
            .guardar(
                new Devolucion(
                    a.getDetalles().getFirst(), hoy, "Mancha", EstadoRevision.CON_NOVEDAD));
    assertThrows(PersistenciaException.class, () -> f.getDevolucionDAO().guardar(d));
    f.getLavanderiaDAO().guardarSolicitud(new SolicitudLavanderia(d, Prioridad.MANCHADA, hoy));
    var pendientes = f.getLavanderiaDAO().consultarPendientes();
    assertEquals(1, pendientes.size());
    assertEquals(1, f.getLavanderiaDAO().consultarPendientes().size());
    var lote = new LoteLavanderia(hoy, 2, EstadoLote.ABIERTO);
    assertThrows(
        PersistenciaException.class,
        () ->
            f.getLavanderiaDAO()
                .confirmarEnvio(List.of(pendientes.getFirst(), pendientes.getFirst()), lote));
    assertEquals(1, f.getLavanderiaDAO().consultarPendientes().size());
    assertEquals(
        EstadoPrenda.DISPONIBLE,
        f.getPrendaDAO().buscarPorReferencia("D1").orElseThrow().getEstado());
    var enviado = f.getLavanderiaDAO().confirmarEnvio(pendientes, lote);
    assertEquals(EstadoLote.ENVIADO, enviado.getEstado());
    assertTrue(f.getLavanderiaDAO().consultarPendientes().isEmpty());
    assertEquals(
        EstadoPrenda.EN_LAVANDERIA,
        f.getPrendaDAO().buscarPorReferencia("D1").orElseThrow().getEstado());
  }
}
