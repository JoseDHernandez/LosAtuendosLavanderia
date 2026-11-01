package com.lavanderia.validacion.prenda;

import java.util.Map;

public interface IPrendaValidator {

    Map<String, String> validarVestido(
            Map<String, String> datos
    );

    Map<String, String> validarTraje(
            Map<String, String> datos
    );

    Map<String, String> validarDisfraz(
            Map<String, String> datos
    );
}