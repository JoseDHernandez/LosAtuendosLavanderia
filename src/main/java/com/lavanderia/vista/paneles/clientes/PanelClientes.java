
package com.lavanderia.vista.paneles.clientes;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.persona.IPersonaValidator;
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

public class PanelClientes extends PanelModulo {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaClientes;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;

    private List<ICliente> clientes;

    public PanelClientes(
            ISistemaFacade sistemaFacade,
            IPersonaValidator validador) {

        super(
                "clientes",
                "Clientes",
                "Consulta y administra los clientes registrados.",
                IconosUI.clientes()
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorPersonas = Objects.requireNonNull(
                sistemaFacade.getGestorPersonas(),
                "El gestor de personas es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de clientes es obligatorio."
        );

        this.clientes = new ArrayList<>();

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por identificación, nombre, teléfono o correo..."
        );

        this.tablaClientes = new TablaDatos();
        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario("Editar cliente");

        inicializarComponentes();
        configurarEventos();
        cargarClientes();
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
                "Clientes registrados",
                FUENTE_SECCION,
                TEXTO
        );

        // Botón registrar.
        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nuevo cliente");

        botonNuevo.setIcon(IconosUI.agregar());

        botonNuevo.addActionListener(
                e -> abrirFormularioRegistro()
        );

        // Botón editar.
        botonEditar.setIcon(IconosUI.editar());

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        botonEditar.setEnabled(false);

        // Botón actualizar.
        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.setIcon(IconosUI.actualizar());

        botonActualizar.addActionListener(
                e -> cargarClientes()
        );

        JPanel panelAcciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        panelAcciones.setOpaque(false);
        panelAcciones.add(campoBusqueda);
        panelAcciones.add(botonEditar);
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
        tablaClientes.setModelo(modeloTabla);
        tablaClientes.setOrdenamientoHabilitado(true);

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                tablaClientes,
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
                        "Identificación",
                        "Nombre",
                        "Teléfono",
                        "Correo",
                        "Dirección"
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
    // APERTURA DE FORMULARIOS
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioClientes formulario = new FormularioClientes(
                propietario,
                gestorPersonas,
                validador,
                this::cargarClientes
        );

        formulario.mostrar();
    }

    private void abrirFormularioEdicion() {

        ICliente cliente = obtenerClienteSeleccionado();

        if (cliente == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente para editar.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioClientes formulario = new FormularioClientes(
                propietario,
                gestorPersonas,
                validador,
                cliente,
                this::cargarClientes
        );

        formulario.mostrar();
    }

    // ==========================================
    // OBTENER CLIENTE SELECCIONADO
    // ==========================================

    private ICliente obtenerClienteSeleccionado() {

        int filaVista = tablaClientes.getFilaSeleccionada();

        if (filaVista == -1) {
            return null;
        }

        // Convertir índice visual al índice del modelo.
        int filaModelo = tablaClientes.getTabla()
                .convertRowIndexToModel(filaVista);

        String identificacion = String.valueOf(
                modeloTabla.getValueAt(filaModelo, 0)
        );

        return clientes.stream()
                .filter(Objects::nonNull)
                .filter(cliente ->
                        identificacion.equals(
                                cliente.getIdentificacion()
                        )
                )
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        campoBusqueda.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarClientes();
            }
        });

        // Habilitar edición al seleccionar una fila.
        tablaClientes.getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        botonEditar.setEnabled(
                                tablaClientes.getFilaSeleccionada() != -1
                        );
                    }
                });

        // Doble clic para editar.
        tablaClientes.getTabla().addMouseListener(
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

    // ==========================================
    // CONSULTA DE CLIENTES
    // ==========================================

    private void cargarClientes() {

        try {

            List<ICliente> resultado =
                    gestorPersonas.listarClientes();

            if (resultado == null) {
                throw new IllegalStateException(
                        "El gestor devolvió una lista de clientes nula."
                );
            }

            clientes = new ArrayList<>(resultado);

            // Conserva el filtro de búsqueda actual.
            filtrarClientes();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los clientes.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // FILTRO DE CLIENTES
    // ==========================================

    private void filtrarClientes() {

        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(clientes);
            return;
        }

        List<ICliente> resultados = clientes.stream()
                .filter(Objects::nonNull)
                .filter(cliente ->
                        contiene(cliente.getIdentificacion(), texto)
                                || contiene(cliente.getNombre(), texto)
                                || contiene(cliente.getTelefono(), texto)
                                || contiene(cliente.getCorreo(), texto)
                                || contiene(cliente.getDireccion(), texto)
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

    private void actualizarTabla(List<ICliente> listaClientes) {

        modeloTabla.setRowCount(0);

        if (listaClientes == null) {
            botonEditar.setEnabled(false);
            return;
        }

        for (ICliente cliente : listaClientes) {

            if (cliente == null) {
                continue;
            }

            modeloTabla.addRow(new Object[]{
                    cliente.getIdentificacion(),
                    cliente.getNombre(),
                    cliente.getTelefono(),
                    cliente.getCorreo(),
                    cliente.getDireccion()
            });
        }

        // Evitar mantener una selección sobre datos anteriores.
        tablaClientes.getTabla().clearSelection();
        botonEditar.setEnabled(false);
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
