
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public abstract class PanelModulo extends JPanel implements IModuloVista {

    private final String id;
    private String titulo;
    private final Icon icono;

    protected final PanelEncabezado encabezado;
    protected final JPanel panelContenido;

    /**
     * Constructor para módulos sin botón de acción inicial.
     */
    protected PanelModulo(
            String id,
            String titulo,
            String descripcion,
            Icon icono
    ) {
        this(id, titulo, descripcion, icono, null, null);
    }

    /**
     * Constructor para módulos con botón de acción inicial.
     */
    protected PanelModulo(
            String id,
            String titulo,
            String descripcion,
            Icon icono,
            String textoAccion,
            ActionListener accion
    ) {

        validarDatos(id, titulo);

        this.id = id.trim();
        this.titulo = titulo.trim();
        this.icono = icono;

        this.encabezado = new PanelEncabezado(
                this.titulo,
                descripcion,
                textoAccion,
                accion
        );

        this.panelContenido = new JPanel(new BorderLayout());

        configurarPanel();
        construirContenido();
    }

    /**
     * Valida los datos obligatorios del módulo.
     */
    private void validarDatos(String id, String titulo) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del módulo es obligatorio."
            );
        }

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título del módulo es obligatorio."
            );
        }
    }

    /**
     * Configura las propiedades visuales generales.
     */
    private void configurarPanel() {

        setLayout(new BorderLayout(0, ESPACIADO_MD));
        setBackground(FONDO);
        setOpaque(true);

        setBorder(new EmptyBorder(
                ESPACIADO_LG,
                MARGEN_PANEL,
                MARGEN_PANEL,
                MARGEN_PANEL
        ));

        panelContenido.setBackground(FONDO);
        panelContenido.setOpaque(true);
    }

    /**
     * Agrega los componentes principales del módulo.
     */
    private void construirContenido() {

        add(encabezado, BorderLayout.NORTH);
        add(panelContenido, BorderLayout.CENTER);
    }

    // ==================================================
    // CONTRATO IMODULOVISTA
    // ==================================================

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public Icon getIcono() {
        return icono;
    }

    @Override
    public JComponent getVista() {
        return this;
    }

    // ==================================================
    // ACCESO A COMPONENTES PARA SUBCLASES
    // ==================================================

    protected JPanel getPanelContenido() {
        return panelContenido;
    }

    protected PanelEncabezado getEncabezado() {
        return encabezado;
    }

    /**
     * Agrega un componente en el centro del contenido.
     *
     * Si ya existe un componente en CENTER,
     * será reemplazado por el nuevo.
     */
    protected void agregarContenido(Component componente) {

        validarComponente(componente);

        panelContenido.add(componente, BorderLayout.CENTER);

        actualizarVista();
    }

    /**
     * Agrega un componente en una posición específica.
     */
    protected void agregarContenido(
            Component componente,
            Object posicion
    ) {

        validarComponente(componente);

        if (posicion == null) {
            throw new IllegalArgumentException(
                    "La posición del componente es obligatoria."
            );
        }

        panelContenido.add(componente, posicion);

        actualizarVista();
    }

    /**
     * Elimina todos los componentes del área de contenido.
     */
    protected void limpiarContenido() {

        panelContenido.removeAll();

        actualizarVista();
    }

    /**
     * Valida que el componente no sea nulo.
     */
    private void validarComponente(Component componente) {

        if (componente == null) {
            throw new IllegalArgumentException(
                    "El componente que se desea agregar no puede ser nulo."
            );
        }
    }

    /**
     * Actualiza la presentación después de modificar el contenido.
     */
    private void actualizarVista() {

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==================================================
    // CONFIGURACIÓN DEL ENCABEZADO
    // ==================================================

    /**
     * Actualiza el título del módulo.
     */
    protected void setTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título no puede estar vacío."
            );
        }

        this.titulo = titulo.trim();
        encabezado.setTitulo(this.titulo);
    }

    /**
     * Actualiza la descripción del encabezado.
     */
    protected void setDescripcion(String descripcion) {
        encabezado.setDescripcion(descripcion);
    }

    /**
     * Actualiza el texto del botón de acción.
     */
    protected void setTextoAccion(String texto) {
        encabezado.setTextoAccion(texto);
    }

    /**
     * Actualiza la acción del botón existente.
     */
    protected void setAccion(ActionListener accion) {
        encabezado.setAccion(accion);
    }

    /**
     * Configura o crea dinámicamente el botón de acción.
     */
    protected void configurarAccion(
            String texto,
            ActionListener accion
    ) {
        encabezado.configurarBotonAccion(texto, accion);
    }

    /**
     * Oculta el botón de acción.
     */
    protected void ocultarAccion() {
        encabezado.ocultarBotonAccion();
    }

    /**
     * Muestra el botón de acción.
     */
    protected void mostrarAccion() {
        encabezado.mostrarBotonAccion();
    }

    /**
     * Elimina el botón de acción.
     */
    protected void eliminarAccion() {
        encabezado.eliminarBotonAccion();
    }

    /**
     * Indica si el encabezado tiene un botón de acción.
     */
    protected boolean tieneAccion() {
        return encabezado.tieneBotonAccion();
    }

}
