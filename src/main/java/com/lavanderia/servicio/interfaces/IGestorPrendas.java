package com.lavanderia.servicio.interfaces;

import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrato de servicio para la gestión del inventario y catálogo de prendas.
 * Proporciona métodos para registrar y actualizar prendas por tipología (vestidos, trajes, disfraces)
 * utilizando el patrón Builder, consultar por referencia o talla y listar el catálogo completo.
 */
public interface IGestorPrendas {

    /**
     * Valida y registra un nuevo Vestido de Dama en el inventario.
     *
     * @param datos Diccionario con especificaciones del vestido (referencia, color, marca, talla, valor, pedrería, largo, piezas).
     * @return Prenda registrada de tipo {@link IPrenda}.
     */
    IPrenda registrarVestido(Map<String, String> datos);

    /**
     * Valida y registra un nuevo Traje de Caballero en el inventario.
     *
     * @param datos Diccionario con especificaciones del traje (referencia, color, marca, talla, valor, tipoTraje, tipoComplemento).
     * @return Prenda registrada de tipo {@link IPrenda}.
     */
    IPrenda registrarTraje(Map<String, String> datos);

    /**
     * Valida y registra un nuevo Disfraz en el inventario.
     *
     * @param datos Diccionario con especificaciones del disfraz (referencia, color, marca, talla, valor, nombre).
     * @return Prenda registrada de tipo {@link IPrenda}.
     */
    IPrenda registrarDisfraz(Map<String, String> datos);

    /**
     * Busca una prenda en el catálogo por su código de referencia único.
     *
     * @param referencia Código de referencia a consultar.
     * @return {@link Optional} con la prenda encontrada.
     */
    Optional<IPrenda> buscarPorReferencia(String referencia);

    /**
     * Lista todas las prendas registradas en el inventario del negocio.
     *
     * @return Lista completa de prendas.
     */
    List<IPrenda> listarPrendas();

    /**
     * Busca y filtra prendas que correspondan a una talla específica.
     *
     * @param talla Talla a consultar.
     * @return Lista de prendas que coinciden con la talla.
     */
    List<IPrenda> buscarPorTalla(String talla);

    /**
     * Actualiza la información de un Vestido de Dama existente.
     *
     * @param referenciaActual Referencia actual de la prenda a modificar.
     * @param datos Nuevos datos del vestido.
     * @return Prenda actualizada.
     */
    IPrenda actualizarVestido(
            String referenciaActual,
            Map<String, String> datos
    );

    /**
     * Actualiza la información de un Traje de Caballero existente.
     *
     * @param referenciaActual Referencia actual del traje a modificar.
     * @param datos Nuevos datos del traje.
     * @return Prenda actualizada.
     */
    IPrenda actualizarTraje(
            String referenciaActual,
            Map<String, String> datos
    );

    /**
     * Actualiza la información de un Disfraz existente.
     *
     * @param referenciaActual Referencia actual del disfraz a modificar.
     * @param datos Nuevos datos del disfraz.
     * @return Prenda actualizada.
     */
    IPrenda actualizarDisfraz(
            String referenciaActual,
            Map<String, String> datos
    );
}
