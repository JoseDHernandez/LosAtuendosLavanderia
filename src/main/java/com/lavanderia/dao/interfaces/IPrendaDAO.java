package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IPrenda;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para el catálogo e inventario de prendas.
 * Define operaciones para persistir prendas (vestidos, trajes, disfraces),
 * validar códigos de referencia, buscar por talla o referencia y actualizar información.
 */
public interface IPrendaDAO {

    /**
     * Persiste una nueva prenda en el inventario de la base de datos.
     *
     * @param prenda Prenda a almacenar.
     */
    void guardar(IPrenda prenda);

    /**
     * Comprueba si una referencia alfanumérica de prenda ya existe en el catálogo.
     *
     * @param referencia Código de referencia a verificar.
     * @return {@code true} si la referencia ya está en uso, {@code false} si está libre.
     */
    boolean existeReferencia(String referencia);

    /**
     * Busca una prenda específica por su código de referencia único.
     *
     * @param referencia Código de referencia de la prenda.
     * @return Un {@link Optional} con la prenda encontrada o vacío si no existe.
     */
    Optional<IPrenda> buscarPorReferencia(String referencia);

    /**
     * Lista todas las prendas registradas en el inventario.
     *
     * @return Lista completa de prendas.
     */
    List<IPrenda> listarPrendas();

    /**
     * Filtra y lista prendas según su talla (ej. XS, S, M, L, XL, etc.).
     *
     * @param talla Talla de las prendas a buscar.
     * @return Lista de prendas que coinciden con la talla indicada.
     */
    List<IPrenda> buscarPorTalla(String talla);

    /**
     * Actualiza la información de una prenda identificada por su referencia actual.
     *
     * @param referenciaActual Código de referencia actual de la prenda a modificar.
     * @param prenda Nuevos datos de la prenda.
     */
    void actualizar(String referenciaActual, IPrenda prenda);
}
