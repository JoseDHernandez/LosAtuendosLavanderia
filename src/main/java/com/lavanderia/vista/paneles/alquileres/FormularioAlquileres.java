
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
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.componentes.SelectorFecha;
import com.lavanderia.vista.estilos.EstilosUI;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Formulario para registrar y editar contratos de alquiler.
 *
 * La vista trabaja con interfaces de servicio y no accede
 * directamente a los DAO ni a la base de datos.
 */
public class FormularioAlquileres extends JDialog {

    private final IGestorPersonas gestorPersonas;
    private final IGestorPrendas gestorPrendas;
    private final IGestorAlquiler gestorAlquiler;

    private final IAlquilerValidator validador;
    private final Runnable alGuardar;

    private final IAlquiler alquilerEditar;

    private final JComboBox<ICliente> selectorCliente;
    private final JComboBox<IEmpleado> selectorEmpleado;
    private final JComboBox<IPrenda> selectorPrenda;

    private final SelectorFecha fechaInicio;
    private final SelectorFecha fechaFin;

    private final DefaultTableModel modeloTabla;
    private final JTable tablaPrendas;

    /*
     * Se conserva el valor acordado de cada prenda.
     * El índice de esta lista corresponde al índice de la tabla.
     */
    private final List<IPrenda> prendasSeleccionadas;
    private final List<BigDecimal> valoresAcordados;

    private final JLabel etiquetaTotal;
    private final JLabel errorCliente;
    private final JLabel errorEmpleado;
    private final JLabel errorGeneral;

    private final PanelFormulario formulario;

    private BotonSecundario botonAgregarPrenda;
    private BotonSecundario botonEliminarPrenda;

    // ==================================================
    // CONSTRUCTORES
    // ==================================================

    /**
     * Constructor para registrar un nuevo alquiler.
     */
    public FormularioAlquileres(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador,
            Runnable alGuardar) {

        this(
                propietario,
                sistemaFacade,
                validador,
                null,
                alGuardar
        );
    }

    /**
     * Constructor para registrar o editar un alquiler.
     *
     * Si alquilerEditar es null, funciona en modo registro.
     * En caso contrario, carga los datos del alquiler existente.
     */
    public FormularioAlquileres(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador,
            IAlquiler alquilerEditar,
            Runnable alGuardar) {

        super(
                propietario,
                alquilerEditar == null
                        ? "Registrar alquiler"
                        : "Editar alquiler",
                ModalityType.APPLICATION_MODAL
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorPersonas = Objects.requireNonNull(
                sistemaFacade.getGestorPersonas(),
                "El gestor de personas es obligatorio."
        );

        this.gestorPrendas = Objects.requireNonNull(
                sistemaFacade.getGestorPrendas(),
                "El gestor de prendas es obligatorio."
        );

        this.gestorAlquiler = Objects.requireNonNull(
                sistemaFacade.getGestorAlquiler(),
                "El gestor de alquileres es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de alquiler es obligatorio."
        );

        this.alquilerEditar = alquilerEditar;
        this.alGuardar = alGuardar;

        this.selectorCliente = new JComboBox<>();
        this.selectorEmpleado = new JComboBox<>();
        this.selectorPrenda = new JComboBox<>();

        this.fechaInicio = new SelectorFecha("Fecha de inicio");
        this.fechaFin = new SelectorFecha("Fecha de finalización");

        this.prendasSeleccionadas = new ArrayList<>();
        this.valoresAcordados = new ArrayList<>();

        this.modeloTabla = crearModeloTabla();
        this.tablaPrendas = new JTable(modeloTabla);

        this.etiquetaTotal = new JLabel("Total: $0");

        this.errorCliente = crearEtiquetaError();
        this.errorEmpleado = crearEtiquetaError();
        this.errorGeneral = crearEtiquetaError();

        this.formulario = new PanelFormulario();

        inicializarComponentes();
        cargarPersonas();
        configurarRenderizadoresPersonas();
        configurarSelectorPrendas();
        configurarTablaPrendas();

        if (alquilerEditar == null) {
            cargarPrendasDisponibles();
        } else {
            cargarDatosAlquiler(alquilerEditar);
        }
    }

    // ==================================================
    // INICIALIZACIÓN
    // ==================================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

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
                alquilerEditar == null
                        ? "Información del alquiler"
                        : "Modificar información del alquiler",
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
        configurarFormularioGeneral();

        // Sección de prendas.
        JPanel panelPrendas = crearPanelPrendas();

        JPanel panelContenido = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelContenido.setOpaque(false);

        panelContenido.add(
                formulario,
                BorderLayout.NORTH
        );

        panelContenido.add(
                panelPrendas,
                BorderLayout.CENTER
        );

        // Mensajes y acciones.
        JPanel panelInferior = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        panelInferior.setOpaque(false);

        panelInferior.add(
                errorGeneral,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelContenido,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);

        setMinimumSize(new Dimension(850, 680));
        setPreferredSize(new Dimension(950, 760));

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void configurarFormularioGeneral() {

        JPanel campoCliente = crearCampo(
                "Cliente",
                selectorCliente,
                errorCliente
        );

        JPanel campoEmpleado = crearCampo(
                "Empleado responsable",
                selectorEmpleado,
                errorEmpleado
        );

        formulario.agregarCampo(campoCliente, 0, 0);
        formulario.agregarCampo(campoEmpleado, 0, 1);

        formulario.agregarCampo(fechaInicio, 1, 0);
        formulario.agregarCampo(fechaFin, 1, 1);

        formulario.configurarBotonGuardar(
                alquilerEditar == null
                        ? "Registrar alquiler"
                        : "Guardar cambios",
                e -> guardarAlquiler()
        );

        formulario.configurarBotonCancelar(
                "Cancelar",
                e -> dispose()
        );
    }

    // ==================================================
    // COMPONENTES AUXILIARES
    // ==================================================

    private JLabel crearEtiquetaError() {

        JLabel etiqueta = new JLabel(" ");

        etiqueta.setFont(FUENTE_PEQUENA);
        etiqueta.setForeground(EstilosUI.ERROR);

        return etiqueta;
    }

    private JLabel crearEtiqueta(
            String texto,
            Font fuente,
            Color color) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);

