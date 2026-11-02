
package com.lavanderia.vista.paneles.comunes;

import com.lavanderia.vista.componentes.CampoBusqueda;
import com.lavanderia.vista.componentes.TablaDatos;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/** Contenedor visual común para presentar datos tabulares. */
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
        campoBusqueda = new CampoBusqueda(placeholderBusqueda);
        tablaDatos = new TablaDatos();

        panelSuperior = new JPanel();
        panelControles = new JPanel(new BorderLayout(ESPACIADO_MD, 0));
        panelFiltros = new JPanel(new FlowLayout(
                FlowLayout.LEFT, ESPACIADO_SM, 0
        ));
        panelAcciones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, ESPACIADO_SM, 0
        ));

        configurarPanel();
        construirContenido();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(0, ESPACIADO_MD));
        setBackground(FONDO);
        setOpaque(true);

        panelSuperior.setLayout(new BoxLayout(
                panelSuperior, BoxLayout.Y_AXIS
        ));
        panelSuperior.setBackground(FONDO);
        panelSuperior.setOpaque(true);

        panelControles.setBackground(FONDO);
        panelControles.setOpaque(true);

        panelFiltros.setBackground(FONDO);
        panelFiltros.setOpaque(true);

        panelAcciones.setBackground(FONDO);
        panelAcciones.setOpaque(true);
    }

    private void construirContenido() {
        panelControles.add(campoBusqueda, BorderLayout.WEST);
        panelControles.add(panelAcciones, BorderLayout.EAST);

        panelSuperior.add(panelControles);
        panelSuperior.add(Box.createVerticalStrut(ESPACIADO_MD));
        panelSuperior.add(panelFiltros);

        add(panelSuperior, BorderLayout.NORTH);
        add(tablaDatos, BorderLayout.CENTER);
    }

    // -------------------------
    // Búsqueda
    // -------------------------

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
        campoBusqueda.addBusquedaListener(listener);
    }

    // -------------------------
    // Tabla
    // -------------------------

    public TablaDatos getTablaDatos() {
        return tablaDatos;
    }

    public void setModelo(TableModel modelo) {
        tablaDatos.setModelo(modelo);
    }

    public TableModel getModelo() {
        return tablaDatos.getModelo();
    }

    // -------------------------
    // Filtros
    // -------------------------

    public void agregarFiltro(Component filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException(
                    "El filtro no puede ser null."
            );
        }

        panelFiltros.add(filtro);
        actualizarControles();
    }

    public void limpiarFiltros() {
        panelFiltros.removeAll();
        actualizarControles();
    }

    // -------------------------
    // Acciones
    // -------------------------

    public void agregarAccion(Component accion) {
        if (accion == null) {
            throw new IllegalArgumentException(
                    "La acción no puede ser null."
            );
        }

        panelAcciones.add(accion);
        actualizarControles();
    }

    public void limpiarAcciones() {
        panelAcciones.removeAll();
        actualizarControles();
    }

    // -------------------------
    // Configuración visual
    // -------------------------

    public void setPanelFiltrosVisible(boolean visible) {
        panelFiltros.setVisible(visible);
        actualizarControles();
    }

    public void setBusquedaVisible(boolean visible) {
        campoBusqueda.setVisible(visible);
        actualizarControles();
    }

    private void actualizarControles() {
        panelSuperior.revalidate();
        panelSuperior.repaint();
    }

    public void limpiar() {
        tablaDatos.limpiar();
        campoBusqueda.limpiar();
        limpiarFiltros();
    }
}
