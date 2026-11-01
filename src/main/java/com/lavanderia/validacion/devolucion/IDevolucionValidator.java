package com.lavanderia.validacion.devolucion;

import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.validacion.ResultadoValidacion;

/**
 * Contrato de validador para inspecciones y devoluciones de prendas.
 * Comprueba que el detalle de alquiler, la fecha de retorno y el estado de revisión sean válidos.
 */
public interface IDevolucionValidator {

    /**
     * Valida la integridad del objeto de devolución.
     *
     * @param devolucion Instancia de {@link IDevolucion} a comprobar.
     * @return {@link ResultadoValidacion} con la devolución validada o con el mapa de errores.
     */
    ResultadoValidacion<IDevolucion> validar(IDevolucion devolucion);
}