        return etiqueta;
    }

    private JPanel crearCampo(
            String titulo,
            JComponent componente,
            JLabel error) {

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
        panel.add(error, BorderLayout.SOUTH);

        return panel;
    }

    // ==================================================
    // SECCIÓN DE PRENDAS
    // ==================================================

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

        // Selector y botón para agregar.
        JPanel panelSeleccion = new JPanel(
                new BorderLayout(ESPACIADO_SM, 0)
        );

        panelSeleccion.setOpaque(false);

        botonAgregarPrenda =
                new BotonSecundario("Agregar prenda");

        botonAgregarPrenda.addActionListener(
                e -> agregarPrenda()
        );

        panelSeleccion.add(
                selectorPrenda,
                BorderLayout.CENTER
        );

        panelSeleccion.add(
                botonAgregarPrenda,
                BorderLayout.EAST
        );

        // Tabla.
        JPanel panelTabla = new JPanel(new BorderLayout());

        panelTabla.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tablaPrendas);

        scroll.setPreferredSize(
                new Dimension(650, 200)
        );

        panelTabla.add(scroll, BorderLayout.CENTER);

        // Acciones inferiores.
        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        panelInferior.setOpaque(false);

        botonEliminarPrenda =
                new BotonSecundario("Quitar seleccionada");

        botonEliminarPrenda.addActionListener(
                e -> eliminarPrenda()
        );

        etiquetaTotal.setFont(FUENTE_SECCION);
        etiquetaTotal.setForeground(TEXTO);

        panelInferior.add(
                botonEliminarPrenda,
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
                panelTabla,
                BorderLayout.CENTER
        );

        panelCentral.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }

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

    private void configurarTablaPrendas() {

        tablaPrendas.setRowHeight(ALTO_FILA_TABLA);

        tablaPrendas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaPrendas.setFillsViewportHeight(true);
        tablaPrendas.setFont(FUENTE_NORMAL);
        tablaPrendas.setForeground(TEXTO);
        tablaPrendas.setBackground(SUPERFICIE);

        tablaPrendas.getTableHeader().setFont(FUENTE_ETIQUETA);
        tablaPrendas.getTableHeader().setBackground(
                SUPERFICIE_SECUNDARIA
        );

        tablaPrendas.getTableHeader().setForeground(TEXTO);

        tablaPrendas.setAutoCreateRowSorter(true);
    }

    // ==================================================
    // CARGA DE PERSONAS
    // ==================================================

    private void cargarPersonas() {

        selectorCliente.removeAllItems();
        selectorEmpleado.removeAllItems();

        List<ICliente> clientes =
                gestorPersonas.listarClientes();

        List<IEmpleado> empleados =
                gestorPersonas.listarEmpleados();

        if (clientes != null) {
            clientes.stream()
                    .filter(Objects::nonNull)
                    .forEach(selectorCliente::addItem);
        }

        if (empleados != null) {
            empleados.stream()
                    .filter(Objects::nonNull)
                    .forEach(selectorEmpleado::addItem);
        }

        selectorCliente.setEnabled(
                selectorCliente.getItemCount() > 0
        );

        selectorEmpleado.setEnabled(
                selectorEmpleado.getItemCount() > 0
        );

        if (selectorCliente.getItemCount() == 0) {
            errorCliente.setText(
                    "No hay clientes registrados."
            );
        }

        if (selectorEmpleado.getItemCount() == 0) {
            errorEmpleado.setText(
                    "No hay empleados registrados."
            );
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

    // ==================================================
    // CARGA DE PRENDAS
    // ==================================================

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

        if (prendas == null) {
            selectorPrenda.setEnabled(false);
            return;
        }

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

        if (botonAgregarPrenda != null) {
            botonAgregarPrenda.setEnabled(
                    selectorPrenda.getItemCount() > 0
            );
        }
    }

    // ==================================================
    // CARGAR ALQUILER PARA EDICIÓN
    // ==================================================

    private void cargarDatosAlquiler(IAlquiler alquiler) {

        if (alquiler == null) {
            return;
        }

        if (alquiler.getCliente() != null) {
            seleccionarCliente(
                    alquiler.getCliente().getIdentificacion()
            );
        }

        if (alquiler.getEmpleado() != null) {
            seleccionarEmpleado(
                    alquiler.getEmpleado().getIdentificacion()
            );
        }

        if (alquiler.getFechaInicio() != null) {
            fechaInicio.setFecha(
                    convertirAFecha(alquiler.getFechaInicio())
            );
        }

        if (alquiler.getFechaFinPrevista() != null) {
            fechaFin.setFecha(
                    convertirAFecha(alquiler.getFechaFinPrevista())
            );
        }

        prendasSeleccionadas.clear();
        valoresAcordados.clear();
        modeloTabla.setRowCount(0);

        List<IDetalleAlquiler> detalles = alquiler.getDetalles();

        if (detalles != null) {

            for (IDetalleAlquiler detalle : detalles) {

                if (detalle == null || detalle.getPrenda() == null) {
                    continue;
                }

                IPrenda prenda = detalle.getPrenda();

                BigDecimal valorAcordado =
                        detalle.getValorAcordado();

                if (valorAcordado == null) {
                    valorAcordado = obtenerPrecioActual(prenda);
                }

                agregarPrendaALaTabla(
                        prenda,
                        valorAcordado
                );
            }
        }

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    private void seleccionarCliente(String identificacion) {

        if (identificacion == null) {
            return;
        }

        for (int i = 0; i < selectorCliente.getItemCount(); i++) {

            ICliente cliente = selectorCliente.getItemAt(i);

            if (cliente != null
                    && identificacion.equals(
                    cliente.getIdentificacion())) {

                selectorCliente.setSelectedIndex(i);
                return;
            }
        }

        errorCliente.setText(
                "El cliente asociado no aparece en el listado."
        );
    }

    private void seleccionarEmpleado(String identificacion) {

        if (identificacion == null) {
            return;
        }

        for (int i = 0; i < selectorEmpleado.getItemCount(); i++) {

            IEmpleado empleado = selectorEmpleado.getItemAt(i);

            if (empleado != null
                    && identificacion.equals(
                    empleado.getIdentificacion())) {

                selectorEmpleado.setSelectedIndex(i);
                return;
            }
        }

        errorEmpleado.setText(
                "El empleado asociado no aparece en el listado."
        );
    }

    // ==================================================
    // FECHAS
    // ==================================================

    private Date convertirAFecha(LocalDate fecha) {

        return Date.from(
                fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    private LocalDate obtenerFecha(SelectorFecha selector) {

        Date fecha = selector.getFecha();

        if (fecha == null) {
            throw new ValidacionException(
                    "Debe seleccionar una fecha."
            );
        }

        return fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ==================================================
    // AGREGAR Y ELIMINAR PRENDAS
    // ==================================================

    private void agregarPrenda() {

        limpiarErrorGeneral();

        IPrenda prenda =
                (IPrenda) selectorPrenda.getSelectedItem();

        if (prenda == null) {
            mostrarErrorGeneral(
                    "Seleccione una prenda disponible para agregar."
            );
            return;
        }

        if (contienePrenda(prenda.getIdPrenda())) {
            mostrarErrorGeneral(
                    "La prenda seleccionada ya está en el alquiler."
            );
            return;
        }

        agregarPrendaALaTabla(
                prenda,
                obtenerPrecioActual(prenda)
        );

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    private void agregarPrendaALaTabla(
            IPrenda prenda,
            BigDecimal valorAcordado) {

        prendasSeleccionadas.add(prenda);
        valoresAcordados.add(valorAcordado);

        modeloTabla.addRow(new Object[]{
                prenda.getReferencia(),
                prenda.getMarca(),
                prenda.getTalla(),
                valorAcordado
        });
    }

    private void eliminarPrenda() {

        limpiarErrorGeneral();

        int filaSeleccionada = tablaPrendas.getSelectedRow();

        if (filaSeleccionada == -1) {
            mostrarErrorGeneral(
                    "Seleccione una prenda de la tabla para quitarla."
            );
            return;
        }

        int filaModelo = tablaPrendas.convertRowIndexToModel(
                filaSeleccionada
        );

        prendasSeleccionadas.remove(filaModelo);
        valoresAcordados.remove(filaModelo);
        modeloTabla.removeRow(filaModelo);

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    private boolean contienePrenda(long idPrenda) {

        return prendasSeleccionadas.stream()
                .filter(Objects::nonNull)
                .anyMatch(prenda ->
                        prenda.getIdPrenda() == idPrenda
                );
    }

    private BigDecimal obtenerPrecioActual(IPrenda prenda) {

        return BigDecimal.valueOf(
                prenda.getValorAlquiler()
        );
    }

    private void actualizarTotal() {

        BigDecimal total = valoresAcordados.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        etiquetaTotal.setText(
                "Total: $" + total.toPlainString()
        );
    }

    // ==================================================
    // VALIDACIÓN INICIAL
    // ==================================================

    private boolean validarSeleccionInicial() {

        boolean valido = true;

        if (selectorCliente.getSelectedItem() == null) {
            errorCliente.setText(
                    "Debe seleccionar un cliente."
            );
            valido = false;
        } else {
            errorCliente.setText(" ");
        }

        if (selectorEmpleado.getSelectedItem() == null) {
            errorEmpleado.setText(
                    "Debe seleccionar un empleado."
            );
            valido = false;
        } else {
            errorEmpleado.setText(" ");
        }

        if (prendasSeleccionadas.isEmpty()) {
            mostrarErrorGeneral(
                    "Debe agregar al menos una prenda al alquiler."
            );
            valido = false;
        }

        if (!valido) {
            revalidate();
            repaint();
        }

        return valido;
    }

    private Map<String, String> construirDatosValidacion(
            ICliente cliente,
            IEmpleado empleado,
            LocalDate inicio,
            LocalDate fin) {

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

        return datos;
    }

    private void mostrarErroresValidacion(
            Map<String, String> errores) {

        limpiarErroresCampos();

        if (errores == null || errores.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entrada : errores.entrySet()) {

            String campo = entrada.getKey();
            String mensaje = entrada.getValue();

            switch (campo) {

                case "fechaInicio" ->
                        fechaInicio.mostrarError(mensaje);

                case "fechaFinPrevista" ->
                        fechaFin.mostrarError(mensaje);

                case "identificacionCliente" ->
                        errorCliente.setText(mensaje);

                case "identificacionEmpleado" ->
                        errorEmpleado.setText(mensaje);

                case "general" ->
                        mostrarErrorGeneral(mensaje);

                default ->
                        mostrarErrorGeneral(mensaje);
            }
        }

        revalidate();
        repaint();
    }

    private void limpiarErroresCampos() {

        fechaInicio.limpiarError();
        fechaFin.limpiarError();

        errorCliente.setText(" ");
        errorEmpleado.setText(" ");

        limpiarErrorGeneral();
    }

    // ==================================================
    // REGISTRO Y EDICIÓN
    // ==================================================

    private void guardarAlquiler() {

        limpiarErroresCampos();

        if (!validarSeleccionInicial()) {
            return;
        }

        formulario.getBotonGuardar().setEnabled(false);

        try {

            ICliente cliente =
                    (ICliente) selectorCliente.getSelectedItem();

            IEmpleado empleado =
                    (IEmpleado) selectorEmpleado.getSelectedItem();

            LocalDate inicio = obtenerFecha(fechaInicio);
            LocalDate fin = obtenerFecha(fechaFin);

            // Validación de datos generales.
            Map<String, String> datos =
                    construirDatosValidacion(
                            cliente,
                            empleado,
                            inicio,
                            fin
                    );

            ResultadoValidacion<Map<String, String>> resultado =
                    validador.validar(datos);

            Map<String, String> datosValidados =
                    resultado.obtenerValor();

            LocalDate fechaInicioValidada = LocalDate.parse(
                    datosValidados.get("fechaInicio")
            );

            LocalDate fechaFinValidada = LocalDate.parse(
                    datosValidados.get("fechaFinPrevista")
            );

            // Construcción de identificadores de prendas.
            List<Long> idsPrendas = prendasSeleccionadas.stream()
                    .map(IPrenda::getIdPrenda)
                    .toList();

            /*
             * En registro se realiza una comprobación previa.
             *
             * En edición no se utiliza esta comprobación general,
             * porque podría detectar el propio alquiler como conflicto.
             *
             * La actualización debe verificar la disponibilidad
             * excluyendo el alquiler actual en la capa de servicio
             * o dentro de la transacción del DAO.
             */
            if (alquilerEditar == null) {

                boolean disponibles =
                        gestorAlquiler.verificarDisponibilidad(
                                idsPrendas,
                                fechaInicioValidada,
                                fechaFinValidada
                        );

                if (!disponibles) {
                    fechaFin.mostrarError(
                            "Una o varias prendas no están disponibles "
                                    + "para las fechas seleccionadas."
                    );
                    return;
                }
            }

            // Construir detalles conservando los valores acordados.
            List<IDetalleAlquiler> detalles =
                    construirDetallesAlquiler();

            IAlquiler resultadoAlquiler;

            if (alquilerEditar == null) {

                IAlquiler nuevoAlquiler = new Alquiler(
                        fechaInicioValidada,
                        fechaFinValidada,
                        cliente,
                        empleado,
                        detalles
                );

                resultadoAlquiler =
                        gestorAlquiler.registrarAlquiler(nuevoAlquiler);

            } else {

                IAlquiler alquilerActualizado = new Alquiler(
                        alquilerEditar.getIdAlquiler(),
                        alquilerEditar.getNumeroConsecutivo(),
                        alquilerEditar.getFechaSolicitud(),
                        fechaInicioValidada,
                        fechaFinValidada,
                        cliente,
                        empleado,
                        alquilerEditar.getEstado(),
                        detalles
                );

                resultadoAlquiler =
                        gestorAlquiler.actualizarAlquiler(
                                alquilerActualizado
                        );
            }

            if (resultadoAlquiler == null) {
                throw new IllegalStateException(
                        "La operación no devolvió el alquiler guardado."
                );
            }

            mostrarResultadoGuardado(resultadoAlquiler);

            dispose();

            if (alGuardar != null) {
                alGuardar.run();
            }

        } catch (ValidacionException ex) {

            if (ex.tieneErroresPorCampo()) {
                mostrarErroresValidacion(ex.getErrores());
            } else {
                mostrarErrorGeneral(ex.getMessage());
            }

        } catch (Exception ex) {

            mostrarErrorGeneral(
                    ex.getMessage() == null
                            ? "No fue posible guardar el alquiler."
                            : "No fue posible guardar el alquiler: "
                            + ex.getMessage()
            );

        } finally {

            if (isDisplayable()) {
                formulario.getBotonGuardar().setEnabled(true);
            }
        }
    }

    private List<IDetalleAlquiler> construirDetallesAlquiler() {

        List<IDetalleAlquiler> detalles = new ArrayList<>();

        for (int i = 0; i < prendasSeleccionadas.size(); i++) {

            IPrenda prenda = prendasSeleccionadas.get(i);
            BigDecimal valorAcordado = valoresAcordados.get(i);

            detalles.add(
                    new DetalleAlquiler(
                            prenda,
                            valorAcordado
                    )
            );
        }

        return detalles;
    }

    // ==================================================
    // RESULTADOS Y ERRORES
    // ==================================================

    private void mostrarResultadoGuardado(IAlquiler alquiler) {

        String mensaje = alquilerEditar == null
                ? "El alquiler número "
                + alquiler.getNumeroConsecutivo()
                + " se registró correctamente."
                : "El alquiler número "
                + alquiler.getNumeroConsecutivo()
                + " se actualizó correctamente.";

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                alquilerEditar == null
                        ? "Alquiler registrado"
                        : "Alquiler actualizado",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarErrorGeneral(String mensaje) {

        if (mensaje == null || mensaje.isBlank()) {
            return;
        }

        errorGeneral.setText(mensaje);
        errorGeneral.setVisible(true);

        revalidate();
        repaint();
    }

    private void limpiarErrorGeneral() {

        errorGeneral.setText(" ");
        errorGeneral.setVisible(false);

        revalidate();
        repaint();
    }

    // ==================================================
    // ACCESO PÚBLICO
    // ==================================================

    /**
     * Muestra el formulario de manera modal.
     */
    public void mostrar() {
        setVisible(true);
    }
}
