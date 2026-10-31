package com.lavanderia.validacion.lavanderia;

import java.util.Map;

public interface ISolicitudLavanderiaValidator {

    Map<String, String> validar(Map<String, String> datos);

    int validarCupo(String cupo);

    int validarCupo(int cupo);
}
