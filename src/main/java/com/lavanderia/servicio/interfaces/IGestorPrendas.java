package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IGestorPrendas {

    IPrenda registrarVestido(Map<String, String> datos);

    IPrenda registrarTraje(Map<String, String> datos);

    IPrenda registrarDisfraz(Map<String, String> datos);

    Optional<IPrenda> buscarPorReferencia(String referencia);

    List<IPrenda> listarPrendas();

    List<IPrenda> buscarPorTalla(String talla);
}