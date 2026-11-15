
package com.lavanderia.vista.componentes;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class TablaDatos extends JPanel {

    private final JTable tabla;
    private final JScrollPane scroll;

    private TableRowSorter<TableModel> ordenador;

    private boolean ordenamientoHabilitado = true;
    private boolean ajustandoColumnas = false;

    private TableModel modeloEscuchado;

    private final TableModelListener listenerModelo =
            this::cuandoCambianLosDatos;

    public TablaDatos() {

        tabla = new JTable();
        scroll = new JScrollPane(tabla);

        configurarPanel();
        configurarTabla();
        configurarScroll();
        configurarRedimensionamiento();
    }

    // ==========================================
    // PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout());
        setBackground(SUPERFICIE);
        setOpaque(true);

        setMinimumSize(new Dimension(0, 0));
    }

    // ==========================================
    // TABLA
    // ==========================================

    private void configurarTabla() {

        tabla.setFont(FUENTE_NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);

        tabla.setRowHeight(ALTO_FILA_TABLA);
        tabla.setRowMargin(0);

        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setSelectionBackground(PRIMARIO_CLARO);
        tabla.setSelectionForeground(TEXTO);

        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(false);

        // El tamaño de las columnas se calcula manualmente.
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        configurarEncabezado();
        configurarRenderizadorCeldas();
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private void configurarEncabezado() {

        JTableHeaderEstilizado renderizador =
                new JTableHeaderEstilizado();

        tabla.getTableHeader().setDefaultRenderer(renderizador);

        tabla.getTableHeader().setFont(FUENTE_ETIQUETA);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setBackground(PRIMARIO);
        tabla.getTableHeader().setOpaque(true);

        tabla.getTableHeader().setPreferredSize(
                new Dimension(0, ALTO_ENCABEZADO_TABLA)
        );

        tabla.getTableHeader().setMinimumSize(
                new Dimension(0, ALTO_ENCABEZADO_TABLA)
        );

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setResizingAllowed(true);
    }

    /**
     * Renderizador del encabezado con fondo primario.
     */
    private class JTableHeaderEstilizado
            extends DefaultTableCellRenderer {

        public JTableHeaderEstilizado() {

            setOpaque(true);
            setBackground(PRIMARIO);
            setForeground(Color.WHITE);

            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.CENTER);

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(
                                    0, 0, 1, 1,
                                    new Color(255, 255, 255, 65)
                            ),
                            BorderFactory.createEmptyBorder(
                                    0,
                                    ESPACIADO_MD - ESPACIADO_XS,
                                    0,
                                    ESPACIADO_MD - ESPACIADO_XS
                            )
                    )
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setBackground(PRIMARIO);
            setForeground(Color.WHITE);

            return this;
        }
    }

    // ==========================================
    // CELDAS Y FILAS ALTERNADAS
    // ==========================================

    private void configurarRenderizadorCeldas() {

        DefaultTableCellRenderer renderizador =
                new RenderizadorCelda();

        tabla.setDefaultRenderer(Object.class, renderizador);
        tabla.setDefaultRenderer(String.class, renderizador);
    }

    private class RenderizadorCelda
            extends DefaultTableCellRenderer {

        public RenderizadorCelda() {

            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            ESPACIADO_MD - ESPACIADO_XS,
                            0,
                            ESPACIADO_MD - ESPACIADO_XS
                    )
            );

            setVerticalAlignment(SwingConstants.CENTER);

            // Alternancia visual de registros.
            if (!isSelected) {

                setBackground(
                        row % 2 == 0
                                ? SUPERFICIE
                                : SUPERFICIE_SECUNDARIA
                );

                setForeground(TEXTO);
            }

            // Muestra el contenido completo al pasar el cursor.
            setToolTipText(
                    value != null ? value.toString() : null
            );

            return this;
        }
    }

    // ==========================================
    // SCROLL
    // ==========================================

    private void configurarScroll() {

        scroll.setBorder(BORDE_PANEL);

        scroll.getViewport().setBackground(SUPERFICIE);
        scroll.setBackground(SUPERFICIE);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scroll.getHorizontalScrollBar().setUnitIncrement(
                ESPACIADO_LG
        );

        scroll.getVerticalScrollBar().setUnitIncrement(
                ALTO_FILA_TABLA
        );

        add(scroll, BorderLayout.CENTER);
    }

    // ==========================================
    // REDIMENSIONAMIENTO
    // ==========================================

    private void configurarRedimensionamiento() {

        scroll.getViewport().addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent e) {

                        ajustarColumnasAutomaticamente();
                    }
                }
        );
    }

    /**
     * Calcula el ancho natural de cada columna y distribuye
     * el 100 % del espacio disponible entre todas ellas.
     */
    private void ajustarColumnasAutomaticamente() {

        if (ajustandoColumnas || tabla.getColumnCount() == 0) {
            return;
        }

        int anchoDisponible =
                scroll.getViewport().getExtentSize().width;

        if (anchoDisponible <= 0) {
            return;
        }

        ajustandoColumnas = true;

        try {

            int cantidad = tabla.getColumnCount();

            int[] anchosNaturales = new int[cantidad];
            int[] anchosMinimos = new int[cantidad];

            for (int i = 0; i < cantidad; i++) {

                TableColumn columna =
                        tabla.getColumnModel().getColumn(i);

                anchosMinimos[i] = columna.getMinWidth();

                anchosNaturales[i] = Math.max(
                        anchosMinimos[i],
                        calcularAnchoNatural(i)
                );
            }

            int sumaMinimos = 0;

            for (int minimo : anchosMinimos) {
                sumaMinimos += minimo;
            }

            // Si los mínimos superan el espacio disponible,
            // se conservan y se permite el desplazamiento horizontal.
            if (sumaMinimos > anchoDisponible) {

                for (int i = 0; i < cantidad; i++) {
                    establecerAncho(i, anchosMinimos[i]);
                }

                return;
            }

            int[] anchosFinales = distribuirAnchos(
                    anchosNaturales,
                    anchosMinimos,
                    anchoDisponible
            );

            for (int i = 0; i < cantidad; i++) {

                establecerAncho(
                        i,
                        anchosFinales[i]
                );
            }

        } finally {

            ajustandoColumnas = false;
        }
    }

    // ==========================================
    // CÁLCULO DEL ANCHO NATURAL
    // ==========================================

    /**
     * Mide el encabezado y los valores de la columna usando
     * las métricas reales de las fuentes.
     */
    private int calcularAnchoNatural(int indiceColumna) {

        FontMetrics metricasContenido =
                tabla.getFontMetrics(tabla.getFont());

        FontMetrics metricasEncabezado =
                tabla.getFontMetrics(
                        tabla.getTableHeader().getFont()
                );

        Object encabezado = tabla.getColumnModel()
                .getColumn(indiceColumna)
                .getHeaderValue();

        int ancho = metricasEncabezado.stringWidth(
                encabezado != null ? encabezado.toString() : ""
        );

        for (int fila = 0; fila < tabla.getRowCount(); fila++) {

            Object valor = tabla.getValueAt(
                    fila,
                    indiceColumna
            );

            if (valor != null) {

                ancho = Math.max(
                        ancho,
                        metricasContenido.stringWidth(
                                valor.toString()
                        )
                );
            }
        }

        // Espacio para márgenes internos y respiración visual.
        return ancho + (ESPACIADO_LG * 2);
    }

    // ==========================================
    // DISTRIBUCIÓN PROPORCIONAL
    // ==========================================

    /**
     * Reparte todo el espacio disponible.
     *
     * Cada columna recibe primero su ancho mínimo.
     * El espacio restante se distribuye proporcionalmente
     * al ancho natural calculado para cada columna.
     */
    private int[] distribuirAnchos(
            int[] naturales,
            int[] minimos,
            int disponible
    ) {

        int cantidad = naturales.length;

        int[] resultado = new int[cantidad];

        int espacioRestante = disponible;
        double pesoTotal = 0;

        for (int i = 0; i < cantidad; i++) {

            resultado[i] = minimos[i];

            espacioRestante -= minimos[i];

            pesoTotal += Math.max(1, naturales[i]);
        }

        int espacioAsignado = 0;

        for (int i = 0; i < cantidad; i++) {

            double peso = Math.max(1, naturales[i]);

            int adicional = (int) Math.floor(
                    espacioRestante * peso / pesoTotal
            );

            resultado[i] += adicional;
            espacioAsignado += adicional;
        }

        // Corrige los píxeles sobrantes por redondeo.
        int sobrante = espacioRestante - espacioAsignado;

        for (int i = 0; sobrante > 0; i++) {

            resultado[i % cantidad]++;
            sobrante--;
        }

        return resultado;
    }

    private void establecerAncho(int indice, int ancho) {

        TableColumn columna =
                tabla.getColumnModel().getColumn(indice);

        columna.setPreferredWidth(ancho);
        columna.setWidth(ancho);
    }

    // ==========================================
    // MODELO
    // ==========================================

    public void setModelo(TableModel modelo) {

        if (modelo == null) {
            throw new IllegalArgumentException(
                    "El modelo de la tabla no puede ser nulo."
            );
        }

        if (modeloEscuchado != null) {
            modeloEscuchado.removeTableModelListener(listenerModelo);
        }

        tabla.setModel(modelo);

        modeloEscuchado = modelo;

        modelo.addTableModelListener(listenerModelo);

        configurarOrdenamiento();
        configurarColumnas();

        revalidate();
        repaint();

        SwingUtilities.invokeLater(
                this::ajustarColumnasAutomaticamente
        );
    }

    private void cuandoCambianLosDatos(TableModelEvent evento) {

        SwingUtilities.invokeLater(
                this::ajustarColumnasAutomaticamente
        );
    }

    public TableModel getModelo() {
        return tabla.getModel();
    }

    // ==========================================
    // ORDENAMIENTO
    // ==========================================

    private void configurarOrdenamiento() {

        if (ordenamientoHabilitado) {

            ordenador = new TableRowSorter<>(
                    tabla.getModel()
            );

            tabla.setRowSorter(ordenador);

        } else {

            ordenador = null;
            tabla.setRowSorter(null);
        }
    }

    public void setOrdenamientoHabilitado(boolean habilitado) {

        ordenamientoHabilitado = habilitado;

        configurarOrdenamiento();
    }

    public boolean isOrdenamientoHabilitado() {
        return ordenamientoHabilitado;
    }

    // ==========================================
    // CONFIGURACIÓN DE COLUMNAS
    // ==========================================

    private void configurarColumnas() {

        for (int i = 0; i < tabla.getColumnCount(); i++) {

            TableColumn columna =
                    tabla.getColumnModel().getColumn(i);

            columna.setMinWidth(ANCHO_COLUMNA_MINIMO);
            columna.setResizable(true);
        }
    }

    public void setAnchoColumna(int columna, int ancho) {

        if (!columnaValida(columna)) {
            return;
        }

        if (ancho <= 0) {
            throw new IllegalArgumentException(
                    "El ancho de columna debe ser mayor que cero."
            );
        }

        tabla.getColumnModel()
                .getColumn(columna)
                .setPreferredWidth(
                        Math.max(ANCHO_COLUMNA_MINIMO, ancho)
                );

        SwingUtilities.invokeLater(
                this::ajustarColumnasAutomaticamente
        );
    }

    public void setAnchoMinimoColumna(int columna, int ancho) {

        if (!columnaValida(columna)) {
            return;
        }

        if (ancho <= 0) {
            throw new IllegalArgumentException(
                    "El ancho mínimo debe ser mayor que cero."
            );
        }

        tabla.getColumnModel()
                .getColumn(columna)
                .setMinWidth(ancho);

        SwingUtilities.invokeLater(
                this::ajustarColumnasAutomaticamente
        );
    }

    public void setAnchoMaximoColumna(int columna, int ancho) {

        if (!columnaValida(columna)) {
            return;
        }

        if (ancho <= 0) {
            throw new IllegalArgumentException(
                    "El ancho máximo debe ser mayor que cero."
            );
        }

        TableColumn tableColumn =
                tabla.getColumnModel().getColumn(columna);

        if (ancho < tableColumn.getMinWidth()) {
            throw new IllegalArgumentException(
                    "El ancho máximo no puede ser menor que el mínimo."
            );
        }

        tableColumn.setMaxWidth(ancho);

        SwingUtilities.invokeLater(
                this::ajustarColumnasAutomaticamente
        );
    }

    private boolean columnaValida(int columna) {

        return columna >= 0
                && columna < tabla.getColumnCount();
    }

    // ==========================================
    // ENCABEZADO VISIBLE
    // ==========================================

    public void setEncabezadoVisible(boolean visible) {

        tabla.getTableHeader().setVisible(visible);

        scroll.setColumnHeaderView(
                visible ? tabla.getTableHeader() : null
        );

        revalidate();
        repaint();
    }

    // ==========================================
    // ACCESO A COMPONENTES
    // ==========================================

    public JTable getTabla() {
        return tabla;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    // ==========================================
    // SELECCIÓN
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
    // LIMPIEZA
    // ==========================================

    public void limpiar() {

        setModelo(new DefaultTableModel());
        limpiarSeleccion();
    }
}
