package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.List;
import java.util.Optional;

public interface IPrendaDAO {

    void guardar(IPrenda prenda);

    boolean existeReferencia(String referencia);

    Optional<IPrenda> buscarPorReferencia(String referencia);

    List<IPrenda> listarPrendas();

    List<IPrenda> buscarPorTalla(String talla);
}