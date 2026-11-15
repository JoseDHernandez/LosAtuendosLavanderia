
package com.lavanderia.vista.paneles.alquileres;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.validacion.alquiler.IAlquilerValidator;
import com.lavanderia.vista.componentes.BotonPrincipal;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Panel para consultar y administrar los alquileres registrados.
 *
 * Utiliza los componentes comunes de la interfaz y depende
 * exclusivamente de las interfaces de servicio y fachada.
 */
public class PanelAlquileres extends PanelModulo {

    private final ISistemaFacade sistemaFacade;
    private final IGestorAlquiler gestorAlquiler;
    private final IAlquilerValidator validador;

    private final PanelTabla panelTabla;
    private final DefaultTableModel modeloTabla;

    private List<IAlquiler> alquileres;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public PanelAlquileres(
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador) {

        super(
                "alquileres",
                "Alquileres",
                "Consulta y administra los servicios de alquiler registrados.",
                IconosUI.alquileres()
        );

        this.sistemaFacade = Objects.requireNonNull(
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

        this.panelTabla = new PanelTabla(
                "Buscar por consecutivo, cliente o empleado..."
        );

        this.modeloTabla = crearModeloTabla();

        inicializarComponentes();
        configurarEventos();
        cargarAlquileres();
    }

    // ==================================================
    // INICIALIZACIÓN
    // ==================================================

    private void inicializarComponentes() {

        // Oculta la acción predeterminada del encabezado.
        // Las acciones se gestionan desde el panel de la tabla.
        ocultarAccion();

        panelTabla.setModelo(modeloTabla);

        configurarAccionesTabla();

        agregarContenido(panelTabla);
    }

    // ==================================================
    // ACCIONES DE LA TABLA
    // ==================================================

    private void configurarAccionesTabla() {

        // ----------------------------------------------
        // NUEVO ALQUILER - BOTÓN PRINCIPAL
        // ----------------------------------------------

        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nuevo alquiler");

        botonNuevo.configurarIcono(
                IconosUI.agregar()
        );

        botonNuevo.addActionListener(
                e -> abrirFormularioRegistro()
        );

        // ----------------------------------------------
        // ACTUALIZAR - BOTÓN SECUNDARIO
        // ----------------------------------------------

        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.configurarIcono(
                IconosUI.actualizar()
        );

        botonActualizar.addActionListener(
                e -> cargarAlquileres()
        );

        // ----------------------------------------------
        // EDITAR - BOTÓN SECUNDARIO
        // ----------------------------------------------

        BotonSecundario botonEditar =
                new BotonSecundario("Editar");

        botonEditar.configurarIcono(
                IconosUI.editar()
        );

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        // ----------------------------------------------
        // CANCELAR - BOTÓN SECUNDARIO
        // ----------------------------------------------

        BotonSecundario botonCancelar =
                new BotonSecundario("Cancelar alquiler");

        botonCancelar.configurarIcono(
                IconosUI.cancelar()
        );

        botonCancelar.addActionListener(
                e -> cancelarAlquiler()
        );

        // Se agregan los componentes reutilizables.
        panelTabla.agregarAccion(botonNuevo);
        panelTabla.agregarAccion(botonActualizar);
        panelTabla.agregarAccion(botonEditar);
        panelTabla.agregarAccion(botonCancelar);
    }

    // ==================================================
    // MODELO DE TABLA
    // ==================================================

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

    // ==================================================
    // EVENTOS
    // ==================================================

    private void configurarEventos() {

        // Búsqueda dinámica.
        panelTabla.getCampoBusqueda().addDocumentListener(
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

        // Doble clic para consultar el detalle.
        panelTabla.getTablaDatos()
                .getTabla()
                .addMouseListener(
                        new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {

                                if (e.getClickCount() == 2
                                        && SwingUtilities.isLeftMouseButton(e)) {

                                    mostrarDetalleAlquiler();
                                }
                            }
                        }
                );
    }

    // ==================================================
    // CONSULTA DE ALQUILERES
    // ==================================================

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

            filtrarAlquileres();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar los alquileres.\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==================================================
    // FILTRADO
    // ==================================================

    private void filtrarAlquileres() {

        String texto = panelTabla.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            actualizarTabla(alquileres);
            return;
        }

        List<IAlquiler> resultados = alquileres.stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        coincideConBusqueda(alquiler, texto)
                )
                .toList();

