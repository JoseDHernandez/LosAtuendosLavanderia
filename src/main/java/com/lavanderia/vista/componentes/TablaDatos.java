
package com.lavanderia.vista.componentes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class TablaDatos extends JPanel {

    private final JTable tabla;
    private final JScrollPane scroll;

    private TableRowSorter<TableModel> ordenador;

    private boolean ordenamientoHabilitado = true;

    public TablaDatos() {

        tabla = new JTable();
        scroll = new JScrollPane(tabla);

        configurarPanel();
        configurarTabla();
        configurarScroll();
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout());
        setBackground(SUPERFICIE);
        setOpaque(true);
    }

    // ==========================================
    // CONFIGURACIÓN DE LA TABLA
    // ==========================================

    private void configurarTabla() {

        tabla.setFont(FUENTE_NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);

        tabla.setRowHeight(ALTO_FILA_TABLA);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setSelectionBackground(PRIMARIO_CLARO);
        tabla.setSelectionForeground(TEXTO);

        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(false);

        configurarEncabezado();
        configurarCeldas();
    }

    // ==========================================
    // CONFIGURACIÓN DEL ENCABEZADO
    // ==========================================

    private void configurarEncabezado() {

        tabla.getTableHeader().setFont(FUENTE_ETIQUETA);
        tabla.getTableHeader().setForeground(TEXTO_SECUNDARIO);
        tabla.getTableHeader().setBackground(SUPERFICIE_SECUNDARIA);

        tabla.getTableHeader().setPreferredSize(
                new Dimension(0, ALTO_ENCABEZADO_TABLA)
        );

        tabla.getTableHeader().setReorderingAllowed(false);
    }

    // ==========================================
    // CONFIGURACIÓN DE LAS CELDAS
    // ==========================================

    private void configurarCeldas() {

        DefaultTableCellRenderer renderizador =
                new DefaultTableCellRenderer();

        renderizador.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        ESPACIADO_MD - ESPACIADO_XS,
                        0,
                        ESPACIADO_MD - ESPACIADO_XS
                )
        );

        renderizador.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        tabla.setDefaultRenderer(
                Object.class,
                renderizador
        );
    }

    // ==========================================
    // CONFIGURACIÓN DEL SCROLL
    // ==========================================

    private void configurarScroll() {

        scroll.setBorder(BORDE_PANEL);
        scroll.getViewport().setBackground(SUPERFICIE);

        scroll.setBackground(SUPERFICIE);

        add(scroll, BorderLayout.CENTER);
    }

    // ==========================================
    // MODELO DE DATOS
    // ==========================================

    public void setModelo(TableModel modelo) {

        if (modelo == null) {
            throw new IllegalArgumentException(
                    "El modelo de la tabla no puede ser nulo."
            );
        }

        tabla.setModel(modelo);

        configurarCeldas();
        configurarOrdenamiento();
    }

    public TableModel getModelo() {
        return tabla.getModel();
    }

    private void configurarOrdenamiento() {

        if (ordenamientoHabilitado) {

            ordenador = new TableRowSorter<>(tabla.getModel());

            tabla.setRowSorter(ordenador);

        } else {

            ordenador = null;
            tabla.setRowSorter(null);
        }
    }

    // ==========================================
    // ACCESO A LA TABLA
    // ==========================================

    public JTable getTabla() {
        return tabla;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    // ==========================================
    // SELECCIÓN DE REGISTROS
    // ==========================================

    public int getFilaSeleccionada() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return -1;
        }

        return tabla.convertRowIndexToModel(fila);
    }

    public boolean haySeleccion() {
        return tabla.getSelectedRow() != -1;
    }

    public void limpiarSeleccion() {
        tabla.clearSelection();
    }

    public int[] getFilasSeleccionadas() {

        int[] filasVista = tabla.getSelectedRows();

        int[] filasModelo = new int[filasVista.length];

        for (int i = 0; i < filasVista.length; i++) {

            filasModelo[i] = tabla.convertRowIndexToModel(
                    filasVista[i]
            );
        }

        return filasModelo;
    }

    public void setSeleccionMultiple(boolean multiple) {

        tabla.setSelectionMode(
                multiple
                        ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                        : ListSelectionModel.SINGLE_SELECTION
        );
    }

    // ==========================================
    // ORDENAMIENTO
    // ==========================================

    public void setOrdenamientoHabilitado(boolean habilitado) {

        ordenamientoHabilitado = habilitado;

        configurarOrdenamiento();
    }

    // ==========================================
    // CONFIGURACIÓN DE COLUMNAS
    // ==========================================

    public void setAnchoColumna(int columna, int ancho) {

        if (columna < 0 || columna >= tabla.getColumnCount()) {
            return;
        }

        if (ancho <= 0) {
            throw new IllegalArgumentException(
                    "El ancho de columna debe ser mayor que cero."
            );
        }

        tabla.getColumnModel()
                .getColumn(columna)
                .setPreferredWidth(ancho);
    }

    public void setEncabezadoVisible(boolean visible) {

        tabla.getTableHeader().setVisible(visible);

        scroll.setColumnHeaderView(
                visible ? tabla.getTableHeader() : null
        );
    }

    // ==========================================
    // LIMPIEZA
    // ==========================================

    public void limpiar() {

        setModelo(new DefaultTableModel());
        limpiarSeleccion();
    }
}
