package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonNavegacion extends JButton {

    private static final Color COLOR_NORMAL =
            new Color(245, 247, 250);

    private static final Color COLOR_HOVER =
            new Color(232, 240, 250);

    private static final Color COLOR_ACTIVO =
            new Color(224, 237, 252);

    private static final Color COLOR_TEXTO =
            new Color(75, 85, 99);

    private static final Color COLOR_PRIMARIO =
            new Color(37, 99, 235);

    private boolean activo = false;
    private boolean hover = false;

    public BotonNavegacion(String icono, String texto) {

        super(icono + "   " + texto);

        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(COLOR_TEXTO);
        setHorizontalAlignment(SwingConstants.LEFT);

        setBorder(BorderFactory.createEmptyBorder(
                12, 16, 12, 12
        ));

        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        actualizarEstilo();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                actualizarEstilo();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                actualizarEstilo();
            }
        });
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        actualizarEstilo();
    }

    private void actualizarEstilo() {

        if (activo) {
            setBackground(COLOR_ACTIVO);
            setForeground(COLOR_PRIMARIO);
            setFont(getFont().deriveFont(Font.BOLD));

        } else if (hover) {
            setBackground(COLOR_HOVER);
            setForeground(COLOR_TEXTO);
            setFont(getFont().deriveFont(Font.PLAIN));

        } else {
            setBackground(COLOR_NORMAL);
            setForeground(COLOR_TEXTO);
            setFont(getFont().deriveFont(Font.PLAIN));
        }
    }
}