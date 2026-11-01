package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IPrendaDAO;
import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.prenda.*;
import com.lavanderia.patrones.builder.*;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
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

        this.prendaDAO = prendaDAO;
        this.validador = validador;
    }

    @Override
    public IPrenda registrarVestido(Map<String, String> datos) {

        Map<String, String> limpio =
                validador.validarVestido(datos);

        verificarReferenciaDisponible(
                limpio.get("referencia")
        );

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

    @Override
    public IPrenda registrarTraje(Map<String, String> datos) {

        Map<String, String> limpio =
                validador.validarTraje(datos);

        verificarReferenciaDisponible(
                limpio.get("referencia")
        );

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

    @Override
    public IPrenda registrarDisfraz(Map<String, String> datos) {

        Map<String, String> limpio =
                validador.validarDisfraz(datos);

        verificarReferenciaDisponible(
                limpio.get("referencia")
        );

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

    private void verificarReferenciaDisponible(String referencia) {

        if (prendaDAO.existeReferencia(referencia)) {
            throw new IllegalArgumentException(
                    "Ya existe una prenda con la referencia: "
                            + referencia
            );
        }
    }

    @Override
    public Optional<IPrenda> buscarPorReferencia(String referencia) {
        return prendaDAO.buscarPorReferencia(referencia);
    }

    @Override
    public List<IPrenda> listarPrendas() {
        return prendaDAO.listarPrendas();
    }

    @Override
    public List<IPrenda> buscarPorTalla(String talla) {
        return prendaDAO.buscarPorTalla(talla);
    }
}