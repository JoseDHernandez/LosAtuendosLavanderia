
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.BotonPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Presenta el encabezado visual reutilizable de los módulos
 * de la aplicación.
 *
 * Permite mostrar un título, una descripción y un botón
 * de acción opcional.
 */
public class PanelEncabezado extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblDescripcion;

    private BotonPrincipal btnAccion;
    private JPanel panelAcciones;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /**
     * Construye un encabezado sin botón de acción.
     *
     * @param titulo Título del módulo.
     * @param descripcion Descripción del módulo.
     */
    public PanelEncabezado(
            String titulo,
            String descripcion) {

        this(titulo, descripcion, null, null);
    }

    /**
     * Construye un encabezado con botón de acción opcional.
     *
     * @param titulo Título del módulo.
     * @param descripcion Descripción del módulo.
     * @param textoAccion Texto del botón.
     * @param accion Acción ejecutada al presionar el botón.
     */
    public PanelEncabezado(
            String titulo,
            String descripcion,
            String textoAccion,
            ActionListener accion) {

        lblTitulo = new JLabel(titulo);
        lblDescripcion = new JLabel(descripcion);

        configurarPanel();
        configurarTextos();
        construirContenido();

        if (textoAccion != null && !textoAccion.isBlank()) {
            configurarBotonAccion(textoAccion, accion);
        }
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(ESPACIADO_LG, 0));
        setBackground(FONDO);
        setOpaque(true);

        setBorder(BorderFactory.createEmptyBorder(
                0,
                0,
                ESPACIADO_LG,
                0
        ));
    }

    // ==========================================
    // CONFIGURACIÓN DE TEXTOS
    // ==========================================

    private void configurarTextos() {

        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(TEXTO);

        lblDescripcion.setFont(FUENTE_NORMAL);
        lblDescripcion.setForeground(TEXTO_SECUNDARIO);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL CONTENIDO
    // ==========================================

    private void construirContenido() {

        JPanel informacion = new JPanel();

        informacion.setLayout(
                new BoxLayout(informacion, BoxLayout.Y_AXIS)
        );

        informacion.setBackground(FONDO);
        informacion.setOpaque(true);

        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(lblTitulo);
        informacion.add(Box.createVerticalStrut(ESPACIADO_XS));
        informacion.add(lblDescripcion);

        add(informacion, BorderLayout.CENTER);

        panelAcciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        panelAcciones.setBackground(FONDO);
        panelAcciones.setOpaque(true);

        add(panelAcciones, BorderLayout.EAST);
    }

    // ==========================================
    // CONFIGURACIÓN DEL BOTÓN DE ACCIÓN
    // ==========================================

    /**
     * Crea o actualiza el botón de acción del encabezado.
     *
     * @param texto Texto que mostrará el botón.
     * @param accion Acción asociada al botón.
     */
    public void configurarBotonAccion(
            String texto,
            ActionListener accion) {

        if (texto == null || texto.isBlank()) {
            ocultarBotonAccion();
            return;
        }

        if (btnAccion == null) {

            btnAccion = new BotonPrincipal(texto);

            panelAcciones.add(btnAccion);

        } else {

            btnAccion.setText(texto);
        }

        setAccion(accion);

        btnAccion.setVisible(true);

        revalidate();
        repaint();
    }

    /**
     * Oculta el botón de acción sin eliminarlo.
     */
    public void ocultarBotonAccion() {

        if (btnAccion != null) {
            btnAccion.setVisible(false);
            revalidate();
            repaint();
        }
    }

    /**
     * Muestra nuevamente el botón si existe.
     */
    public void mostrarBotonAccion() {

        if (btnAccion != null) {
            btnAccion.setVisible(true);
            revalidate();
            repaint();
        }
    }

    /**
     * Elimina el botón de acción del encabezado.
     */
    public void eliminarBotonAccion() {

        if (btnAccion != null) {

            panelAcciones.remove(btnAccion);
            btnAccion = null;

            revalidate();
            repaint();
        }
    }

    // ==========================================
    // ACCESO Y ACTUALIZACIÓN DE TEXTOS
    // ==========================================

    public void setTitulo(String titulo) {
        lblTitulo.setText(titulo);
    }

    public void setDescripcion(String descripcion) {
        lblDescripcion.setText(descripcion);
    }

    public void setTextoAccion(String texto) {

        if (btnAccion != null) {
            btnAccion.setText(texto);
        }
    }

    // ==========================================
    // ACCIÓN DEL BOTÓN
    // ==========================================

    /**
     * Reemplaza la acción asociada al botón.
     * Si todavía no existe un botón, no realiza ninguna operación.
     *
     * Para crear el botón dinámicamente se debe utilizar
     * configurarBotonAccion().
     *
     * @param accion Nueva acción.
     */
    public void setAccion(ActionListener accion) {

        if (btnAccion == null) {
            return;
        }

        for (ActionListener listener :
                btnAccion.getActionListeners()) {

            btnAccion.removeActionListener(listener);
        }

        if (accion != null) {
            btnAccion.addActionListener(accion);
        }
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    public BotonPrincipal getBotonAccion() {
        return btnAccion;
    }

    public boolean tieneBotonAccion() {
        return btnAccion != null;
    }

    public boolean isBotonAccionVisible() {
        return btnAccion != null && btnAccion.isVisible();
    }
}
