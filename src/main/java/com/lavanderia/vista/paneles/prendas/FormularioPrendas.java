
package com.lavanderia.vista.paneles.prendas;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;
import com.lavanderia.modelo.interfaces.IDisfraz;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.prenda.IPrendaValidator;
import com.lavanderia.vista.componentes.BotonPrincipal;
import com.lavanderia.vista.componentes.BotonSecundario;
import com.lavanderia.vista.estilos.IconosUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class FormularioPrendas extends JDialog {

    private enum TipoPrenda {
        VESTIDO("Vestido de dama"),
        TRAJE("Traje de caballero"),
        DISFRAZ("Disfraz");

        private final String descripcion;

        TipoPrenda(String descripcion) {
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    // ==========================================
    // DEPENDENCIAS
    // ==========================================

    private final IGestorPrendas gestor;
    private final IPrendaValidator validador;
    private final IPrenda original;
    private final Runnable alGuardar;

    // ==========================================
    // COMPONENTES GENERALES
    // ==========================================

    private final JComboBox<TipoPrenda> selectorTipo =
            new JComboBox<>(TipoPrenda.values());

    private final JTextField txtReferencia = new JTextField();
    private final JTextField txtColor = new JTextField();
    private final JTextField txtMarca = new JTextField();
    private final JTextField txtTalla = new JTextField();
    private final JTextField txtValorAlquiler = new JTextField();

    // ==========================================
    // COMPONENTES DE VESTIDO
    // ==========================================

    private final JComboBox<Boolean> cbPedreria =
            new JComboBox<>(new Boolean[]{false, true});

    private final JTextField txtLargo = new JTextField();
    private final JTextField txtNumeroPiezas = new JTextField();

    // ==========================================
    // COMPONENTES DE TRAJE
    // ==========================================

    private final JTextField txtTipoTraje = new JTextField();
    private final JTextField txtTipoComplemento = new JTextField();

    // ==========================================
    // COMPONENTES DE DISFRAZ
    // ==========================================

    private final JTextField txtNombreDisfraz = new JTextField();

    // ==========================================
    // CONTENEDORES
    // ==========================================

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelEspecifico = new JPanel(cardLayout);

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public FormularioPrendas(
            Window propietario,
            IGestorPrendas gestor,
            IPrendaValidator validador,
            Runnable alGuardar
    ) {
        this(propietario, gestor, validador, null, alGuardar);
    }

    public FormularioPrendas(
            Window propietario,
            IGestorPrendas gestor,
            IPrendaValidator validador,
            IPrenda original,
            Runnable alGuardar
    ) {
        super(
                propietario,
                original == null
                        ? "Registrar prenda"
                        : "Editar prenda",
                ModalityType.APPLICATION_MODAL
        );

        this.gestor = Objects.requireNonNull(gestor);
        this.validador = Objects.requireNonNull(validador);
        this.original = original;
        this.alGuardar = alGuardar;

        configurarVentana();
        configurarCampos();
        construirFormulario();
        configurarEventos();

        if (original != null) {
            cargarDatosOriginales();
        }

        setLocationRelativeTo(propietario);
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private void configurarVentana() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(620, 690);
        setMinimumSize(new Dimension(560, 620));
        setResizable(true);
    }

    private void configurarCampos() {

        configurarCampo(txtReferencia);
        configurarCampo(txtColor);
        configurarCampo(txtMarca);
        configurarCampo(txtTalla);
        configurarCampo(txtValorAlquiler);

        configurarCampo(txtLargo);
        configurarCampo(txtNumeroPiezas);

        configurarCampo(txtTipoTraje);
        configurarCampo(txtTipoComplemento);

        configurarCampo(txtNombreDisfraz);

        txtValorAlquiler.setToolTipText(
                "Ingresa el valor de alquiler en pesos."
        );

        txtNumeroPiezas.setToolTipText(
                "Ingresa la cantidad de piezas del vestido."
        );
    }

    private void configurarCampo(JTextField campo) {

        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);

        campo.setBorder(BorderFactory.createCompoundBorder(
                BORDE_SIMPLE,
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_SM
                )
        ));

        campo.setPreferredSize(
                new Dimension(200, ALTO_CAMPO)
        );
    }

    // ==========================================
    // CONSTRUCCIÓN DEL FORMULARIO
    // ==========================================

    private void construirFormulario() {

        JPanel principal = new JPanel(new BorderLayout(
                0,
                ESPACIADO_MD
        ));

        principal.setBackground(FONDO);

        principal.setBorder(new EmptyBorder(
                ESPACIADO_LG,
                MARGEN_PANEL,
                ESPACIADO_LG,
                MARGEN_PANEL
        ));

        JPanel encabezado = crearEncabezado();

        JPanel campos = new JPanel();
        campos.setOpaque(false);
        campos.setLayout(new BoxLayout(
                campos,
                BoxLayout.Y_AXIS
        ));

        JPanel generales = crearSeccion(
                "Información general"
        );

        agregarCampo(generales, "Tipo de prenda", selectorTipo);
        agregarCampo(generales, "Referencia", txtReferencia);
        agregarCampo(generales, "Color", txtColor);
        agregarCampo(generales, "Marca", txtMarca);
        agregarCampo(generales, "Talla", txtTalla);
        agregarCampo(generales, "Valor de alquiler", txtValorAlquiler);

        JPanel vestidos = crearSeccion(
                "Características del vestido"
        );

        agregarCampo(vestidos, "Tiene pedrería", cbPedreria);
        agregarCampo(vestidos, "Largo", txtLargo);
        agregarCampo(vestidos, "Número de piezas", txtNumeroPiezas);

        JPanel trajes = crearSeccion(
                "Características del traje"
        );

        agregarCampo(trajes, "Tipo de traje", txtTipoTraje);
        agregarCampo(trajes, "Tipo de complemento", txtTipoComplemento);

        JPanel disfraces = crearSeccion(
                "Características del disfraz"
        );

        agregarCampo(disfraces, "Nombre", txtNombreDisfraz);

        panelEspecifico.add(
                vestidos,
                TipoPrenda.VESTIDO.name()
        );

        panelEspecifico.add(
                trajes,
                TipoPrenda.TRAJE.name()
        );

        panelEspecifico.add(
                disfraces,
                TipoPrenda.DISFRAZ.name()
        );

        panelEspecifico.setOpaque(false);

        campos.add(generales);
        campos.add(Box.createVerticalStrut(ESPACIADO_MD));
        campos.add(panelEspecifico);

        JScrollPane scroll = new JScrollPane(campos);

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel acciones = crearPanelAcciones();

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(scroll, BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);

        panel.setLayout(new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
        ));

        JLabel titulo = new JLabel(
                original == null
                        ? "Registrar nueva prenda"
                        : "Editar prenda"
        );

        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(TEXTO);

        JLabel descripcion = new JLabel(
                original == null
                        ? "Ingresa la información de la prenda."
                        : "Actualiza la información de la prenda seleccionada."
        );

        descripcion.setFont(FUENTE_NORMAL);
        descripcion.setForeground(TEXTO_SECUNDARIO);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(ESPACIADO_XS));
        panel.add(descripcion);
        panel.add(Box.createVerticalStrut(ESPACIADO_MD));

        return panel;
    }

    // ==========================================
    // SECCIONES Y CAMPOS
    // ==========================================

    private JPanel crearSeccion(String titulo) {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(SUPERFICIE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BORDE_PANEL,
                new EmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_MD,
                        ESPACIADO_SM,
                        ESPACIADO_MD
                )
        ));

        return panel;
    }

    private void agregarCampo(
            JPanel panel,
            String etiqueta,
            JComponent componente
    ) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(
                ESPACIADO_XS,
                ESPACIADO_SM,
                ESPACIADO_XS,
                ESPACIADO_SM
        );

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        int fila = panel.getComponentCount() / 2;

        JLabel label = new JLabel(etiqueta);
        label.setFont(FUENTE_ETIQUETA);
        label.setForeground(TEXTO);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.35;

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;

        panel.add(componente, gbc);
    }

    // ==========================================
    // BOTONES
    // ==========================================

    private JPanel crearPanelAcciones() {

        JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                ESPACIADO_SM,
                0
        ));

        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(
                ESPACIADO_MD,
                0,
                0,
                0
        ));

        BotonSecundario btnCancelar =
                new BotonSecundario("Cancelar");

        btnCancelar.configurarIcono(IconosUI.cancelar());
        btnCancelar.addActionListener(e -> dispose());

        BotonPrincipal btnGuardar = new BotonPrincipal(
                original == null
                        ? "Registrar prenda"
                        : "Guardar cambios"
        );

        btnGuardar.configurarIcono(IconosUI.guardar());
        btnGuardar.addActionListener(e -> guardar());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        getRootPane().setDefaultButton(btnGuardar);

        return panel;
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        selectorTipo.addActionListener(e -> {

            TipoPrenda tipo =
                    (TipoPrenda) selectorTipo.getSelectedItem();

            if (tipo != null) {
                cardLayout.show(
                        panelEspecifico,
                        tipo.name()
                );
            }
        });

        if (original != null) {
            selectorTipo.setEnabled(false);
        }
    }

    // ==========================================
    // CARGAR DATOS PARA EDICIÓN
    // ==========================================

    private void cargarDatosOriginales() {

        txtReferencia.setText(original.getReferencia());
        txtColor.setText(original.getColor());
        txtMarca.setText(original.getMarca());
        txtTalla.setText(original.getTalla());

        txtValorAlquiler.setText(
                String.valueOf(original.getValorAlquiler())
        );

        if (original instanceof IVestidoDama vestido) {

            selectorTipo.setSelectedItem(TipoPrenda.VESTIDO);

            cbPedreria.setSelectedItem(
                    vestido.isTienePedreria()
            );

            txtLargo.setText(vestido.getLargo());

            txtNumeroPiezas.setText(
                    String.valueOf(vestido.getNumeroPiezas())
            );

        } else if (original instanceof ITrajeCaballero traje) {

            selectorTipo.setSelectedItem(TipoPrenda.TRAJE);

            txtTipoTraje.setText(
                    traje.getTipoTraje()
            );

            txtTipoComplemento.setText(
                    traje.getTipoComplemento()
            );

        } else if (original instanceof IDisfraz disfraz) {

            selectorTipo.setSelectedItem(TipoPrenda.DISFRAZ);

            txtNombreDisfraz.setText(
                    disfraz.getNombre()
            );
        }
    }

    // ==========================================
    // OBTENER DATOS
    // ==========================================

    private Map<String, String> obtenerDatos() {

        Map<String, String> datos = new HashMap<>();

        datos.put("referencia", txtReferencia.getText().trim());
        datos.put("color", txtColor.getText().trim());
        datos.put("marca", txtMarca.getText().trim());
        datos.put("talla", txtTalla.getText().trim());

        datos.put(
                "valorAlquiler",
                txtValorAlquiler.getText().trim()
        );

        // El estado se establece automáticamente.
        datos.put(
                "estado",
                original == null
                        ? EstadoPrenda.DISPONIBLE.name()
                        : original.getEstado().name()
        );

        TipoPrenda tipo =
                (TipoPrenda) selectorTipo.getSelectedItem();

        if (tipo == TipoPrenda.VESTIDO) {

            datos.put(
                    "tienePedreria",
                    String.valueOf(cbPedreria.getSelectedItem())
            );

            datos.put("largo", txtLargo.getText().trim());

            datos.put(
                    "numeroPiezas",
                    txtNumeroPiezas.getText().trim()
            );

        } else if (tipo == TipoPrenda.TRAJE) {

            datos.put(
                    "tipoTraje",
                    txtTipoTraje.getText().trim()
            );

            datos.put(
                    "tipoComplemento",
                    txtTipoComplemento.getText().trim()
            );

        } else if (tipo == TipoPrenda.DISFRAZ) {

            datos.put(
                    "nombre",
                    txtNombreDisfraz.getText().trim()
            );
        }

        return datos;
    }

    // ==========================================
    // GUARDAR
    // ==========================================

    private void guardar() {

        TipoPrenda tipo =
                (TipoPrenda) selectorTipo.getSelectedItem();

        if (tipo == null) {
            mostrarMensaje(
                    "Selecciona un tipo de prenda.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Map<String, String> datos = obtenerDatos();

        try {

            ResultadoValidacion<Map<String, String>> resultado =
                    validarDatos(tipo, datos);

            if (!resultado.esValido()) {
                mostrarErrores(resultado.getErrores());
                return;
            }

            // Utilizar los datos normalizados por el validador.
            Map<String, String> datosValidados =
                    resultado.obtenerValor();

            if (original == null) {
                registrar(tipo, datosValidados);
            } else {
                actualizar(tipo, datosValidados);
            }

            if (alGuardar != null) {
                alGuardar.run();
            }

            dispose();

        } catch (RuntimeException ex) {

            mostrarMensaje(
                    ex.getMessage() == null
                            ? "No fue posible guardar la prenda."
                            : ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // VALIDACIÓN
    // ==========================================

    private ResultadoValidacion<Map<String, String>> validarDatos(
            TipoPrenda tipo,
            Map<String, String> datos
    ) {

        return switch (tipo) {
            case VESTIDO -> validador.validarVestido(datos);
            case TRAJE -> validador.validarTraje(datos);
            case DISFRAZ -> validador.validarDisfraz(datos);
        };
    }

    // ==========================================
    // REGISTRO
    // ==========================================

    private void registrar(
            TipoPrenda tipo,
            Map<String, String> datos
    ) {

        switch (tipo) {
            case VESTIDO -> gestor.registrarVestido(datos);
            case TRAJE -> gestor.registrarTraje(datos);
            case DISFRAZ -> gestor.registrarDisfraz(datos);
        }
    }

    // ==========================================
    // ACTUALIZACIÓN
    // ==========================================

    private void actualizar(
            TipoPrenda tipo,
            Map<String, String> datos
    ) {

        String referenciaActual =
                original.getReferencia();

        switch (tipo) {

            case VESTIDO ->
                    gestor.actualizarVestido(
                            referenciaActual,
                            datos
                    );

            case TRAJE ->
                    gestor.actualizarTraje(
                            referenciaActual,
                            datos
                    );

            case DISFRAZ ->
                    gestor.actualizarDisfraz(
                            referenciaActual,
                            datos
                    );
        }
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarErrores(Map<String, String> errores) {

        StringBuilder mensaje = new StringBuilder(
                "Revisa los siguientes campos:\n\n"
        );

        errores.forEach((campo, error) ->
                mensaje.append("• ")
                        .append(campo)
                        .append(": ")
                        .append(error)
                        .append("\n")
        );

        mostrarMensaje(
                mensaje.toString(),
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarMensaje(
            String mensaje,
            int tipo
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Gestión de prendas",
                tipo
        );
    }

    // ==========================================
    // MOSTRAR FORMULARIO
    // ==========================================

    public void mostrar() {
        setVisible(true);
    }
}