        actualizarTabla(resultados);
    }

    private boolean coincideConBusqueda(
            IAlquiler alquiler,
            String texto) {

        if (contiene(
                String.valueOf(alquiler.getNumeroConsecutivo()),
                texto)) {
            return true;
        }

        if (alquiler.getCliente() != null) {

            if (contiene(
                    alquiler.getCliente().getNombre(),
                    texto)
                    || contiene(
                    alquiler.getCliente().getIdentificacion(),
                    texto)) {

                return true;
            }
        }

        if (alquiler.getEmpleado() != null) {

            if (contiene(
                    alquiler.getEmpleado().getNombre(),
                    texto)
                    || contiene(
                    alquiler.getEmpleado().getIdentificacion(),
                    texto)) {

                return true;
            }
        }

        return false;
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

    private void actualizarTabla(List<IAlquiler> listaAlquileres) {

        modeloTabla.setRowCount(0);

        if (listaAlquileres == null) {
            return;
        }

        for (IAlquiler alquiler : listaAlquileres) {

            if (alquiler == null) {
                continue;
            }

            String nombreCliente =
                    alquiler.getCliente() == null
                            ? "Sin cliente"
                            : alquiler.getCliente().getNombre();

            String nombreEmpleado =
                    alquiler.getEmpleado() == null
                            ? "Sin empleado"
                            : alquiler.getEmpleado().getNombre();

            int cantidadPrendas =
                    alquiler.getDetalles() == null
                            ? 0
                            : alquiler.getDetalles().size();

            modeloTabla.addRow(new Object[]{
                    alquiler.getNumeroConsecutivo(),
                    alquiler.getFechaSolicitud(),
                    alquiler.getFechaInicio(),
                    alquiler.getFechaFinPrevista(),
                    nombreCliente,
                    nombreEmpleado,
                    cantidadPrendas,
                    alquiler.getEstado()
            });
        }

        panelTabla.getTablaDatos()
                .getTabla()
                .clearSelection();
    }

    // ==================================================
    // REGISTRAR ALQUILER
    // ==================================================

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

    // ==================================================
    // EDITAR ALQUILER
    // ==================================================

    private void abrirFormularioEdicion() {

        IAlquiler alquiler = obtenerAlquilerSeleccionado();

        if (alquiler == null) {
            mostrarError(
                    "Seleccione un alquiler para editar."
            );
            return;
        }

        if (!permiteEdicion(alquiler)) {
            mostrarError(
                    "No es posible editar un alquiler cancelado o finalizado."
            );
            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioAlquileres formulario =
                new FormularioAlquileres(
                        propietario,
                        sistemaFacade,
                        validador,
                        alquiler,
                        this::cargarAlquileres
                );

        formulario.mostrar();
    }

    private boolean permiteEdicion(IAlquiler alquiler) {

        return alquiler.getEstado() != EstadoAlquiler.CANCELADO
                && alquiler.getEstado() != EstadoAlquiler.FINALIZADO;
    }

    // ==================================================
    // CANCELAR ALQUILER
    // ==================================================

    private void cancelarAlquiler() {

        IAlquiler alquiler = obtenerAlquilerSeleccionado();

        if (alquiler == null) {
            mostrarError(
                    "Seleccione un alquiler para cancelar."
            );
            return;
        }

        if (!permiteCancelacion(alquiler)) {

            if (alquiler.getEstado() == EstadoAlquiler.CANCELADO) {
                mostrarError(
                        "El alquiler seleccionado ya está cancelado."
                );
            } else {
                mostrarError(
                        "No es posible cancelar un alquiler finalizado."
                );
            }

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        DialogoConfirmacion dialogo = new DialogoConfirmacion(
                propietario,
                "Cancelar alquiler",
                "¿Está seguro de que desea cancelar el alquiler número "
                        + alquiler.getNumeroConsecutivo()
                        + "?\n\n"
                        + "El registro se conservará en el historial."
        );

        if (!dialogo.mostrarDialogo()) {
            return;
        }

        ejecutarCancelacion(alquiler);
    }

    private boolean permiteCancelacion(IAlquiler alquiler) {

        return alquiler.getEstado() != EstadoAlquiler.CANCELADO
                && alquiler.getEstado() != EstadoAlquiler.FINALIZADO;
    }

    private void ejecutarCancelacion(IAlquiler alquiler) {

        try {

            gestorAlquiler.cambiarEstado(
                    alquiler.getIdAlquiler(),
                    EstadoAlquiler.CANCELADO
            );

            cargarAlquileres();

            JOptionPane.showMessageDialog(
                    this,
                    "El alquiler fue cancelado correctamente.",
                    "Alquiler cancelado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cancelar el alquiler.\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==================================================
    // OBTENER ALQUILER SELECCIONADO
    // ==================================================

    private IAlquiler obtenerAlquilerSeleccionado() {

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            return null;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        Object valorConsecutivo =
                modeloTabla.getValueAt(filaModelo, 0);

        if (valorConsecutivo == null) {
            return null;
        }

        int consecutivo;

        try {
            consecutivo = Integer.parseInt(
                    valorConsecutivo.toString()
            );
        } catch (NumberFormatException ex) {
            return null;
        }

        return alquileres.stream()
                .filter(Objects::nonNull)
                .filter(alquiler ->
                        alquiler.getNumeroConsecutivo() == consecutivo
                )
                .findFirst()
                .orElse(null);
    }

    // ==================================================
    // DETALLE DEL ALQUILER
    // ==================================================

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
                .append(alquiler.getCliente() == null
                        ? "Sin cliente"
                        : alquiler.getCliente().getNombre()
                        + " - "
                        + alquiler.getCliente().getIdentificacion())
                .append("\n");

        detalle.append("Empleado: ")
                .append(alquiler.getEmpleado() == null
                        ? "Sin empleado"
                        : alquiler.getEmpleado().getNombre()
                        + " - "
                        + alquiler.getEmpleado().getIdentificacion())
                .append("\n\n");

        detalle.append("Estado: ")
                .append(alquiler.getEstado())
                .append("\n\n");

        detalle.append("Prendas:\n");

        List<IDetalleAlquiler> detalles = alquiler.getDetalles();

        if (detalles == null || detalles.isEmpty()) {

            detalle.append("No hay prendas asociadas.\n");

        } else {

            for (IDetalleAlquiler detalleAlquiler : detalles) {

                if (detalleAlquiler == null
                        || detalleAlquiler.getPrenda() == null) {
                    continue;
                }

                BigDecimal valor =
                        detalleAlquiler.getValorAcordado();

                detalle.append("• ")
                        .append(detalleAlquiler.getPrenda().getReferencia())
                        .append(" - $")
                        .append(valor == null ? "0" : valor.toPlainString())
                        .append("\n");
            }
        }

        JTextArea areaDetalle = new JTextArea(
                detalle.toString()
        );

        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);
        areaDetalle.setFont(FUENTE_NORMAL);
        areaDetalle.setForeground(TEXTO);
        areaDetalle.setBackground(SUPERFICIE);

        areaDetalle.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM
                )
        );

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

    // ==================================================
    // MENSAJES
    // ==================================================

    private String obtenerMensajeError(RuntimeException ex) {

        return ex.getMessage() == null
                || ex.getMessage().isBlank()
                ? "Ocurrió un error inesperado."
                : ex.getMessage();
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
