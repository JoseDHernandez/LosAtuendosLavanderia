
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/** Componente reutilizable para ingresar criterios de búsqueda. */
public class CampoBusqueda extends JPanel {

    private final JTextField txtBusqueda;
    private final JButton btnLimpiar;

    private boolean hoverLimpiar = false;

    public CampoBusqueda(String placeholder) {

        txtBusqueda = new JTextField();
        btnLimpiar = new JButton("×");

        configurarPanel();
        configurarCampo(placeholder);
        configurarBoton();
        configurarEventos();

        add(txtBusqueda, BorderLayout.CENTER);
        add(btnLimpiar, BorderLayout.EAST);

        btnLimpiar.addActionListener(e -> limpiar());
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(ESPACIADO_SM, 0));
        setBackground(SUPERFICIE);
        setOpaque(true);

        setBorder(BorderFactory.createCompoundBorder(
                BORDE_SIMPLE,
                BorderFactory.createEmptyBorder(
                        0,
                        ESPACIADO_MD - ESPACIADO_XS,
                        0,
                        ESPACIADO_SM
                )
        ));

        setPreferredSize(
                new Dimension(ANCHO_BUSQUEDA, ALTO_BUSQUEDA)
        );
    }

    // ==========================================
    // CONFIGURACIÓN DEL CAMPO
    // ==========================================

    private void configurarCampo(String placeholder) {

        txtBusqueda.setFont(FUENTE_NORMAL);
        txtBusqueda.setForeground(TEXTO);
        txtBusqueda.setBackground(SUPERFICIE);
        txtBusqueda.setCaretColor(PRIMARIO);

        txtBusqueda.setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_XS / 2,
                ESPACIADO_SM,
                ESPACIADO_XS / 2
        ));

        if (placeholder != null && !placeholder.isBlank()) {
            txtBusqueda.putClientProperty(
                    "JTextField.placeholderText",
                    placeholder
            );
        }
    }

    // ==========================================
    // CONFIGURACIÓN DEL BOTÓN
    // ==========================================

    private void configurarBoton() {

        btnLimpiar.setFont(FUENTE_ICONO);

        btnLimpiar.setForeground(TEXTO_SECUNDARIO);
        btnLimpiar.setBackground(SUPERFICIE);

        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_XS / 2,
                ESPACIADO_SM,
                ESPACIADO_XS / 2,
                ESPACIADO_SM
        ));

        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setContentAreaFilled(true);
        btnLimpiar.setOpaque(true);

        btnLimpiar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnLimpiar.setRolloverEnabled(false);
        btnLimpiar.setToolTipText("Limpiar búsqueda");
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        btnLimpiar.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (btnLimpiar.isEnabled()) {
                    hoverLimpiar = true;
                    actualizarEstiloBoton();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverLimpiar = false;
                actualizarEstiloBoton();
            }
        });
    }

    // ==========================================
    // ACTUALIZACIÓN VISUAL
    // ==========================================

    private void actualizarEstiloBoton() {

        if (!btnLimpiar.isEnabled()) {

            btnLimpiar.setForeground(TEXTO_DESHABILITADO);
            btnLimpiar.setBackground(SUPERFICIE);

        } else if (hoverLimpiar) {

            btnLimpiar.setForeground(PRIMARIO);
            btnLimpiar.setBackground(NAV_HOVER);

        } else {

            btnLimpiar.setForeground(TEXTO_SECUNDARIO);
            btnLimpiar.setBackground(SUPERFICIE);
        }

        btnLimpiar.repaint();
    }

    // ==========================================
    // ACCESO A LA INFORMACIÓN
    // ==========================================

    public String getText() {
        return txtBusqueda.getText().trim();
    }

    public void setText(String texto) {
        txtBusqueda.setText(texto);
    }

    public void limpiar() {
        txtBusqueda.setText("");
        txtBusqueda.requestFocusInWindow();
    }

    // ==========================================
    // LISTENERS
    // ==========================================

    public void addBusquedaListener(ActionListener listener) {
        txtBusqueda.addActionListener(listener);
    }

    public void addDocumentListener(
            javax.swing.event.DocumentListener listener
    ) {
        txtBusqueda.getDocument().addDocumentListener(listener);
    }

    // ==========================================
    // CONFIGURACIÓN GENERAL
    // ==========================================

    public JTextField getCampo() {
        return txtBusqueda;
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        txtBusqueda.setEnabled(enabled);
        btnLimpiar.setEnabled(enabled);

        if (!enabled) {
            hoverLimpiar = false;
        }

        actualizarEstiloBoton();
    }
}
