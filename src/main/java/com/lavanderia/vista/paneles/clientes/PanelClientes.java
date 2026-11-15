
package com.lavanderia.vista.paneles.clientes;

import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.paneles.comunes.PanelModulo;
import com.lavanderia.vista.paneles.comunes.PanelTabla;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Panel para consultar, registrar y editar clientes.
 *
 * Utiliza los componentes comunes de interfaz y trabaja
 * exclusivamente con interfaces de servicio y fachada.
 */
public class PanelClientes extends PanelModulo {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;

    private final PanelTabla panelTabla;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;

    private List<ICliente> clientes;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public PanelClientes(
            ISistemaFacade sistemaFacade,
            IPersonaValidator validador) {

        super(
                "clientes",
                "Clientes",
                "Consulta y administra los clientes registrados.",
                com.lavanderia.vista.estilos.IconosUI.clientes()
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

        this.panelTabla = new PanelTabla(
                "Buscar por identificación, nombre, teléfono o correo..."
        );

        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario(
                "Editar cliente"
        );

        inicializarComponentes();
        configurarEventos();
        cargarClientes();
    }

    // ==================================================
    // INICIALIZACIÓN
    // ==================================================

    private void inicializarComponentes() {

        // Acción principal del encabezado heredado.
        configurarAccion(
                "Nuevo cliente",
                e -> abrirFormularioRegistro()
        );

        // Configurar tabla común.
        panelTabla.setModelo(modeloTabla);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        // Acciones secundarias.
        configurarAccionesTabla();

        agregarContenido(panelTabla);
    }

    private void configurarAccionesTabla() {

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        botonEditar.setEnabled(false);

        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.setIcon(
                com.lavanderia.vista.estilos.IconosUI.actualizar()
        );

        botonActualizar.addActionListener(
                e -> cargarClientes()
        );

        panelTabla.agregarAccion(botonEditar);
        panelTabla.agregarAccion(botonActualizar);
    }

    // ==================================================
    // MODELO DE TABLA
    // ==================================================

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
            public boolean isCellEditable(
                    int fila,
                    int columna) {

                return false;
            }
        };
    }

    // ==================================================
    // EVENTOS
    // ==================================================

    private void configurarEventos() {

        // Búsqueda dinámica.
        panelTabla.getCampoBusqueda().addDocumentListener(
                new DocumentListener() {

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
                }
        );

        // Habilitar edición según la selección.
        panelTabla.getTablaDatos()
                .getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        actualizarEstadoBotonEditar();
                    }
                });

        // Doble clic para editar.
        panelTabla.getTablaDatos()
                .getTabla()
                .addMouseListener(
                        new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {

                                if (e.getClickCount() == 2
                                        && SwingUtilities.isLeftMouseButton(e)) {

                                    abrirFormularioEdicion();
                                }
                            }
                        }
                );
    }

    private void actualizarEstadoBotonEditar() {

        botonEditar.setEnabled(
                obtenerClienteSeleccionado() != null
        );
    }

    // ==================================================
    // REGISTRAR CLIENTE
    // ==================================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioClientes formulario =
                new FormularioClientes(
                        propietario,
                        gestorPersonas,
                        validador,
                        this::cargarClientes
                );

        formulario.mostrar();
    }

    // ==================================================
    // EDITAR CLIENTE
    // ==================================================

    private void abrirFormularioEdicion() {

        ICliente cliente = obtenerClienteSeleccionado();

        if (cliente == null) {
            mostrarAdvertencia(
                    "Seleccione un cliente para editar."
            );
            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioClientes formulario =
                new FormularioClientes(
                        propietario,
                        gestorPersonas,
                        validador,
                        cliente,
                        this::cargarClientes
                );

        formulario.mostrar();
    }

    // ==================================================
    // OBTENER CLIENTE SELECCIONADO
    // ==================================================

    private ICliente obtenerClienteSeleccionado() {

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            return null;
        }

        // Convertir el índice visual al índice del modelo.
        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        Object valorIdentificacion =
                modeloTabla.getValueAt(filaModelo, 0);

        if (valorIdentificacion == null) {
            return null;
        }

        String identificacion = valorIdentificacion.toString();

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

    // ==================================================
    // CONSULTA DE CLIENTES
    // ==================================================

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

            // Conservar el texto de búsqueda actual.
            filtrarClientes();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los clientes.\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==================================================
    // FILTRADO
    // ==================================================

    private void filtrarClientes() {

        String texto = panelTabla.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(clientes);
            return;
        }

        List<ICliente> resultados = clientes.stream()
                .filter(Objects::nonNull)
                .filter(cliente ->
                        coincideConBusqueda(cliente, texto)
                )
                .toList();

        actualizarTabla(resultados);
    }

    private boolean coincideConBusqueda(
            ICliente cliente,
            String texto) {

        return contiene(cliente.getIdentificacion(), texto)
                || contiene(cliente.getNombre(), texto)
                || contiene(cliente.getTelefono(), texto)
                || contiene(cliente.getCorreo(), texto)
                || contiene(cliente.getDireccion(), texto);
    }

    private boolean contiene(
            String valor,
            String texto) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==================================================
    // ACTUALIZACIÓN DE LA TABLA
    // ==================================================

    private void actualizarTabla(List<ICliente> listaClientes) {

        modeloTabla.setRowCount(0);

        if (listaClientes != null) {

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
        }

        // Evitar mantener una selección sobre datos anteriores.
        panelTabla.getTablaDatos()
                .getTabla()
                .clearSelection();

        botonEditar.setEnabled(false);
    }

    // ==================================================
    // MENSAJES
    // ==================================================

    private void mostrarAdvertencia(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String obtenerMensajeError(RuntimeException ex) {

        return ex.getMessage() == null
                || ex.getMessage().isBlank()
                ? "Ocurrió un error inesperado."
                : ex.getMessage();
    }
}
