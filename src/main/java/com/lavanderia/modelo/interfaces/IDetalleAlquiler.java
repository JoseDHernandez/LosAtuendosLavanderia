
package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.prenda.Prenda;
import java.math.BigDecimal;

public interface IDetalleAlquiler {

    long getIdDetalle();

    Prenda getPrenda();

    BigDecimal getValorAcordado();
}
