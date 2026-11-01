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

/**
 * Servicio de lógica de negocio para la gestión de devoluciones y peritaje de vestuario.
 * Implementa {@link IGestorDevoluciones}. Se encarga de validar recepciones, evitar devoluciones duplicadas
 * sobre un mismo detalle de alquiler, persistir las devoluciones mediante {@link IDevolucionDAO} y
 * coordinar la sincronización automática con {@link IGestorLavanderia} si la prenda presenta novedades.
 */
public class GestorDevoluciones implements IGestorDevoluciones {

    /** DAO para la persistencia y consulta de devoluciones. */
    private final IDevolucionDAO devolucionDAO;

    /** Validador de reglas de formato y negocio para devoluciones. */
    private final IDevolucionValidator validador;

    /** Gestor de lavandería para disparar órdenes de lavado cuando se registran novedades. */
    private final IGestorLavanderia gestorLavanderia;

    /**
     * Construye el gestor inyectando sus dependencias obligatorias.
     *
     * @param devolucionDAO DAO de devoluciones.
     * @param validador Validador de devoluciones.
     * @param gestorLavanderia Gestor de lavandería para coordinación de novedades.
     * @throws IllegalArgumentException Si alguno de los componentes suministrados es nulo.
     */
    public GestorDevoluciones(
            IDevolucionDAO devolucionDAO,
            IDevolucionValidator validador,
            IGestorLavanderia gestorLavanderia) {

        // Validar no nulidad de los componentes inyectados
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

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si los datos son inválidos o si el detalle ya tiene devolución registrada.
     */
    @Override
    public IDevolucion registrarDevolucion(IDevolucion devolucion) {

        // 1. Validar la integridad de los datos de la devolución
        IDevolucion devolucionValidada = validarDevolucion(devolucion);

        // 2. Comprobar que el ítem de detalle de alquiler no tenga ya una devolución asentada
        long idDetalle = devolucionValidada
                .getDetalleAlquiler()
                .getIdDetalle();

        if (devolucionDAO.existePorIdDetalle(idDetalle)) {
            throw new ValidacionException(
                    "Este detalle de alquiler ya tiene registrada una devolución."
            );
        }

        // 3. Construir la entidad de dominio y persistirla en la base de datos
        Devolucion nuevaDevolucion = new Devolucion(
                devolucionValidada.getDetalleAlquiler(),
                devolucionValidada.getFechaDevolucion(),
                devolucionValidada.getObservaciones(),
                devolucionValidada.getEstadoRevision()
        );

        nuevaDevolucion = devolucionDAO.guardar(nuevaDevolucion);

        // 4. Si la prenda se marcó con novedad, enviar solicitud a lavandería
        gestionarSolicitudLavanderia(nuevaDevolucion);

        return nuevaDevolucion;
    }

    // --------------------------------------------------
    // ACTUALIZACIÓN DE DEVOLUCIONES
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws ValidacionException Si la devolución no existe o si se intenta alterar el detalle de alquiler.
     */
    @Override
    public IDevolucion actualizarDevolucion(
            long idDevolucion,
            IDevolucion devolucion) {

        // 1. Buscar la devolución original existente
        Devolucion actual = devolucionDAO
                .buscarPorId(idDevolucion)
                .orElseThrow(() -> new ValidacionException(
                        "No se encontró la devolución que desea actualizar."
                ));

        // 2. Validar los nuevos datos de la devolución
        IDevolucion devolucionValidada = validarDevolucion(devolucion);

        // 3. Impedir cambiar la línea de detalle de alquiler asociada a la devolución
        if (actual.getDetalleAlquiler().getIdDetalle()
                != devolucionValidada.getDetalleAlquiler().getIdDetalle()) {

            throw new ValidacionException(
                    "No se puede cambiar el detalle de alquiler de una devolución."
            );
        }

        // 4. Construir el objeto de devolución actualizado
        Devolucion actualizada = new Devolucion(
                actual.getIdDevolucion(),
                actual.getDetalleAlquiler(),
                devolucionValidada.getFechaDevolucion(),
                devolucionValidada.getObservaciones(),
                devolucionValidada.getEstadoRevision()
        );

        // 5. Persistir los cambios en la base de datos
        devolucionDAO.actualizar(actualizada);

        // 6. Sincronizar el estado con el flujo de lavandería
        gestionarSolicitudLavanderia(actualizada);

        return actualizada;
    }

    // --------------------------------------------------
    // VALIDACIÓN
    // --------------------------------------------------

    /**
     * Valida la devolución a través del componente {@link IDevolucionValidator}.
     *
     * @param devolucion Datos de devolución a verificar.
     * @return Instancia validada de {@link IDevolucion}.
     * @throws ValidacionException Si ocurren fallos de validación.
     */
    private IDevolucion validarDevolucion(IDevolucion devolucion) {

        ResultadoValidacion<IDevolucion> resultado =
                validador.validar(devolucion);

        return resultado.obtenerValor();
    }

    // --------------------------------------------------
    // GESTIÓN DE SOLICITUDES DE LAVANDERÍA
    // --------------------------------------------------

    /**
     * Coordina el ciclo de vida de la prenda en lavandería según el dictamen técnico:
     * Si está CON_NOVEDAD, crea o actualiza la solicitud de lavado; de lo contrario, cancela solicitudes previas.
     *
     * @param devolucion Instancia de la devolución inspeccionada.
     */
    private void gestionarSolicitudLavanderia(IDevolucion devolucion) {

        if (devolucion.getEstadoRevision() == EstadoRevision.CON_NOVEDAD) {

            // Registrar o actualizar solicitud con prioridad estándar
            gestorLavanderia.registrarOActualizarSolicitud(
                    devolucion,
                    Prioridad.NORMAL,
                    devolucion.getFechaDevolucion()
            );

        } else {

            // Si no tiene novedades, descartar o cancelar cualquier solicitud de lavado asociada
            gestorLavanderia.cancelarSolicitudPorDevolucion(
                    devolucion.getIdDevolucion()
            );
        }
    }

    // --------------------------------------------------
    // CONSULTAS
    // --------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IDevolucion> consultarPorId(long idDevolucion) {

        // Buscar por clave primaria en el DAO
        return devolucionDAO.buscarPorId(idDevolucion)
                .map(devolucion -> (IDevolucion) devolucion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IDevolucion> consultarPorDetalle(long idDetalle) {

        // Buscar por detalle de alquiler
        return devolucionDAO.buscarPorIdDetalle(idDetalle)
                .map(devolucion -> (IDevolucion) devolucion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IDevolucion> listarTodas() {

        // Listar y mapear devoluciones a la interfaz genérica
        return devolucionDAO.listarDevoluciones()
                .stream()
                .map(devolucion -> (IDevolucion) devolucion)
                .toList();
    }
}
