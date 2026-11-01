package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IPrendaDAO;
import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.prenda.VestidoDama;
import com.lavanderia.modelo.prenda.TrajeCaballero;
import com.lavanderia.modelo.prenda.Disfraz;
import com.lavanderia.patrones.builder.VestidoBuilder;
import com.lavanderia.patrones.builder.TrajeBuilder;
import com.lavanderia.patrones.builder.DisfrazBuilder;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.prenda.IPrendaValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la administración del catálogo e inventario de Prendas.
 * Implementa {@link IGestorPrendas}. Coordina la validación mediante {@link IPrendaValidator},
 * el control de referencias únicas en {@link IPrendaDAO}, la construcción polimórfica de prendas
 * utilizando el patrón Builder ({@link VestidoBuilder}, {@link TrajeBuilder}, {@link DisfrazBuilder})
 * y la actualización preservando el estado operativo actual.
 */
public class GestorPrendas implements IGestorPrendas {

    /** Repositorio de persistencia para el inventario de prendas. */
    private final IPrendaDAO prendaDAO;

    /** Validador de formato y consistencia de atributos para vestidos, trajes y disfraces. */
    private final IPrendaValidator validador;

    /**
     * Construye una nueva instancia de GestorPrendas inyectando sus dependencias.
     *
     * @param prendaDAO DAO de prendas.
     * @param validador Validador polimórfico de prendas.
     * @throws IllegalArgumentException Si alguno de los componentes es nulo.
     */
    public GestorPrendas(
            IPrendaDAO prendaDAO,
            IPrendaValidator validador) {

        // Validar no nulidad de dependencias inyectadas
        if (prendaDAO == null || validador == null) {
            throw new IllegalArgumentException(
                    "Los componentes del gestor no pueden ser nulos."
            );
        }

        this.prendaDAO = prendaDAO;
        this.validador = validador;
    }

