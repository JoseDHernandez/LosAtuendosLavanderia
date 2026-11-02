
package com.lavanderia.vista.paneles.empleados;

import com.lavanderia.modelo.interfaces.IEmpleado;
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

/** Presenta el módulo de empleados y sus acciones en la interfaz Swing. */
public class PanelEmpleados extends PanelModulo {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaEmpleados;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;

    private List<IEmpleado> empleados;

    public PanelEmpleados(
            ISistemaFacade sistemaFacade,
            IPersonaValidator validador
    ) {
        super(
                "empleados",
                "Empleados",
                "Consulta y administra los empleados registrados.",
                IconosUI.empleados()
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
                "El validador de empleados es obligatorio."
        );

        this.empleados = new ArrayList<>();

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por identificación, nombre, teléfono o correo..."
        );

        this.tablaEmpleados = new TablaDatos();
        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario("Editar empleado");

        inicializarComponentes();
        configurarEventos();
        cargarEmpleados();
    }

    // ==========================================
    // INICIALIZACIÓN DE COMPONENTES
    // ==========================================

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelPrincipal.setOpaque(false);

        JPanel panelHerramientas = new JPanel(
                new BorderLayout()
        );

        panelHerramientas.setOpaque(false);

        JLabel etiquetaResultados = crearEtiqueta(
                "Empleados registrados",
                FUENTE_SECCION,
                TEXTO
        );

        // Botón registrar
        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nuevo empleado");

        botonNuevo.configurarIcono(IconosUI.agregar());
        botonNuevo.addActionListener(e -> abrirFormularioRegistro());

// Botón editar
      BotonSecundario botonEditar = new BotonSecundario("Editar empleado");

        botonEditar.configurarIcono(IconosUI.editar());
        botonEditar.addActionListener(e -> abrirFormularioEdicion());
        botonEditar.setEnabled(false);

// Botón actualizar
        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.configurarIcono(IconosUI.actualizar());
        botonActualizar.addActionListener(e -> cargarEmpleados());

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

        tablaEmpleados.setModelo(modeloTabla);
        tablaEmpleados.setOrdenamientoHabilitado(true);

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                tablaEmpleados,
                BorderLayout.CENTER
        );

        agregarContenido(panelPrincipal);
    }

    // ==========================================
    // APERTURA DE FORMULARIOS
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioEmpleados formulario = new FormularioEmpleados(
                propietario,
                gestorPersonas,
                validador,
                this::cargarEmpleados
        );

        formulario.mostrar();
    }

    private void abrirFormularioEdicion() {

        IEmpleado empleado = obtenerEmpleadoSeleccionado();

        if (empleado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un empleado para editar.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioEmpleados formulario = new FormularioEmpleados(
                propietario,
                gestorPersonas,
                validador,
                empleado,
                this::cargarEmpleados
        );

        formulario.mostrar();
    }

    // ==========================================
    // OBTENER EMPLEADO SELECCIONADO
    // ==========================================

    private IEmpleado obtenerEmpleadoSeleccionado() {

        int filaVista = tablaEmpleados.getFilaSeleccionada();

        if (filaVista == -1) {
            return null;
        }

        int filaModelo = tablaEmpleados.getTabla()
                .convertRowIndexToModel(filaVista);

        String identificacion = String.valueOf(
                modeloTabla.getValueAt(filaModelo, 0)
        );

        return empleados.stream()
                .filter(Objects::nonNull)
                .filter(empleado ->
                        identificacion.equals(
                                empleado.getIdentificacion()
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
                filtrarEmpleados();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarEmpleados();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarEmpleados();
            }
        });

        tablaEmpleados.getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        botonEditar.setEnabled(
                                tablaEmpleados.getFilaSeleccionada() != -1
                        );
                    }
                });

        tablaEmpleados.getTabla().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            abrirFormularioEdicion();
                        }
                    }
                }
        );
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "Identificación",
                        "Nombre",
                        "Cargo",
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
    // CONSULTA DE EMPLEADOS
    // ==========================================

    private void cargarEmpleados() {

        try {

            List<IEmpleado> resultado =
                    gestorPersonas.listarEmpleados();

            if (resultado == null) {
                throw new IllegalStateException(
                        "El gestor devolvió una lista de empleados nula."
                );
            }

            empleados = new ArrayList<>(resultado);

            filtrarEmpleados();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los empleados.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // FILTRO DE EMPLEADOS
    // ==========================================

    private void filtrarEmpleados() {

        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(empleados);
            return;
        }

        List<IEmpleado> resultados = empleados.stream()
                .filter(Objects::nonNull)
                .filter(empleado ->
                        contiene(empleado.getIdentificacion(), texto)
                                || contiene(empleado.getNombre(), texto)
                                || contiene(empleado.getCargo(), texto)
                                || contiene(empleado.getTelefono(), texto)
                                || contiene(empleado.getCorreo(), texto)
                )
                .toList();

        actualizarTabla(resultados);
    }

    private boolean contiene(String valor, String texto) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZACIÓN DE LA TABLA
    // ==========================================

    private void actualizarTabla(List<IEmpleado> listaEmpleados) {

        modeloTabla.setRowCount(0);

        if (listaEmpleados == null) {
            botonEditar.setEnabled(false);
            return;
        }

        for (IEmpleado empleado : listaEmpleados) {

            if (empleado == null) {
                continue;
            }

            modeloTabla.addRow(new Object[]{
                    empleado.getIdentificacion(),
                    empleado.getNombre(),
                    empleado.getCargo(),
                    empleado.getTelefono(),
                    empleado.getCorreo(),
                    empleado.getDireccion()
            });
        }

        tablaEmpleados.getTabla().clearSelection();
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
