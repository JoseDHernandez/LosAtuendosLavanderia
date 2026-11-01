
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class BotonSecundario extends JButton {

    private boolean hover = false;

    public BotonSecundario(String texto) {
        super(texto);

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

        setBorder(BorderFactory.createCompoundBorder(
                BORDE_SIMPLE,
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM + ESPACIADO_XS,
                        ESPACIADO_LG - ESPACIADO_XS,
                        ESPACIADO_SM + ESPACIADO_XS,
                        ESPACIADO_LG - ESPACIADO_XS
                )
        ));

        setFocusPainted(false);
        setBorderPainted(true);
        setContentAreaFilled(true);
        setOpaque(true);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(false);

        setPreferredSize(
                new Dimension(
                        ANCHO_BOTON_MINIMO,
                        ALTO_BOTON_PRINCIPAL
                )
        );
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
    // ACTUALIZACIÓN VISUAL
    // ==========================================

    private void actualizarEstilo() {

        if (!isEnabled()) {

            setBackground(FONDO);
            setForeground(TEXTO_DESHABILITADO);

        } else if (hover) {

            setBackground(NAV_HOVER);
            setForeground(TEXTO);

        } else {

            setBackground(SUPERFICIE);
            setForeground(TEXTO);
        }

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

    public void configurarIcono(Icon icono) {
        setIcon(icono);
        setIconTextGap(ESPACIADO_SM);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
    }
}
