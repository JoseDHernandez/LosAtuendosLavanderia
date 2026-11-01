
package com.lavanderia.vista.estilos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public abstract class EstilosUI {

    // =====================================================
    // 1. COLORES
    // =====================================================

    // Identidad principal
    public static final Color PRIMARIO =
            new Color(37, 99, 235);

    public static final Color PRIMARIO_HOVER =
            new Color(29, 78, 216);

    public static final Color PRIMARIO_CLARO =
            new Color(224, 237, 252);

    // Fondos y superficies
    public static final Color FONDO =
            new Color(245, 247, 250);

    public static final Color SUPERFICIE =
            Color.WHITE;

    public static final Color SUPERFICIE_SECUNDARIA =
            new Color(249, 250, 251);

    // Textos
    public static final Color TEXTO =
            new Color(31, 41, 55);

    public static final Color TEXTO_SECUNDARIO =
            new Color(107, 114, 128);

    public static final Color TEXTO_DESHABILITADO =
            new Color(156, 163, 175);

    // Bordes
    public static final Color BORDE =
            new Color(229, 231, 235);

    // Estados: éxito
    public static final Color EXITO =
            new Color(22, 163, 74);

    public static final Color EXITO_CLARO =
            new Color(220, 252, 231);

    // Estados: advertencia
    public static final Color ADVERTENCIA =
            new Color(217, 119, 6);

    public static final Color ADVERTENCIA_CLARO =
            new Color(254, 243, 199);

    // Estados: error
    public static final Color ERROR =
            new Color(220, 38, 38);

    public static final Color ERROR_CLARO =
            new Color(254, 226, 226);

    // Navegación lateral
    public static final Color NAV_NORMAL = FONDO;

    public static final Color NAV_HOVER =
            new Color(232, 240, 250);

    public static final Color NAV_ACTIVO = PRIMARIO_CLARO;

    public static final Color NAV_TEXTO =
            new Color(75, 85, 99);

    // =====================================================
    // 2. TIPOGRAFÍAS
    // =====================================================

    public static final String FUENTE_PRINCIPAL =
            "Segoe UI";

    public static final Font FUENTE_TITULO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 24);

    public static final Font FUENTE_SUBTITULO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 18);

    public static final Font FUENTE_SECCION =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 15);

    public static final Font FUENTE_NORMAL =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 14);

    public static final Font FUENTE_PEQUENA =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 12);

    public static final Font FUENTE_ETIQUETA =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 12);

    public static final Font FUENTE_INDICADOR =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 27);

    public static final Font FUENTE_LOGO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 20);

    public static final Font FUENTE_ICONO =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 20);


    // =====================================================
    // 3. DIMENSIONES GENERALES
    // =====================================================

    // Ventana principal
    public static final int ANCHO_VENTANA = 1280;
    public static final int ALTO_VENTANA = 800;

    public static final int ANCHO_MINIMO = 1000;
    public static final int ALTO_MINIMO = 650;

    // Navegación
    public static final int ANCHO_MENU_LATERAL = 245;
    public static final int ALTO_BOTON = 46;
    public static final int TAMANO_AVATAR = 38;

    // Campos de entrada
    public static final int ALTO_CAMPO = 42;
    public static final int ANCHO_CAMPO_MINIMO = 180;

    // Botones
    public static final int ALTO_BOTON_PRINCIPAL = 42;
    public static final int ANCHO_BOTON_MINIMO = 100;

    // Tablas
    public static final int ALTO_FILA_TABLA = 42;
    public static final int ALTO_ENCABEZADO_TABLA = 44;
    public static final int ANCHO_COLUMNA_MINIMO = 80;

    // Búsqueda
    public static final int ANCHO_BUSQUEDA = 320;
    public static final int ALTO_BUSQUEDA = 42;

    // Diálogos
    public static final int ANCHO_DIALOGO = 420;
    public static final int ALTO_DIALOGO = 210;

    // Tarjetas
    public static final int ALTO_TARJETA_INDICADOR = 135;


    // =====================================================
    // 4. ESPACIADOS
    // =====================================================

    public static final int ESPACIADO_XS = 4;
    public static final int ESPACIADO_SM = 8;
    public static final int ESPACIADO_MD = 16;
    public static final int ESPACIADO_LG = 24;
    public static final int ESPACIADO_XL = 32;

    public static final int MARGEN_PANEL = 30;

    public static final int ESPACIADO_FORMULARIO = 18;
    public static final int ESPACIADO_SECCION = 24;


    // =====================================================
    // 5. BORDES Y MÁRGENES
    // =====================================================

    public static final int RADIO_BORDE = 8;

    public static final Border BORDE_SIMPLE =
            new LineBorder(BORDE, 1);

    public static final Border BORDE_PANEL =
            new LineBorder(BORDE, 1);

    public static final Border BORDE_CAMPO =
            new LineBorder(BORDE, 1);

    public static final Border MARGEN_PANEL_ESTANDAR =
            new EmptyBorder(
                    ESPACIADO_LG,
                    ESPACIADO_LG,
                    ESPACIADO_LG,
                    ESPACIADO_LG
            );

    public static final Border MARGEN_DASHBOARD =
            new EmptyBorder(28, 30, 30, 30);

    public static final Border MARGEN_COMPONENTE =
            new EmptyBorder(
                    ESPACIADO_SM,
                    ESPACIADO_MD,
                    ESPACIADO_SM,
                    ESPACIADO_MD
            );


    // =====================================================
    // 6. MÉTODOS AUXILIARES
    // =====================================================

    protected EstilosUI() {
        // Clase utilitaria: no requiere instancias.
    }

    /**
     * Crea una etiqueta con fuente y color personalizados.
     */
    public static JLabel crearEtiqueta(
            String texto,
            Font fuente,
            Color color
    ) {
        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);

        return etiqueta;
    }

    /**
     * Crea una etiqueta alineada a la izquierda.
     */
    public static JLabel crearEtiquetaIzquierda(
            String texto,
            Font fuente,
            Color color
    ) {
        JLabel etiqueta = crearEtiqueta(texto, fuente, color);

        etiqueta.setHorizontalAlignment(SwingConstants.LEFT);

        return etiqueta;
    }

    /**
     * Crea un panel con un color de fondo.
     */
    public static JPanel crearPanel(Color fondo) {
        JPanel panel = new JPanel();

        panel.setBackground(fondo);
        panel.setOpaque(true);

        return panel;
    }

    /**
     * Aplica un color de fondo a un componente.
     */
    public static void aplicarFondo(
            JComponent componente,
            Color color
    ) {
        componente.setBackground(color);
        componente.setOpaque(true);
    }

    /**
     * Aplica una fuente y un color de texto.
     */
    public static void aplicarTexto(
            JComponent componente,
            Font fuente,
            Color color
    ) {
        componente.setFont(fuente);
        componente.setForeground(color);
    }

    /**
     * Aplica un borde con margen interno.
     */
    public static void aplicarMargen(
            JComponent componente,
            int superior,
            int izquierdo,
            int inferior,
            int derecho
    ) {
        componente.setBorder(
                BorderFactory.createEmptyBorder(
                        superior,
                        izquierdo,
                        inferior,
                        derecho
                )
        );
    }

    /**
     * Configura dimensiones preferidas.
     */
    public static void aplicarDimension(
            Component componente,
            int ancho,
            int alto
    ) {
        componente.setPreferredSize(
                new Dimension(ancho, alto)
        );
    }
}
