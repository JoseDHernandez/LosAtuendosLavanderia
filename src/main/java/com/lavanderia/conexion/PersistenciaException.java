package com.lavanderia.conexion;

/** Error de infraestructura; conserva la causa para el registro técnico. */
public class PersistenciaException extends RuntimeException {
  /**
   * Comunica un fallo de persistencia conservando la excepción original para su diagnóstico.
   *
   * @param mensaje descripción del fallo de persistencia
   * @param causa excepción original que produjo el fallo
   */
  public PersistenciaException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
