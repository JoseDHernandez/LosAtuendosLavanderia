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

/**
 * Constantes de diseño y utilidades de estilo visual para la interfaz gráfica Swing.
 * Centraliza la paleta cromática (colores primarios, fondos, textos y estados), tipografías,
 * dimensiones estándar, márgenes, bordes redondeados y métodos auxiliares para componentes UI.
 */
public abstract class EstilosUI {

    // =====================================================
    // 1. COLORES
    // =====================================================

    // Identidad principal
    /** Color azul principal para elementos destacados y botones de acción. */
    public static final Color PRIMARIO =
            new Color(37, 99, 235);

    /** Color azul oscuro al situar el puntero (hover) sobre elementos primarios. */
    public static final Color PRIMARIO_HOVER =
            new Color(29, 78, 216);

    /** Tono azul claro utilizado en selecciones y estados activos suaves. */
    public static final Color PRIMARIO_CLARO =
            new Color(224, 237, 252);

    // Fondos y superficies
    /** Color de fondo general de las ventanas y paneles base. */
    public static final Color FONDO =
            new Color(245, 247, 250);

    /** Color blanco de superficie para tarjetas, tablas y contenedores. */
    public static final Color SUPERFICIE =
            Color.WHITE;

    /** Tono de superficie secundaria para alternancia de filas o fondos tenues. */
    public static final Color SUPERFICIE_SECUNDARIA =
            new Color(249, 250, 251);

    // Textos
    /** Color de texto principal de alto contraste (Gris oscuro). */
    public static final Color TEXTO =
            new Color(31, 41, 55);

    /** Color de texto secundario para subtítulos y descripciones. */
    public static final Color TEXTO_SECUNDARIO =
            new Color(107, 114, 128);

    /** Color atenuado para etiquetas o controles deshabilitados. */
    public static final Color TEXTO_DESHABILITADO =
            new Color(156, 163, 175);

    // Bordes
    /** Color sutil para líneas de separación y bordes de componentes. */
    public static final Color BORDE =
            new Color(229, 231, 235);

    // Estados: éxito
    /** Color verde para estados exitosos o prendas disponibles. */
    public static final Color EXITO =
            new Color(22, 163, 74);

    /** Fondo verde claro para insignias (badges) de éxito. */
    public static final Color EXITO_CLARO =
            new Color(220, 252, 231);

    // Estados: advertencia
    /** Color ámbar para alertas o estados pendientes. */
    public static final Color ADVERTENCIA =
            new Color(217, 119, 6);

    /** Fondo ámbar claro para insignias de advertencia. */
    public static final Color ADVERTENCIA_CLARO =
            new Color(254, 243, 199);

    // Estados: error
    /** Color rojo para mensajes de error o estados cancelados/con novedad. */
    public static final Color ERROR =
            new Color(220, 38, 38);

    /** Fondo rojo claro para alertas e insignias de error. */
    public static final Color ERROR_CLARO =
            new Color(254, 226, 226);

    // Navegación lateral
    /** Color de fondo para botones de navegación en reposo. */
    public static final Color NAV_NORMAL = FONDO;

    /** Color de fondo al situar el cursor sobre un botón de navegación. */
    public static final Color NAV_HOVER =
            new Color(232, 240, 250);

    /** Color de fondo para el módulo de navegación actualmente seleccionado. */
    public static final Color NAV_ACTIVO = PRIMARIO_CLARO;

    /** Color de texto para los enlaces del menú lateral. */
    public static final Color NAV_TEXTO =
            new Color(75, 85, 99);

    // =====================================================
    // 2. TIPOGRAFÍAS
    // =====================================================

    /** Familia tipográfica base para toda la interfaz gráfica. */
    public static final String FUENTE_PRINCIPAL =
            "Segoe UI";

