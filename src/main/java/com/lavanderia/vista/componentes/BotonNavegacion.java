package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Componente de botón personalizado para las opciones del menú lateral de navegación.
 * Extiende {@link JButton} y gestiona tres estados visuales:
 * <ul>
 *   <li><b>Normal:</b> Fondo transparente/neutro y texto en color suave.</li>
 *   <li><b>Hover:</b> Fondo sombreado al situar el puntero del ratón.</li>
 *   <li><b>Activo:</b> Fondo primario tenue, texto azul y tipografía en negrita para el módulo actual.</li>
 * </ul>
 */
public class BotonNavegacion extends JButton {

    /** Tipografía base utilizada para los botones del menú. */
    private static final Font FUENTE_BASE = FUENTE_NORMAL;

    /** Indica si el botón representa el módulo que se encuentra activo actualmente. */
    private boolean activo = false;

    /** Indica si el cursor del ratón se encuentra posicionado sobre el botón. */
    private boolean hover = false;

    /**
     * Construye un nuevo botón de navegación con icono y texto.
     *
     * @param icono Icono representativo de la opción.
     * @param texto Etiqueta descriptiva del módulo.
     */
    public BotonNavegacion(Icon icono, String texto) {

        super(texto, icono);

        // Configurar geometría, colores y bordes
        configurarBoton();
        // Registrar escuchadores de ratón para interactividad
        configurarEventos();

        // Aplicar el estilo visual inicial
        actualizarEstilo();
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    /**
     * Ajusta la alineación, espaciados internos, cursor de mano y dimensiones del botón.
     */
    private void configurarBoton() {

        setFont(FUENTE_BASE);
        setForeground(NAV_TEXTO);

        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(ESPACIADO_SM);

        // Margen interno de relleno
        setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_MD - ESPACIADO_XS,
                ESPACIADO_MD,
                ESPACIADO_MD - ESPACIADO_XS,
                ESPACIADO_MD - ESPACIADO_XS
        ));

        // Desactivar decoración nativa para control manual del renderizado
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

    /**
     * Registra eventos de ratón para detectar estados de hover (entrada y salida del cursor).
     */
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

    /**
     * Define si el botón pasa a representar el módulo actualmente abierto.
     *
     * @param activo {@code true} para activarlo, {@code false} para devolverlo a estado normal.
     */
    public void setActivo(boolean activo) {

        this.activo = activo;
        actualizarEstilo();
    }

    /**
     * Consulta si el botón se encuentra en estado activo.
     *
     * @return {@code true} si está activo.
     */
    public boolean isActivo() {
        return activo;
    }

    // ==========================================
    // ACTUALIZACIÓN VISUAL
    // ==========================================

    /**
     * Actualiza la combinación de colores y pesos tipográficos según los estados de habilitación, hover y selección.
     */
    private void actualizarEstilo() {

        if (!isEnabled()) {

            // Estado deshabilitado
            setBackground(FONDO);
            setForeground(TEXTO_DESHABILITADO);
            setFont(FUENTE_BASE);

        } else if (activo) {

            // Módulo seleccionado: fondo azul claro y tipografía negrita
            setBackground(NAV_ACTIVO);
            setForeground(PRIMARIO);
            setFont(FUENTE_BASE.deriveFont(Font.BOLD));

        } else if (hover) {

            // Puntero encima: fondo hover sutil
            setBackground(NAV_HOVER);
            setForeground(NAV_TEXTO);
            setFont(FUENTE_BASE);

        } else {

            // Estado de reposo
            setBackground(NAV_NORMAL);
            setForeground(NAV_TEXTO);
            setFont(FUENTE_BASE);
        }

        repaint();
    }

    /**
     * Sobrescribe la habilitación del componente asegurando la actualización estética.
     *
     * @param enabled {@code true} para habilitar, {@code false} para deshabilitar.
     */
    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        if (!enabled) {
            hover = false;
        }

        actualizarEstilo();
    }
}
