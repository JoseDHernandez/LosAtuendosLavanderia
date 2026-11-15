
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Botón personalizado para las opciones del menú lateral.
 *
 * Características:
 * - Adaptación automática al ancho disponible.
 * - Ajuste de tipografía para evitar textos cortados.
 * - Conservación del texto original.
 * - Estados visuales normal, hover, activo y deshabilitado.
 * - Dimensiones flexibles compatibles con BoxLayout.
 * - Accesibilidad mediante nombres y descripciones.
 */
public class BotonNavegacion extends JButton {

    // =====================================================
    // 1. ATRIBUTOS
    // =====================================================

    /** Tipografía base del botón. */
    private static final Font FUENTE_BASE = FUENTE_NORMAL;

    /** Tamaño mínimo permitido para la tipografía. */
    private static final float TAMANO_FUENTE_MINIMO = 10f;

    /** Tamaño máximo de la tipografía. */
    private static final float TAMANO_FUENTE_MAXIMO = 14f;

    /** Texto original del botón. */
    private final String textoOriginal;

    /** Indica si el módulo está seleccionado. */
    private boolean activo = false;

    /** Indica si el cursor está sobre el botón. */
    private boolean hover = false;

    /** Ancho disponible para el botón. */
    private int anchoDisponible = ANCHO_MENU_LATERAL - ESPACIADO_LG;

    /** Evita actualizaciones innecesarias de tipografía. */
    private boolean actualizandoFuente = false;

    // =====================================================
    // 2. CONSTRUCTOR
    // =====================================================

    /**
     * Construye un botón de navegación.
     *
     * @param icono Icono representativo del módulo.
     * @param texto Nombre del módulo.
     */
    public BotonNavegacion(Icon icono, String texto) {

        super(
                texto == null ? "" : texto,
                icono
        );

        this.textoOriginal = texto == null ? "" : texto;

        configurarBoton();
        configurarEventos();
        configurarAdaptabilidad();

        actualizarEstilo();
    }

    // =====================================================
    // 3. CONFIGURACIÓN GENERAL
    // =====================================================

