
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Contenedor común y reutilizable para organizar los campos
 * y las acciones de los formularios de la aplicación.
 *
 * Utiliza GridBagLayout para distribuir los campos y permite
 * configurar dinámicamente los botones de guardar y cancelar.
 */
public class PanelFormulario extends JPanel {

    private final JPanel panelCampos;
    private final JPanel panelAcciones;
    private final JScrollPane scrollCampos;

    private BotonPrincipal btnGuardar;
    private BotonSecundario btnCancelar;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /**
     * Construye un formulario con botones predeterminados.
     */
    public PanelFormulario() {
        this("Guardar", null, "Cancelar", null);
    }

    /**
     * Construye un formulario con acciones configurables.
     *
     * @param textoGuardar Texto del botón guardar.
     * @param accionGuardar Acción del botón guardar.
     * @param textoCancelar Texto del botón cancelar.
     * @param accionCancelar Acción del botón cancelar.
     */
    public PanelFormulario(
            String textoGuardar,
            ActionListener accionGuardar,
            String textoCancelar,
            ActionListener accionCancelar) {

        panelCampos = new JPanel(new GridBagLayout());

        panelAcciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        scrollCampos = new JScrollPane(panelCampos);

        configurarPanel();

        construirContenido(
                textoGuardar,
                accionGuardar,
                textoCancelar,
                accionCancelar
        );
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(0, ESPACIADO_LG));
        setBackground(SUPERFICIE);
        setOpaque(true);

        setBorder(new EmptyBorder(
                ESPACIADO_LG,
                ESPACIADO_LG,
                ESPACIADO_LG,
                ESPACIADO_LG
        ));

        panelCampos.setBackground(SUPERFICIE);
        panelCampos.setOpaque(true);

