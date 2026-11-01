
package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoRevision;
import java.time.LocalDate;

public interface IDevolucion {

    long getIdDevolucion();

    IDetalleAlquiler getDetalleAlquiler();

    LocalDate getFechaDevolucion();

    String getObservaciones();

    EstadoRevision getEstadoRevision();
}
