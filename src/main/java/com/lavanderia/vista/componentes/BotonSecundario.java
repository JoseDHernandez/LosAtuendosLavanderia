
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Botón destinado a las acciones secundarias.
 *
 * Características:
 * - Utiliza dimensiones centralizadas en EstilosUI.
 * - Mantiene el texto en una sola línea.
 * - Alinea el icono a la izquierda del texto.
 * - Conserva los estados visuales normal, hover y deshabilitado.
 * - Permite actualizar su texto e icono.
 */
public class BotonSecundario extends JButton {

    private boolean hover;

    private String textoOriginal;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public BotonSecundario(String texto) {

        super(texto != null ? texto : "");

        this.textoOriginal = texto != null ? texto : "";

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

        // Alineación del contenido.
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        // Icono a la izquierda del texto.
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);

        setIconTextGap(ESPACIADO_SM);

        // Borde y márgenes internos.
        setBorder(BorderFactory.createCompoundBorder(
                BORDE_SIMPLE,
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_MD,
                        ESPACIADO_SM,
                        ESPACIADO_MD
                )
        ));

        // Comportamiento visual.
        setFocusPainted(false);
        setBorderPainted(true);
        setContentAreaFilled(true);
        setOpaque(true);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setRolloverEnabled(false);

        // Dimensiones centralizadas.
        setPreferredSize(dimensionBotonSecundario());

        setMinimumSize(new Dimension(
                ANCHO_BOTON_MINIMO,
                ALTO_BOTON_PRINCIPAL
        ));

        setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                ALTO_BOTON_PRINCIPAL
        ));

        // Accesibilidad.
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

    // ==========================================
    // MÉTODOS PÚBLICOS
    // ==========================================

    /**
     * Actualiza el texto del botón.
     */
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

    /**
     * Devuelve el texto original del botón.
     */
    public String getTextoOriginal() {

        return textoOriginal;
    }

    /**
     * Configura el icono del botón.
     */
    public void configurarIcono(Icon icono) {

        setIcon(icono);

        setIconTextGap(ESPACIADO_SM);

        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);

        revalidate();
        repaint();
    }
}
