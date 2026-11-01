
package com.lavanderia.modelo.interfaces;

import java.math.BigDecimal;

public interface IDetalleAlquiler {

    long getIdDetalle();

    IPrenda getPrenda();

    BigDecimal getValorAcordado();
}
