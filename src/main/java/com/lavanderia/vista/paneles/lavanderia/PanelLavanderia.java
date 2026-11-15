
package com.lavanderia.vista.paneles.lavanderia;

import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelModulo;
import com.lavanderia.vista.paneles.comunes.PanelTabla;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PanelLavanderia extends PanelModulo {

    private final ISistemaFacade sistemaFacade;
    private final IGestorLavanderia gestorLavanderia;

    private final PanelTabla panelSolicitudes;
    private final PanelTabla panelLotes;

    private final DefaultTableModel modeloSolicitudes;
    private final DefaultTableModel modeloLotes;

    private final BotonSecundario botonActualizarSolicitudes;
    private final BotonSecundario botonActualizarLotes;

    private List<ISolicitudLavanderia> solicitudes;
    private List<ILoteLavanderia> lotes;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelLavanderia(ISistemaFacade sistemaFacade) {

        super(
                "lavanderia",
                "Lavandería",
                "Consulta y administra las solicitudes y envíos de lavandería.",
                IconosUI.lavanderia()
        );

        this.sistemaFacade = Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorLavanderia = Objects.requireNonNull(
                sistemaFacade.getGestorLavanderia(),
                "El gestor de lavandería es obligatorio."
        );

        this.solicitudes = new ArrayList<>();
        this.lotes = new ArrayList<>();

        this.panelSolicitudes = new PanelTabla(
                "Buscar por referencia, marca, prioridad o estado..."
        );

        this.panelLotes = new PanelTabla(
                "Buscar por número de lote o estado..."
        );

        this.modeloSolicitudes = crearModeloSolicitudes();
        this.modeloLotes = crearModeloLotes();

        this.botonActualizarSolicitudes =
                new BotonSecundario("Actualizar");

        this.botonActualizarLotes =
                new BotonSecundario("Actualizar");

        inicializarComponentes();
        configurarEventos();

        actualizarDatos();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        // Acción principal del módulo.
        configurarAccion(
                "Preparar envío",
                e -> abrirFormularioEnvio()
        );

        // Configuración de tabla de solicitudes.
        panelSolicitudes.setModelo(modeloSolicitudes);

        panelSolicitudes.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        // Configuración de tabla de lotes.
        panelLotes.setModelo(modeloLotes);

        panelLotes.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        // Botones de actualización.
        botonActualizarSolicitudes.configurarIcono(
                IconosUI.actualizar()
        );

        botonActualizarLotes.configurarIcono(
                IconosUI.actualizar()
        );

        panelSolicitudes.agregarAccion(
                botonActualizarSolicitudes
        );

        panelLotes.agregarAccion(
                botonActualizarLotes
        );

        // Pestañas.
        JTabbedPane pestanas = new JTabbedPane();

        pestanas.addTab(
                "Solicitudes",
                panelSolicitudes
        );

        pestanas.addTab(
                "Lotes de lavandería",
                panelLotes
        );

        agregarContenido(pestanas);
    }

    // ==========================================
    // CONFIGURACIÓN DE EVENTOS
    // ==========================================

    private void configurarEventos() {

        botonActualizarSolicitudes.addActionListener(
                e -> cargarSolicitudes()
        );

        botonActualizarLotes.addActionListener(
                e -> cargarLotes()
        );

        // Búsqueda de solicitudes.
        panelSolicitudes.getCampoBusqueda()
                .addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarSolicitudes();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarSolicitudes();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarSolicitudes();
                    }
                });

        // Búsqueda de lotes.
        panelLotes.getCampoBusqueda()
                .addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarLotes();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarLotes();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarLotes();
                    }
                });

        // Doble clic para mostrar detalle de solicitud.
        panelSolicitudes.getTablaDatos()
                .getTabla()
                .addMouseListener(
                        new java.awt.event.MouseAdapter() {

                            @Override
                            public void mouseClicked(
                                    java.awt.event.MouseEvent e
                            ) {

                                if (e.getClickCount() == 2
                                        && SwingUtilities.isLeftMouseButton(e)) {

                                    mostrarDetalleSolicitud();
                                }
                            }
                        }
                );
    }

    // ==========================================
    // MODELO DE SOLICITUDES
    // ==========================================

    private DefaultTableModel crearModeloSolicitudes() {

        return new DefaultTableModel(
                new Object[]{
                        "Solicitud",
                        "Referencia",
                        "Marca",
                        "Talla",
                        "Fecha recepción",
                        "Prioridad",
                        "Estado",
                        "Lote"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };
    }

    // ==========================================
    // MODELO DE LOTES
    // ==========================================

    private DefaultTableModel crearModeloLotes() {

        return new DefaultTableModel(
                new Object[]{
                        "Lote",
                        "Fecha envío",
                        "Cupo",
                        "Estado"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };
    }

    // ==========================================
    // ACTUALIZAR INFORMACIÓN
    // ==========================================

    private void actualizarDatos() {

        cargarSolicitudes();
        cargarLotes();
    }

    // ==========================================
    // CARGAR SOLICITUDES
    // ==========================================

    private void cargarSolicitudes() {

        try {

            List<ISolicitudLavanderia> resultado =
                    gestorLavanderia.listarSolicitudes();

            if (resultado == null) {
                throw new IllegalStateException(
                        "El gestor devolvió una lista de solicitudes nula."
                );
            }

            solicitudes = new ArrayList<>(resultado);

            filtrarSolicitudes();

        } catch (RuntimeException ex) {

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible cargar las solicitudes.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // CARGAR LOTES
    // ==========================================

    private void cargarLotes() {

        try {

            List<ILoteLavanderia> resultado =
                    gestorLavanderia.listarLotes();

            if (resultado == null) {
                throw new IllegalStateException(
                        "El gestor devolvió una lista de lotes nula."
                );
            }

            lotes = new ArrayList<>(resultado);

            filtrarLotes();

        } catch (RuntimeException ex) {

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible cargar los lotes.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // FILTRAR SOLICITUDES
    // ==========================================

    private void filtrarSolicitudes() {

        String texto = panelSolicitudes.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTablaSolicitudes(solicitudes);
            return;
        }

        List<ISolicitudLavanderia> resultados =
                solicitudes.stream()
                        .filter(Objects::nonNull)
                        .filter(solicitud ->
                                coincideSolicitud(solicitud, texto)
                        )
                        .toList();

        actualizarTablaSolicitudes(resultados);
    }

    private boolean coincideSolicitud(
            ISolicitudLavanderia solicitud,
            String texto
    ) {

        IPrenda prenda = obtenerPrenda(solicitud);

        String lote = solicitud.getLote() == null
                ? ""
                : String.valueOf(
                solicitud.getLote().getIdLote()
        );

        return contiene(
                String.valueOf(solicitud.getIdSolicitud()),
                texto
        )
                || contiene(
                prenda == null ? null : prenda.getReferencia(),
                texto
        )
                || contiene(
                prenda == null ? null : prenda.getMarca(),
                texto
        )
                || contiene(
                prenda == null ? null : prenda.getTalla(),
                texto
        )
                || contiene(
                String.valueOf(solicitud.getPrioridad()),
                texto
        )
                || contiene(
                String.valueOf(solicitud.getEstado()),
                texto
        )
                || contiene(
                String.valueOf(solicitud.getFechaRecepcion()),
                texto
        )
                || contiene(lote, texto);
    }

    // ==========================================
    // FILTRAR LOTES
    // ==========================================

    private void filtrarLotes() {

        String texto = panelLotes.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTablaLotes(lotes);
            return;
        }

        List<ILoteLavanderia> resultados =
                lotes.stream()
                        .filter(Objects::nonNull)
                        .filter(lote ->
                                contiene(
                                        String.valueOf(lote.getIdLote()),
                                        texto
                                )
                                        || contiene(
                                        String.valueOf(lote.getEstado()),
                                        texto
                                )
                                        || contiene(
                                        String.valueOf(lote.getFechaEnvio()),
                                        texto
                                )
                                        || contiene(
                                        String.valueOf(lote.getCupo()),
                                        texto
                                )
                        )
                        .toList();

        actualizarTablaLotes(resultados);
    }

    private boolean contiene(
            String valor,
            String texto
    ) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZAR TABLA DE SOLICITUDES
    // ==========================================

    private void actualizarTablaSolicitudes(
            List<ISolicitudLavanderia> lista
    ) {

        modeloSolicitudes.setRowCount(0);

        if (lista != null) {

            for (ISolicitudLavanderia solicitud : lista) {

                if (solicitud == null) {
                    continue;
                }

                IPrenda prenda = obtenerPrenda(solicitud);

                if (prenda == null) {
                    continue;
                }

                String lote = solicitud.getLote() == null
                        ? "Sin asignar"
                        : String.valueOf(
                        solicitud.getLote().getIdLote()
                );

                modeloSolicitudes.addRow(new Object[]{
                        solicitud.getIdSolicitud(),
                        prenda.getReferencia(),
                        prenda.getMarca(),
                        prenda.getTalla(),
                        solicitud.getFechaRecepcion(),
                        solicitud.getPrioridad(),
                        solicitud.getEstado(),
                        lote
                });
            }
        }

        panelSolicitudes.getTablaDatos()
                .getTabla()
                .clearSelection();
    }

    // ==========================================
    // ACTUALIZAR TABLA DE LOTES
    // ==========================================

    private void actualizarTablaLotes(
            List<ILoteLavanderia> lista
    ) {

        modeloLotes.setRowCount(0);

        if (lista != null) {

            for (ILoteLavanderia lote : lista) {

                if (lote == null) {
                    continue;
                }

                modeloLotes.addRow(new Object[]{
                        lote.getIdLote(),
                        lote.getFechaEnvio(),
                        lote.getCupo(),
                        lote.getEstado()
                });
            }
        }

        panelLotes.getTablaDatos()
                .getTabla()
                .clearSelection();
    }

    // ==========================================
    // OBTENER PRENDA
    // ==========================================

    private IPrenda obtenerPrenda(
            ISolicitudLavanderia solicitud
    ) {

        if (solicitud == null
                || solicitud.getDevolucion() == null
                || solicitud.getDevolucion()
                .getDetalleAlquiler() == null) {

            return null;
        }

        return solicitud.getDevolucion()
                .getDetalleAlquiler()
                .getPrenda();
    }

    // ==========================================
    // FORMULARIO DE ENVÍO
    // ==========================================

    private void abrirFormularioEnvio() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioEnvioLavanderia formulario =
                new FormularioEnvioLavanderia(
                        propietario,
                        sistemaFacade,
                        this::actualizarDatos
                );

        formulario.mostrar();
    }

    // ==========================================
    // DETALLE DE SOLICITUD
    // ==========================================

    private void mostrarDetalleSolicitud() {

        JTable tabla =
                panelSolicitudes.getTablaDatos().getTabla();

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(filaVista);

        long idSolicitud = Long.parseLong(
                String.valueOf(
                        modeloSolicitudes.getValueAt(filaModelo, 0)
                )
        );

        ISolicitudLavanderia solicitud = solicitudes.stream()
                .filter(Objects::nonNull)
                .filter(s ->
                        s.getIdSolicitud() == idSolicitud
                )
                .findFirst()
                .orElse(null);

        if (solicitud == null) {
            return;
        }

        IPrenda prenda = obtenerPrenda(solicitud);

        StringBuilder detalle = new StringBuilder();

        detalle.append("Solicitud N.º ")
                .append(solicitud.getIdSolicitud())
                .append("\n\n");

        detalle.append("Referencia: ")
                .append(prenda != null
                        ? prenda.getReferencia()
                        : "N/D")
                .append("\n");

        detalle.append("Marca: ")
                .append(prenda != null
                        ? prenda.getMarca()
                        : "N/D")
                .append("\n");

        detalle.append("Talla: ")
                .append(prenda != null
                        ? prenda.getTalla()
                        : "N/D")
                .append("\n");

        detalle.append("Fecha de recepción: ")
                .append(solicitud.getFechaRecepcion())
                .append("\n");

        detalle.append("Prioridad: ")
                .append(solicitud.getPrioridad())
                .append("\n");

        detalle.append("Estado: ")
                .append(solicitud.getEstado())
                .append("\n");

        detalle.append("Lote: ")
                .append(solicitud.getLote() == null
                        ? "Sin asignar"
                        : solicitud.getLote().getIdLote())
                .append("\n");

        detalle.append("\nObservaciones: ")
                .append(
                        solicitud.getDevolucion() == null
                                ? "Sin observaciones"
                                : solicitud.getDevolucion()
                                .getObservaciones()
                );

        JTextArea areaDetalle = new JTextArea(
                detalle.toString()
        );

        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);
        areaDetalle.setFont(
                com.lavanderia.vista.estilos.EstilosUI.FUENTE_NORMAL
        );

        areaDetalle.setBackground(
                com.lavanderia.vista.estilos.EstilosUI.SUPERFICIE
        );

        JScrollPane scroll = new JScrollPane(areaDetalle);

        scroll.setPreferredSize(
                new Dimension(420, 320)
        );

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Detalle de solicitud",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String obtenerMensajeError(Throwable ex) {

        Throwable causa = ex;

        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        return causa.getMessage() != null
                ? causa.getMessage()
                : "Ocurrió un error inesperado.";
    }
}
