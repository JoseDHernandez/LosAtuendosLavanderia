
package com.lavanderia.vista.paneles.devoluciones;

import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorDevoluciones;
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

/** Presenta el módulo de devoluciones y sus acciones en la interfaz Swing. */
public class PanelDevoluciones extends PanelModulo {

    private final ISistemaFacade sistemaFacade;
    private final IGestorAlquiler gestorAlquiler;
    private final IGestorDevoluciones gestorDevoluciones;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaDevoluciones;
    private final DefaultTableModel modeloTabla;

    private List<IDevolucion> devoluciones;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelDevoluciones(ISistemaFacade sistemaFacade) {

        super(
                "devoluciones",
                "Devoluciones",
                "Consulta y administra las devoluciones de prendas.",
                IconosUI.lavanderia()
        );

        this.sistemaFacade = Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorAlquiler = Objects.requireNonNull(
                sistemaFacade.getGestorAlquiler(),
                "El gestor de alquileres es obligatorio."
        );

        this.gestorDevoluciones = Objects.requireNonNull(
                sistemaFacade.getGestorDevoluciones(),
                "El gestor de devoluciones es obligatorio."
        );

        this.devoluciones = new ArrayList<>();

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por devolución, alquiler, cliente o prenda..."
        );

        this.tablaDevoluciones = new TablaDatos();
        this.modeloTabla = crearModeloTabla();

