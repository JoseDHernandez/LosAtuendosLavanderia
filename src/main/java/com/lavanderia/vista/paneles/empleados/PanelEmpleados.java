
package com.lavanderia.vista.paneles.empleados;

import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.componentes.DialogoConfirmacion;
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

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelEmpleados extends PanelModulo {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;

    private final PanelTabla panelTabla;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;
    private final BotonSecundario botonEliminar;
    private final BotonSecundario botonActualizar;

    private List<IEmpleado> empleados;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

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

        this.panelTabla = new PanelTabla(
                "Buscar por identificación, nombre, cargo o correo..."
        );

        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario(
                "Editar empleado"
        );

        this.botonEliminar = new BotonSecundario(
                "Eliminar empleado"
        );

        this.botonActualizar = new BotonSecundario(
                "Actualizar"
        );

        inicializarComponentes();
        configurarEventos();
        cargarEmpleados();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        // Acción principal del encabezado.
        configurarAccion(
                "Nuevo empleado",
                e -> abrirFormularioRegistro()
        );

        // Configuración de la tabla.
        panelTabla.setModelo(modeloTabla);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        panelTabla.getTablaDatos()
                .setSeleccionMultiple(false);

        // Acciones secundarias.
        botonEditar.configurarIcono(
                IconosUI.editar()
        );

        botonEliminar.configurarIcono(
                IconosUI.eliminar()
        );

        botonActualizar.configurarIcono(
                IconosUI.actualizar()
        );

        botonEditar.setEnabled(false);
        botonEliminar.setEnabled(false);

        panelTabla.agregarAccion(botonEditar);
        panelTabla.agregarAccion(botonEliminar);
        panelTabla.agregarAccion(botonActualizar);

        agregarContenido(panelTabla);
    }

    // ==========================================
    // CONFIGURACIÓN DE EVENTOS
    // ==========================================

    private void configurarEventos() {

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        botonEliminar.addActionListener(
                e -> eliminarEmpleado()
        );

        botonActualizar.addActionListener(
                e -> cargarEmpleados()
        );

        panelTabla.getCampoBusqueda()
                .addDocumentListener(new DocumentListener() {

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

        panelTabla.getTablaDatos()
                .getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        actualizarEstadoAcciones();
                    }
                });

        // Doble clic para editar.
        panelTabla.getTablaDatos()
                .getTabla()
                .addMouseListener(
                        new java.awt.event.MouseAdapter() {

                            @Override
                            public void mouseClicked(
                                    java.awt.event.MouseEvent e
                            ) {

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
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };
    }

    // ==========================================
    // REGISTRAR EMPLEADO
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioEmpleados formulario =
                new FormularioEmpleados(
                        propietario,
                        gestorPersonas,
                        validador,
                        this::cargarEmpleados
                );

        formulario.mostrar();
    }

    // ==========================================
    // ACTUALIZAR EMPLEADO
    // ==========================================

    private void abrirFormularioEdicion() {

        IEmpleado empleado = obtenerEmpleadoSeleccionado();

        if (empleado == null) {
            mostrarAdvertencia(
                    "Seleccione un empleado para actualizar."
            );
            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioEmpleados formulario =
                new FormularioEmpleados(
                        propietario,
                        gestorPersonas,
                        validador,
                        empleado,
                        this::cargarEmpleados
                );

        formulario.mostrar();
    }

    // ==========================================
    // ELIMINAR EMPLEADO
    // ==========================================

    private void eliminarEmpleado() {

        IEmpleado empleado = obtenerEmpleadoSeleccionado();

        if (empleado == null) {
            mostrarAdvertencia(
                    "Seleccione un empleado para eliminar."
            );
            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        DialogoConfirmacion dialogo =
                new DialogoConfirmacion(
                        propietario,
                        "Eliminar empleado",
                        "¿Está seguro de que desea eliminar al empleado "
                                + empleado.getNombre()
                                + "?\n\n"
                                + "Esta operación no se puede deshacer."
                );

        if (!dialogo.mostrarDialogo()) {
            return;
        }

        try {

            gestorPersonas.eliminarEmpleado(
                    empleado.getIdentificacion()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "El empleado fue eliminado correctamente.",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarEmpleados();

        } catch (RuntimeException ex) {

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible eliminar el empleado.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // OBTENER EMPLEADO SELECCIONADO
    // ==========================================

    private IEmpleado obtenerEmpleadoSeleccionado() {

        int filaModelo = panelTabla.getTablaDatos()
                .getFilaSeleccionada();

        if (filaModelo == -1) {
            return null;
        }

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
    // ESTADO DE LAS ACCIONES
    // ==========================================

    private void actualizarEstadoAcciones() {

        boolean haySeleccion =
                panelTabla.getTablaDatos().haySeleccion();

        botonEditar.setEnabled(haySeleccion);
        botonEliminar.setEnabled(haySeleccion);
    }

    // ==========================================
    // CONSULTAR EMPLEADOS
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

            ex.printStackTrace(System.err);

            mostrarError(
                    "No fue posible cargar los empleados.\n\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // FILTRAR EMPLEADOS
    // ==========================================

    private void filtrarEmpleados() {

        String texto = panelTabla.getTextoBusqueda()
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
                                || contiene(empleado.getDireccion(), texto)
                )
                .toList();

        actualizarTabla(resultados);
    }

    private boolean contiene(
            String valor,
            String texto
    ) {

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    // ==========================================
    // ACTUALIZAR TABLA
    // ==========================================

    private void actualizarTabla(List<IEmpleado> listaEmpleados) {

        modeloTabla.setRowCount(0);

        if (listaEmpleados != null) {

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
        }

        panelTabla.getTablaDatos().limpiarSeleccion();

        actualizarEstadoAcciones();
    }

    // ==========================================
    // MENSAJES
    // ==========================================

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
