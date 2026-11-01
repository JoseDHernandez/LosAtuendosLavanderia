
package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoLote;
import java.time.LocalDate;

public interface ILoteLavanderia {

    long getIdLote();

    LocalDate getFechaEnvio();

    int getCupo();

    EstadoLote getEstado();
}
