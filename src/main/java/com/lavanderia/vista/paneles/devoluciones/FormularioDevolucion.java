
package com.lavanderia.vista.paneles.devoluciones;

import com.lavanderia.modelo.enums.EstadoRevision;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IDevolucion;
import com.lavanderia.modelo.lavanderia.Devolucion;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorDevoluciones;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioDevolucion extends JDialog {

    private final IGestorAlquiler gestorAlquiler;
    private final IGestorDevoluciones gestorDevoluciones;

    private final IDevolucion devolucionEdicion;
    private final Runnable alGuardar;

    private final JComboBox<OpcionDetalle> selectorDetalle;
    private final JSpinner fechaDevolucion;
    private final JComboBox<EstadoRevision> selectorEstado;
    private final JTextArea campoObservaciones;

    private final JLabel etiquetaDetalle;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    // Constructor para registrar una devolución.
    public FormularioDevolucion(
            Window propietario,
            ISistemaFacade sistemaFacade,
            Runnable alGuardar) {

        this(
                propietario,
                sistemaFacade,
                null,
                alGuardar
        );
    }

    // Constructor para editar una devolución.
    public FormularioDevolucion(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IDevolucion devolucion,
            Runnable alGuardar) {

        super(
                propietario,
                devolucion == null
                        ? "Registrar devolución"
                        : "Actualizar devolución",
                ModalityType.APPLICATION_MODAL
        );

        Objects.requireNonNull(
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

        this.devolucionEdicion = devolucion;
        this.alGuardar = alGuardar;

        this.selectorDetalle = new JComboBox<>();
        this.fechaDevolucion = crearSelectorFecha(
                devolucion == null
                        ? LocalDate.now()
                        : devolucion.getFechaDevolucion()
        );

        this.selectorEstado = new JComboBox<>(
                EstadoRevision.values()
        );

        this.campoObservaciones = new JTextArea(4, 30);
        this.etiquetaDetalle = new JLabel();

        inicializarComponentes();
        configurarSelectorDetalle();
        cargarDetallesDisponibles();

        if (devolucion != null) {
            cargarDatosDevolucion(devolucion);
        }
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        panelPrincipal.setBackground(SUPERFICIE);
        panelPrincipal.setBorder(MARGEN_PANEL_ESTANDAR);

        // Encabezado.
        JPanel panelEncabezado = new JPanel();

        panelEncabezado.setLayout(
                new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS)
        );

        panelEncabezado.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                devolucionEdicion == null
                        ? "Información de la devolución"
                        : "Editar devolución",
                FUENTE_SUBTITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                devolucionEdicion == null
                        ? "Seleccione el detalle de alquiler y registre su devolución."
                        : "Modifique la información de revisión de la devolución.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        panelEncabezado.add(titulo);
        panelEncabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );
        panelEncabezado.add(descripcion);

        // Formulario.
        JPanel formulario = new JPanel(
                new GridLayout(0, 1, 0, ESPACIADO_MD)
        );

        formulario.setOpaque(false);

        formulario.add(
                crearCampo(
                        "Detalle del alquiler",
                        selectorDetalle
                )
        );

        formulario.add(
                crearCampo(
                        "Fecha de devolución",
                        fechaDevolucion
                )
        );

        formulario.add(
                crearCampo(
                        "Estado de revisión",
                        selectorEstado
                )
        );

        configurarCampoObservaciones();

        formulario.add(
                crearCampo(
                        "Observaciones",
                        new JScrollPane(campoObservaciones)
                )
        );

        etiquetaDetalle.setFont(FUENTE_PEQUENA);
        etiquetaDetalle.setForeground(TEXTO_SECUNDARIO);

        // Contenedor del formulario.
        JPanel panelContenido = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        panelContenido.setOpaque(false);

        panelContenido.add(
                formulario,
                BorderLayout.CENTER
        );

        panelContenido.add(
                etiquetaDetalle,
                BorderLayout.SOUTH
        );

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        panelBotones.setOpaque(false);

        BotonSecundario botonCancelar =
                new BotonSecundario("Cancelar");

        BotonPrincipal botonGuardar =
                new BotonPrincipal(
                        devolucionEdicion == null
                                ? "Registrar devolución"
                                : "Guardar cambios"
                );

        botonCancelar.addActionListener(e -> dispose());

        botonGuardar.addActionListener(
                e -> guardarDevolucion()
        );

        panelBotones.add(botonCancelar);
        panelBotones.add(botonGuardar);

        panelPrincipal.add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelContenido,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelBotones,
                BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);

        pack();

        setMinimumSize(
                new Dimension(ANCHO_DIALOGO, 470)
        );

        setLocationRelativeTo(getOwner());
    }

    private JPanel crearCampo(
            String titulo,
            JComponent componente) {

        JPanel panel = new JPanel(
                new BorderLayout(0, ESPACIADO_XS)
        );

        panel.setOpaque(false);

        JLabel etiqueta = crearEtiqueta(
                titulo,
                FUENTE_ETIQUETA,
                TEXTO
        );

        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);

        return panel;
    }

    private void configurarCampoObservaciones() {

        campoObservaciones.setLineWrap(true);
        campoObservaciones.setWrapStyleWord(true);
        campoObservaciones.setFont(FUENTE_NORMAL);
        campoObservaciones.setForeground(TEXTO);
        campoObservaciones.setBackground(SUPERFICIE);
        campoObservaciones.setBorder(
                BorderFactory.createCompoundBorder(
                        BORDE_SIMPLE,
                        BorderFactory.createEmptyBorder(
                                ESPACIADO_SM,
                                ESPACIADO_SM,
                                ESPACIADO_SM,
                                ESPACIADO_SM
                        )
                )
        );
    }

    // ==========================================
    // SELECTOR DE FECHA
    // ==========================================

    private JSpinner crearSelectorFecha(LocalDate fecha) {

        Date valorInicial = Date.from(
                fecha.atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        JSpinner selector = new JSpinner(
                new SpinnerDateModel(
                        valorInicial,
                        null,
                        null,
                        java.util.Calendar.DAY_OF_MONTH
                )
        );

        selector.setEditor(
                new JSpinner.DateEditor(
                        selector,
                        "dd/MM/yyyy"
                )
        );

        selector.setPreferredSize(
                new Dimension(180, ALTO_CAMPO)
        );

        return selector;
    }

    private LocalDate obtenerFecha() {

        Date fecha = (Date) fechaDevolucion.getValue();

        return fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ==========================================
    // DETALLES DE ALQUILER
    // ==========================================

    private void configurarSelectorDetalle() {

        selectorDetalle.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado) {

                        super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                enfocado
                        );

                        if (valor instanceof OpcionDetalle opcion) {
                            setText(opcion.toString());
                        }

                        return this;
                    }
                }
        );
    }

    private void cargarDetallesDisponibles() {

        selectorDetalle.removeAllItems();

        List<OpcionDetalle> opciones = new ArrayList<>();

        List<IAlquiler> alquileres =
                gestorAlquiler.listarTodos();

        if (alquileres != null) {

            for (IAlquiler alquiler : alquileres) {

                if (alquiler == null || alquiler.getDetalles() == null) {
                    continue;
                }

                for (IDetalleAlquiler detalle : alquiler.getDetalles()) {

                    if (detalle == null) {
                        continue;
                    }

                    boolean tieneDevolucion =
                            gestorDevoluciones
                                    .consultarPorDetalle(
                                            detalle.getIdDetalle()
                                    )
                                    .isPresent();

                    if (!tieneDevolucion) {

                        opciones.add(
                                crearOpcionDetalle(alquiler, detalle)
                        );
                    }
                }
            }
        }

        // En edición se conserva el detalle de la devolución actual.
        if (devolucionEdicion != null) {

            IDetalleAlquiler detalleActual =
                    devolucionEdicion.getDetalleAlquiler();

            boolean yaIncluido = opciones.stream()
                    .anyMatch(opcion ->
                            opcion.detalle().getIdDetalle()
                                    == detalleActual.getIdDetalle()
                    );

            if (!yaIncluido) {

                opciones.add(
                        buscarOpcionDetalle(
                                alquileres,
                                detalleActual
                        )
                );
            }
        }

        opciones.stream()
                .filter(Objects::nonNull)
                .forEach(selectorDetalle::addItem);

        selectorDetalle.setEnabled(
                devolucionEdicion == null
                        && selectorDetalle.getItemCount() > 0
        );

        if (devolucionEdicion == null
                && selectorDetalle.getItemCount() == 0) {

            etiquetaDetalle.setText(
                    "No hay detalles de alquiler pendientes de devolución."
            );
        }
    }

    private OpcionDetalle crearOpcionDetalle(
            IAlquiler alquiler,
            IDetalleAlquiler detalle) {

        return new OpcionDetalle(
                detalle,
                alquiler.getNumeroConsecutivo(),
                alquiler.getCliente().getNombre()
        );
    }

    private OpcionDetalle buscarOpcionDetalle(
            List<IAlquiler> alquileres,
            IDetalleAlquiler detalleBuscado) {

        if (alquileres != null) {

            for (IAlquiler alquiler : alquileres) {

                if (alquiler == null || alquiler.getDetalles() == null) {
                    continue;
                }

                for (IDetalleAlquiler detalle : alquiler.getDetalles()) {

                    if (detalle != null
                            && detalle.getIdDetalle()
                            == detalleBuscado.getIdDetalle()) {

                        return crearOpcionDetalle(
                                alquiler,
                                detalle
                        );
                    }
                }
            }
        }

        return new OpcionDetalle(
                detalleBuscado,
                0,
                "Cliente no disponible"
        );
    }

    // ==========================================
    // CARGAR DATOS PARA EDICIÓN
    // ==========================================

    private void cargarDatosDevolucion(IDevolucion devolucion) {

        for (int i = 0; i < selectorDetalle.getItemCount(); i++) {

            OpcionDetalle opcion = selectorDetalle.getItemAt(i);

            if (opcion.detalle().getIdDetalle()
                    == devolucion.getDetalleAlquiler().getIdDetalle()) {

                selectorDetalle.setSelectedIndex(i);
                break;
            }
        }

        selectorDetalle.setEnabled(false);

        fechaDevolucion.setValue(
                Date.from(
                        devolucion.getFechaDevolucion()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                )
        );

        selectorEstado.setSelectedItem(
                devolucion.getEstadoRevision()
        );

        campoObservaciones.setText(
                devolucion.getObservaciones()
        );

        etiquetaDetalle.setText(
                "El detalle de alquiler no puede modificarse."
        );
    }

    // ==========================================
    // REGISTRO Y ACTUALIZACIÓN
    // ==========================================

    private void guardarDevolucion() {

        try {

            OpcionDetalle opcionSeleccionada =
                    (OpcionDetalle) selectorDetalle.getSelectedItem();

            if (opcionSeleccionada == null) {

                throw new ValidacionException(
                        "Debe seleccionar un detalle de alquiler."
                );
            }

            LocalDate fecha = obtenerFecha();

            EstadoRevision estado =
                    (EstadoRevision) selectorEstado.getSelectedItem();

            if (estado == null) {

                throw new ValidacionException(
                        "Debe seleccionar un estado de revisión."
                );
            }

            String observaciones =
                    campoObservaciones.getText().trim();

            IDevolucion devolucion = new Devolucion(
                    opcionSeleccionada.detalle(),
                    fecha,
                    observaciones,
                    estado
            );

            if (devolucionEdicion == null) {

                IDevolucion registrada =
                        gestorDevoluciones.registrarDevolucion(
                                devolucion
                        );

                mostrarMensaje(
                        "La devolución se registró correctamente.\n"
                                + "Identificador: "
                                + registrada.getIdDevolucion(),
                        "Devolución registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                gestorDevoluciones.actualizarDevolucion(
                        devolucionEdicion.getIdDevolucion(),
                        devolucion
                );

                mostrarMensaje(
                        "La devolución se actualizó correctamente.",
                        "Devolución actualizada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            if (alGuardar != null) {
                alGuardar.run();
            }

            dispose();

        } catch (ValidacionException ex) {

            mostrarMensaje(
                    ex.getMessage(),
                    "Error de validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception ex) {

            mostrarMensaje(
                    "No fue posible guardar la devolución: "
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // UTILIDADES
    // ==========================================

    private void mostrarMensaje(
            String mensaje,
            String titulo,
            int tipo) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                tipo
        );
    }

    public void mostrar() {
        setVisible(true);
    }

    // ==========================================
    // OPCIÓN DEL SELECTOR
    // ==========================================

    private record OpcionDetalle(
            IDetalleAlquiler detalle,
            int consecutivo,
            String cliente) {

        @Override
        public String toString() {

            return "Alquiler N.º " + consecutivo
                    + " | " + detalle.getPrenda().getReferencia()
                    + " | " + cliente;
        }
    }
}
