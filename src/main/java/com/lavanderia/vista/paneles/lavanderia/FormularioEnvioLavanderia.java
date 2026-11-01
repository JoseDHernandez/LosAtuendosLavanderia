
package com.lavanderia.vista.paneles.lavanderia;

import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.facade.ISistemaFacade;
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

    private final IGestorLavanderia gestorLavanderia;

    private final JSpinner campoCupo;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;

    private final JLabel etiquetaCantidad;

    private List<ISolicitudLavanderia> tanda;

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

        this.modeloTabla = crearModeloTabla();
        this.tabla = new JTable(modeloTabla);

        this.etiquetaCantidad = new JLabel(
                "No se ha generado una tanda."
        );

        this.tanda = new ArrayList<>();

        inicializarComponentes(alConfirmar);
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes(Runnable alConfirmar) {

        setLayout(
                new BorderLayout(
                        ESPACIADO_MD,
                        ESPACIADO_MD
                )
        );

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

        // Configuración del cupo.
        JPanel panelConfiguracion = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelConfiguracion.setOpaque(false);

        JLabel etiquetaCupo = new JLabel(
                "Capacidad máxima del envío:"
        );

        etiquetaCupo.setFont(FUENTE_NORMAL);

        campoCupo.setPreferredSize(
                new Dimension(100, ALTO_CAMPO)
        );

        BotonSecundario botonGenerar =
                new BotonSecundario("Generar tanda");

        botonGenerar.addActionListener(
                e -> generarTanda()
        );

        panelConfiguracion.add(etiquetaCupo);
        panelConfiguracion.add(campoCupo);
        panelConfiguracion.add(botonGenerar);

        // Tabla de solicitudes seleccionadas.
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(30);
        tabla.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(BORDE_SIMPLE);

        // Pie de formulario.
        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        panelInferior.setOpaque(false);

        etiquetaCantidad.setFont(FUENTE_NORMAL);
        etiquetaCantidad.setForeground(TEXTO_SECUNDARIO);

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

        panelPrincipal.add(
                panelConfiguracion,
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

        setSize(850, 500);
        setMinimumSize(new Dimension(700, 400));
        setLocationRelativeTo(getOwner());
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
            public boolean isCellEditable(int fila, int columna) {
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

            tanda = gestorLavanderia.seleccionarTanda(cupo);

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
                    tanda.size() + " solicitudes seleccionadas."
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
                    prenda != null ? prenda.getReferencia() : "N/D",
                    prenda != null ? prenda.getMarca() : "N/D",
                    prenda != null ? prenda.getTalla() : "N/D",
                    solicitud.getPrioridad(),
                    solicitud.getFechaRecepcion()
            });
        }
    }

    private IPrenda obtenerPrenda(
            ISolicitudLavanderia solicitud) {

        if (solicitud == null
                || solicitud.getDevolucion() == null
                || solicitud.getDevolucion().getDetalleAlquiler() == null) {

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

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas el envío de "
                        + tanda.size()
                        + " solicitudes a lavandería?",
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
                            + "\nPrendas: " + tanda.size(),
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

    public void mostrar() {
        setVisible(true);
    }
}