    // --------------------------------------------------
    // REGISTRO DE VESTIDOS
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos son inválidos o la referencia ya existe.
     */
    @Override
    public IPrenda registrarVestido(Map<String, String> datos) {

        // 1. Validar campos requeridos y tipos para un vestido de dama
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarVestido(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 2. Verificar que la referencia alfanumérica esté disponible
        verificarReferenciaDisponible(limpio.get("referencia"));

        // 3. Ensamblar la prenda utilizando el patrón Builder
        VestidoDama vestido = new VestidoBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(
                        Long.parseLong(limpio.get("valorAlquiler"))
                )
                .conEstado(
                        EstadoPrenda.valueOf(limpio.get("estado"))
                )
                .conPedreria(
                        Boolean.parseBoolean(limpio.get("tienePedreria"))
                )
                .conLargo(limpio.get("largo"))
                .conNumeroPiezas(
                        Integer.parseInt(limpio.get("numeroPiezas"))
                )
                .build();

        // 4. Guardar en base de datos
        prendaDAO.guardar(vestido);

        return vestido;
    }

    // --------------------------------------------------
    // REGISTRO DE TRAJES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos fallan la validación o la referencia está ocupada.
     */
    @Override
    public IPrenda registrarTraje(Map<String, String> datos) {

        // 1. Validar campos específicos del traje de caballero
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarTraje(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 2. Verificar disponibilidad de referencia
        verificarReferenciaDisponible(limpio.get("referencia"));

        // 3. Construir traje con TrajeBuilder
        TrajeCaballero traje = new TrajeBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(
                        Long.parseLong(limpio.get("valorAlquiler"))
                )
                .conEstado(
                        EstadoPrenda.valueOf(limpio.get("estado"))
                )
                .conTipoTraje(limpio.get("tipoTraje"))
                .conTipoComplemento(limpio.get("tipoComplemento"))
                .build();

        // 4. Persistir en repositorio
        prendaDAO.guardar(traje);

        return traje;
    }

    // --------------------------------------------------
    // REGISTRO DE DISFRACES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos son inválidos o la referencia ya existe.
     */
    @Override
    public IPrenda registrarDisfraz(Map<String, String> datos) {

        // 1. Validar campos específicos del disfraz
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarDisfraz(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 2. Verificar disponibilidad de referencia
        verificarReferenciaDisponible(limpio.get("referencia"));

        // 3. Construir disfraz con DisfrazBuilder
        Disfraz disfraz = new DisfrazBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(
                        Long.parseLong(limpio.get("valorAlquiler"))
                )
                .conEstado(
                        EstadoPrenda.valueOf(limpio.get("estado"))
                )
                .conNombre(limpio.get("nombre"))
                .build();

        // 4. Persistir en base de datos
        prendaDAO.guardar(disfraz);

        return disfraz;
    }

    // ==========================================
    // ACTUALIZACIÓN DE VESTIDOS
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la prenda no existe, no es un vestido, o la nueva referencia ya existe.
     */
    @Override
    public IPrenda actualizarVestido(
            String referenciaActual,
            Map<String, String> datos) {

        // 1. Verificar existencia y subtipo de la prenda
        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.IVestidoDama)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un vestido."
            );
        }

        // 2. Validar nuevos datos
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarVestido(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 3. Verificar que la referencia esté disponible (excluyendo la actual)
        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

        // 4. Reconstruir con el builder preservando el estado operativo original
        VestidoDama vestido = new VestidoBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(Long.parseLong(limpio.get("valorAlquiler")))
                .conEstado(original.getEstado())
                .conPedreria(Boolean.parseBoolean(limpio.get("tienePedreria")))
                .conLargo(limpio.get("largo"))
                .conNumeroPiezas(Integer.parseInt(limpio.get("numeroPiezas")))
                .build();

        // 5. Actualizar en el DAO
        prendaDAO.actualizar(referenciaActual, vestido);

        return vestido;
    }

    // ==========================================
    // ACTUALIZACIÓN DE TRAJES
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la prenda no existe, no es un traje, o los datos son inválidos.
     */
    @Override
    public IPrenda actualizarTraje(
            String referenciaActual,
            Map<String, String> datos) {

        // 1. Verificar existencia previa
        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.ITrajeCaballero)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un traje."
            );
        }

        // 2. Validar datos
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarTraje(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 3. Validar disponibilidad de referencia
        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

        // 4. Construir traje actualizado preservando estado
        TrajeCaballero traje = new TrajeBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(Long.parseLong(limpio.get("valorAlquiler")))
                .conEstado(original.getEstado())
                .conTipoTraje(limpio.get("tipoTraje"))
                .conTipoComplemento(limpio.get("tipoComplemento"))
                .build();

        // 5. Actualizar en persistencia
        prendaDAO.actualizar(referenciaActual, traje);

        return traje;
    }

    // ==========================================
    // ACTUALIZACIÓN DE DISFRACES
    // ==========================================

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la prenda no existe, no es un disfraz, o la referencia está ocupada.
     */
    @Override
    public IPrenda actualizarDisfraz(
            String referenciaActual,
            Map<String, String> datos) {

        // 1. Obtener y validar tipo de prenda
        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.IDisfraz)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un disfraz."
            );
        }

        // 2. Validar nuevos datos
        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarDisfraz(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        // 3. Comprobar referencia
        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

        // 4. Construir disfraz actualizado
        Disfraz disfraz = new DisfrazBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(Long.parseLong(limpio.get("valorAlquiler")))
                .conEstado(original.getEstado())
                .conNombre(limpio.get("nombre"))
                .build();

        // 5. Persistir cambios
        prendaDAO.actualizar(referenciaActual, disfraz);

        return disfraz;
    }

    // ==========================================
    // VALIDACIONES PARA ACTUALIZACIÓN
    // ==========================================

    /**
     * Busca una prenda existente por su referencia o arroja {@link ValidacionException} si no existe.
     *
     * @param referencia Código de referencia.
     * @return Prenda encontrada.
     */
    private IPrenda obtenerPrendaExistente(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "La referencia original es obligatoria."
            );
        }

        return prendaDAO.buscarPorReferencia(referencia.trim())
                .orElseThrow(() -> new ValidacionException(
                        "No existe una prenda con la referencia: "
                                + referencia
                ));
    }

    /**
     * Comprueba disponibilidad de la referencia durante la edición, omitiendo la referencia actual.
     *
     * @param referencia Nueva referencia deseada.
     * @param referenciaExcluida Referencia actual de la prenda.
     */
    private void verificarReferenciaDisponible(
            String referencia,
            String referenciaExcluida) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "La referencia de la prenda es obligatoria."
            );
        }

        if (!referencia.trim().equals(referenciaExcluida.trim())
                && prendaDAO.existeReferencia(referencia.trim())) {

            throw new ValidacionException(
                    "Ya existe una prenda con la referencia: "
                            + referencia
            );
        }
    }

    // --------------------------------------------------
    // VALIDACIONES DE NEGOCIO
    // --------------------------------------------------

    /**
     * Comprueba que la referencia no esté registrada en ninguna prenda del catálogo.
     *
     * @param referencia Código de referencia a verificar.
     */
    private void verificarReferenciaDisponible(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "La referencia de la prenda es obligatoria."
            );
        }

        if (prendaDAO.existeReferencia(referencia)) {
            throw new ValidacionException(
                    "Ya existe una prenda con la referencia: " + referencia
            );
        }
    }

    // --------------------------------------------------
    // CONSULTAS
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la referencia es nula o vacía.
     */
    @Override
    public Optional<IPrenda> buscarPorReferencia(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "Debe proporcionar una referencia para realizar la búsqueda."
            );
        }

        return prendaDAO.buscarPorReferencia(referencia.trim());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IPrenda> listarPrendas() {
        return prendaDAO.listarPrendas();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la talla es nula o vacía.
     */
    @Override
    public List<IPrenda> buscarPorTalla(String talla) {

        if (talla == null || talla.isBlank()) {
            throw new ValidacionException(
                    "Debe proporcionar una talla para realizar la búsqueda."
            );
        }

        return prendaDAO.buscarPorTalla(talla.trim());
    }
}