    /** Fuente para títulos principales de pantalla (24pt negrita). */
    public static final Font FUENTE_TITULO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 24);

    /** Fuente para subtítulos destacados (18pt negrita). */
    public static final Font FUENTE_SUBTITULO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 18);

    /** Fuente para títulos de sección o encabezados de tarjeta (15pt negrita). */
    public static final Font FUENTE_SECCION =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 15);

    /** Fuente para textos regulares, celdas y campos de entrada (14pt regular). */
    public static final Font FUENTE_NORMAL =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 14);

    /** Fuente compacta para pies de página o aclaraciones (12pt regular). */
    public static final Font FUENTE_PEQUENA =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 12);

    /** Fuente para etiquetas de formulario (12pt negrita). */
    public static final Font FUENTE_ETIQUETA =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 12);

    /** Fuente de gran tamaño para tarjetas métricas o contadores del dashboard (27pt negrita). */
    public static final Font FUENTE_INDICADOR =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 27);

    /** Fuente del logotipo o cabecera de la marca (20pt negrita). */
    public static final Font FUENTE_LOGO =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 20);

    /** Fuente para iconos de texto (20pt regular). */
    public static final Font FUENTE_ICONO =
            new Font(FUENTE_PRINCIPAL, Font.PLAIN, 20);

    // =====================================================
    // 3. DIMENSIONES GENERALES
    // =====================================================

    // Ventana principal
    /** Ancho predeterminado al abrir la ventana principal. */
    public static final int ANCHO_VENTANA = 1280;

    /** Alto predeterminado de la ventana principal. */
    public static final int ALTO_VENTANA = 800;

    /** Ancho mínimo permitido para la ventana. */
    public static final int ANCHO_MINIMO = 1000;

    /** Alto mínimo permitido para la ventana. */
    public static final int ALTO_MINIMO = 650;

    // Navegación
    /** Ancho en píxeles del menú lateral de navegación. */
    public static final int ANCHO_MENU_LATERAL = 245;

    /** Alto estándar de los botones de navegación. */
    public static final int ALTO_BOTON = 46;

    /** Diámetro del avatar de usuario o icono de perfil. */
    public static final int TAMANO_AVATAR = 38;

    // Campos de entrada
    /** Altura estándar para campos de texto y combos. */
    public static final int ALTO_CAMPO = 42;

    /** Ancho mínimo para campos de formulario. */
    public static final int ANCHO_CAMPO_MINIMO = 180;

    // Botones
    /** Altura estándar para botones principales de acción. */
    public static final int ALTO_BOTON_PRINCIPAL = 42;

    /** Ancho mínimo para botones de acción. */
    public static final int ANCHO_BOTON_MINIMO = 100;

    // Tablas
    /** Altura estándar para cada fila de datos en las tablas Swing. */
    public static final int ALTO_FILA_TABLA = 42;

    /** Altura para la cabecera de columnas de la tabla. */
    public static final int ALTO_ENCABEZADO_TABLA = 44;

    /** Ancho mínimo para las columnas de una tabla. */
    public static final int ANCHO_COLUMNA_MINIMO = 80;

    // Búsqueda
    /** Ancho del campo de búsqueda global en encabezados. */
    public static final int ANCHO_BUSQUEDA = 320;

    /** Alto del campo de búsqueda. */
    public static final int ALTO_BUSQUEDA = 42;

    // Diálogos
    /** Ancho estándar para ventanas modales de confirmación. */
    public static final int ANCHO_DIALOGO = 420;

    /** Alto estándar para ventanas modales de confirmación. */
    public static final int ALTO_DIALOGO = 210;

    // Tarjetas
    /** Altura fija para tarjetas de métricas del dashboard. */
    public static final int ALTO_TARJETA_INDICADOR = 135;

    // =====================================================
    // 4. ESPACIADOS
    // =====================================================

    /** Espaciado extra pequeño (4px). */
    public static final int ESPACIADO_XS = 4;

    /** Espaciado pequeño (8px). */
    public static final int ESPACIADO_SM = 8;

    /** Espaciado mediano estándar (16px). */
    public static final int ESPACIADO_MD = 16;

    /** Espaciado grande (24px). */
    public static final int ESPACIADO_LG = 24;

    /** Espaciado extra grande (32px). */
    public static final int ESPACIADO_XL = 32;

    /** Margen perimetral para paneles completos (30px). */
    public static final int MARGEN_PANEL = 30;

    /** Separación vertical entre campos de un formulario (18px). */
    public static final int ESPACIADO_FORMULARIO = 18;

    /** Separación entre bloques o secciones lógicas (24px). */
    public static final int ESPACIADO_SECCION = 24;

    // =====================================================
    // 5. BORDES Y MÁRGENES
    // =====================================================

    /** Radio de curvatura para esquinas redondeadas. */
    public static final int RADIO_BORDE = 8;

    /** Borde lineal simple de 1px en color BORDE. */
    public static final Border BORDE_SIMPLE =
            new LineBorder(BORDE, 1);

    /** Borde para tarjetas y paneles contenedores. */
    public static final Border BORDE_PANEL =
            new LineBorder(BORDE, 1);

    /** Borde para cajas de texto y campos de selección. */
    public static final Border BORDE_CAMPO =
            new LineBorder(BORDE, 1);

    /** Margen interior estándar para paneles (24px en todos los costados). */
    public static final Border MARGEN_PANEL_ESTANDAR =
            new EmptyBorder(
                    ESPACIADO_LG,
                    ESPACIADO_LG,
                    ESPACIADO_LG,
                    ESPACIADO_LG
            );

    /** Margen amplio para la vista de dashboard principal. */
    public static final Border MARGEN_DASHBOARD =
            new EmptyBorder(28, 30, 30, 30);

    /** Margen interno compacto para componentes interactivos. */
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

    /**
     * Constructor protegido. Clase de utilidades estáticas: no requiere instanciación externa.
     */
    protected EstilosUI() {
        // Clase utilitaria: no requiere instancias.
    }

    /**
     * Crea una etiqueta {@link JLabel} configurada con texto, fuente y color específicos.
     *
     * @param texto Cadena de texto a mostrar.
     * @param fuente Tipografía a aplicar.
     * @param color Color del texto.
     * @return Etiqueta configurada.
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
     * Crea una etiqueta {@link JLabel} alineada a la izquierda con fuente y color específicos.
     *
     * @param texto Contenido textual.
     * @param fuente Tipografía.
     * @param color Color.
     * @return Etiqueta alineada a la izquierda.
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
     * Construye un {@link JPanel} opaco con el color de fondo suministrado.
     *
     * @param fondo Color de fondo.
     * @return Panel configurado.
     */
    public static JPanel crearPanel(Color fondo) {
        JPanel panel = new JPanel();

        panel.setBackground(fondo);
        panel.setOpaque(true);

        return panel;
    }

    /**
     * Aplica de forma rápida un color de fondo y activa la opacidad en un componente.
     *
     * @param componente Componente a estilizar.
     * @param color Color de fondo deseado.
     */
    public static void aplicarFondo(
            JComponent componente,
            Color color
    ) {
        componente.setBackground(color);
        componente.setOpaque(true);
    }

    /**
     * Configura conjuntamente la fuente y el color de primer plano de un componente.
     *
     * @param componente Componente Swing.
     * @param fuente Tipografía.
     * @param color Color del texto.
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
     * Aplica un borde vacío que actúa como margen interno de relleno (padding).
     *
     * @param componente Componente Swing.
     * @param superior Margen superior en píxeles.
     * @param izquierdo Margen izquierdo en píxeles.
     * @param inferior Margen inferior en píxeles.
     * @param derecho Margen derecho en píxeles.
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
     * Fija la dimensión preferida de un componente Swing.
     *
     * @param componente Componente a redimensionar.
     * @param ancho Ancho preferido en píxeles.
     * @param alto Alto preferido en píxeles.
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
