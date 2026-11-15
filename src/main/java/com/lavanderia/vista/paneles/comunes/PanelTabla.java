
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.CampoBusqueda;
import com.lavanderia.vista.componentes.TablaDatos;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Contenedor visual común para presentar datos tabulares.
 *
 * Centraliza la búsqueda, los filtros, las acciones
 * y la visualización de una tabla.
 */
public class PanelTabla extends JPanel {

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaDatos;

    private final JPanel panelSuperior;
    private final JPanel panelControles;
    private final JPanel panelFiltros;
    private final JPanel panelAcciones;

    public PanelTabla() {
        this("Buscar registros...");
    }

    public PanelTabla(String placeholderBusqueda) {

        if (placeholderBusqueda == null || placeholderBusqueda.isBlank()) {
            throw new IllegalArgumentException(
                    "El texto de búsqueda no puede estar vacío."
            );
        }

        campoBusqueda = new CampoBusqueda(placeholderBusqueda);
        tablaDatos = new TablaDatos();

        panelSuperior = new JPanel();
        panelControles = new JPanel(
                new BorderLayout(ESPACIADO_MD, 0)
        );
        panelFiltros = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        ESPACIADO_SM,
                        0
                )
        );
        panelAcciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        configurarPanel();
        construirContenido();
    }

    // ==================================================
    // CONFIGURACIÓN INICIAL
    // ==================================================

    private void configurarPanel() {

        setLayout(new BorderLayout(0, ESPACIADO_MD));
        setBackground(FONDO);
        setOpaque(true);

        panelSuperior.setLayout(
                new BoxLayout(
                        panelSuperior,
                        BoxLayout.Y_AXIS
                )
        );

        configurarPanelInterno(panelSuperior);
        configurarPanelInterno(panelControles);
        configurarPanelInterno(panelFiltros);
        configurarPanelInterno(panelAcciones);
    }

    private void configurarPanelInterno(JPanel panel) {
        panel.setBackground(FONDO);
        panel.setOpaque(true);
    }

    private void construirContenido() {

        panelControles.add(
                campoBusqueda,
                BorderLayout.WEST
        );

        panelControles.add(
                panelAcciones,
                BorderLayout.EAST
        );

        panelSuperior.add(panelControles);

        panelSuperior.add(
                Box.createVerticalStrut(ESPACIADO_MD)
        );

        panelSuperior.add(panelFiltros);

        add(panelSuperior, BorderLayout.NORTH);
        add(tablaDatos, BorderLayout.CENTER);
    }

    // ==================================================
    // BÚSQUEDA
    // ==================================================

    public CampoBusqueda getCampoBusqueda() {
        return campoBusqueda;
    }

    public String getTextoBusqueda() {
        return campoBusqueda.getText();
    }

    public void limpiarBusqueda() {
        campoBusqueda.limpiar();
    }

    public void addBusquedaListener(ActionListener listener) {

        validarListener(listener);

        campoBusqueda.addBusquedaListener(listener);
    }

    public void setBusquedaVisible(boolean visible) {

        campoBusqueda.setVisible(visible);

        actualizarControles();
    }

    public boolean isBusquedaVisible() {
        return campoBusqueda.isVisible();
    }

    // ==================================================
    // TABLA
    // ==================================================

    public TablaDatos getTablaDatos() {
        return tablaDatos;
    }

    public void setModelo(TableModel modelo) {

        if (modelo == null) {
            throw new IllegalArgumentException(
                    "El modelo de la tabla no puede ser null."
            );
        }

        tablaDatos.setModelo(modelo);
    }

    public TableModel getModelo() {
        return tablaDatos.getModelo();
    }

    // ==================================================
    // FILTROS
    // ==================================================

    public void agregarFiltro(Component filtro) {

        validarComponente(filtro, "El filtro");

        panelFiltros.add(filtro);

        actualizarControles();
    }

    public void eliminarFiltro(Component filtro) {

        if (filtro == null) {
            return;
        }

        panelFiltros.remove(filtro);

        actualizarControles();
    }

    public void limpiarFiltros() {

        panelFiltros.removeAll();

        actualizarControles();
    }

    public void setPanelFiltrosVisible(boolean visible) {

        panelFiltros.setVisible(visible);

        actualizarControles();
    }

    public boolean isPanelFiltrosVisible() {
        return panelFiltros.isVisible();
    }

    // ==================================================
    // ACCIONES
    // ==================================================

    public void agregarAccion(Component accion) {

        validarComponente(accion, "La acción");

        panelAcciones.add(accion);

        actualizarControles();
    }

    public void eliminarAccion(Component accion) {

        if (accion == null) {
            return;
        }

        panelAcciones.remove(accion);

        actualizarControles();
    }

    public void limpiarAcciones() {

        panelAcciones.removeAll();

        actualizarControles();
    }

    public boolean tieneAcciones() {
        return panelAcciones.getComponentCount() > 0;
    }

    // ==================================================
    // ACCESO A PANELES INTERNOS
    // ==================================================

    public JPanel getPanelFiltros() {
        return panelFiltros;
    }

    public JPanel getPanelAcciones() {
        return panelAcciones;
    }

    public JPanel getPanelControles() {
        return panelControles;
    }

    // ==================================================
    // LIMPIEZA
    // ==================================================

    /**
     * Limpia los datos de la tabla y los controles de búsqueda.
     * Conserva las acciones y filtros configurados.
     */
    public void limpiarDatos() {

        tablaDatos.limpiar();
        campoBusqueda.limpiar();

        actualizarControles();
    }

    /**
     * Limpia todos los elementos dinámicos del panel.
     */
    public void limpiar() {

        tablaDatos.limpiar();
        campoBusqueda.limpiar();

        panelFiltros.removeAll();
        panelAcciones.removeAll();

        actualizarControles();
    }

    // ==================================================
    // UTILIDADES
    // ==================================================

    private void validarComponente(
            Component componente,
            String nombre
    ) {

        if (componente == null) {
            throw new IllegalArgumentException(
                    nombre + " no puede ser null."
            );
        }
    }

    private void validarListener(ActionListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException(
                    "El listener de búsqueda no puede ser null."
            );
        }
    }

    private void actualizarControles() {

        panelSuperior.revalidate();
        panelSuperior.repaint();

        revalidate();
        repaint();
    }
}
