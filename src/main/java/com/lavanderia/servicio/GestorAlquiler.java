package com.lavanderia.servicio;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.dao.interfaces.IAlquilerDAO;
import com.lavanderia.dao.interfaces.IClienteDAO;
import com.lavanderia.dao.interfaces.IEmpleadoDAO;
import com.lavanderia.dao.interfaces.IPrendaDAO;
import com.lavanderia.validacion.alquiler.IAlquilerValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GestorAlquiler implements IGestorAlquiler {

    private final IAlquilerDAO alquilerDAO;
    private final IClienteDAO clienteDAO;
    private final IEmpleadoDAO empleadoDAO;
    private final IPrendaDAO prendaDAO;
    private final IAlquilerValidator validator;

    public GestorAlquiler(
            IAlquilerDAO alquilerDAO,
            IClienteDAO clienteDAO,
            IEmpleadoDAO empleadoDAO,
            IPrendaDAO prendaDAO,
            IAlquilerValidator validator) {

        this.alquilerDAO = alquilerDAO;
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
        this.prendaDAO = prendaDAO;
        this.validator = validator;
    }
    @Override
    public IAlquiler registrarAlquiler(IAlquiler alquiler) {
        return null;
    }

    @Override
    public Optional<IAlquiler> consultarPorConsecutivo(int consecutivo) {
        return Optional.empty();
    }

    @Override
    public List<IAlquiler> consultarPorCliente(long idCliente) {
        return List.of();
    }

    @Override
    public List<IAlquiler> consultarPorFecha(LocalDate fecha) {
        return List.of();
    }

    @Override
    public boolean verificarDisponibilidad(
            List<Long> idsPrendas,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return false;
    }

    @Override
    public void cambiarEstado(
            long idAlquiler,
            EstadoAlquiler estado) {
        // Pendiente de implementación.
    }
    // Implementación de operaciones
}