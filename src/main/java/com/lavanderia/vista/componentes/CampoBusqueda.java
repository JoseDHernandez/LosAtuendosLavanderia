
package com.lavanderia.vista.componentes;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Componente reutilizable para ingresar criterios de búsqueda.
 *
 * Características:
 * - Placeholder dibujado directamente en el campo.
 * - Botón para limpiar la búsqueda.
 * - Dimensiones centralizadas en EstilosUI.
 * - Conserva los listeners y métodos públicos.
 */
public class CampoBusqueda extends JPanel {

    private final JTextField txtBusqueda;
    private final JButton btnLimpiar;

    private final String placeholder;

    private boolean hoverLimpiar;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public CampoBusqueda(String placeholder) {

        this.placeholder = placeholder != null
                ? placeholder
                : "";

        txtBusqueda = new JTextField() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                if (getText().isEmpty()
                        && !isFocusOwner()
                        && !CampoBusqueda.this.placeholder.isBlank()) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    );

                    g2.setFont(getFont());
                    g2.setColor(TEXTO_SECUNDARIO);

                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();

                    int x = insets.left;
                    int y = (getHeight() - fm.getHeight()) / 2
                            + fm.getAscent();

                    g2.drawString(
                            CampoBusqueda.this.placeholder,
                            x,
                            y
                    );

                    g2.dispose();
                }
            }
        };

        btnLimpiar = new JButton("×");

        configurarPanel();
        configurarCampo();
        configurarBoton();
        configurarEventos();

        add(txtBusqueda, BorderLayout.CENTER);
        add(btnLimpiar, BorderLayout.EAST);

        btnLimpiar.addActionListener(e -> limpiar());

        actualizarEstiloBoton();
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(ESPACIADO_XS, 0));

        setBackground(SUPERFICIE);
        setOpaque(true);

        setBorder(BorderFactory.createCompoundBorder(
                BORDE_CAMPO,
                BorderFactory.createEmptyBorder(
                        0,
                        ESPACIADO_MD,
                        0,
                        ESPACIADO_XS
                )
        ));

        setPreferredSize(dimensionBusqueda());

        setMinimumSize(new Dimension(
                ANCHO_CAMPO_MINIMO,
                ALTO_BUSQUEDA
        ));

        setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                ALTO_BUSQUEDA
        ));
    }

    // ==========================================
    // CONFIGURACIÓN DEL CAMPO
    // ==========================================

    private void configurarCampo() {

        txtBusqueda.setFont(FUENTE_NORMAL);

        txtBusqueda.setForeground(TEXTO);
        txtBusqueda.setBackground(SUPERFICIE);
        txtBusqueda.setCaretColor(PRIMARIO);

        txtBusqueda.setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_XS,
                ESPACIADO_SM,
                ESPACIADO_XS
        ));

        txtBusqueda.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        txtBusqueda.setMinimumSize(new Dimension(
                ANCHO_CAMPO_MINIMO,
                ALTO_CAMPO
        ));

        txtBusqueda.setPreferredSize(new Dimension(
                ANCHO_BUSQUEDA - 40,
                ALTO_BUSQUEDA
        ));

        txtBusqueda.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                ALTO_BUSQUEDA
        ));

        txtBusqueda.getAccessibleContext()
                .setAccessibleDescription(placeholder);

        // Redibujar el placeholder al recibir o perder el foco.
        txtBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txtBusqueda.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                txtBusqueda.repaint();
            }
        });
    }

    // ==========================================
    // CONFIGURACIÓN DEL BOTÓN
    // ==========================================

    private void configurarBoton() {

        btnLimpiar.setFont(FUENTE_ICONO);

        btnLimpiar.setForeground(TEXTO_SECUNDARIO);
        btnLimpiar.setBackground(SUPERFICIE);

        btnLimpiar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        btnLimpiar.setVerticalAlignment(
                SwingConstants.CENTER
        );

        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_XS,
                ESPACIADO_SM,
                ESPACIADO_XS,
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

        btnLimpiar.setPreferredSize(new Dimension(
                ALTO_CAMPO,
                ALTO_CAMPO
        ));

        btnLimpiar.setMinimumSize(new Dimension(
                ALTO_CAMPO,
                ALTO_CAMPO
        ));

        btnLimpiar.setMaximumSize(new Dimension(
                ALTO_CAMPO,
                ALTO_CAMPO
        ));

        btnLimpiar.getAccessibleContext()
                .setAccessibleName("Limpiar búsqueda");
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

    /**
     * Obtiene el contenido sin espacios al inicio o al final.
     */
    public String getText() {

        return txtBusqueda.getText().trim();
    }

    /**
     * Establece el contenido del campo.
     */
    public void setText(String texto) {

        txtBusqueda.setText(
                texto != null ? texto : ""
        );

        txtBusqueda.repaint();
    }

    /**
     * Limpia el campo y devuelve el foco al área de búsqueda.
     */
    public void limpiar() {

        txtBusqueda.setText("");

        txtBusqueda.requestFocusInWindow();

        txtBusqueda.repaint();
    }

    /**
     * Devuelve el campo de texto interno.
     */
    public JTextField getCampo() {

        return txtBusqueda;
    }

    // ==========================================
    // LISTENERS
    // ==========================================

    /**
     * Registra un listener para búsquedas al presionar Enter.
     */
    public void addBusquedaListener(ActionListener listener) {

        txtBusqueda.addActionListener(listener);
    }

    /**
     * Registra un listener para detectar cambios en el contenido.
     */
    public void addDocumentListener(DocumentListener listener) {

        txtBusqueda.getDocument().addDocumentListener(listener);
    }

    // ==========================================
    // CONFIGURACIÓN GENERAL
    // ==========================================

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        if (txtBusqueda == null || btnLimpiar == null) {
            return;
        }

        txtBusqueda.setEnabled(enabled);
        btnLimpiar.setEnabled(enabled);

        if (!enabled) {
            hoverLimpiar = false;
        }

        actualizarEstiloBoton();
    }
}
