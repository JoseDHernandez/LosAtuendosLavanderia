package com.lavanderia.vista.paneles.alquileres;

import com.lavanderia.modelo.alquiler.Alquiler;
import com.lavanderia.modelo.alquiler.DetalleAlquiler;
import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IAlquiler;
import com.lavanderia.modelo.interfaces.ICliente;
import com.lavanderia.modelo.interfaces.IDetalleAlquiler;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorAlquiler;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.alquiler.IAlquilerValidator;
import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioAlquileres extends JDialog {

    private final IGestorPersonas gestorPersonas;
    private final IGestorPrendas gestorPrendas;
    private final IGestorAlquiler gestorAlquiler;

    private final IAlquilerValidator validador;
    private final Runnable alGuardar;

    // Clientes y empleados.
    private final JComboBox<ICliente> selectorCliente;
    private final JComboBox<IEmpleado> selectorEmpleado;

    // Fechas.
    private final JSpinner fechaInicio;
    private final JSpinner fechaFin;

    // Prendas.
    private final JComboBox<IPrenda> selectorPrenda;
    private final DefaultTableModel modeloTabla;
    private final JTable tablaPrendas;

    private final List<IPrenda> prendasSeleccionadas;

    private final JLabel etiquetaTotal;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FormularioAlquileres(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador,
            Runnable alGuardar) {

        super(
                propietario,
                "Registrar alquiler",
                ModalityType.APPLICATION_MODAL
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorPersonas = sistemaFacade.getGestorPersonas();
        this.gestorPrendas = sistemaFacade.getGestorPrendas();
        this.gestorAlquiler = sistemaFacade.getGestorAlquiler();

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de alquiler es obligatorio."
        );

        this.alGuardar = alGuardar;

        this.prendasSeleccionadas = new ArrayList<>();

        this.selectorCliente = new JComboBox<>();
        this.selectorEmpleado = new JComboBox<>();
        this.selectorPrenda = new JComboBox<>();

        this.fechaInicio = crearSelectorFecha();
        this.fechaFin = crearSelectorFecha();

        this.modeloTabla = crearModeloTabla();
        this.tablaPrendas = new JTable(modeloTabla);

        this.etiquetaTotal = new JLabel("Total: $0");

        inicializarComponentes();
        cargarPersonas();
        cargarPrendasDisponibles();
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
                "Información del alquiler",
                FUENTE_SUBTITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                "Seleccione el cliente, el empleado, las fechas y las prendas.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        panelEncabezado.add(titulo);
        panelEncabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );
        panelEncabezado.add(descripcion);

        // Formulario general.
        JPanel panelContenido = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelContenido.setOpaque(false);

        PanelFormulario formulario = new PanelFormulario();

        formulario.agregarCampo(
                crearCampo("Cliente", selectorCliente),
                0, 0
        );

        formulario.agregarCampo(
                crearCampo("Empleado responsable", selectorEmpleado),
                0, 1
        );

        formulario.agregarCampo(
                crearCampo("Fecha de inicio", fechaInicio),
                1, 0
        );

        formulario.agregarCampo(
                crearCampo("Fecha de finalización", fechaFin),
                1, 1
        );

        // Sección de prendas.
        JPanel panelPrendas = crearPanelPrendas();

        panelContenido.add(
                formulario,
                BorderLayout.NORTH
        );

        panelContenido.add(
                panelPrendas,
                BorderLayout.CENTER
        );

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, ESPACIADO_SM, 0)
        );

        panelBotones.setOpaque(false);

        BotonSecundario botonCancelar =
                new BotonSecundario("Cancelar");

        BotonPrincipal botonGuardar =
                new BotonPrincipal("Registrar alquiler");

        botonCancelar.addActionListener(e -> dispose());

        botonGuardar.addActionListener(e -> registrarAlquiler());

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
                new Dimension(ANCHO_DIALOGO + 250, 680)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // SECCIÓN DE PRENDAS
    // ==========================================

    private JPanel crearPanelPrendas() {

        JPanel panel = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        panel.setOpaque(false);

        JLabel titulo = crearEtiqueta(
                "Prendas del alquiler",
                FUENTE_SECCION,
                TEXTO
        );

        JPanel panelSeleccion = new JPanel(
                new BorderLayout(ESPACIADO_SM, 0)
        );

        panelSeleccion.setOpaque(false);

        configurarSelectorPrendas();

        BotonSecundario botonAgregar =
                new BotonSecundario("Agregar prenda");

        botonAgregar.addActionListener(
                e -> agregarPrenda()
        );

        panelSeleccion.add(
                selectorPrenda,
                BorderLayout.CENTER
        );

        panelSeleccion.add(
                botonAgregar,
                BorderLayout.EAST
        );

        // Tabla.
        tablaPrendas.setRowHeight(30);
        tablaPrendas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaPrendas.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tablaPrendas);

        scroll.setPreferredSize(
                new Dimension(650, 180)
        );

        // Acciones inferiores.
        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        panelInferior.setOpaque(false);

        BotonSecundario botonEliminar =
                new BotonSecundario("Quitar seleccionada");

        botonEliminar.addActionListener(
                e -> eliminarPrenda()
        );

        etiquetaTotal.setFont(FUENTE_SECCION);
        etiquetaTotal.setForeground(TEXTO);

        panelInferior.add(
                botonEliminar,
                BorderLayout.WEST
        );

        panelInferior.add(
                etiquetaTotal,
                BorderLayout.EAST
        );

        JPanel panelCentral = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        panelCentral.setOpaque(false);

        panelCentral.add(
                panelSeleccion,
                BorderLayout.NORTH
        );

        panelCentral.add(
                scroll,
                BorderLayout.CENTER
        );

        panelCentral.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        panel.add(titulo, BorderLayout.NORTH);

        panel.add(
                panelCentral,
                BorderLayout.CENTER
        );

        return panel;
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

    // ==========================================
    // SELECTORES DE FECHA
    // ==========================================

    private JSpinner crearSelectorFecha() {

        JSpinner selector = new JSpinner(
                new SpinnerDateModel()
        );

        selector.setEditor(
                new JSpinner.DateEditor(selector, "dd/MM/yyyy")
        );

        selector.setPreferredSize(
                new Dimension(180, ALTO_CAMPO)
        );

        return selector;
    }

    private LocalDate obtenerFecha(JSpinner selector) {

        java.util.Date fecha =
                (java.util.Date) selector.getValue();

        return fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ==========================================
    // CARGAR CLIENTES Y EMPLEADOS
    // ==========================================

    private void cargarPersonas() {

        selectorCliente.removeAllItems();
        selectorEmpleado.removeAllItems();

        List<ICliente> clientes =
                gestorPersonas.listarClientes();

        List<IEmpleado> empleados =
                gestorPersonas.listarEmpleados();

        clientes.forEach(selectorCliente::addItem);
        empleados.forEach(selectorEmpleado::addItem);

        configurarRenderizadoresPersonas();

        if (clientes.isEmpty()) {
            selectorCliente.setEnabled(false);
        }

        if (empleados.isEmpty()) {
            selectorEmpleado.setEnabled(false);
        }
    }

    private void configurarRenderizadoresPersonas() {

        selectorCliente.setRenderer(
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

                        if (valor instanceof ICliente cliente) {
                            setText(
                                    cliente.getNombre()
                                            + " - "
                                            + cliente.getIdentificacion()
                            );
                        }

                        return this;
                    }
                }
        );

        selectorEmpleado.setRenderer(
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

                        if (valor instanceof IEmpleado empleado) {
                            setText(
                                    empleado.getNombre()
                                            + " - "
                                            + empleado.getIdentificacion()
                            );
                        }

                        return this;
                    }
                }
        );
    }

    // ==========================================
    // CARGAR PRENDAS DISPONIBLES
    // ==========================================

    private void configurarSelectorPrendas() {

        selectorPrenda.setRenderer(
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

                        if (valor instanceof IPrenda prenda) {

                            setText(
                                    prenda.getReferencia()
                                            + " | "
                                            + prenda.getMarca()
                                            + " | Talla "
                                            + prenda.getTalla()
                                            + " | $"
                                            + prenda.getValorAlquiler()
                            );
                        }

                        return this;
                    }
                }
        );
    }

    private void cargarPrendasDisponibles() {

        selectorPrenda.removeAllItems();

        List<IPrenda> prendas =
                gestorPrendas.listarPrendas();

        prendas.stream()
                .filter(Objects::nonNull)
                .filter(prenda ->
                        prenda.getEstado() == EstadoPrenda.DISPONIBLE
                )
                .filter(prenda ->
                        !contienePrenda(prenda.getIdPrenda())
                )
                .forEach(selectorPrenda::addItem);

        selectorPrenda.setEnabled(
                selectorPrenda.getItemCount() > 0
        );
    }

    // ==========================================
    // AGREGAR Y ELIMINAR PRENDAS
    // ==========================================

    private void agregarPrenda() {

        IPrenda prendaSeleccionada =
                (IPrenda) selectorPrenda.getSelectedItem();

        if (prendaSeleccionada == null) {

            mostrarMensaje(
                    "No hay prendas disponibles para agregar.",
                    "Prendas no disponibles",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (contienePrenda(prendaSeleccionada.getIdPrenda())) {
            return;
        }

        prendasSeleccionadas.add(prendaSeleccionada);

        modeloTabla.addRow(new Object[]{
                prendaSeleccionada.getReferencia(),
                prendaSeleccionada.getMarca(),
                prendaSeleccionada.getTalla(),
                prendaSeleccionada.getValorAlquiler()
        });

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    private void eliminarPrenda() {

        int fila = tablaPrendas.getSelectedRow();

        if (fila == -1) {

            mostrarMensaje(
                    "Seleccione una prenda de la tabla.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int filaModelo =
                tablaPrendas.convertRowIndexToModel(fila);

        prendasSeleccionadas.remove(filaModelo);
        modeloTabla.removeRow(filaModelo);

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    private boolean contienePrenda(long idPrenda) {

        return prendasSeleccionadas.stream()
                .anyMatch(prenda ->
                        prenda.getIdPrenda() == idPrenda
                );
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "Referencia",
                        "Marca",
                        "Talla",
                        "Valor acordado"
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

    private void actualizarTotal() {

        long total = prendasSeleccionadas.stream()
                .mapToLong(IPrenda::getValorAlquiler)
                .sum();

        etiquetaTotal.setText(
                "Total: $" + total
        );
    }

    // ==========================================
    // REGISTRO DEL ALQUILER
    // ==========================================

    private void registrarAlquiler() {

        try {

            ICliente cliente =
                    (ICliente) selectorCliente.getSelectedItem();

            IEmpleado empleado =
                    (IEmpleado) selectorEmpleado.getSelectedItem();

            LocalDate inicio = obtenerFecha(fechaInicio);
            LocalDate fin = obtenerFecha(fechaFin);

            // Verificar que existan personas registradas.
            if (cliente == null || empleado == null) {

                throw new ValidacionException(
                        "Debe seleccionar un cliente y un empleado."
                );
            }

            // Verificar que haya prendas.
            if (prendasSeleccionadas.isEmpty()) {

                throw new ValidacionException(
                        "Debe agregar al menos una prenda al alquiler."
                );
            }

            // Preparar datos para el validador.
            Map<String, String> datos = new HashMap<>();

            datos.put("fechaInicio", inicio.toString());
            datos.put("fechaFinPrevista", fin.toString());

            datos.put(
                    "identificacionCliente",
                    cliente.getIdentificacion()
            );

            datos.put(
                    "identificacionEmpleado",
                    empleado.getIdentificacion()
            );

            ResultadoValidacion<Map<String, String>> resultado =
                    validador.validar(datos);

            Map<String, String> datosValidados =
                    resultado.obtenerValor();

            // Recuperar las fechas normalizadas.
            LocalDate fechaInicioValidada =
                    LocalDate.parse(
                            datosValidados.get("fechaInicio")
                    );

            LocalDate fechaFinValidada =
                    LocalDate.parse(
                            datosValidados.get("fechaFinPrevista")
                    );

            // Verificar disponibilidad para el periodo.
            List<Long> idsPrendas = prendasSeleccionadas.stream()
                    .map(IPrenda::getIdPrenda)
                    .collect(Collectors.toList());

            boolean disponibles =
                    gestorAlquiler.verificarDisponibilidad(
                            idsPrendas,
                            fechaInicioValidada,
                            fechaFinValidada
                    );

            if (!disponibles) {

                throw new ValidacionException(
                        "Una o varias prendas no están disponibles "
                                + "para las fechas seleccionadas."
                );
            }

            // Construir los detalles del alquiler.
            List<IDetalleAlquiler> detalles =
                    prendasSeleccionadas.stream()
                            .map(prenda ->
                                    (IDetalleAlquiler)
                                            new DetalleAlquiler(
                                                    prenda,
                                                    BigDecimal.valueOf(
                                                            prenda.getValorAlquiler()
                                                    )
                                            )
                            )
                            .collect(Collectors.toList());

            // Construir el alquiler.
            IAlquiler alquiler = new Alquiler(
                    fechaInicioValidada,
                    fechaFinValidada,
                    cliente,
                    empleado,
                    detalles
            );

            // Delegar el registro al gestor.
            IAlquiler alquilerRegistrado =
                    gestorAlquiler.registrarAlquiler(alquiler);

            mostrarMensaje(
                    "El alquiler número "
                            + alquilerRegistrado.getNumeroConsecutivo()
                            + " se registró correctamente.",
                    "Alquiler registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );

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
                    "No fue posible registrar el alquiler: "
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
}