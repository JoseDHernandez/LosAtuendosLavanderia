
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelFormulario extends JPanel {

    private final JPanel panelCampos;
    private final JPanel panelAcciones;
    private final JScrollPane scrollCampos;

    private BotonPrincipal btnGuardar;
    private BotonSecundario btnCancelar;

    public PanelFormulario() {
        this("Guardar", null, "Cancelar", null);
    }

    public PanelFormulario(
            String textoGuardar,
            ActionListener accionGuardar,
            String textoCancelar,
            ActionListener accionCancelar) {

        panelCampos = new JPanel(new GridBagLayout());
        panelAcciones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, ESPACIADO_SM, 0
        ));

        scrollCampos = new JScrollPane(panelCampos);

        configurarPanel();
        construirContenido(
                textoGuardar,
                accionGuardar,
                textoCancelar,
                accionCancelar
        );
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(0, ESPACIADO_LG));
        setBackground(SUPERFICIE);
        setOpaque(true);

        setBorder(new EmptyBorder(
                ESPACIADO_LG,
                ESPACIADO_LG,
                ESPACIADO_LG,
                ESPACIADO_LG
        ));

        panelCampos.setBackground(SUPERFICIE);
        panelCampos.setOpaque(true);
        panelCampos.setBorder(new EmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM
        ));

        scrollCampos.setBorder(null);
        scrollCampos.getViewport().setBackground(SUPERFICIE);
        scrollCampos.getViewport().setOpaque(true);
        scrollCampos.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        panelAcciones.setBackground(SUPERFICIE);
        panelAcciones.setOpaque(true);
    }

    private void construirContenido(
            String textoGuardar,
            ActionListener accionGuardar,
            String textoCancelar,
            ActionListener accionCancelar) {

        if (textoCancelar != null && !textoCancelar.isBlank()) {
            btnCancelar = new BotonSecundario(textoCancelar);

            if (accionCancelar != null) {
                btnCancelar.addActionListener(accionCancelar);
            }

            panelAcciones.add(btnCancelar);
        }

        if (textoGuardar != null && !textoGuardar.isBlank()) {
            btnGuardar = new BotonPrincipal(textoGuardar);

            if (accionGuardar != null) {
                btnGuardar.addActionListener(accionGuardar);
            }

            panelAcciones.add(btnGuardar);
        }

        add(scrollCampos, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    // -------------------------
    // Gestión de campos
    // -------------------------

    public void agregarCampo(
            Component componente,
            int fila,
            int columna) {

        agregarCampo(componente, fila, columna, 1);
    }

    public void agregarCampo(
            Component componente,
            int fila,
            int columna,
            int anchoColumnas) {

        if (componente == null) {
            throw new IllegalArgumentException(
                    "El componente no puede ser null."
            );
        }

        if (fila < 0 || columna < 0 || anchoColumnas < 1) {
            throw new IllegalArgumentException(
                    "La posición y el ancho del campo no son válidos."
            );
        }

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.gridwidth = anchoColumnas;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.insets = new Insets(
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM
        );

        panelCampos.add(componente, gbc);
        actualizarFormulario();
    }

    public void agregarSeparador(int fila, int columna, int anchoColumnas) {
        JSeparator separador = new JSeparator();

        agregarCampo(
                separador,
                fila,
                columna,
                anchoColumnas
        );
    }

    public void limpiarCampos() {
        panelCampos.removeAll();
        actualizarFormulario();
    }

    public JPanel getPanelCampos() {
        return panelCampos;
    }

    // -------------------------
    // Gestión de botones
    // -------------------------

    public BotonPrincipal getBotonGuardar() {
        return btnGuardar;
    }

    public BotonSecundario getBotonCancelar() {
        return btnCancelar;
    }

    public void setAccionGuardar(ActionListener accion) {
        reemplazarAccion(btnGuardar, accion);
    }

    public void setAccionCancelar(ActionListener accion) {
        reemplazarAccion(btnCancelar, accion);
    }

    private void reemplazarAccion(
            AbstractButton boton,
            ActionListener accion) {

        if (boton == null) {
            return;
        }

        for (ActionListener listener : boton.getActionListeners()) {
            boton.removeActionListener(listener);
        }

        if (accion != null) {
            boton.addActionListener(accion);
        }
    }

    public void setBotonesHabilitados(boolean habilitados) {
        if (btnGuardar != null) {
            btnGuardar.setEnabled(habilitados);
        }

        if (btnCancelar != null) {
            btnCancelar.setEnabled(habilitados);
        }
    }

    // -------------------------
    // Actualización visual
    // -------------------------

    private void actualizarFormulario() {
        panelCampos.revalidate();
        panelCampos.repaint();
    }
}
