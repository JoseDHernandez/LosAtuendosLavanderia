
package com.lavanderia.vista.paneles.lavanderia;

import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;

import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.patrones.strategy.EstrategiaFIFO;
import com.lavanderia.patrones.strategy.EstrategiaPrioridad;

import com.lavanderia.servicio.interfaces.IGestorLavanderia;

import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelTabla;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioEnvioLavanderia extends JDialog {

    // ==========================================
    // DEPENDENCIAS
    // ==========================================

    private final IGestorLavanderia gestorLavanderia;
    private final Runnable alConfirmar;

    // ==========================================
    // CONTROLES
    // ==========================================

    private final JSpinner campoCupo;
    private final JComboBox<String> selectorEstrategia;

    private final PanelTabla panelTabla;
    private final DefaultTableModel modeloTabla;

    private final JLabel etiquetaCantidad;
    private final JLabel etiquetaDescripcionEstrategia;

    private final BotonSecundario botonGenerar;
    private final BotonPrincipal botonConfirmar;
    private final BotonSecundario botonCancelar;

    // ==========================================
    // ESTADO
    // ==========================================

    private List<ISolicitudLavanderia> tanda;

    private boolean procesando;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FormularioEnvioLavanderia(
            Window propietario,
            ISistemaFacade sistemaFacade,
            Runnable alConfirmar
    ) {

        super(
                propietario,
                "Preparar envío a lavandería",
                ModalityType.APPLICATION_MODAL
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorLavanderia = Objects.requireNonNull(
                sistemaFacade.getGestorLavanderia(),
                "El gestor de lavandería es obligatorio."
        );

        this.alConfirmar = alConfirmar;

        campoCupo = new JSpinner(
                new SpinnerNumberModel(10, 1, 1000, 1)
        );

        selectorEstrategia = new JComboBox<>(
                new String[]{
                        "Prioridad",
                        "FIFO"
                }
        );

        modeloTabla = crearModeloTabla();

        panelTabla = new PanelTabla();

        etiquetaCantidad = new JLabel(
                "No se ha generado una tanda."
        );

        etiquetaDescripcionEstrategia = new JLabel();

        botonGenerar = new BotonSecundario(
                "Generar tanda"
        );

        botonConfirmar = new BotonPrincipal(
                "Confirmar envío"
        );

        botonCancelar = new BotonSecundario(
                "Cancelar"
        );

        tanda = new ArrayList<>();

        inicializarComponentes();
        configurarEventos();
        actualizarDescripcionEstrategia();
        actualizarEstadoConfirmacion();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel principal = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_LG
                )
        );

        principal.setBackground(SUPERFICIE);
        principal.setBorder(MARGEN_PANEL_ESTANDAR);

        principal.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        principal.add(
                crearPanelTabla(),
                BorderLayout.CENTER
        );

        principal.add(
                crearPanelInferior(),
                BorderLayout.SOUTH
        );

        setContentPane(principal);

        setMinimumSize(
                new Dimension(
                        ANCHO_MINIMO,
                        ALTO_MINIMO
                )
        );

        setSize(
                ANCHO_VENTANA - 120,
                ALTO_VENTANA - 100
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel encabezado = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_MD
                )
        );

        encabezado.setOpaque(false);

        JPanel titulos = new JPanel(
                new GridLayout(2, 1, 0, ESPACIADO_XS)
        );

        titulos.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                "Configuración del envío",
                FUENTE_SECCION,
                TEXTO
        );

        JLabel subtitulo = crearEtiqueta(
                "Define la capacidad y el criterio para seleccionar "
                        + "las solicitudes que se enviarán a lavandería.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        titulos.add(titulo);
        titulos.add(subtitulo);

        encabezado.add(
                titulos,
                BorderLayout.NORTH
        );

        encabezado.add(
                crearControlesConfiguracion(),
                BorderLayout.CENTER
        );

        return encabezado;
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private JPanel crearControlesConfiguracion() {

        JPanel contenedor = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_SM
                )
        );

        contenedor.setOpaque(false);

        JPanel controles = new JPanel(
                new GridBagLayout()
        );

        controles.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(
                0,
                0,
                0,
                ESPACIADO_MD
        );

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Capacidad máxima.

        gbc.gridx = 0;
        gbc.weightx = 0;

        controles.add(
                crearEtiqueta(
                        "Capacidad máxima",
                        FUENTE_ETIQUETA,
                        TEXTO
                ),
                gbc
        );

        gbc.gridx = 1;

        controles.add(
                configurarCampoCupo(),
                gbc
        );

        // Estrategia.

        gbc.gridx = 2;

        controles.add(
                crearEtiqueta(
                        "Estrategia de selección",
                        FUENTE_ETIQUETA,
                        TEXTO
                ),
                gbc
        );

        gbc.gridx = 3;
        gbc.weightx = 1;

        controles.add(
                configurarSelectorEstrategia(),
                gbc
        );

        // Botón generar.

        gbc.gridx = 4;
        gbc.weightx = 0;
        gbc.insets = new Insets(
                0,
                0,
                0,
                0
        );

        botonGenerar.configurarIcono(
                IconosUI.actualizar()
        );

        controles.add(
                botonGenerar,
                gbc
        );

        contenedor.add(
                controles,
                BorderLayout.CENTER
        );

        contenedor.add(
                crearDescripcionEstrategia(),
                BorderLayout.SOUTH
        );

        return contenedor;
    }

    // ==========================================
    // CAMPO DE CUPO
    // ==========================================

    private JSpinner configurarCampoCupo() {

        campoCupo.setFont(FUENTE_NORMAL);
        campoCupo.setForeground(TEXTO);
        campoCupo.setBackground(SUPERFICIE);

        campoCupo.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        campoCupo.setMinimumSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        campoCupo.setBorder(
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

        JSpinner.NumberEditor editor =
                new JSpinner.NumberEditor(campoCupo, "#");

        campoCupo.setEditor(editor);

        editor.getTextField().setFont(FUENTE_NORMAL);
        editor.getTextField().setForeground(TEXTO);
        editor.getTextField().setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        return campoCupo;
    }

    // ==========================================
    // SELECTOR DE ESTRATEGIA
    // ==========================================

    private JComboBox<String> configurarSelectorEstrategia() {

        selectorEstrategia.setFont(FUENTE_NORMAL);
        selectorEstrategia.setForeground(TEXTO);
        selectorEstrategia.setBackground(SUPERFICIE);
        selectorEstrategia.setFocusable(true);

        selectorEstrategia.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selectorEstrategia.setMinimumSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selectorEstrategia.setBorder(
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

        selectorEstrategia.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado
                    ) {

                        super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                enfocado
                        );

                        setFont(FUENTE_NORMAL);

                        if (seleccionado) {
                            setBackground(PRIMARIO);
                            setForeground(Color.WHITE);
                        } else {
                            setBackground(SUPERFICIE);
                            setForeground(TEXTO);
                        }

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        ESPACIADO_XS,
                                        ESPACIADO_SM,
                                        ESPACIADO_XS,
                                        ESPACIADO_SM
                                )
                        );

                        return this;
                    }
                }
        );

        return selectorEstrategia;
    }

    // ==========================================
    // DESCRIPCIÓN DE ESTRATEGIA
    // ==========================================

    private JLabel crearDescripcionEstrategia() {

        etiquetaDescripcionEstrategia.setFont(
                FUENTE_PEQUENA
        );

        etiquetaDescripcionEstrategia.setForeground(
                TEXTO_SECUNDARIO
        );

        etiquetaDescripcionEstrategia.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        0,
                        ESPACIADO_SM,
                        0
                )
        );

        return etiquetaDescripcionEstrategia;
    }

    private void actualizarDescripcionEstrategia() {

        String seleccion =
                (String) selectorEstrategia.getSelectedItem();

        if ("FIFO".equals(seleccion)) {

            etiquetaDescripcionEstrategia.setText(
                    "FIFO: las primeras solicitudes recibidas "
                            + "son las primeras en ser atendidas."
            );

        } else {

            etiquetaDescripcionEstrategia.setText(
                    "Prioridad: las solicitudes especiales, manchadas "
                            + "y delicadas se atienden primero. FIFO desempata."
            );
        }
    }

    // ==========================================
    // PANEL DE TABLA
    // ==========================================

    private JPanel crearPanelTabla() {

        JPanel panel = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_SM
                )
        );

        panel.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                "Solicitudes seleccionadas",
                FUENTE_SECCION,
                TEXTO
        );

        panelTabla.setBusquedaVisible(false);
        panelTabla.setPanelFiltrosVisible(false);
        panelTabla.setModelo(modeloTabla);
        panelTabla.getPanelControles().setVisible(false);
        JTable tabla = panelTabla.getTablaDatos().getTabla();

        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(ALTO_FILA_TABLA);
        tabla.setAutoCreateRowSorter(false);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.getTableHeader().setReorderingAllowed(false);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(false);

        panel.add(
                titulo,
                BorderLayout.NORTH
        );

        panel.add(
                panelTabla,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "Solicitud",
                        "Referencia",
                        "Marca",
                        "Talla",
                        "Prioridad",
                        "Fecha de recepción"
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
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        botonGenerar.addActionListener(
                e -> generarTanda()
        );

        botonConfirmar.addActionListener(
                e -> confirmarEnvio()
        );

        botonCancelar.addActionListener(
                e -> dispose()
        );

        selectorEstrategia.addActionListener(
                e -> {
                    actualizarDescripcionEstrategia();
                    invalidarTanda();
                }
        );

        campoCupo.addChangeListener(
                e -> invalidarTanda()
        );
    }

    // ==========================================
    // INVALIDAR TANDA
    // ==========================================

    private void invalidarTanda() {

        if (procesando || tanda.isEmpty()) {
            return;
        }

        tanda.clear();

        actualizarTabla();

        etiquetaCantidad.setText(
                "La configuración cambió. Genera una nueva tanda."
        );

        actualizarEstadoConfirmacion();
    }

    // ==========================================
    // GENERAR TANDA
    // ==========================================

    private void generarTanda() {

        if (procesando) {
            return;
        }

        try {

            int cupo = (Integer) campoCupo.getValue();

            String estrategiaSeleccionada =
                    (String) selectorEstrategia.getSelectedItem();

            if (estrategiaSeleccionada == null) {

                throw new IllegalStateException(
                        "Debe seleccionar una estrategia."
                );
            }

            if ("FIFO".equals(estrategiaSeleccionada)) {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaFIFO()
                );

            } else {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaPrioridad()
                );
            }

            List<ISolicitudLavanderia> resultado =
                    gestorLavanderia.seleccionarTanda(cupo);

            tanda = resultado == null
                    ? new ArrayList<>()
                    : new ArrayList<>(resultado);

            actualizarTabla();

            if (tanda.isEmpty()) {

                etiquetaCantidad.setText(
                        "No hay solicitudes pendientes para enviar."
                );

                actualizarEstadoConfirmacion();

                JOptionPane.showMessageDialog(
                        this,
                        "No hay solicitudes pendientes disponibles.",
                        "Sin solicitudes",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            etiquetaCantidad.setText(
                    tanda.size()
                            + " solicitudes seleccionadas mediante "
                            + estrategiaSeleccionada
                            + "."
            );

            actualizarEstadoConfirmacion();

        } catch (RuntimeException ex) {

            tanda.clear();

            actualizarTabla();

            etiquetaCantidad.setText(
                    "No se pudo generar la tanda."
            );

            actualizarEstadoConfirmacion();

            mostrarError(
                    "No fue posible generar la tanda.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // ACTUALIZAR TABLA
    // ==========================================

    private void actualizarTabla() {

        modeloTabla.setRowCount(0);

        for (ISolicitudLavanderia solicitud : tanda) {

            if (solicitud == null) {
                continue;
            }

            IPrenda prenda = obtenerPrenda(solicitud);

            modeloTabla.addRow(
                    new Object[]{
                            solicitud.getIdSolicitud(),
                            prenda != null
                                    ? prenda.getReferencia()
                                    : "N/D",
                            prenda != null
                                    ? prenda.getMarca()
                                    : "N/D",
                            prenda != null
                                    ? prenda.getTalla()
                                    : "N/D",
                            solicitud.getPrioridad(),
                            solicitud.getFechaRecepcion()
                    }
            );
        }

        panelTabla.getTablaDatos()
                .getTabla()
                .clearSelection();
    }

    // ==========================================
    // OBTENER PRENDA
    // ==========================================

    private IPrenda obtenerPrenda(
            ISolicitudLavanderia solicitud
    ) {

        if (solicitud == null
                || solicitud.getDevolucion() == null
                || solicitud.getDevolucion()
                .getDetalleAlquiler() == null) {

            return null;
        }

        return solicitud.getDevolucion()
                .getDetalleAlquiler()
                .getPrenda();
    }

    // ==========================================
    // ESTADO DE CONFIRMACIÓN
    // ==========================================

    private void actualizarEstadoConfirmacion() {

        boolean habilitado =
                !procesando
                        && tanda != null
                        && !tanda.isEmpty();

        botonConfirmar.setEnabled(habilitado);

        botonGenerar.setEnabled(!procesando);
        botonCancelar.setEnabled(!procesando);

        campoCupo.setEnabled(!procesando);
        selectorEstrategia.setEnabled(!procesando);
    }

    // ==========================================
    // CONFIRMAR ENVÍO
    // ==========================================

    private void confirmarEnvio() {

        if (procesando) {
            return;
        }

        if (tanda == null || tanda.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes generar una tanda.",
                    "Sin solicitudes",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String estrategia =
                (String) selectorEstrategia.getSelectedItem();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas el envío de "
                        + tanda.size()
                        + " solicitudes a lavandería?\n\n"
                        + "Estrategia utilizada: "
                        + estrategia,
                "Confirmar despacho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        procesando = true;
        actualizarEstadoConfirmacion();

        try {

            ILoteLavanderia lote =
                    gestorLavanderia.confirmarEnvio(
                            new ArrayList<>(tanda)
                    );

            if (lote == null) {

                throw new IllegalStateException(
                        "El gestor no devolvió el lote confirmado."
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "El envío fue confirmado correctamente.\n\n"
                            + "Lote: " + lote.getIdLote()
                            + "\nFecha: " + lote.getFechaEnvio()
                            + "\nSolicitudes: " + tanda.size()
                            + "\nEstrategia: " + estrategia,
                    "Envío confirmado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (alConfirmar != null) {

                try {

                    alConfirmar.run();

                } catch (RuntimeException ex) {

                    ex.printStackTrace(System.err);

                    JOptionPane.showMessageDialog(
                            getOwner(),
                            "El envío se confirmó, pero no fue posible "
                                    + "actualizar la información del panel.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (RuntimeException ex) {

            procesando = false;
            actualizarEstadoConfirmacion();

            mostrarError(
                    "No fue posible confirmar el envío.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // PANEL INFERIOR
    // ==========================================

    private JPanel crearPanelInferior() {

        JPanel inferior = new JPanel(
                new BorderLayout(
                        ESPACIADO_MD,
                        0
                )
        );

        inferior.setOpaque(false);

        etiquetaCantidad.setFont(FUENTE_NORMAL);
        etiquetaCantidad.setForeground(TEXTO_SECUNDARIO);

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        acciones.setOpaque(false);

        botonCancelar.configurarIcono(
                IconosUI.cancelar()
        );

        botonConfirmar.configurarIcono(
                IconosUI.guardar()
        );

        acciones.add(botonCancelar);
        acciones.add(botonConfirmar);

        inferior.add(
                etiquetaCantidad,
                BorderLayout.CENTER
        );

        inferior.add(
                acciones,
                BorderLayout.EAST
        );

        return inferior;
    }

    // ==========================================
    // ETIQUETAS
    // ==========================================

    private JLabel crearEtiqueta(
            String texto,
            Font fuente,
            Color color
    ) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);

        return etiqueta;
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

    // ==========================================
    // MOSTRAR FORMULARIO
    // ==========================================

    public void mostrar() {

        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
