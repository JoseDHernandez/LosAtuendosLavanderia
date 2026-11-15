
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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioEnvioLavanderia extends JDialog {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final IGestorLavanderia gestorLavanderia;

    private final JSpinner campoCupo;
    private final JComboBox<String> selectorEstrategia;

    private final JTable tabla;
    private final DefaultTableModel modeloTabla;

    private final JLabel etiquetaCantidad;
    private final JLabel etiquetaDescripcionEstrategia;

    private final BotonSecundario botonGenerar;
    private final BotonPrincipal botonConfirmar;

    private final Runnable alConfirmar;

    private List<ISolicitudLavanderia> tanda;

    private boolean procesando;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FormularioEnvioLavanderia(
            Window propietario,
            ISistemaFacade sistemaFacade,
            Runnable alConfirmar) {

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

        this.campoCupo = new JSpinner(
                new SpinnerNumberModel(10, 1, 1000, 1)
        );

        this.selectorEstrategia = new JComboBox<>(
                new String[]{
                        "Prioridad",
                        "FIFO"
                }
        );

        this.modeloTabla = crearModeloTabla();
        this.tabla = new JTable(modeloTabla);

        this.etiquetaCantidad = new JLabel(
                "No se ha generado una tanda."
        );

        this.etiquetaDescripcionEstrategia = new JLabel();

        this.botonGenerar = new BotonSecundario(
                "Generar tanda"
        );

        this.botonConfirmar = new BotonPrincipal(
                "Confirmar envío"
        );

        this.tanda = new ArrayList<>();

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

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_MD
                )
        );

        panelPrincipal.setBorder(MARGEN_PANEL_ESTANDAR);
        panelPrincipal.setBackground(SUPERFICIE);

        JPanel panelSuperior = crearPanelConfiguracion();

        configurarTabla();

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BORDE_SIMPLE);

        JPanel panelInferior = crearPanelInferior();

        panelPrincipal.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);

        setSize(950, 560);

        setMinimumSize(
                new Dimension(800, 480)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // CONFIGURACIÓN SUPERIOR
    // ==========================================

    private JPanel crearPanelConfiguracion() {

        JPanel contenedor = new JPanel(
                new BorderLayout(
                        0,
                        ESPACIADO_SM
                )
        );

        contenedor.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                "Configuración del envío",
                FUENTE_SECCION,
                TEXTO
        );

        JPanel panelConfiguracion = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        ESPACIADO_MD,
                        0
                )
        );

        panelConfiguracion.setOpaque(false);

        JLabel etiquetaCupo = crearEtiqueta(
                "Capacidad máxima",
                FUENTE_ETIQUETA,
                TEXTO
        );

        campoCupo.setPreferredSize(
                new Dimension(100, ALTO_CAMPO)
        );

        JLabel etiquetaEstrategia = crearEtiqueta(
                "Estrategia de selección",
                FUENTE_ETIQUETA,
                TEXTO
        );

        selectorEstrategia.setPreferredSize(
                new Dimension(170, ALTO_CAMPO)
        );

        selectorEstrategia.setFont(FUENTE_NORMAL);

        botonGenerar.configurarIcono(
                com.lavanderia.vista.estilos.IconosUI.actualizar()
        );

        panelConfiguracion.add(etiquetaCupo);
        panelConfiguracion.add(campoCupo);

        panelConfiguracion.add(etiquetaEstrategia);
        panelConfiguracion.add(selectorEstrategia);

        panelConfiguracion.add(botonGenerar);

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

        contenedor.add(
                titulo,
                BorderLayout.NORTH
        );

        contenedor.add(
                panelConfiguracion,
                BorderLayout.CENTER
        );

        contenedor.add(
                etiquetaDescripcionEstrategia,
                BorderLayout.SOUTH
        );

        return contenedor;
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
    // DESCRIPCIÓN DE ESTRATEGIA
    // ==========================================

    private void actualizarDescripcionEstrategia() {

        String seleccion =
                (String) selectorEstrategia.getSelectedItem();

        if ("FIFO".equals(seleccion)) {

            etiquetaDescripcionEstrategia.setText(
                    "FIFO: primero en llegar, primero en ser atendido."
            );

        } else {

            etiquetaDescripcionEstrategia.setText(
                    "Prioridad: solicitudes especiales, manchadas y "
                            + "delicadas antes que las normales. FIFO desempata."
            );
        }
    }

    // ==========================================
    // INVALIDAR TANDA
    // ==========================================

    private void invalidarTanda() {

        if (tanda.isEmpty()) {
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
    // CONFIGURACIÓN DE TABLA
    // ==========================================

    private void configurarTabla() {

        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(ALTO_FILA_TABLA);

        tabla.setAutoCreateRowSorter(false);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getTableHeader().setFont(
                FUENTE_NORMAL
        );

        tabla.setFont(FUENTE_NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setSelectionBackground(PRIMARIO);
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
                        "Fecha recepción"
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

            // Configurar la estrategia seleccionada.
            if ("FIFO".equals(estrategiaSeleccionada)) {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaFIFO()
                );

            } else {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaPrioridad()
                );
            }

            // Obtener la tanda sin confirmar el envío.
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

                JOptionPane.showMessageDialog(
                        this,
                        "No hay solicitudes pendientes disponibles.",
                        "Sin solicitudes",
                        JOptionPane.INFORMATION_MESSAGE
                );

                actualizarEstadoConfirmacion();
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

            modeloTabla.addRow(new Object[]{
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
            });
        }
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

        botonConfirmar.setEnabled(
                !procesando
                        && tanda != null
                        && !tanda.isEmpty()
        );

        botonGenerar.setEnabled(!procesando);
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

        JPanel panelInferior = new JPanel(
                new BorderLayout(
                        ESPACIADO_MD,
                        0
                )
        );

        panelInferior.setOpaque(false);

        etiquetaCantidad.setFont(FUENTE_NORMAL);
        etiquetaCantidad.setForeground(TEXTO_SECUNDARIO);

        JPanel panelAcciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        panelAcciones.setOpaque(false);

        BotonSecundario botonCancelar =
                new BotonSecundario("Cancelar");

        botonCancelar.addActionListener(
                e -> dispose()
        );

        panelAcciones.add(botonCancelar);
        panelAcciones.add(botonConfirmar);

        panelInferior.add(
                etiquetaCantidad,
                BorderLayout.CENTER
        );

        panelInferior.add(
                panelAcciones,
                BorderLayout.EAST
        );

        return panelInferior;
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
