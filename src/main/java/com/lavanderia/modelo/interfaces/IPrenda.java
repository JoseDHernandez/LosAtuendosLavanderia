
package com.lavanderia.modelo.interfaces;

import com.lavanderia.modelo.enums.EstadoPrenda;

public interface IPrenda {

    long getIdPrenda();

    String getReferencia();

    String getColor();

    String getMarca();

    String getTalla();

    long getValorAlquiler();

    EstadoPrenda getEstado();

    void cambiarEstado(EstadoPrenda nuevoEstado);
}