    /**
     * Configura las propiedades visuales y geométricas.
     */
    private void configurarBoton() {

        setFont(FUENTE_BASE);
        setForeground(NAV_TEXTO);

        // Alineación del contenido.
        setHorizontalAlignment(SwingConstants.LEFT);

        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalAlignment(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);

        setIconTextGap(ESPACIADO_SM);

        // Márgenes internos.
        setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_MD - ESPACIADO_XS,
                        ESPACIADO_MD,
                        ESPACIADO_MD - ESPACIADO_XS,
                        ESPACIADO_MD - ESPACIADO_XS
                )
        );

        // Renderizado personalizado.
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);

        setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        setRolloverEnabled(false);

        // Dimensiones flexibles.
        setPreferredSize(
                new Dimension(
                        anchoDisponible,
                        ALTO_BOTON
                )
        );

        setMinimumSize(
                new Dimension(
                        0,
                        ALTO_BOTON
                )
        );

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_BOTON
                )
        );

        // Información accesible.
        setToolTipText(textoOriginal);

        getAccessibleContext().setAccessibleName(
                textoOriginal
        );
    }

    // =====================================================
    // 4. ADAPTABILIDAD
    // =====================================================

    /**
     * Registra los eventos necesarios para adaptar
     * la tipografía al tamaño real del botón.
     */
    private void configurarAdaptabilidad() {

        addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent e) {

                        ajustarTipografia();
                    }
                }
        );
    }

    /**
     * Permite establecer el ancho disponible desde MainFrame.
     *
     * @param ancho Ancho disponible en píxeles.
     */
    public void setAnchoDisponible(int ancho) {

        if (ancho <= 0) {
            return;
        }

        anchoDisponible = ancho;

        setPreferredSize(
                new Dimension(
                        anchoDisponible,
                        ALTO_BOTON
                )
        );

        ajustarTipografia();

        revalidate();
        repaint();
    }

    /**
     * Obtiene el ancho disponible configurado.
     *
     * @return Ancho del botón.
     */
    public int getAnchoDisponible() {
        return anchoDisponible;
    }

    /**
     * Ajusta el tamaño de la fuente según el espacio
     * horizontal disponible para mostrar el texto.
     *
     * Evita que el texto quede recortado cuando
     * disminuye el ancho del menú lateral.
     */
    private void ajustarTipografia() {

        if (actualizandoFuente) {
            return;
        }

        if (textoOriginal.isEmpty()) {
            return;
        }

        int anchoReal = getWidth();

        if (anchoReal <= 0) {
            anchoReal = anchoDisponible;
        }

        Insets margenes = getInsets();

        int anchoIcono = getIcon() != null
                ? getIcon().getIconWidth() + getIconTextGap()
                : 0;

        int anchoTexto = anchoReal
                - margenes.left
                - margenes.right
                - anchoIcono
                - ESPACIADO_XS;

        // Reducir la tipografía proporcionalmente al espacio.
        float tamano = TAMANO_FUENTE_MAXIMO;

        Font fuente = FUENTE_BASE.deriveFont(tamano);

        FontMetrics metricas = getFontMetrics(fuente);

        while (metricas.stringWidth(textoOriginal) > anchoTexto
                && tamano > TAMANO_FUENTE_MINIMO) {

            tamano -= 0.5f;

            fuente = FUENTE_BASE.deriveFont(tamano);

            metricas = getFontMetrics(fuente);
        }

        actualizandoFuente = true;

        try {

            setFont(fuente);

        } finally {

            actualizandoFuente = false;
        }

        repaint();
    }

    // =====================================================
    // 5. EVENTOS DEL RATÓN
    // =====================================================

    /**
     * Configura los estados hover del botón.
     */
    private void configurarEventos() {

        addMouseListener(
                new MouseAdapter() {

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
                }
        );
    }

    // =====================================================
    // 6. ESTADO ACTIVO
    // =====================================================

    /**
     * Establece si el botón representa el módulo actual.
     *
     * @param activo Estado de selección.
     */
    public void setActivo(boolean activo) {

        this.activo = activo;

        actualizarEstilo();
    }

    /**
     * Consulta si el botón está seleccionado.
     *
     * @return true si está activo.
     */
    public boolean isActivo() {
        return activo;
    }

    // =====================================================
    // 7. ACTUALIZACIÓN DE ESTILOS
    // =====================================================

    /**
     * Actualiza colores y tipografía según el estado.
     */
    private void actualizarEstilo() {

        if (!isEnabled()) {

            // Estado deshabilitado.
            setBackground(FONDO);

            setForeground(TEXTO_DESHABILITADO);

        } else if (activo) {

            // Módulo seleccionado.
            setBackground(NAV_ACTIVO);

            setForeground(PRIMARIO);

        } else if (hover) {

            // Cursor sobre el botón.
            setBackground(NAV_HOVER);

            setForeground(NAV_TEXTO);

        } else {

            // Estado normal.
            setBackground(NAV_NORMAL);

            setForeground(NAV_TEXTO);
        }

        // Actualizar tipografía conservando el estado.
        ajustarTipografia();

        repaint();
    }

    // =====================================================
    // 8. CONTROL DE HABILITACIÓN
    // =====================================================

    /**
     * Actualiza el estilo cuando cambia la habilitación.
     *
     * @param enabled Estado de habilitación.
     */
    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        if (!enabled) {
            hover = false;
        }

        actualizarEstilo();
    }

    // =====================================================
    // 9. ACTUALIZACIÓN DEL TEXTO
    // =====================================================

    /**
     * Obtiene el texto original completo.
     *
     * @return Texto descriptivo del módulo.
     */
    public String getTextoOriginal() {
        return textoOriginal;
    }
}
