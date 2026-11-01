
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class BotonNavegacion extends JButton {

    private static final Font FUENTE_BASE = FUENTE_NORMAL;

    private boolean activo = false;
    private boolean hover = false;

    public BotonNavegacion(Icon icono, String texto) {

        super(texto, icono);

        configurarBoton();
        configurarEventos();

        actualizarEstilo();
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private void configurarBoton() {

        setFont(FUENTE_BASE);
        setForeground(NAV_TEXTO);

        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(ESPACIADO_SM);

        setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_MD - ESPACIADO_XS,
                ESPACIADO_MD,
                ESPACIADO_MD - ESPACIADO_XS,
                ESPACIADO_MD - ESPACIADO_XS
        ));

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(false);

        setPreferredSize(
                new Dimension(
                        ANCHO_MENU_LATERAL - ESPACIADO_LG,
                        ALTO_BOTON
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
    // ESTADO DEL BOTÓN
    // ==========================================

    public void setActivo(boolean activo) {

        this.activo = activo;
        actualizarEstilo();
    }

    public boolean isActivo() {
        return activo;
    }

    // ==========================================
    // ACTUALIZACIÓN VISUAL
    // ==========================================

    private void actualizarEstilo() {

        if (!isEnabled()) {

            setBackground(FONDO);
            setForeground(TEXTO_DESHABILITADO);
            setFont(FUENTE_BASE);

        } else if (activo) {

            setBackground(NAV_ACTIVO);
            setForeground(PRIMARIO);
            setFont(FUENTE_BASE.deriveFont(Font.BOLD));

        } else if (hover) {

            setBackground(NAV_HOVER);
            setForeground(NAV_TEXTO);
            setFont(FUENTE_BASE);

        } else {

            setBackground(NAV_NORMAL);
            setForeground(NAV_TEXTO);
            setFont(FUENTE_BASE);
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
}
