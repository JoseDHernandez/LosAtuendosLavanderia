
package com.lavanderia.vista.paneles.devoluciones;

import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorDevoluciones;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelModulo;
import com.lavanderia.vista.paneles.comunes.PanelTabla;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PanelDevoluciones extends PanelModulo {

    private final ISistemaFacade sistemaFacade;
    private final IGestorAlquiler gestorAlquiler;
    private final IGestorDevoluciones gestorDevoluciones;

    private final PanelTabla panelTabla;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;
    private final BotonSecundario botonActualizar;

    private List<IDevolucion> devoluciones;
    private List<IAlquiler> alquileres;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelDevoluciones(ISistemaFacade sistemaFacade) {

        super(
                "devoluciones",
                "Devoluciones",
                "Consulta y administra las devoluciones de prendas.",
                IconosUI.lavanderia()
        );

        this.sistemaFacade = Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorAlquiler = Objects.requireNonNull(
                sistemaFacade.getGestorAlquiler(),
                "El gestor de alquileres es obligatorio."
        );

        this.gestorDevoluciones = Objects.requireNonNull(
                sistemaFacade.getGestorDevoluciones(),
                "El gestor de devoluciones es obligatorio."
        );

        this.devoluciones = new ArrayList<>();
        this.alquileres = new ArrayList<>();

        this.panelTabla = new PanelTabla(
                "Buscar por devolución, alquiler, cliente o prenda..."
        );

        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario(
                "Editar devolución"
        );

        this.botonActualizar = new BotonSecundario(
                "Actualizar"
        );

        inicializarComponentes();
        configurarEventos();
        cargarDevoluciones();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        // Acción principal del módulo.
        configurarAccion(
                "Nueva devolución",
                e -> abrirFormularioRegistro()
        );

        // Configuración de la tabla.
        panelTabla.setModelo(modeloTabla);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        // Acciones secundarias.
        botonEditar.configurarIcono(
                IconosUI.editar()
        );

        botonActualizar.configurarIcono(
                IconosUI.actualizar()
        );

        botonEditar.setEnabled(false);

        panelTabla.agregarAccion(botonEditar);
        panelTabla.agregarAccion(botonActualizar);

        agregarContenido(panelTabla);
    }

    // ==========================================
    // CONFIGURACIÓN DE EVENTOS
    // ==========================================

    private void configurarEventos() {

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        botonActualizar.addActionListener(
                e -> cargarDevoluciones()
        );

        panelTabla.getCampoBusqueda()
                .addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }
                });

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        tabla.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                actualizarEstadoAcciones();
            }
        });

        // Doble clic para editar.
        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            abrirFormularioEdicion();
                        }
                    }
                }
        );
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "ID devolución",
                        "Alquiler",
                        "Referencia",
                        "Cliente",
                        "Fecha devolución",
                        "Estado revisión",
                        "Observaciones"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };
    }

    // ==========================================
    // CARGAR INFORMACIÓN
    // ==========================================

    private void cargarDevoluciones() {

        try {

            List<IAlquiler> alquileresConsultados =
                    gestorAlquiler.listarTodos();

            List<IDevolucion> devolucionesConsultadas =
                    gestorDevoluciones.listarTodas();

            if (alquileresConsultados == null
                    || devolucionesConsultadas == null) {

                throw new IllegalStateException(
                        "No fue posible obtener la información de devoluciones."
                );
            }

            alquileres = new ArrayList<>(
                    alquileresConsultados
            );

            devoluciones = new ArrayList<>(
                    devolucionesConsultadas
            );

            filtrarDevoluciones();

        } catch (RuntimeException ex) {

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible cargar las devoluciones.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // FILTRAR DEVOLUCIONES
    // ==========================================

    private void filtrarDevoluciones() {

        String texto = panelTabla.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(devoluciones);
            return;
        }

        List<IDevolucion> resultados = devoluciones.stream()
                .filter(Objects::nonNull)
                .filter(devolucion -> coincideConBusqueda(
                        devolucion,
                        texto
                ))
                .toList();

        actualizarTabla(resultados);
    }

    private boolean coincideConBusqueda(
            IDevolucion devolucion,
            String texto
    ) {

        IDetalleAlquiler detalle =
                devolucion.getDetalleAlquiler();

        IAlquiler alquiler = detalle == null
                ? null
                : buscarAlquilerPorDetalle(
                detalle.getIdDetalle()
        );

        String consecutivo = alquiler == null
                ? ""
                : String.valueOf(
                alquiler.getNumeroConsecutivo()
        );

        String cliente = obtenerNombreCliente(alquiler);
        String identificacion = obtenerIdentificacionCliente(alquiler);
        String referencia = obtenerReferencia(detalle);

        return contiene(
                String.valueOf(devolucion.getIdDevolucion()),
                texto
        )
                || contiene(consecutivo, texto)
                || contiene(cliente, texto)
                || contiene(identificacion, texto)
                || contiene(referencia, texto)
                || contiene(
                String.valueOf(devolucion.getFechaDevolucion()),
                texto
        )
                || contiene(
                String.valueOf(devolucion.getEstadoRevision()),
                texto
        )
                || contiene(devolucion.getObservaciones(), texto);
    }

    private boolean contiene(
            String valor,
            String texto
    ) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZAR TABLA
    // ==========================================

    private void actualizarTabla(
            List<IDevolucion> listaDevoluciones
    ) {

        modeloTabla.setRowCount(0);

        if (listaDevoluciones != null) {

            for (IDevolucion devolucion : listaDevoluciones) {

                if (devolucion == null) {
                    continue;
                }

                IDetalleAlquiler detalle =
                        devolucion.getDetalleAlquiler();

                IAlquiler alquiler = detalle == null
                        ? null
                        : buscarAlquilerPorDetalle(
                        detalle.getIdDetalle()
                );

                modeloTabla.addRow(new Object[]{
                        devolucion.getIdDevolucion(),
                        obtenerConsecutivo(alquiler),
                        obtenerReferencia(detalle),
                        obtenerNombreCliente(alquiler),
                        devolucion.getFechaDevolucion(),
                        devolucion.getEstadoRevision(),
                        devolucion.getObservaciones()
                });
            }
        }

        panelTabla.getTablaDatos()
                .getTabla()
                .clearSelection();

        actualizarEstadoAcciones();
    }

    // ==========================================
    // DATOS RELACIONADOS
    // ==========================================

    private IAlquiler buscarAlquilerPorDetalle(
            long idDetalle
    ) {

        return alquileres.stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        alquiler.getDetalles() != null
                )
                .filter(alquiler ->
                        alquiler.getDetalles()
                                .stream()
                                .filter(Objects::nonNull)
                                .anyMatch(detalle ->
                                        detalle.getIdDetalle() == idDetalle
                                )
                )
                .findFirst()
                .orElse(null);
    }

    private String obtenerConsecutivo(IAlquiler alquiler) {

        return alquiler == null
                ? "No disponible"
                : String.valueOf(alquiler.getNumeroConsecutivo());
    }

    private String obtenerNombreCliente(IAlquiler alquiler) {

        return alquiler == null || alquiler.getCliente() == null
                ? "No disponible"
                : alquiler.getCliente().getNombre();
    }

    private String obtenerIdentificacionCliente(IAlquiler alquiler) {

        return alquiler == null || alquiler.getCliente() == null
                ? ""
                : alquiler.getCliente().getIdentificacion();
    }

    private String obtenerReferencia(IDetalleAlquiler detalle) {

        return detalle == null || detalle.getPrenda() == null
                ? "No disponible"
                : detalle.getPrenda().getReferencia();
    }

    // ==========================================
    // ESTADO DE ACCIONES
    // ==========================================

    private void actualizarEstadoAcciones() {

        boolean haySeleccion =
                panelTabla.getTablaDatos()
                        .getTabla()
                        .getSelectedRow() != -1;

        botonEditar.setEnabled(haySeleccion);
    }

    // ==========================================
    // REGISTRAR DEVOLUCIÓN
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioDevolucion formulario =
                new FormularioDevolucion(
                        propietario,
                        sistemaFacade,
                        this::cargarDevoluciones
                );

        formulario.mostrar();
    }

    // ==========================================
    // ACTUALIZAR DEVOLUCIÓN
    // ==========================================

    private void abrirFormularioEdicion() {

        IDevolucion devolucion =
                obtenerDevolucionSeleccionada();

        if (devolucion == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una devolución para actualizar.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioDevolucion formulario =
                new FormularioDevolucion(
                        propietario,
                        sistemaFacade,
                        devolucion,
                        this::cargarDevoluciones
                );

        formulario.mostrar();
    }

    // ==========================================
    // OBTENER DEVOLUCIÓN SELECCIONADA
    // ==========================================

    private IDevolucion obtenerDevolucionSeleccionada() {

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(filaVista);

        long idDevolucion = Long.parseLong(
                String.valueOf(
                        modeloTabla.getValueAt(filaModelo, 0)
                )
        );

        return devoluciones.stream()
                .filter(Objects::nonNull)
                .filter(devolucion ->
                        devolucion.getIdDevolucion() == idDevolucion
                )
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String obtenerMensajeError(Throwable ex) {

        Throwable causa = ex;

        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        return causa.getMessage() != null
                ? causa.getMessage()
                : "Ocurrió un error inesperado.";
    }
}
