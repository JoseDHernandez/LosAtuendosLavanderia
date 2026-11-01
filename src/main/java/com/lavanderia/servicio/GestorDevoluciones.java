
package com.lavanderia.servicio;

import com.lavanderia.dao.interfaces.IDevolucionDAO;
import com.lavanderia.modelo.enums.EstadoRevision;
import com.lavanderia.modelo.enums.Prioridad;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.lavanderia.Devolucion;
import com.lavanderia.servicio.interfaces.IGestorDevoluciones;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.devolucion.IDevolucionValidator;

import java.util.List;
import java.util.Optional;

public class GestorDevoluciones implements IGestorDevoluciones {

    private final IDevolucionDAO devolucionDAO;
    private final IDevolucionValidator validador;
    private final IGestorLavanderia gestorLavanderia;

    public GestorDevoluciones(
            IDevolucionDAO devolucionDAO,
            IDevolucionValidator validador,
            IGestorLavanderia gestorLavanderia) {

        if (devolucionDAO == null
                || validador == null
                || gestorLavanderia == null) {

            throw new IllegalArgumentException(
                    "Los componentes del gestor no pueden ser nulos."
            );
        }

        this.devolucionDAO = devolucionDAO;
        this.validador = validador;
        this.gestorLavanderia = gestorLavanderia;
    }

    // --------------------------------------------------
    // REGISTRO DE DEVOLUCIONES
    // --------------------------------------------------

    @Override
    public IDevolucion registrarDevolucion(IDevolucion devolucion) {

        // 1. Validar los datos.
        IDevolucion devolucionValidada = validarDevolucion(devolucion);

        // 2. Comprobar que el detalle no tenga otra devolución.
        long idDetalle = devolucionValidada
                .getDetalleAlquiler()
                .getIdDetalle();

        if (devolucionDAO.existePorIdDetalle(idDetalle)) {
            throw new ValidacionException(
                    "Este detalle de alquiler ya tiene registrada una devolución."
            );
        }

        // 3. Construir y persistir la devolución.
        Devolucion nuevaDevolucion = new Devolucion(
                devolucionValidada.getDetalleAlquiler(),
                devolucionValidada.getFechaDevolucion(),
                devolucionValidada.getObservaciones(),
                devolucionValidada.getEstadoRevision()
        );

        nuevaDevolucion = devolucionDAO.guardar(nuevaDevolucion);

        // 4. Gestionar la solicitud de lavandería.
        gestionarSolicitudLavanderia(nuevaDevolucion);

        return nuevaDevolucion;
    }

    // --------------------------------------------------
    // ACTUALIZACIÓN DE DEVOLUCIONES
    // --------------------------------------------------

    @Override
    public IDevolucion actualizarDevolucion(
            long idDevolucion,
            IDevolucion devolucion) {

        // 1. Buscar la devolución existente.
        Devolucion actual = devolucionDAO
                .buscarPorId(idDevolucion)
                .orElseThrow(() -> new ValidacionException(
                        "No se encontró la devolución que desea actualizar."
                ));

        // 2. Validar los nuevos datos.
        IDevolucion devolucionValidada = validarDevolucion(devolucion);

        // 3. Impedir cambiar el detalle asociado.
        if (actual.getDetalleAlquiler().getIdDetalle()
                != devolucionValidada.getDetalleAlquiler().getIdDetalle()) {

            throw new ValidacionException(
                    "No se puede cambiar el detalle de alquiler de una devolución."
            );
        }

        // 4. Construir la devolución actualizada.
        Devolucion actualizada = new Devolucion(
                actual.getIdDevolucion(),
                actual.getDetalleAlquiler(),
                devolucionValidada.getFechaDevolucion(),
                devolucionValidada.getObservaciones(),
                devolucionValidada.getEstadoRevision()
        );

        // 5. Persistir los cambios.
        devolucionDAO.actualizar(actualizada);

        // 6. Sincronizar la solicitud de lavandería.
        gestionarSolicitudLavanderia(actualizada);

        return actualizada;
    }

    // --------------------------------------------------
    // VALIDACIÓN
    // --------------------------------------------------

    private IDevolucion validarDevolucion(IDevolucion devolucion) {

        ResultadoValidacion<IDevolucion> resultado =
                validador.validar(devolucion);

        return resultado.obtenerValor();
    }

    // --------------------------------------------------
    // GESTIÓN DE SOLICITUDES DE LAVANDERÍA
    // --------------------------------------------------

    private void gestionarSolicitudLavanderia(IDevolucion devolucion) {

        if (devolucion.getEstadoRevision() == EstadoRevision.CON_NOVEDAD) {

            gestorLavanderia.registrarOActualizarSolicitud(
                    devolucion,
                    Prioridad.NORMAL,
                    devolucion.getFechaDevolucion()
            );

        } else {

            gestorLavanderia.cancelarSolicitudPorDevolucion(
                    devolucion.getIdDevolucion()
            );
        }
    }

    // --------------------------------------------------
    // CONSULTAS
    // --------------------------------------------------

    @Override
    public Optional<IDevolucion> consultarPorId(long idDevolucion) {

        return devolucionDAO.buscarPorId(idDevolucion)
                .map(devolucion -> (IDevolucion) devolucion);
    }

    @Override
    public Optional<IDevolucion> consultarPorDetalle(long idDetalle) {

        return devolucionDAO.buscarPorIdDetalle(idDetalle)
                .map(devolucion -> (IDevolucion) devolucion);
    }

    @Override
    public List<IDevolucion> listarTodas() {

        return devolucionDAO.listarDevoluciones()
                .stream()
                .map(devolucion -> (IDevolucion) devolucion)
                .toList();
    }
}
