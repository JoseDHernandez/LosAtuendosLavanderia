package com.lavanderia.validacion.devolucion;

import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.validacion.ResultadoValidacion;

public interface IDevolucionValidator {

    ResultadoValidacion<IDevolucion> validar(IDevolucion devolucion);
}