        panelCampos.setBorder(new EmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM
        ));

        scrollCampos.setBorder(null);

        scrollCampos.getViewport().setBackground(SUPERFICIE);
        scrollCampos.getViewport().setOpaque(true);

        scrollCampos.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollCampos.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        panelAcciones.setBackground(SUPERFICIE);
        panelAcciones.setOpaque(true);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL CONTENIDO
    // ==========================================

    private void construirContenido(
            String textoGuardar,
            ActionListener accionGuardar,
            String textoCancelar,
            ActionListener accionCancelar) {

        if (textoCancelar != null && !textoCancelar.isBlank()) {
            configurarBotonCancelar(
                    textoCancelar,
                    accionCancelar
            );
        }

        if (textoGuardar != null && !textoGuardar.isBlank()) {
            configurarBotonGuardar(
                    textoGuardar,
                    accionGuardar
            );
        }

        add(scrollCampos, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    // ==========================================
    // GESTIÓN DE CAMPOS
    // ==========================================

    /**
     * Agrega un componente ocupando una columna.
     *
     * @param componente Componente que se agregará.
     * @param fila Fila de ubicación.
     * @param columna Columna de ubicación.
     */
    public void agregarCampo(
            Component componente,
            int fila,
            int columna) {

        agregarCampo(componente, fila, columna, 1);
    }

    /**
     * Agrega un componente con posibilidad de ocupar varias columnas.
     *
     * @param componente Componente que se agregará.
     * @param fila Fila de ubicación.
     * @param columna Columna de ubicación.
     * @param anchoColumnas Cantidad de columnas que ocupará.
     */
    public void agregarCampo(
            Component componente,
            int fila,
            int columna,
            int anchoColumnas) {

        if (componente == null) {
            throw new IllegalArgumentException(
                    "El componente no puede ser null."
            );
        }

        if (fila < 0 || columna < 0 || anchoColumnas < 1) {
            throw new IllegalArgumentException(
                    "La posición y el ancho del campo no son válidos."
            );
        }

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.gridwidth = anchoColumnas;

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.insets = new Insets(
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM,
                ESPACIADO_SM
        );

        panelCampos.add(componente, gbc);

        actualizarFormulario();
    }

    /**
     * Agrega un separador horizontal.
     *
     * @param fila Fila de ubicación.
     * @param columna Columna de ubicación.
     * @param anchoColumnas Columnas que ocupará.
     */
    public void agregarSeparador(
            int fila,
            int columna,
            int anchoColumnas) {

        agregarCampo(
                new JSeparator(),
                fila,
                columna,
                anchoColumnas
        );
    }

    /**
     * Elimina todos los componentes del área de campos.
     */
    public void limpiarCampos() {

        panelCampos.removeAll();

        actualizarFormulario();
    }

    /**
     * Habilita o deshabilita los componentes del área de campos.
     *
     * @param habilitados Estado de habilitación.
     */
    public void setCamposHabilitados(boolean habilitados) {

        cambiarEstadoComponentes(
                panelCampos,
                habilitados
        );
    }

    /**
     * Cambia recursivamente el estado de habilitación
     * de los componentes contenidos en un contenedor.
     */
    private void cambiarEstadoComponentes(
            Container contenedor,
            boolean habilitados) {

        for (Component componente : contenedor.getComponents()) {

            componente.setEnabled(habilitados);

            if (componente instanceof Container hijo) {
                cambiarEstadoComponentes(
                        hijo,
                        habilitados
                );
            }
        }
    }

    /**
     * Retorna el panel interno donde se agregan los campos.
     */
    public JPanel getPanelCampos() {
        return panelCampos;
    }

    /**
     * Retorna el contenedor desplazable de los campos.
     */
    public JScrollPane getScrollCampos() {
        return scrollCampos;
    }

    // ==========================================
    // CONFIGURACIÓN DEL BOTÓN GUARDAR
    // ==========================================

    /**
     * Crea o actualiza el botón guardar.
     *
     * @param texto Texto del botón.
     * @param accion Acción asociada.
     */
    public void configurarBotonGuardar(
            String texto,
            ActionListener accion) {

        if (texto == null || texto.isBlank()) {
            ocultarBotonGuardar();
            return;
        }

        if (btnGuardar == null) {

            btnGuardar = new BotonPrincipal(texto);

            panelAcciones.add(btnGuardar);

        } else {

            btnGuardar.setText(texto);
        }

        setAccionGuardar(accion);

        btnGuardar.setVisible(true);

        actualizarFormulario();
    }

    /**
     * Oculta el botón guardar.
     */
    public void ocultarBotonGuardar() {

        if (btnGuardar != null) {
            btnGuardar.setVisible(false);
            actualizarFormulario();
        }
    }

    /**
     * Muestra el botón guardar si existe.
     */
    public void mostrarBotonGuardar() {

        if (btnGuardar != null) {
            btnGuardar.setVisible(true);
            actualizarFormulario();
        }
    }

    /**
     * Elimina el botón guardar.
     */
    public void eliminarBotonGuardar() {

        if (btnGuardar != null) {

            panelAcciones.remove(btnGuardar);
            btnGuardar = null;

            actualizarFormulario();
        }
    }

    // ==========================================
    // CONFIGURACIÓN DEL BOTÓN CANCELAR
    // ==========================================

    /**
     * Crea o actualiza el botón cancelar.
     *
     * @param texto Texto del botón.
     * @param accion Acción asociada.
     */
    public void configurarBotonCancelar(
            String texto,
            ActionListener accion) {

        if (texto == null || texto.isBlank()) {
            ocultarBotonCancelar();
            return;
        }

        if (btnCancelar == null) {

            btnCancelar = new BotonSecundario(texto);

            panelAcciones.add(btnCancelar, 0);

        } else {

            btnCancelar.setText(texto);
        }

        setAccionCancelar(accion);

        btnCancelar.setVisible(true);

        actualizarFormulario();
    }

    /**
     * Oculta el botón cancelar.
     */
    public void ocultarBotonCancelar() {

        if (btnCancelar != null) {
            btnCancelar.setVisible(false);
            actualizarFormulario();
        }
    }

    /**
     * Muestra el botón cancelar si existe.
     */
    public void mostrarBotonCancelar() {

        if (btnCancelar != null) {
            btnCancelar.setVisible(true);
            actualizarFormulario();
        }
    }

    /**
     * Elimina el botón cancelar.
     */
    public void eliminarBotonCancelar() {

        if (btnCancelar != null) {

            panelAcciones.remove(btnCancelar);
            btnCancelar = null;

            actualizarFormulario();
        }
    }

    // ==========================================
    // ACCESO A LOS BOTONES
    // ==========================================

    public BotonPrincipal getBotonGuardar() {
        return btnGuardar;
    }

    public BotonSecundario getBotonCancelar() {
        return btnCancelar;
    }

    public boolean tieneBotonGuardar() {
        return btnGuardar != null;
    }

    public boolean tieneBotonCancelar() {
        return btnCancelar != null;
    }

    // ==========================================
    // GESTIÓN DE ACCIONES
    // ==========================================

    /**
     * Reemplaza la acción del botón guardar.
     */
    public void setAccionGuardar(ActionListener accion) {

        reemplazarAccion(btnGuardar, accion);
    }

    /**
     * Reemplaza la acción del botón cancelar.
     */
    public void setAccionCancelar(ActionListener accion) {

        reemplazarAccion(btnCancelar, accion);
    }

    /**
     * Reemplaza los ActionListener de un botón.
     */
    private void reemplazarAccion(
            AbstractButton boton,
            ActionListener accion) {

        if (boton == null) {
            return;
        }

        for (ActionListener listener : boton.getActionListeners()) {
            boton.removeActionListener(listener);
        }

        if (accion != null) {
            boton.addActionListener(accion);
        }
    }

    /**
     * Habilita o deshabilita ambos botones.
     */
    public void setBotonesHabilitados(boolean habilitados) {

        if (btnGuardar != null) {
            btnGuardar.setEnabled(habilitados);
        }

        if (btnCancelar != null) {
            btnCancelar.setEnabled(habilitados);
        }
    }

    // ==========================================
    // ACTUALIZACIÓN VISUAL
    // ==========================================

    private void actualizarFormulario() {

        panelCampos.revalidate();
        panelCampos.repaint();

        panelAcciones.revalidate();
        panelAcciones.repaint();

        revalidate();
        repaint();
    }
}
