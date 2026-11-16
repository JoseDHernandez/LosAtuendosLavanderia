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
import com.lavanderia.vista.componentes.SelectorFecha;
import com.lavanderia.vista.estilos.EstilosUI;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;
import com.lavanderia.vista.paneles.comunes.PanelTabla;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Formulario para registrar y editar contratos de alquiler.
 *
 * Utiliza interfaces de servicio y componentes comunes.
 * La disponibilidad se consulta mediante IGestorAlquiler.
 */
public class FormularioAlquileres extends JDialog {

    // ==========================================
    // DEPENDENCIAS
    // ==========================================

    private final IGestorPersonas gestorPersonas;
    private final IGestorPrendas gestorPrendas;
    private final IGestorAlquiler gestorAlquiler;

    private final IAlquilerValidator validador;
    private final Runnable alGuardar;

    private final IAlquiler alquilerEditar;

    // ==========================================
    // SELECTORES
    // ==========================================

    private final JComboBox<ICliente> selectorCliente;
    private final JComboBox<IEmpleado> selectorEmpleado;
    private final JComboBox<IPrenda> selectorPrenda;

    // ==========================================
    // FECHAS
    // ==========================================

    private final SelectorFecha fechaInicio;
    private final SelectorFecha fechaFin;

    // ==========================================
    // TABLA
    // ==========================================

    private final DefaultTableModel modeloTabla;
    private final PanelTabla panelTabla;

    // ==========================================
    // PRENDAS SELECCIONADAS
    // ==========================================

    private final List<IPrenda> prendasSeleccionadas;
    private final List<BigDecimal> valoresAcordados;

    /*
     * Identificadores de las prendas que pertenecían
     * originalmente al alquiler que se está editando.
     *
     * Permite volver a agregarlas sin que la consulta de
     * disponibilidad detecte el propio alquiler como conflicto.
     */
    private final Set<Long> idsPrendasOriginales;

    // ==========================================
    // ETIQUETAS Y ERRORES
    // ==========================================

    private final JLabel etiquetaTotal;

    private final JLabel errorCliente;
    private final JLabel errorEmpleado;
    private final JLabel errorPrenda;
    private final JLabel errorGeneral;

    // ==========================================
    // FORMULARIO Y BOTONES
    // ==========================================

    private final PanelFormulario formulario;

    private BotonPrincipal botonGuardar;
    private BotonSecundario botonCancelar;
    private BotonSecundario botonAgregarPrenda;
    private BotonSecundario botonEliminarPrenda;

    private boolean guardando;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public FormularioAlquileres(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador,
            Runnable alGuardar
    ) {

        this(
                propietario,
                sistemaFacade,
                validador,
                null,
                alGuardar
        );
    }

    public FormularioAlquileres(
            Window propietario,
            ISistemaFacade sistemaFacade,
            IAlquilerValidator validador,
            IAlquiler alquilerEditar,
            Runnable alGuardar
    ) {

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
                "El validador de alquileres es obligatorio."
        );

        this.alquilerEditar = alquilerEditar;
        this.alGuardar = alGuardar;

        selectorCliente = new JComboBox<>();
        selectorEmpleado = new JComboBox<>();
        selectorPrenda = new JComboBox<>();

        fechaInicio = new SelectorFecha("Fecha de inicio");
        fechaFin = new SelectorFecha("Fecha de finalización");

        // El rango inicial siempre es válido: hoy hasta mañana.
        LocalDate hoy = LocalDate.now();
        fechaInicio.setFecha(convertirAFecha(hoy));
        fechaFin.setFecha(convertirAFecha(hoy.plusDays(1)));

        prendasSeleccionadas = new ArrayList<>();
        valoresAcordados = new ArrayList<>();

        idsPrendasOriginales = new HashSet<>();

        modeloTabla = crearModeloTabla();

        panelTabla = new PanelTabla();
        etiquetaTotal = new JLabel("Total: $0");

        errorCliente = crearEtiquetaError();
        errorEmpleado = crearEtiquetaError();
        errorPrenda = crearEtiquetaError();
        errorGeneral = crearEtiquetaError();

        /*
         * Se crea sin botones internos porque las acciones
         * se ubican al final del formulario completo.
         */
        formulario = new PanelFormulario(
                null,
                null,
                null,
                null
        );

