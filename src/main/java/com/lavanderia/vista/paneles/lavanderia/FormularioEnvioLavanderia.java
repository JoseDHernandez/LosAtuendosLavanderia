
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

    private List<ISolicitudLavanderia> tanda;

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

        this.etiquetaDescripcionEstrategia = new JLabel(
                "Primero se atienden las solicitudes especiales, "
                        + "manchadas y delicadas."
        );

        this.tanda = new ArrayList<>();

        inicializarComponentes(alConfirmar);
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes(Runnable alConfirmar) {

        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(
                        ESPACIADO_MD,
                        ESPACIADO_MD
                )
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        );

        panelPrincipal.setBackground(FONDO);

        // Configuración superior
        JPanel panelSuperior = crearPanelConfiguracion();

        // Tabla de solicitudes
        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(BORDE_SIMPLE);

        // Información y acciones inferiores
        JPanel panelInferior = crearPanelInferior(alConfirmar);

        panelPrincipal.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                scroll,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        add(panelPrincipal, BorderLayout.CENTER);

        setSize(900, 540);

        setMinimumSize(
                new Dimension(750, 450)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // CONFIGURACIÓN SUPERIOR
    // ==========================================

    private JPanel crearPanelConfiguracion() {

        JPanel contenedor = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        contenedor.setOpaque(false);

        JPanel panelConfiguracion = new JPanel(
                new FlowLayout(FlowLayout.LEFT, ESPACIADO_MD, 0)
        );

        panelConfiguracion.setOpaque(false);

        // Capacidad
        JLabel etiquetaCupo = new JLabel(
                "Capacidad máxima:"
        );

        etiquetaCupo.setFont(FUENTE_NORMAL);

        campoCupo.setPreferredSize(
                new Dimension(90, ALTO_CAMPO)
        );

        // Estrategia
        JLabel etiquetaEstrategia = new JLabel(
                "Estrategia de selección:"
        );

        etiquetaEstrategia.setFont(FUENTE_NORMAL);

        selectorEstrategia.setPreferredSize(
                new Dimension(160, ALTO_CAMPO)
        );

        selectorEstrategia.setFont(FUENTE_NORMAL);

        selectorEstrategia.addActionListener(
                e -> actualizarDescripcionEstrategia()
        );

        // Botón generar
        BotonSecundario botonGenerar =
                new BotonSecundario("Generar tanda");

        botonGenerar.addActionListener(
                e -> generarTanda()
        );

        panelConfiguracion.add(etiquetaCupo);
        panelConfiguracion.add(campoCupo);

        panelConfiguracion.add(etiquetaEstrategia);
        panelConfiguracion.add(selectorEstrategia);

        panelConfiguracion.add(botonGenerar);

        // Descripción de estrategia
        etiquetaDescripcionEstrategia.setFont(
                FUENTE_PEQUENA
        );

        etiquetaDescripcionEstrategia.setForeground(
                TEXTO_SECUNDARIO
        );

        etiquetaDescripcionEstrategia.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM
                )
        );

        contenedor.add(
                panelConfiguracion,
                BorderLayout.NORTH
        );

        contenedor.add(
                etiquetaDescripcionEstrategia,
                BorderLayout.SOUTH
        );

        contenedor.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        ESPACIADO_SM,
                        0
                )
        );

        return contenedor;
    }

    // ==========================================
    // DESCRIPCIÓN DE ESTRATEGIA
    // ==========================================

    private void actualizarDescripcionEstrategia() {

        String seleccion = (String)
                selectorEstrategia.getSelectedItem();

        if ("FIFO".equals(seleccion)) {

            etiquetaDescripcionEstrategia.setText(
                    "FIFO: primero en llegar, primero en ser atendido."
            );

        } else {

            etiquetaDescripcionEstrategia.setText(
                    "Prioridad: especiales, manchadas y delicadas "
                            + "antes que las normales. FIFO desempata."
            );
        }

        // La tanda anterior ya no representa necesariamente
        // la selección actual: se debe volver a generar.
        tanda.clear();

        actualizarTabla();

        etiquetaCantidad.setText(
                "Genera una nueva tanda con la estrategia seleccionada."
        );
    }

    // ==========================================
    // CONFIGURACIÓN DE TABLA
    // ==========================================

    private void configurarTabla() {

        tabla.setFillsViewportHeight(true);

        tabla.setRowHeight(32);

        tabla.setAutoCreateRowSorter(false);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getTableHeader().setFont(
                FUENTE_NORMAL
        );

        tabla.setFont(FUENTE_NORMAL);
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
                    int columna) {

                return false;
            }
        };
    }

    // ==========================================
    // GENERACIÓN DE TANDA
    // ==========================================

    private void generarTanda() {

        try {

            int cupo = (Integer) campoCupo.getValue();

            String estrategiaSeleccionada =
                    (String) selectorEstrategia.getSelectedItem();

            // Configurar la estrategia seleccionada
            if ("FIFO".equals(estrategiaSeleccionada)) {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaFIFO()
                );

            } else {

                gestorLavanderia.cambiarEstrategia(
                        new EstrategiaPrioridad()
                );
            }

            // Seleccionar solicitudes según la estrategia activa
            tanda = new ArrayList<>(
                    gestorLavanderia.seleccionarTanda(cupo)
            );

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

                return;
            }

            etiquetaCantidad.setText(
                    tanda.size()
                            + " solicitudes seleccionadas mediante "
                            + estrategiaSeleccionada
                            + "."
            );

        } catch (RuntimeException ex) {

            tanda = new ArrayList<>();

            actualizarTabla();

            etiquetaCantidad.setText(
                    "No se pudo generar la tanda."
            );

            mostrarError(
                    "No fue posible generar la tanda.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // ACTUALIZACIÓN DE TABLA
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
            ISolicitudLavanderia solicitud) {

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
    // CONFIRMACIÓN DEL ENVÍO
    // ==========================================

    private void confirmarEnvio(Runnable alConfirmar) {

        if (tanda == null || tanda.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes generar una tanda.",
                    "Sin solicitudes",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String estrategia = (String)
                selectorEstrategia.getSelectedItem();

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

        try {

            ILoteLavanderia lote =
                    gestorLavanderia.confirmarEnvio(tanda);

            JOptionPane.showMessageDialog(
                    this,
                    "El envío fue confirmado correctamente.\n\n"
                            + "Lote: " + lote.getIdLote()
                            + "\nFecha: " + lote.getFechaEnvio()
                            + "\nPrendas: " + tanda.size()
                            + "\nEstrategia: " + estrategia,
                    "Envío confirmado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (alConfirmar != null) {
                alConfirmar.run();
            }

            dispose();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible confirmar el envío.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // PANEL INFERIOR
    // ==========================================

    private JPanel crearPanelInferior(Runnable alConfirmar) {

        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        panelInferior.setOpaque(false);

        etiquetaCantidad.setFont(FUENTE_NORMAL);

        etiquetaCantidad.setForeground(
                TEXTO_SECUNDARIO
        );

        JPanel panelAcciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelAcciones.setOpaque(false);

        BotonSecundario botonCancelar =
                new BotonSecundario("Cancelar");

        botonCancelar.addActionListener(
                e -> dispose()
        );

        BotonPrincipal botonConfirmar =
                new BotonPrincipal("Confirmar envío");

        botonConfirmar.addActionListener(
                e -> confirmarEnvio(alConfirmar)
        );

        panelAcciones.add(botonCancelar);

        panelAcciones.add(botonConfirmar);

        panelInferior.add(
                etiquetaCantidad,
                BorderLayout.WEST
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

    // ==========================================
    // MOSTRAR FORMULARIO
    // ==========================================

    public void mostrar() {
        setVisible(true);
    }
}