        inicializarComponentes();
        configurarEventos();
        cargarDevoluciones();
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
                "Devoluciones registradas",
                FUENTE_SECCION,
                TEXTO
        );

        // Botón nueva devolución.
        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nueva devolución");

        botonNuevo.addActionListener(
                e -> abrirFormularioRegistro()
        );

        // Botón editar.
        BotonSecundario botonEditar =
                new BotonSecundario("Editar devolución");

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        // Botón actualizar tabla.
        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.addActionListener(
                e -> cargarDevoluciones()
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
        panelAcciones.add(botonEditar);
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
        tablaDevoluciones.setModelo(modeloTabla);
        tablaDevoluciones.setOrdenamientoHabilitado(true);

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                tablaDevoluciones,
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
                        "ID devolución",
                        "Alquiler",
                        "Referencia",
                        "Cliente",
                        "Fecha devolución",
                        "Estado revisión",
                        "Observaciones"
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
    // CONSULTA DE DEVOLUCIONES
    // ==========================================

    private void cargarDevoluciones() {

        try {

            List<IDevolucion> resultado =
                    gestorDevoluciones.listarTodas();

            if (resultado == null) {

                throw new IllegalStateException(
                        "El gestor devolvió una lista de devoluciones nula."
                );
            }

            devoluciones = new ArrayList<>(resultado);

            // Mantener el texto de búsqueda actual.
            filtrarDevoluciones();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar las devoluciones.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // FILTRO DE DEVOLUCIONES
    // ==========================================

    private void configurarEventos() {

        campoBusqueda.addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarDevoluciones();
                    }
                }
        );

        // Doble clic para editar la devolución.
        tablaDevoluciones.getTabla().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            abrirFormularioEdicion();
                        }
                    }
                }
        );
    }

    private void filtrarDevoluciones() {

        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(devoluciones);
            return;
        }

        List<IDevolucion> resultados = devoluciones.stream()
                .filter(Objects::nonNull)
                .filter(devolucion -> {

                    IDetalleAlquiler detalle =
                            devolucion.getDetalleAlquiler();

                    IAlquiler alquiler =
                            buscarAlquilerPorDetalle(
                                    detalle.getIdDetalle()
                            );

                    String consecutivo = alquiler == null
                            ? ""
                            : String.valueOf(
                            alquiler.getNumeroConsecutivo()
                    );

                    String cliente = alquiler == null
                            ? ""
                            : alquiler.getCliente().getNombre();

                    String identificacion = alquiler == null
                            ? ""
                            : alquiler.getCliente().getIdentificacion();

                    String referencia =
                            detalle.getPrenda().getReferencia();

                    return contiene(
                            String.valueOf(
                                    devolucion.getIdDevolucion()
                            ),
                            texto
                    )
                            || contiene(consecutivo, texto)
                            || contiene(cliente, texto)
                            || contiene(identificacion, texto)
                            || contiene(referencia, texto)
                            || contiene(
                            String.valueOf(
                                    devolucion.getFechaDevolucion()
                            ),
                            texto
                    )
                            || contiene(
                            String.valueOf(
                                    devolucion.getEstadoRevision()
                            ),
                            texto
                    )
                            || contiene(
                            devolucion.getObservaciones(),
                            texto
                    );
                })
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
            List<IDevolucion> listaDevoluciones) {

        modeloTabla.setRowCount(0);

        if (listaDevoluciones == null) {
            return;
        }

        for (IDevolucion devolucion : listaDevoluciones) {

            if (devolucion == null
                    || devolucion.getDetalleAlquiler() == null) {
                continue;
            }

            IDetalleAlquiler detalle =
                    devolucion.getDetalleAlquiler();

            IAlquiler alquiler =
                    buscarAlquilerPorDetalle(
                            detalle.getIdDetalle()
                    );

            String consecutivo = alquiler == null
                    ? "No disponible"
                    : String.valueOf(
                    alquiler.getNumeroConsecutivo()
            );

            String cliente = alquiler == null
                    ? "No disponible"
                    : alquiler.getCliente().getNombre();

            String referencia =
                    detalle.getPrenda().getReferencia();

            modeloTabla.addRow(new Object[]{
                    devolucion.getIdDevolucion(),
                    consecutivo,
                    referencia,
                    cliente,
                    devolucion.getFechaDevolucion(),
                    devolucion.getEstadoRevision(),
                    devolucion.getObservaciones()
            });
        }

        tablaDevoluciones.getTabla().clearSelection();
    }

    // ==========================================
    // BÚSQUEDA DEL ALQUILER ASOCIADO
    // ==========================================

    private IAlquiler buscarAlquilerPorDetalle(long idDetalle) {

        return gestorAlquiler.listarTodos()
                .stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        alquiler.getDetalles() != null
                )
                .filter(alquiler ->
                        alquiler.getDetalles()
                                .stream()
                                .filter(Objects::nonNull)
                                .anyMatch(detalle ->
                                        detalle.getIdDetalle() == idDetalle
                                )
                )
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // APERTURA DEL FORMULARIO
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioDevolucion formulario =
                new FormularioDevolucion(
                        propietario,
                        sistemaFacade,
                        this::cargarDevoluciones
                );

        formulario.mostrar();
    }

    private void abrirFormularioEdicion() {

        IDevolucion devolucion =
                obtenerDevolucionSeleccionada();

        if (devolucion == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una devolución para editar.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioDevolucion formulario =
                new FormularioDevolucion(
                        propietario,
                        sistemaFacade,
                        devolucion,
                        this::cargarDevoluciones
                );

        formulario.mostrar();
    }

    // ==========================================
    // OBTENER DEVOLUCIÓN SELECCIONADA
    // ==========================================

    private IDevolucion obtenerDevolucionSeleccionada() {

        int filaVista =
                tablaDevoluciones.getFilaSeleccionada();

        if (filaVista == -1) {
            return null;
        }

        int filaModelo = tablaDevoluciones.getTabla()
                .convertRowIndexToModel(filaVista);

        long idDevolucion = Long.parseLong(
                String.valueOf(
                        modeloTabla.getValueAt(filaModelo, 0)
                )
        );

        return devoluciones.stream()
                .filter(Objects::nonNull)
                .filter(devolucion ->
                        devolucion.getIdDevolucion() == idDevolucion
                )
                .findFirst()
                .orElse(null);
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