        inicializarComponentes();

        cargarPersonas();
        configurarSelectores();
        configurarTablaPrendas();

        if (alquilerEditar == null) {
            cargarPrendasDisponibles();
        } else {
            cargarDatosAlquiler(alquilerEditar);
        }

        // Se conectan después de asignar las fechas iniciales.
        configurarEventosSelectorPrendas();
        configurarEventosFechas();

        setLocationRelativeTo(propietario);
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel principal = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        principal.setBackground(SUPERFICIE);
        principal.setBorder(MARGEN_PANEL_ESTANDAR);

        principal.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        JPanel contenido = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        contenido.setOpaque(false);

        configurarFormularioGeneral();

        contenido.add(
                formulario,
                BorderLayout.NORTH
        );

        contenido.add(
                crearPanelPrendas(),
                BorderLayout.CENTER
        );

        principal.add(
                contenido,
                BorderLayout.CENTER
        );

        principal.add(
                crearPanelInferior(),
                BorderLayout.SOUTH
        );

        setContentPane(principal);

        setMinimumSize(
                new Dimension(900, 720)
        );

        setPreferredSize(
                new Dimension(1100, 850)
        );

        pack();
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setOpaque(false);

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

        panel.add(titulo);

        panel.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        panel.add(descripcion);

        return panel;
    }

    // ==========================================
    // FORMULARIO GENERAL
    // ==========================================

    private void configurarFormularioGeneral() {

        formulario.agregarCampo(
                crearCampo(
                        "Cliente",
                        selectorCliente,
                        errorCliente
                ),
                0,
                0
        );

        formulario.agregarCampo(
                crearCampo(
                        "Empleado responsable",
                        selectorEmpleado,
                        errorEmpleado
                ),
                0,
                1
        );

        formulario.agregarCampo(
                fechaInicio,
                1,
                0
        );

        formulario.agregarCampo(
                fechaFin,
                1,
                1
        );
    }

    // ==========================================
    // COMPONENTES AUXILIARES
    // ==========================================

    private JLabel crearEtiquetaError() {

        JLabel etiqueta = new JLabel(" ");

        etiqueta.setFont(FUENTE_PEQUENA);
        etiqueta.setForeground(EstilosUI.ERROR);

        return etiqueta;
    }

    private JLabel crearEtiqueta(
            String texto,
            Font fuente,
            Color color
    ) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);

        return etiqueta;
    }

    private JPanel crearCampo(
            String titulo,
            JComponent componente,
            JLabel error
    ) {

        JPanel panel = new JPanel(
                new BorderLayout(0, ESPACIADO_XS)
        );

        panel.setOpaque(false);

        JLabel etiqueta = crearEtiqueta(
                titulo,
                FUENTE_ETIQUETA,
                TEXTO
        );

        panel.add(
                etiqueta,
                BorderLayout.NORTH
        );

        panel.add(
                componente,
                BorderLayout.CENTER
        );

        panel.add(
                error,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // ==========================================
    // ESTILO DE SELECTORES
    // ==========================================

    private void configurarSelectores() {

        configurarEstiloSelector(selectorCliente);
        configurarEstiloSelector(selectorEmpleado);
        configurarEstiloSelector(selectorPrenda);

        configurarRenderizadoresPersonas();
        configurarRenderizadorPrendas();
    }

    private void configurarEstiloSelector(
            JComboBox<?> selector
    ) {

        selector.setFont(FUENTE_NORMAL);
        selector.setForeground(TEXTO);
        selector.setBackground(SUPERFICIE);

        selector.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selector.setMinimumSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        selector.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_CAMPO
                )
        );

        selector.setBorder(
                BorderFactory.createCompoundBorder(
                        BORDE_SIMPLE,
                        BorderFactory.createEmptyBorder(
                                0,
                                ESPACIADO_SM,
                                0,
                                ESPACIADO_SM
                        )
                )
        );

        selector.setFocusable(true);
    }

    // ==========================================
    // RENDERIZADORES DE PERSONAS
    // ==========================================

    private void configurarRenderizadoresPersonas() {

        selectorCliente.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado
                    ) {

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
                            boolean enfocado
                    ) {

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
    // CARGA DE PERSONAS
    // ==========================================

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

        errorCliente.setText(
                selectorCliente.getItemCount() == 0
                        ? "No hay clientes registrados."
                        : " "
        );

        errorEmpleado.setText(
                selectorEmpleado.getItemCount() == 0
                        ? "No hay empleados registrados."
                        : " "
        );
    }

    // ==========================================
    // RENDERIZADOR DE PRENDAS
    // ==========================================

    private void configurarRenderizadorPrendas() {

        selectorPrenda.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado
                    ) {

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

    // ==========================================
    // ACTUALIZACIÓN DEL SELECTOR DE PRENDAS
    // ==========================================

    /**
     * Se actualiza la disponibilidad cada vez que el usuario
     * abre el selector.
     *
     * Así se utilizan las fechas que estén seleccionadas
     * en ese momento, sin depender de la implementación
     * interna de SelectorFecha.
     */
    /**
     * Actualiza la disponibilidad al cambiar las fechas.
     * También confirma la edición manual al salir del campo.
     */
    private void configurarEventosFechas() {

        fechaInicio.getSelector().addChangeListener(
                evento -> actualizarDisponibilidadPorFecha()
        );

        fechaFin.getSelector().addChangeListener(
                evento -> actualizarDisponibilidadPorFecha()
        );

        JSpinner.DateEditor editorInicio =
                (JSpinner.DateEditor) fechaInicio.getSelector().getEditor();

        JSpinner.DateEditor editorFin =
                (JSpinner.DateEditor) fechaFin.getSelector().getEditor();

        editorInicio.getTextField().addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusLost(java.awt.event.FocusEvent evento) {
                        actualizarDisponibilidadPorFecha();
                    }
                }
        );

        editorFin.getTextField().addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusLost(java.awt.event.FocusEvent evento) {
                        actualizarDisponibilidadPorFecha();
                    }
                }
        );
    }

    private void actualizarDisponibilidadPorFecha() {

        try {
            obtenerFecha(fechaInicio);
            obtenerFecha(fechaFin);
            cargarPrendasDisponibles();

        } catch (RuntimeException ex) {

            selectorPrenda.removeAllItems();
            selectorPrenda.setEnabled(false);
            botonAgregarPrenda.setEnabled(false);

            mostrarErrorPrenda(
                    "Ingrese fechas válidas para consultar las prendas."
            );
        }
    }

    private void configurarEventosSelectorPrendas() {

        selectorPrenda.addPopupMenuListener(
                new PopupMenuListener() {

                    @Override
                    public void popupMenuWillBecomeVisible(
                            PopupMenuEvent evento
                    ) {

                        cargarPrendasDisponibles();
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(
                            PopupMenuEvent evento
                    ) {
                    }

                    @Override
                    public void popupMenuCanceled(
                            PopupMenuEvent evento
                    ) {
                    }
                }
        );
    }

    /**
     * Consulta el catálogo y conserva solamente las prendas:
     *
     * 1. Registradas y en estado DISPONIBLE.
     * 2. No agregadas todavía a la tabla.
     * 3. Sin conflictos en el intervalo seleccionado.
     *
     * En edición, las prendas que pertenecían originalmente
     * al alquiler se consideran elegibles porque el DAO
     * excluye el propio alquiler al actualizarlo.
     */
    private void cargarPrendasDisponibles() {

        selectorPrenda.removeAllItems();

        limpiarErrorPrenda();

        List<IPrenda> prendas;

        try {

            prendas = gestorPrendas.listarPrendas();

        } catch (RuntimeException ex) {

            selectorPrenda.setEnabled(false);
            botonAgregarPrenda.setEnabled(false);

            mostrarErrorPrenda(
                    obtenerMensajeError(
                            ex,
                            "No fue posible consultar el inventario."
                    )
            );

            return;
        }

        if (prendas == null) {

            selectorPrenda.setEnabled(false);
            botonAgregarPrenda.setEnabled(false);

            mostrarErrorPrenda(
                    "No fue posible consultar las prendas registradas."
            );

            return;
        }

        LocalDate inicio;
        LocalDate fin;

        try {

            inicio = obtenerFecha(fechaInicio);
            fin = obtenerFecha(fechaFin);

            if (!fin.isAfter(inicio)) {

                throw new ValidacionException(
                        "La fecha de finalización debe ser posterior a la fecha de inicio."
                );
            }

        } catch (RuntimeException ex) {

            selectorPrenda.setEnabled(false);
            botonAgregarPrenda.setEnabled(false);

            mostrarErrorPrenda(
                    "Seleccione un rango de fechas válido para consultar las prendas."
            );

            return;
        }

        int cantidadDisponibles = 0;

        for (IPrenda prenda : prendas) {

            if (prenda == null) {
                continue;
            }

            long idPrenda = prenda.getIdPrenda();

            if (contienePrenda(idPrenda)) {
                continue;
            }

            boolean perteneceAlquilerActual =
                    idsPrendasOriginales.contains(idPrenda);

            if (!perteneceAlquilerActual
                    && prenda.getEstado() != EstadoPrenda.DISPONIBLE) {
                continue;
            }

            if (!estaDisponibleParaFechas(
                    prenda,
                    inicio,
                    fin
            )) {
                continue;
            }

            selectorPrenda.addItem(prenda);
            cantidadDisponibles++;
        }

        boolean hayDisponibles = cantidadDisponibles > 0;

        selectorPrenda.setEnabled(hayDisponibles);
        botonAgregarPrenda.setEnabled(hayDisponibles);

        if (!hayDisponibles) {

            mostrarErrorPrenda(
                    "No hay prendas disponibles para las fechas seleccionadas."
            );
        }

        selectorPrenda.revalidate();
        selectorPrenda.repaint();
    }

    /**
     * Consulta disponibilidad para una sola prenda.
     *
     * Se usa una lista de un elemento porque el método actual
     * del gestor devuelve un único boolean para toda la lista.
     */
    private boolean estaDisponibleParaFechas(
            IPrenda prenda,
            LocalDate inicio,
            LocalDate fin
    ) {

        if (prenda == null) {
            return false;
        }

        if (prenda.getEstado() != EstadoPrenda.DISPONIBLE) {
            return false;
        }

        long idPrenda = prenda.getIdPrenda();

        /*
         * Si la prenda pertenecía al alquiler que se está editando,
         * el conflicto que se consulta correspondería al propio
         * alquiler. Se permite seleccionarla porque el DAO de
         * actualización excluye ese alquiler de sus conflictos.
         */
        if (alquilerEditar != null
                && idsPrendasOriginales.contains(idPrenda)) {
            return true;
        }

        try {

            return gestorAlquiler.verificarDisponibilidad(
                    List.of(idPrenda),
                    inicio,
                    fin
            );

        } catch (RuntimeException ex) {

            /*
             * Ante una respuesta inválida o un error de consulta,
             * la prenda no se ofrece como disponible.
             */
            return false;
        }
    }

    // ==========================================
    // TABLA DE PRENDAS
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
                    int columna
            ) {

                return false;
            }
        };
    }

    private void configurarTablaPrendas() {

        panelTabla.setBusquedaVisible(false);
        panelTabla.setPanelFiltrosVisible(false);
        panelTabla.setModelo(modeloTabla);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(false);

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        tabla.setRowHeight(ALTO_FILA_TABLA);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setFillsViewportHeight(true);
    }

    // ==========================================
    // PANEL DE PRENDAS
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

        // ------------------------------------------
        // SELECTOR Y BOTÓN AGREGAR
        // ------------------------------------------

        JPanel panelSeleccion = new JPanel(
                new BorderLayout(ESPACIADO_SM, 0)
        );

        panelSeleccion.setOpaque(false);

        botonAgregarPrenda =
                new BotonSecundario("Agregar prenda");

        botonAgregarPrenda.configurarIcono(
                IconosUI.agregar()
        );

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

        JPanel panelSelector = new JPanel(
                new BorderLayout(0, ESPACIADO_XS)
        );

        panelSelector.setOpaque(false);

        panelSelector.add(
                panelSeleccion,
                BorderLayout.CENTER
        );

        panelSelector.add(
                errorPrenda,
                BorderLayout.SOUTH
        );

        // ------------------------------------------
        // ACCIONES Y TOTAL
        // ------------------------------------------

        botonEliminarPrenda =
                new BotonSecundario("Quitar seleccionada");

        botonEliminarPrenda.configurarIcono(
                IconosUI.eliminar()
        );

        botonEliminarPrenda.addActionListener(
                e -> eliminarPrenda()
        );

        etiquetaTotal.setFont(FUENTE_SECCION);
        etiquetaTotal.setForeground(TEXTO);

        JPanel panelAccionesTabla = new JPanel(
                new BorderLayout()
        );

        panelAccionesTabla.setOpaque(false);

        panelAccionesTabla.add(
                botonEliminarPrenda,
                BorderLayout.WEST
        );

        panelAccionesTabla.add(
                etiquetaTotal,
                BorderLayout.EAST
        );

        // ------------------------------------------
        // CONTENIDO
        // ------------------------------------------

        JPanel centro = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        centro.setOpaque(false);

        centro.add(
                panelSelector,
                BorderLayout.NORTH
        );

        centro.add(
                panelTabla,
                BorderLayout.CENTER
        );

        centro.add(
                panelAccionesTabla,
                BorderLayout.SOUTH
        );

        panel.add(
                titulo,
                BorderLayout.NORTH
        );

        panel.add(
                centro,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ==========================================
    // CARGAR ALQUILER PARA EDICIÓN
    // ==========================================

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
        idsPrendasOriginales.clear();

        modeloTabla.setRowCount(0);

        List<IDetalleAlquiler> detalles =
                alquiler.getDetalles();

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

                idsPrendasOriginales.add(
                        prenda.getIdPrenda()
                );

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
                    cliente.getIdentificacion()
            )) {

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
                    empleado.getIdentificacion()
            )) {

                selectorEmpleado.setSelectedIndex(i);
                return;
            }
        }

        errorEmpleado.setText(
                "El empleado asociado no aparece en el listado."
        );
    }

    // ==========================================
    // FECHAS
    // ==========================================

    private Date convertirAFecha(LocalDate fecha) {

        return Date.from(
                fecha.atStartOfDay(
                        ZoneId.systemDefault()
                ).toInstant()
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

    // ==========================================
    // AGREGAR PRENDA
    // ==========================================

    private void agregarPrenda() {

        limpiarErrorPrenda();

        IPrenda prenda =
                (IPrenda) selectorPrenda.getSelectedItem();

        if (prenda == null) {

            mostrarErrorPrenda(
                    "Seleccione una prenda disponible."
            );

            return;
        }

        if (contienePrenda(prenda.getIdPrenda())) {

            mostrarErrorPrenda(
                    "La prenda seleccionada ya está en el alquiler."
            );

            return;
        }

        /*
         * Se comprueba nuevamente la disponibilidad antes de agregar.
         * El selector puede haber quedado abierto mientras otro usuario
         * registraba un alquiler.
         */
        try {

            LocalDate inicio = obtenerFecha(fechaInicio);
            LocalDate fin = obtenerFecha(fechaFin);

            if (!fin.isAfter(inicio)) {

                mostrarErrorPrenda(
                        "La fecha de finalización debe ser posterior a la fecha de inicio."
                );

                return;
            }

            if (prenda.getEstado() != EstadoPrenda.DISPONIBLE) {

                mostrarErrorPrenda(
                        "La prenda ya no se encuentra disponible."
                );

                cargarPrendasDisponibles();
                return;
            }

            if (!estaDisponibleParaFechas(
                    prenda,
                    inicio,
                    fin
            )) {

                mostrarErrorPrenda(
                        "La prenda ya está reservada para esas fechas."
                );

                cargarPrendasDisponibles();
                return;
            }

        } catch (RuntimeException ex) {

            mostrarErrorPrenda(
                    obtenerMensajeError(
                            ex,
                            "No fue posible comprobar la disponibilidad."
                    )
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

    // ==========================================
    // AGREGAR A LA TABLA
    // ==========================================

    private void agregarPrendaALaTabla(
            IPrenda prenda,
            BigDecimal valorAcordado
    ) {

        prendasSeleccionadas.add(prenda);
        valoresAcordados.add(valorAcordado);

        modeloTabla.addRow(
                new Object[]{
                        prenda.getReferencia(),
                        prenda.getMarca(),
                        prenda.getTalla(),
                        valorAcordado
                }
        );
    }

    // ==========================================
    // ELIMINAR PRENDA
    // ==========================================

    private void eliminarPrenda() {

        limpiarErrorPrenda();

        JTable tabla = panelTabla.getTablaDatos().getTabla();

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {

            mostrarErrorPrenda(
                    "Seleccione una prenda de la tabla para quitarla."
            );

            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(
                filaVista
        );

        prendasSeleccionadas.remove(filaModelo);
        valoresAcordados.remove(filaModelo);

        modeloTabla.removeRow(filaModelo);

        actualizarTotal();
        cargarPrendasDisponibles();
    }

    // ==========================================
    // UTILIDADES DE PRENDAS
    // ==========================================

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
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        etiquetaTotal.setText(
                "Total: $" + total.toPlainString()
        );
    }

    // ==========================================
    // PANEL INFERIOR Y BOTONES
    // ==========================================

    private JPanel crearPanelInferior() {

        JPanel panel = new JPanel(
                new BorderLayout(0, ESPACIADO_SM)
        );

        panel.setOpaque(false);

        errorGeneral.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        acciones.setOpaque(false);

        botonCancelar =
                new BotonSecundario("Cancelar");

        botonCancelar.configurarIcono(
                IconosUI.cancelar()
        );

        botonCancelar.addActionListener(
                e -> dispose()
        );

        botonGuardar =
                new BotonPrincipal(
                        alquilerEditar == null
                                ? "Registrar alquiler"
                                : "Guardar cambios"
                );

        botonGuardar.configurarIcono(
                IconosUI.guardar()
        );

        botonGuardar.addActionListener(
                e -> guardarAlquiler()
        );

        acciones.add(botonCancelar);
        acciones.add(botonGuardar);

        panel.add(
                errorGeneral,
                BorderLayout.NORTH
        );

        panel.add(
                acciones,
                BorderLayout.SOUTH
        );

        getRootPane().setDefaultButton(botonGuardar);

        return panel;
    }

    // ==========================================
    // VALIDACIÓN INICIAL
    // ==========================================

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

            mostrarErrorPrenda(
                    "Debe agregar al menos una prenda al alquiler."
            );

            valido = false;
        }

        return valido;
    }

    // ==========================================
    // DATOS PARA VALIDACIÓN
    // ==========================================

    private Map<String, String> construirDatosValidacion(
            ICliente cliente,
            IEmpleado empleado,
            LocalDate inicio,
            LocalDate fin
    ) {

        Map<String, String> datos = new HashMap<>();

        datos.put(
                "fechaInicio",
                inicio.toString()
        );

        datos.put(
                "fechaFinPrevista",
                fin.toString()
        );

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

    // ==========================================
    // ERRORES DE VALIDACIÓN
    // ==========================================

    private void mostrarErroresValidacion(
            Map<String, String> errores
    ) {

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

        limpiarErrorPrenda();
        limpiarErrorGeneral();
    }

    // ==========================================
    // GUARDAR ALQUILER
    // ==========================================

    private void guardarAlquiler() {

        if (guardando) {
            return;
        }

        limpiarErroresCampos();

        if (!validarSeleccionInicial()) {
            return;
        }

        guardando = true;

        botonGuardar.setEnabled(false);
        botonCancelar.setEnabled(false);

        try {

            ICliente cliente =
                    (ICliente) selectorCliente.getSelectedItem();

            IEmpleado empleado =
                    (IEmpleado) selectorEmpleado.getSelectedItem();

            LocalDate inicio = obtenerFecha(fechaInicio);
            LocalDate fin = obtenerFecha(fechaFin);

            // --------------------------------------
            // VALIDAR FECHAS Y PERSONAS
            // --------------------------------------

            Map<String, String> datos =
                    construirDatosValidacion(
                            cliente,
                            empleado,
                            inicio,
                            fin
                    );

            ResultadoValidacion<Map<String, String>> resultado =
                    validador.validar(datos);

            if (resultado == null) {
                throw new IllegalStateException(
                        "El validador no devolvió un resultado."
                );
            }

            if (!resultado.esValido()) {
                mostrarErroresValidacion(resultado.getErrores());
                return;
            }

            Map<String, String> datosValidados =
                    resultado.getValor().orElseThrow(
                            () -> new ValidacionException(
                                    "No fue posible obtener los datos validados."
                            )
                    );

            LocalDate fechaInicioValidada =
                    LocalDate.parse(
                            datosValidados.get("fechaInicio")
                    );

            LocalDate fechaFinValidada =
                    LocalDate.parse(
                            datosValidados.get("fechaFinPrevista")
                    );

            // --------------------------------------
            // VERIFICAR DISPONIBILIDAD EN REGISTRO
            // --------------------------------------

            /*
             * En registro se realiza una comprobación previa.
             * El propio GestorAlquiler también verifica la
             * disponibilidad y el DAO la vuelve a comprobar
             * dentro de la transacción.
             *
             * En edición no se hace esta consulta general porque
             * detectaría el propio alquiler como conflicto.
             * AlquilerDAOSQLite.actualizar() ya excluye su ID.
             */

            if (alquilerEditar == null) {

                List<Long> idsPrendas =
                        prendasSeleccionadas.stream()
                                .map(IPrenda::getIdPrenda)
                                .toList();

                boolean disponibles =
                        gestorAlquiler.verificarDisponibilidad(
                                idsPrendas,
                                fechaInicioValidada,
                                fechaFinValidada
                        );

                if (!disponibles) {

                    mostrarErrorPrenda(
                            "Una o varias prendas ya no están disponibles "
                                    + "para las fechas seleccionadas. "
                                    + "Actualice la selección."
                    );

                    cargarPrendasDisponibles();
                    return;
                }
            }

            // --------------------------------------
            // CONSTRUIR DETALLES
            // --------------------------------------

            List<IDetalleAlquiler> detalles =
                    construirDetallesAlquiler();

            IAlquiler resultadoAlquiler;

            // --------------------------------------
            // REGISTRAR
            // --------------------------------------

            if (alquilerEditar == null) {

                IAlquiler nuevoAlquiler = new Alquiler(
                        fechaInicioValidada,
                        fechaFinValidada,
                        cliente,
                        empleado,
                        detalles
                );

                resultadoAlquiler =
                        gestorAlquiler.registrarAlquiler(
                                nuevoAlquiler
                        );

            } else {

                // ----------------------------------
                // ACTUALIZAR
                // ----------------------------------

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

            mostrarResultadoGuardado(
                    resultadoAlquiler
            );

            dispose();

            if (alGuardar != null) {
                alGuardar.run();
            }

        } catch (ValidacionException ex) {

            if (ex.tieneErroresPorCampo()) {

                mostrarErroresValidacion(
                        ex.getErrores()
                );

            } else {

                mostrarErrorGeneral(
                        obtenerMensajeError(
                                ex,
                                "Los datos del alquiler no son válidos."
                        )
                );
            }

        } catch (Exception ex) {

            mostrarErrorGeneral(
                    obtenerMensajeError(
                            ex,
                            "No fue posible guardar el alquiler."
                    )
            );

        } finally {

            guardando = false;

            if (isDisplayable()) {

                botonGuardar.setEnabled(true);
                botonCancelar.setEnabled(true);
            }
        }
    }

    // ==========================================
    // CONSTRUIR DETALLES
    // ==========================================

    private List<IDetalleAlquiler> construirDetallesAlquiler() {

        List<IDetalleAlquiler> detalles = new ArrayList<>();

        for (int i = 0; i < prendasSeleccionadas.size(); i++) {

            IPrenda prenda = prendasSeleccionadas.get(i);

            BigDecimal valorAcordado =
                    valoresAcordados.get(i);

            detalles.add(
                    new DetalleAlquiler(
                            prenda,
                            valorAcordado
                    )
            );
        }

        return detalles;
    }

    // ==========================================
    // MENSAJES
    // ==========================================

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

    private void mostrarErrorPrenda(String mensaje) {

        errorPrenda.setText(
                mensaje == null || mensaje.isBlank()
                        ? " "
                        : mensaje
        );

        revalidate();
        repaint();
    }

    private void limpiarErrorPrenda() {
        errorPrenda.setText(" ");
    }

    private void mostrarErrorGeneral(String mensaje) {

        errorGeneral.setText(
                mensaje == null || mensaje.isBlank()
                        ? " "
                        : mensaje
        );

        revalidate();
        repaint();
    }

    private void limpiarErrorGeneral() {
        errorGeneral.setText(" ");
    }

    private String obtenerMensajeError(
            Exception ex,
            String mensajePredeterminado
    ) {

        return ex.getMessage() == null
                || ex.getMessage().isBlank()
                ? mensajePredeterminado
                : ex.getMessage();
    }

    // ==========================================
    // MOSTRAR FORMULARIO
    // ==========================================

    public void mostrar() {
        setVisible(true);
    }
}
