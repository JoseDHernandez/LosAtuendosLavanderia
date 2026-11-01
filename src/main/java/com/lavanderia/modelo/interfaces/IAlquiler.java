
package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import java.time.LocalDate;
import java.util.List;

public interface IAlquiler {

    long getIdAlquiler();

    int getNumeroConsecutivo();

    LocalDate getFechaSolicitud();

    LocalDate getFechaInicio();

    LocalDate getFechaFinPrevista();

    ICliente getCliente();

    IEmpleado getEmpleado();

    EstadoAlquiler getEstado();

    List<IDetalleAlquiler> getDetalles();

    void cambiarEstado(EstadoAlquiler nuevoEstado);
}
