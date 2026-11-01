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
     * Constructor para módulos sin botón de acción en el encabezado.
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
     * Constructor para módulos con botón de acción en el encabezado.
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

        this.id = id;
        this.titulo = titulo;
        this.icono = icono;

        this.encabezado = new PanelEncabezado(
                titulo,
                descripcion,
                textoAccion,
                accion
        );

        this.panelContenido = new JPanel(new BorderLayout());

        configurarPanel();
        construirContenido();
    }

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

    private void construirContenido() {
        add(encabezado, BorderLayout.NORTH);
        add(panelContenido, BorderLayout.CENTER);
    }

    // ==========================================
    // CONTRATO IMODULOVISTA
    // ==========================================

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

    // ==========================================
    // MÉTODOS PARA SUBCLASES
    // ==========================================

    protected JPanel getPanelContenido() {
        return panelContenido;
    }

    protected PanelEncabezado getEncabezado() {
        return encabezado;
    }

    protected void agregarContenido(Component componente) {
        panelContenido.add(componente, BorderLayout.CENTER);
    }

    protected void agregarContenido(Component componente, Object posicion) {
        panelContenido.add(componente, posicion);
    }

    protected void setTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título no puede estar vacío."
            );
        }

        this.titulo = titulo;
        encabezado.setTitulo(titulo);
    }

    protected void setDescripcion(String descripcion) {
        encabezado.setDescripcion(descripcion);
    }

    protected void setTextoAccion(String texto) {
        encabezado.setTextoAccion(texto);
    }

    protected void setAccion(ActionListener accion) {
        encabezado.setAccion(accion);
    }
}