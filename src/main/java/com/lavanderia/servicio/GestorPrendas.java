
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

public class GestorPrendas implements IGestorPrendas {

    private final IPrendaDAO prendaDAO;
    private final IPrendaValidator validador;

    public GestorPrendas(
            IPrendaDAO prendaDAO,
            IPrendaValidator validador) {

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

    @Override
    public IPrenda registrarVestido(Map<String, String> datos) {

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarVestido(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(limpio.get("referencia"));

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

        prendaDAO.guardar(vestido);

        return vestido;
    }

    // --------------------------------------------------
    // REGISTRO DE TRAJES
    // --------------------------------------------------

    @Override
    public IPrenda registrarTraje(Map<String, String> datos) {

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarTraje(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(limpio.get("referencia"));

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

        prendaDAO.guardar(traje);

        return traje;
    }

    // --------------------------------------------------
    // REGISTRO DE DISFRACES
    // --------------------------------------------------

    @Override
    public IPrenda registrarDisfraz(Map<String, String> datos) {

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarDisfraz(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(limpio.get("referencia"));

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

        prendaDAO.guardar(disfraz);

        return disfraz;
    }


    // ==========================================
    // ACTUALIZACIÓN DE VESTIDOS
    // ==========================================

    @Override
    public IPrenda actualizarVestido(
            String referenciaActual,
            Map<String, String> datos) {

        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.IVestidoDama)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un vestido."
            );
        }

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarVestido(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

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

        prendaDAO.actualizar(referenciaActual, vestido);

        return vestido;
    }

    // ==========================================
    // ACTUALIZACIÓN DE TRAJES
    // ==========================================

    @Override
    public IPrenda actualizarTraje(
            String referenciaActual,
            Map<String, String> datos) {

        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.ITrajeCaballero)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un traje."
            );
        }

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarTraje(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

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

        prendaDAO.actualizar(referenciaActual, traje);

        return traje;
    }

    // ==========================================
    // ACTUALIZACIÓN DE DISFRACES
    // ==========================================

    @Override
    public IPrenda actualizarDisfraz(
            String referenciaActual,
            Map<String, String> datos) {

        IPrenda original = obtenerPrendaExistente(referenciaActual);

        if (!(original instanceof com.lavanderia.modelo.interfaces.IDisfraz)) {
            throw new ValidacionException(
                    "La prenda seleccionada no es un disfraz."
            );
        }

        ResultadoValidacion<Map<String, String>> resultado =
                validador.validarDisfraz(datos);

        Map<String, String> limpio = resultado.obtenerValor();

        verificarReferenciaDisponible(
                limpio.get("referencia"),
                referenciaActual
        );

        Disfraz disfraz = new DisfrazBuilder()
                .conReferencia(limpio.get("referencia"))
                .conColor(limpio.get("color"))
                .conMarca(limpio.get("marca"))
                .conTalla(limpio.get("talla"))
                .conValorAlquiler(Long.parseLong(limpio.get("valorAlquiler")))
                .conEstado(original.getEstado())
                .conNombre(limpio.get("nombre"))
                .build();

        prendaDAO.actualizar(referenciaActual, disfraz);

        return disfraz;
    }

    // ==========================================
    // VALIDACIONES PARA ACTUALIZACIÓN
    // ==========================================

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

    @Override
    public Optional<IPrenda> buscarPorReferencia(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            throw new ValidacionException(
                    "Debe proporcionar una referencia para realizar la búsqueda."
            );
        }

        return prendaDAO.buscarPorReferencia(referencia.trim());
    }

    @Override
    public List<IPrenda> listarPrendas() {
        return prendaDAO.listarPrendas();
    }

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
