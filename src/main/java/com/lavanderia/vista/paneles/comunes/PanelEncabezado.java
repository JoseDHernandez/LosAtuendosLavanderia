
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.BotonPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelEncabezado extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblDescripcion;

    private BotonPrincipal btnAccion;

    public PanelEncabezado(
            String titulo,
            String descripcion
    ) {
        this(titulo, descripcion, null, null);
    }

    public PanelEncabezado(
            String titulo,
            String descripcion,
            String textoAccion,
            ActionListener accion
    ) {
        lblTitulo = new JLabel(titulo);
        lblDescripcion = new JLabel(descripcion);

        configurarPanel();
        configurarTextos();
        construirContenido(textoAccion, accion);
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(ESPACIADO_LG, 0));
        setBackground(FONDO);
        setOpaque(true);

        setBorder(BorderFactory.createEmptyBorder(
                0,
                0,
                ESPACIADO_LG,
                0
        ));
    }

    // ==========================================
    // CONFIGURACIÓN DE TEXTOS
    // ==========================================

    private void configurarTextos() {

        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(TEXTO);

        lblDescripcion.setFont(FUENTE_NORMAL);
        lblDescripcion.setForeground(TEXTO_SECUNDARIO);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL CONTENIDO
    // ==========================================

    private void construirContenido(
            String textoAccion,
            ActionListener accion
    ) {

        JPanel informacion = new JPanel();
        informacion.setLayout(
                new BoxLayout(informacion, BoxLayout.Y_AXIS)
        );

        informacion.setBackground(FONDO);
        informacion.setOpaque(true);

        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(lblTitulo);
        informacion.add(Box.createVerticalStrut(ESPACIADO_XS));
        informacion.add(lblDescripcion);

        add(informacion, BorderLayout.CENTER);

        if (textoAccion != null && !textoAccion.isBlank()) {

            btnAccion = new BotonPrincipal(textoAccion);

            if (accion != null) {
                btnAccion.addActionListener(accion);
            }

            JPanel acciones = new JPanel(
                    new FlowLayout(FlowLayout.RIGHT, 0, 0)
            );

            acciones.setBackground(FONDO);
            acciones.setOpaque(true);

            acciones.add(btnAccion);

            add(acciones, BorderLayout.EAST);
        }
    }

    // ==========================================
    // ACCESO Y ACTUALIZACIÓN
    // ==========================================

    public void setTitulo(String titulo) {
        lblTitulo.setText(titulo);
    }

    public void setDescripcion(String descripcion) {
        lblDescripcion.setText(descripcion);
    }

    public void setTextoAccion(String texto) {

        if (btnAccion != null) {
            btnAccion.setText(texto);
        }
    }

    public void setAccion(ActionListener accion) {

        if (btnAccion == null) {
            return;
        }

        for (ActionListener listener :
                btnAccion.getActionListeners()) {
            btnAccion.removeActionListener(listener);
        }

        if (accion != null) {
            btnAccion.addActionListener(accion);
        }
    }

    public BotonPrincipal getBotonAccion() {
        return btnAccion;
    }
}
