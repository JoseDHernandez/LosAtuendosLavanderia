
package com.lavanderia.vista.paneles.lavanderia;

import com.lavanderia.modelo.interfaces.ILoteLavanderia;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.ISolicitudLavanderia;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorLavanderia;
import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.componentes.CampoBusqueda;
import com.lavanderia.vista.componentes.TablaDatos;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelModulo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelLavanderia extends PanelModulo {

    private final ISistemaFacade sistemaFacade;
    private final IGestorLavanderia gestorLavanderia;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaSolicitudes;
    private final TablaDatos tablaLotes;

    private final DefaultTableModel modeloSolicitudes;
    private final DefaultTableModel modeloLotes;

    private List<ISolicitudLavanderia> solicitudes;
    private List<ILoteLavanderia> lotes;

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

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por referencia o identificación..."
        );

        this.tablaSolicitudes = new TablaDatos();
        this.tablaLotes = new TablaDatos();

        this.modeloSolicitudes = crearModeloSolicitudes();
        this.modeloLotes = crearModeloLotes();

        inicializarComponentes();
        configurarEventos();

        cargarSolicitudes();
        cargarLotes();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelPrincipal.setOpaque(false);

        // Encabezado de herramientas.
        JPanel panelHerramientas = new JPanel(
                new BorderLayout()
        );

        panelHerramientas.setOpaque(false);

        JLabel etiqueta = crearEtiqueta(
                "Gestión de lavandería",
                FUENTE_SECCION,
                TEXTO
        );

        BotonPrincipal botonNuevo =
                new BotonPrincipal("Preparar envío");

        botonNuevo.addActionListener(
                e -> abrirFormularioEnvio()
        );

        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.addActionListener(e -> {
            cargarSolicitudes();
            cargarLotes();
        });

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        acciones.setOpaque(false);
        acciones.add(campoBusqueda);
        acciones.add(botonActualizar);
        acciones.add(botonNuevo);

        panelHerramientas.add(etiqueta, BorderLayout.WEST);
        panelHerramientas.add(acciones, BorderLayout.EAST);

        // Pestañas.
        JTabbedPane pestañas = new JTabbedPane();

        tablaSolicitudes.setModelo(modeloSolicitudes);
        tablaSolicitudes.setOrdenamientoHabilitado(true);

        tablaLotes.setModelo(modeloLotes);
        tablaLotes.setOrdenamientoHabilitado(true);

        pestañas.addTab(
                "Solicitudes",
                tablaSolicitudes
        );

        pestañas.addTab(
                "Lotes de lavandería",
                tablaLotes
        );

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                pestañas,
                BorderLayout.CENTER
        );

        agregarContenido(panelPrincipal);
    }

    // ==========================================
    // MODELOS DE TABLA
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
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

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
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // ==========================================
    // CONSULTA DE SOLICITUDES
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

            mostrarError(
                    "No fue posible cargar las solicitudes.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // CONSULTA DE LOTES
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

            actualizarTablaLotes(lotes);

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los lotes.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // EVENTOS Y FILTROS
    // ==========================================

    private void configurarEventos() {

        campoBusqueda.addDocumentListener(
                new DocumentListener() {

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
                }
        );

        tablaSolicitudes.getTabla().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            mostrarDetalleSolicitud();
                        }
                    }
                }
        );
    }

    private void filtrarSolicitudes() {

        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTablaSolicitudes(solicitudes);
            return;
        }

        List<ISolicitudLavanderia> resultados =
                solicitudes.stream()
                        .filter(Objects::nonNull)
                        .filter(solicitud -> {

                            IPrenda prenda = obtenerPrenda(solicitud);

                            if (prenda == null) {
                                return false;
                            }

                            return contiene(
                                    prenda.getReferencia(),
                                    texto
                            ) || contiene(
                                    prenda.getMarca(),
                                    texto
                            ) || contiene(
                                    prenda.getTalla(),
                                    texto
                            ) || contiene(
                                    String.valueOf(
                                            solicitud.getIdSolicitud()
                                    ),
                                    texto
                            );
                        })
                        .toList();

        actualizarTablaSolicitudes(resultados);
    }

    private boolean contiene(String valor, String texto) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZACIÓN DE TABLAS
    // ==========================================

    private void actualizarTablaSolicitudes(
            List<ISolicitudLavanderia> lista) {

        modeloSolicitudes.setRowCount(0);

        if (lista == null) {
            return;
        }

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

        tablaSolicitudes.getTabla().clearSelection();
    }

    private void actualizarTablaLotes(
            List<ILoteLavanderia> lista) {

        modeloLotes.setRowCount(0);

        if (lista == null) {
            return;
        }

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

        tablaLotes.getTabla().clearSelection();
    }

    // ==========================================
    // CONSULTA DE PRENDA
    // ==========================================

    private IPrenda obtenerPrenda(
            ISolicitudLavanderia solicitud) {

        if (solicitud == null
                || solicitud.getDevolucion() == null
                || solicitud.getDevolucion().getDetalleAlquiler() == null) {

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

    private void actualizarDatos() {
        cargarSolicitudes();
        cargarLotes();
    }

    // ==========================================
    // DETALLE DE SOLICITUD
    // ==========================================

    private void mostrarDetalleSolicitud() {

        int filaVista =
                tablaSolicitudes.getFilaSeleccionada();

        if (filaVista == -1) {
            return;
        }

        int filaModelo = tablaSolicitudes.getTabla()
                .convertRowIndexToModel(filaVista);

        long idSolicitud = Long.parseLong(
                String.valueOf(
                        modeloSolicitudes.getValueAt(filaModelo, 0)
                )
        );

        ISolicitudLavanderia solicitud = solicitudes.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getIdSolicitud() == idSolicitud)
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
                .append(prenda != null ? prenda.getReferencia() : "N/D")
                .append("\n");

        detalle.append("Marca: ")
                .append(prenda != null ? prenda.getMarca() : "N/D")
                .append("\n");

        detalle.append("Talla: ")
                .append(prenda != null ? prenda.getTalla() : "N/D")
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
                .append(solicitud.getDevolucion().getObservaciones());

        JTextArea areaDetalle = new JTextArea(
                detalle.toString()
        );

        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);
        areaDetalle.setFont(FUENTE_NORMAL);
        areaDetalle.setBackground(SUPERFICIE);

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
}
