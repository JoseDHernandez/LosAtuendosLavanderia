package com.lavanderia.vista.paneles.alquileres;

import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.validacion.alquiler.IAlquilerValidator;
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

public class PanelAlquileres extends PanelModulo {
    private final ISistemaFacade sistemaFacade;
    private final IGestorAlquiler gestorAlquiler;
    private final IAlquilerValidator validador;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaAlquileres;
    private final DefaultTableModel modeloTabla;

    private List<IAlquiler> alquileres;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelAlquileres(
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador) {

        super(
                "alquileres",
                "Alquileres",
                "Consulta y administra los servicios de alquiler registrados.",
                IconosUI.alquileres()
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorAlquiler = Objects.requireNonNull(
                sistemaFacade.getGestorAlquiler(),
                "El gestor de alquileres es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de alquileres es obligatorio."
        );

        this.alquileres = new ArrayList<>();

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por consecutivo, cliente o empleado..."
        );

        this.tablaAlquileres = new TablaDatos();
        this.modeloTabla = crearModeloTabla();
        this.sistemaFacade = Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );
        inicializarComponentes();
        configurarEventos();
        cargarAlquileres();
    }

    // ==========================================
    // INICIALIZACIÓN DE COMPONENTES
    // ==========================================

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelPrincipal.setOpaque(false);

        // Barra de herramientas.
        JPanel panelHerramientas = new JPanel(
                new BorderLayout()
        );

        panelHerramientas.setOpaque(false);

        JLabel etiquetaResultados = crearEtiqueta(
                "Alquileres registrados",
                FUENTE_SECCION,
                TEXTO
        );

        // Botón nuevo alquiler.
        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nuevo alquiler");

        botonNuevo.addActionListener(
                e -> abrirFormularioRegistro()
        );

        // Botón actualizar.
        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.addActionListener(
                e -> cargarAlquileres()
        );

        // Acciones.
        JPanel panelAcciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        panelAcciones.setOpaque(false);

        panelAcciones.add(campoBusqueda);
        panelAcciones.add(botonActualizar);
        panelAcciones.add(botonNuevo);

        panelHerramientas.add(
                etiquetaResultados,
                BorderLayout.WEST
        );

        panelHerramientas.add(
                panelAcciones,
                BorderLayout.EAST
        );

        // Tabla.
        tablaAlquileres.setModelo(modeloTabla);
        tablaAlquileres.setOrdenamientoHabilitado(true);

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                tablaAlquileres,
                BorderLayout.CENTER
        );

        agregarContenido(panelPrincipal);
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "Consecutivo",
                        "Fecha solicitud",
                        "Fecha inicio",
                        "Fecha fin",
                        "Cliente",
                        "Empleado",
                        "Prendas",
                        "Estado"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna) {

                return false;
            }
        };
    }

    // ==========================================
    // CONSULTA DE ALQUILERES
    // ==========================================

    private void cargarAlquileres() {

        try {

            List<IAlquiler> resultado =
                    gestorAlquiler.listarTodos();

            if (resultado == null) {

                throw new IllegalStateException(
                        "El gestor devolvió una lista de alquileres nula."
                );
            }

            alquileres = new ArrayList<>(resultado);

            // Mantener el texto de búsqueda actual.
            filtrarAlquileres();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los alquileres.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // FILTRO DE ALQUILERES
    // ==========================================

    private void configurarEventos() {

        campoBusqueda.addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarAlquileres();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarAlquileres();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarAlquileres();
                    }
                }
        );

        // Doble clic para consultar el alquiler.
        tablaAlquileres.getTabla().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            mostrarDetalleAlquiler();
                        }
                    }
                }
        );
    }

    private void filtrarAlquileres() {

        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(alquileres);
            return;
        }

        List<IAlquiler> resultados = alquileres.stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        contiene(
                                String.valueOf(
                                        alquiler.getNumeroConsecutivo()
                                ),
                                texto
                        )
                                || contiene(
                                alquiler.getCliente().getNombre(),
                                texto
                        )
                                || contiene(
                                alquiler.getCliente().getIdentificacion(),
                                texto
                        )
                                || contiene(
                                alquiler.getEmpleado().getNombre(),
                                texto
                        )
                                || contiene(
                                alquiler.getEmpleado().getIdentificacion(),
                                texto
                        )
                )
                .toList();

        actualizarTabla(resultados);
    }

    private boolean contiene(
            String valor,
            String texto) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZACIÓN DE LA TABLA
    // ==========================================

    private void actualizarTabla(
            List<IAlquiler> listaAlquileres) {

        modeloTabla.setRowCount(0);

        if (listaAlquileres == null) {
            return;
        }

        for (IAlquiler alquiler : listaAlquileres) {

            if (alquiler == null) {
                continue;
            }

            modeloTabla.addRow(new Object[]{
                    alquiler.getNumeroConsecutivo(),
                    alquiler.getFechaSolicitud(),
                    alquiler.getFechaInicio(),
                    alquiler.getFechaFinPrevista(),
                    alquiler.getCliente().getNombre(),
                    alquiler.getEmpleado().getNombre(),
                    alquiler.getDetalles().size(),
                    alquiler.getEstado()
            });
        }

        tablaAlquileres.getTabla().clearSelection();
    }

    // ==========================================
    // APERTURA DEL FORMULARIO
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioAlquileres formulario =
                new FormularioAlquileres(
                        propietario,
                        sistemaFacade,
                        validador,
                        this::cargarAlquileres
                );

        formulario.mostrar();
    }

    // ==========================================
    // CONSULTAR ALQUILER SELECCIONADO
    // ==========================================

    private IAlquiler obtenerAlquilerSeleccionado() {

        int filaVista =
                tablaAlquileres.getFilaSeleccionada();

        if (filaVista == -1) {
            return null;
        }

        int filaModelo = tablaAlquileres.getTabla()
                .convertRowIndexToModel(filaVista);

        int consecutivo = Integer.parseInt(
                String.valueOf(
                        modeloTabla.getValueAt(filaModelo, 0)
                )
        );

        return alquileres.stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        alquiler.getNumeroConsecutivo() == consecutivo
                )
                .findFirst()
                .orElse(null);
    }

    private void mostrarDetalleAlquiler() {

        IAlquiler alquiler = obtenerAlquilerSeleccionado();

        if (alquiler == null) {
            return;
        }

        StringBuilder detalle = new StringBuilder();

        detalle.append("Alquiler N.º ")
                .append(alquiler.getNumeroConsecutivo())
                .append("\n\n");

        detalle.append("Fecha de solicitud: ")
                .append(alquiler.getFechaSolicitud())
                .append("\n");

        detalle.append("Fecha de inicio: ")
                .append(alquiler.getFechaInicio())
                .append("\n");

        detalle.append("Fecha de finalización: ")
                .append(alquiler.getFechaFinPrevista())
                .append("\n\n");

        detalle.append("Cliente: ")
                .append(alquiler.getCliente().getNombre())
                .append(" - ")
                .append(alquiler.getCliente().getIdentificacion())
                .append("\n");

        detalle.append("Empleado: ")
                .append(alquiler.getEmpleado().getNombre())
                .append(" - ")
                .append(alquiler.getEmpleado().getIdentificacion())
                .append("\n\n");

        detalle.append("Estado: ")
                .append(alquiler.getEstado())
                .append("\n\n");

        detalle.append("Prendas:\n");

        alquiler.getDetalles().forEach(detalleAlquiler -> {

            detalle.append("• ")
                    .append(detalleAlquiler.getPrenda().getReferencia())
                    .append(" - $")
                    .append(detalleAlquiler.getValorAcordado())
                    .append("\n");
        });

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
                new Dimension(420, 350)
        );

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Detalle del alquiler",
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