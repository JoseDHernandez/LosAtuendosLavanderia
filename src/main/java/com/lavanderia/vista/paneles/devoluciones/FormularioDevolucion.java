package com.lavanderia.vista.paneles.devoluciones;

import com.lavanderia.modelo.enums.EstadoRevision;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.lavanderia.Devolucion;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorDevoluciones;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.vista.componentes.SelectorFecha;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioDevolucion extends JDialog {

    private final IGestorAlquiler gestorAlquiler;
    private final IGestorDevoluciones gestorDevoluciones;

    private final IDevolucion devolucionEdicion;
    private final Runnable alGuardar;

    private final JComboBox<OpcionDetalle> selectorDetalle;
    private final SelectorFecha fechaDevolucion;
    private final JComboBox<EstadoRevision> selectorEstado;
    private final JTextArea campoObservaciones;

    private final JLabel etiquetaDetalle;

    private final PanelFormulario formulario;

    private boolean guardando;

    // ==========================================
    // CONSTRUCTOR DE REGISTRO
    // ==========================================

    public FormularioDevolucion(
            Window propietario,
            ISistemaFacade sistemaFacade,
            Runnable alGuardar) {

        this(
                propietario,
                sistemaFacade,
                null,
                alGuardar
        );
    }

    // ==========================================
    // CONSTRUCTOR DE REGISTRO Y ACTUALIZACIÓN
    // ==========================================

    public FormularioDevolucion(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IDevolucion devolucion,
            Runnable alGuardar) {

        super(
                propietario,
                devolucion == null
                        ? "Registrar devolución"
                        : "Actualizar devolución",
                ModalityType.APPLICATION_MODAL
        );

        Objects.requireNonNull(
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

        this.devolucionEdicion = devolucion;
        this.alGuardar = alGuardar;

        this.selectorDetalle = new JComboBox<>();

        this.fechaDevolucion = new SelectorFecha(
                "Fecha de devolución"
        );

        this.selectorEstado = new JComboBox<>(
                EstadoRevision.values()
        );

        configurarSelectorEstado();

        this.campoObservaciones = new JTextArea(4, 30);

        this.etiquetaDetalle = new JLabel();

        this.formulario = new PanelFormulario();

        inicializarComponentes();
        configurarSelectorDetalle();
        configurarEstiloSelector(selectorDetalle);
        configurarEstiloSelector(selectorEstado);
        configurarCampoObservaciones();

        cargarDetallesDisponibles();

        if (esEdicion()) {
            cargarDatosDevolucion();
        } else {
            fechaDevolucion.setFecha(
                    convertirFecha(LocalDate.now())
            );
        }
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        panelPrincipal.setBackground(SUPERFICIE);
        panelPrincipal.setBorder(MARGEN_PANEL_ESTANDAR);

        // Encabezado.
        JPanel panelEncabezado = new JPanel();

        panelEncabezado.setLayout(
                new BoxLayout(
                        panelEncabezado,
                        BoxLayout.Y_AXIS
                )
        );

        panelEncabezado.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                esEdicion()
                        ? "Actualizar información de la devolución"
                        : "Información de la devolución",
                FUENTE_SUBTITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                esEdicion()
                        ? "Modifique la fecha, el estado de revisión y las observaciones."
                        : "Seleccione el detalle de alquiler y registre su devolución.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        panelEncabezado.add(titulo);

        panelEncabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        panelEncabezado.add(descripcion);

        // Campos.
        formulario.agregarCampo(
                crearCampo(
                        "Detalle del alquiler",
                        selectorDetalle
                ),
                0,
                0,
                2
        );

        formulario.agregarCampo(
                fechaDevolucion,
                1,
                0
        );

        formulario.agregarCampo(
                crearCampo(
                        "Estado de revisión",
                        selectorEstado
                ),
                1,
                1
        );

        formulario.agregarCampo(
                crearCampo(
                        "Observaciones",
                        crearScrollObservaciones()
                ),
                2,
                0,
                2
        );

        etiquetaDetalle.setFont(FUENTE_PEQUENA);
        etiquetaDetalle.setForeground(TEXTO_SECUNDARIO);

        formulario.agregarCampo(
                etiquetaDetalle,
                3,
                0,
                2
        );

        // Botones comunes.
        formulario.configurarBotonGuardar(
                esEdicion()
                        ? "Actualizar devolución"
                        : "Registrar devolución",
                e -> guardarDevolucion()
        );

        formulario.configurarBotonCancelar(
                "Cancelar",
                e -> dispose()
        );

        panelPrincipal.add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                formulario,
                BorderLayout.CENTER
        );

        setContentPane(panelPrincipal);

        pack();

        setMinimumSize(
                new Dimension(700, 620)
        );

        setSize(
                Math.max(getPreferredSize().width, 760),
                Math.max(getPreferredSize().height, 650)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // CREACIÓN DE CAMPOS
    // ==========================================

    private JPanel crearCampo(
            String titulo,
            JComponent componente) {

        JPanel panel = new JPanel(
                new BorderLayout(0, ESPACIADO_XS)
        );

        panel.setOpaque(false);

        JLabel etiqueta = crearEtiqueta(
                titulo,
                FUENTE_ETIQUETA,
                TEXTO
        );

        panel.add(
                etiqueta,
                BorderLayout.NORTH
        );

        panel.add(
                componente,
                BorderLayout.CENTER
        );

        return panel;
    }

    private void configurarCampoObservaciones() {

        campoObservaciones.setLineWrap(true);
        campoObservaciones.setWrapStyleWord(true);
        campoObservaciones.setFont(FUENTE_NORMAL);
        campoObservaciones.setForeground(TEXTO);
        campoObservaciones.setBackground(SUPERFICIE);

        campoObservaciones.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM
                )
        );

        campoObservaciones.setRows(4);
        campoObservaciones.setColumns(30);
    }

    private JScrollPane crearScrollObservaciones() {

        JScrollPane scroll = new JScrollPane(
                campoObservaciones,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setBorder(BORDE_SIMPLE);
        scroll.setPreferredSize(new Dimension(520, 125));
        scroll.getViewport().setBackground(SUPERFICIE);

        return scroll;
    }

    // ==========================================
    // SELECTOR DE DETALLES
    // ==========================================

    private void configurarSelectorDetalle() {

        selectorDetalle.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado) {

                        super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                enfocado
                        );

                        if (valor instanceof OpcionDetalle opcion) {
                            setText(opcion.toString());
                        }

                        aplicarEstiloCeldaSelector(
                                this,
                                seleccionado
                        );

                        return this;
                    }
                }
        );
    }

    /**
     * El combo conserva EstadoRevision como valor real, pero muestra
     * textos comprensibles para el usuario.
     */
    private void configurarSelectorEstado() {

        selectorEstado.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado) {

                        super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                enfocado
                        );

                        if (valor instanceof EstadoRevision estado) {
                            setText(textoEstadoRevision(estado));
                        }

                        aplicarEstiloCeldaSelector(
                                this,
                                seleccionado
                        );

                        return this;
                    }
                }
        );
    }

    private String textoEstadoRevision(EstadoRevision estado) {

        return switch (estado) {
            case PENDIENTE -> "Pendiente de revisión";
            case SIN_NOVEDAD -> "Sin novedades";
            case CON_NOVEDAD -> "Con novedades";
        };
    }


    private void configurarEstiloSelector(JComboBox<?> selector) {

        selector.setFont(FUENTE_NORMAL);
        selector.setForeground(TEXTO);
        selector.setBackground(SUPERFICIE);
        selector.setFocusable(true);

        selector.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selector.setMinimumSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selector.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_CAMPO
                )
        );

        selector.setBorder(
                BorderFactory.createCompoundBorder(
                        BORDE_SIMPLE,
                        BorderFactory.createEmptyBorder(
                                0,
                                ESPACIADO_SM,
                                0,
                                ESPACIADO_SM
                        )
                )
        );
    }


    private void aplicarEstiloCeldaSelector(
            JLabel etiqueta,
            boolean seleccionado
    ) {

        etiqueta.setFont(FUENTE_NORMAL);
        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_XS,
                        ESPACIADO_SM,
                        ESPACIADO_XS,
                        ESPACIADO_SM
                )
        );

        if (seleccionado) {
            etiqueta.setBackground(PRIMARIO);
            etiqueta.setForeground(Color.WHITE);
            etiqueta.setOpaque(true);
        } else {
            etiqueta.setBackground(SUPERFICIE);
            etiqueta.setForeground(TEXTO);
            etiqueta.setOpaque(true);
        }
    }

    // ==========================================
    // CARGAR DETALLES PENDIENTES
    // ==========================================

    private void cargarDetallesDisponibles() {

        selectorDetalle.removeAllItems();

        List<OpcionDetalle> opciones = new ArrayList<>();

        List<IAlquiler> alquileres =
                gestorAlquiler.listarTodos();

        if (alquileres != null) {

            for (IAlquiler alquiler : alquileres) {

                if (alquiler == null
                        || alquiler.getDetalles() == null) {
                    continue;
                }

                for (IDetalleAlquiler detalle : alquiler.getDetalles()) {

                    if (detalle == null) {
                        continue;
                    }

                    boolean tieneDevolucion =
                            gestorDevoluciones
                                    .consultarPorDetalle(
                                            detalle.getIdDetalle()
                                    )
                                    .isPresent();

                    if (!tieneDevolucion) {

                        opciones.add(
                                crearOpcionDetalle(
                                        alquiler,
                                        detalle
                                )
                        );
                    }
                }
            }
        }

        // En actualización se conserva el detalle original.
        if (esEdicion()
                && devolucionEdicion.getDetalleAlquiler() != null) {

            IDetalleAlquiler detalleActual =
                    devolucionEdicion.getDetalleAlquiler();

            boolean yaIncluido = opciones.stream()
                    .anyMatch(opcion ->
                            opcion.detalle().getIdDetalle()
                                    == detalleActual.getIdDetalle()
                    );

            if (!yaIncluido) {

                OpcionDetalle opcionActual =
                        buscarOpcionDetalle(
                                alquileres,
                                detalleActual
                        );

                if (opcionActual != null) {
                    opciones.add(opcionActual);
                }
            }
        }

        for (OpcionDetalle opcion : opciones) {
            selectorDetalle.addItem(opcion);
        }

        selectorDetalle.setEnabled(
                !esEdicion()
                        && selectorDetalle.getItemCount() > 0
        );

        if (!esEdicion()
                && selectorDetalle.getItemCount() == 0) {

            etiquetaDetalle.setText(
                    "No hay detalles de alquiler pendientes de devolución."
            );
        } else if (!esEdicion()) {

            etiquetaDetalle.setText(
                    "Seleccione el detalle correspondiente a la prenda devuelta."
            );
        }
    }

    private OpcionDetalle crearOpcionDetalle(
            IAlquiler alquiler,
            IDetalleAlquiler detalle) {

        String nombreCliente = alquiler.getCliente() != null
                ? alquiler.getCliente().getNombre()
                : "Cliente no disponible";

        return new OpcionDetalle(
                detalle,
                alquiler.getNumeroConsecutivo(),
                nombreCliente
        );
    }

    private OpcionDetalle buscarOpcionDetalle(
            List<IAlquiler> alquileres,
            IDetalleAlquiler detalleBuscado) {

        if (alquileres == null || detalleBuscado == null) {
            return null;
        }

        for (IAlquiler alquiler : alquileres) {

            if (alquiler == null || alquiler.getDetalles() == null) {
                continue;
            }

            for (IDetalleAlquiler detalle : alquiler.getDetalles()) {

                if (detalle != null
                        && detalle.getIdDetalle()
                        == detalleBuscado.getIdDetalle()) {

                    return crearOpcionDetalle(
                            alquiler,
                            detalle
                    );
                }
            }
        }

        return new OpcionDetalle(
                detalleBuscado,
                0,
                "Cliente no disponible"
        );
    }

    // ==========================================
    // CARGAR DATOS PARA ACTUALIZACIÓN
    // ==========================================

    private void cargarDatosDevolucion() {

        IDetalleAlquiler detalleActual =
                devolucionEdicion.getDetalleAlquiler();

        if (detalleActual != null) {

            for (int i = 0;
                 i < selectorDetalle.getItemCount();
                 i++) {

                OpcionDetalle opcion =
                        selectorDetalle.getItemAt(i);

                if (opcion.detalle().getIdDetalle()
                        == detalleActual.getIdDetalle()) {

                    selectorDetalle.setSelectedIndex(i);
                    break;
                }
            }
        }

        selectorDetalle.setEnabled(false);

        fechaDevolucion.setFecha(
                convertirFecha(
                        devolucionEdicion.getFechaDevolucion()
                )
        );

        selectorEstado.setSelectedItem(
                devolucionEdicion.getEstadoRevision()
        );

        campoObservaciones.setText(
                devolucionEdicion.getObservaciones() != null
                        ? devolucionEdicion.getObservaciones()
                        : ""
        );

        etiquetaDetalle.setText(
                "El detalle de alquiler no puede modificarse."
        );
    }

    // ==========================================
    // REGISTRAR O ACTUALIZAR
    // ==========================================

    private void guardarDevolucion() {

        if (guardando) {
            return;
        }

        try {

            OpcionDetalle opcionSeleccionada =
                    (OpcionDetalle) selectorDetalle.getSelectedItem();

            if (opcionSeleccionada == null) {

                throw new ValidacionException(
                        "Debe seleccionar un detalle de alquiler."
                );
            }

            LocalDate fecha = obtenerFecha();

            EstadoRevision estado =
                    (EstadoRevision) selectorEstado.getSelectedItem();

            if (estado == null) {

                throw new ValidacionException(
                        "Debe seleccionar un estado de revisión."
                );
            }

            String observaciones =
                    campoObservaciones.getText().trim();

            guardando = true;
            formulario.setBotonesHabilitados(false);

            IDevolucion devolucion = new Devolucion(
                    opcionSeleccionada.detalle(),
                    fecha,
                    observaciones,
                    estado
            );

            if (esEdicion()) {

                gestorDevoluciones.actualizarDevolucion(
                        devolucionEdicion.getIdDevolucion(),
                        devolucion
                );

            } else {

                IDevolucion registrada =
                        gestorDevoluciones.registrarDevolucion(
                                devolucion
                        );

                if (registrada == null) {
                    throw new IllegalStateException(
                            "No se recibió la devolución registrada."
                    );
                }
            }

            mostrarMensajeExito();

            dispose();

            if (alGuardar != null) {
                try {
                    alGuardar.run();
                } catch (RuntimeException ex) {
                    ex.printStackTrace(System.err);

                    JOptionPane.showMessageDialog(
                            getOwner(),
                            "La devolución se guardó, pero no fue posible "
                                    + "actualizar la tabla.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (ValidacionException ex) {

            mostrarErrorValidacion(ex.getMessage());

        } catch (RuntimeException ex) {

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible "
                            + (esEdicion() ? "actualizar" : "registrar")
                            + " la devolución.\n\n"
                            + obtenerMensajeError(ex)
            );

        } finally {

            if (isDisplayable()) {
                guardando = false;
                formulario.setBotonesHabilitados(true);
            }
        }
    }

    // ==========================================
    // CONVERSIÓN DE FECHAS
    // ==========================================

    private LocalDate obtenerFecha() {

        return fechaDevolucion.getFecha()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Date convertirFecha(LocalDate fecha) {

        return Date.from(
                fecha.atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarMensajeExito() {

        JOptionPane.showMessageDialog(
                this,
                esEdicion()
                        ? "La devolución se actualizó correctamente."
                        : "La devolución se registró correctamente.",
                esEdicion()
                        ? "Devolución actualizada"
                        : "Devolución registrada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarErrorValidacion(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Datos inválidos",
                JOptionPane.WARNING_MESSAGE
        );
    }

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

    // ==========================================
    // UTILIDADES
    // ==========================================

    private boolean esEdicion() {
        return devolucionEdicion != null;
    }

    public void mostrar() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    // ==========================================
    // OPCIÓN DEL SELECTOR
    // ==========================================

    private record OpcionDetalle(
            IDetalleAlquiler detalle,
            int consecutivo,
            String cliente) {

        @Override
        public String toString() {

            String referencia = detalle.getPrenda() != null
                    ? detalle.getPrenda().getReferencia()
                    : "Prenda no disponible";

            return "Alquiler N.º " + consecutivo
                    + " | " + referencia
                    + " | " + cliente;
        }
    }
}
