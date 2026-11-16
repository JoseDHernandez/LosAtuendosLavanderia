
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Botón destinado a las acciones secundarias.
 *
 * El fondo se pinta manualmente para evitar que el Look and Feel
 * de Swing introduzca superficies blancas no deseadas.
 */
public class BotonSecundario extends JButton {

    private boolean hover;
    private String textoOriginal;

    public BotonSecundario(String texto) {

        super(texto != null ? texto : "");

        textoOriginal = texto != null ? texto : "";

        configurarBoton();
        configurarEventos();
        actualizarEstilo();
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private void configurarBoton() {

        setFont(FUENTE_ETIQUETA);
        setForeground(TEXTO);
        setBackground(SUPERFICIE);

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setIconTextGap(ESPACIADO_SM);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BORDE_SIMPLE,
                        BorderFactory.createEmptyBorder(
                                ESPACIADO_SM,
                                ESPACIADO_MD,
                                ESPACIADO_SM,
                                ESPACIADO_MD
                        )
                )
        );

        // Evitar el pintado predeterminado del Look and Feel.
        setFocusPainted(false);
        setBorderPainted(true);
        setContentAreaFilled(false);
        setOpaque(false);

        setRolloverEnabled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setPreferredSize(dimensionBotonSecundario());

        setMinimumSize(
                new Dimension(
                        ANCHO_BOTON_MINIMO,
                        ALTO_BOTON_PRINCIPAL
                )
        );

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_BOTON_PRINCIPAL
                )
        );

        getAccessibleContext().setAccessibleName(
                textoOriginal
        );

        setToolTipText(textoOriginal);
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (isEnabled()) {
                    hover = true;
                    actualizarEstilo();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                hover = false;
                actualizarEstilo();
            }
        });
    }

    // ==========================================
    // PINTADO PERSONALIZADO
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color fondo;

            if (!isEnabled()) {
                fondo = FONDO;
            } else if (hover) {
                fondo = NAV_HOVER;
            } else {
                fondo = SUPERFICIE;
            }

            g2.setColor(fondo);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    8,
                    8
            );

        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }

    // ==========================================
    // ESTILO
    // ==========================================

    private void actualizarEstilo() {

        setForeground(
                isEnabled()
                        ? TEXTO
                        : TEXTO_DESHABILITADO
        );

        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        if (!enabled) {
            hover = false;
        }

        actualizarEstilo();
    }

    // ==========================================
    // MÉTODOS PÚBLICOS
    // ==========================================

    public void setTexto(String texto) {

        textoOriginal = texto != null ? texto : "";

        super.setText(textoOriginal);

        getAccessibleContext().setAccessibleName(
                textoOriginal
        );

        setToolTipText(textoOriginal);

        revalidate();
        repaint();
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public void configurarIcono(Icon icono) {

        setIcon(icono);

        setIconTextGap(ESPACIADO_SM);

        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);

        revalidate();
        repaint();
    }
}
