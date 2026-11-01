
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class BotonPrincipal extends JButton {

    private boolean hover = false;

    public BotonPrincipal(String texto) {
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
        setForeground(Color.WHITE);
        setBackground(PRIMARIO);

        setHorizontalAlignment(SwingConstants.CENTER);

        setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_SM + ESPACIADO_XS,
                ESPACIADO_LG - ESPACIADO_XS,
                ESPACIADO_SM + ESPACIADO_XS,
                ESPACIADO_LG - ESPACIADO_XS
        ));

        setFocusPainted(false);
        setBorderPainted(false);
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

            setBackground(TEXTO_DESHABILITADO);
            setForeground(Color.WHITE);

        } else if (hover) {

            setBackground(PRIMARIO_HOVER);
            setForeground(Color.WHITE);

        } else {

            setBackground(PRIMARIO);
            setForeground(Color.WHITE);
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
