
package com.lavanderia.vista.paneles.prendas;

import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;
import com.lavanderia.modelo.interfaces.IDisfraz;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.prenda.IPrendaValidator;
import com.lavanderia.vista.componentes.CampoTexto;
import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;

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

    private boolean guardando;

    // ==========================================
    // COMPONENTES GENERALES
    // ==========================================

    private final JComboBox<TipoPrenda> selectorTipo =
            new JComboBox<>(TipoPrenda.values());

    private final CampoTexto txtReferencia =
            new CampoTexto("Referencia", "Referencia única de la prenda");

    private final CampoTexto txtColor =
            new CampoTexto("Color", "Color de la prenda");

    private final CampoTexto txtMarca =
            new CampoTexto("Marca", "Marca de la prenda");

    private final CampoTexto txtTalla =
            new CampoTexto("Talla", "Talla de la prenda");

    private final CampoTexto txtValorAlquiler =
            new CampoTexto("Valor de alquiler", "Valor en pesos");

    // ==========================================
    // COMPONENTES DE VESTIDO
    // ==========================================

    private final JComboBox<Boolean> cbPedreria =
            new JComboBox<>(new Boolean[]{false, true});

    private final CampoTexto txtLargo =
            new CampoTexto("Largo", "Largo del vestido");

    private final CampoTexto txtNumeroPiezas =
            new CampoTexto("Número de piezas", "Cantidad de piezas");

    // ==========================================
    // COMPONENTES DE TRAJE
    // ==========================================

    private final CampoTexto txtTipoTraje =
            new CampoTexto("Tipo de traje", "Ej. formal, smoking...");

    private final CampoTexto txtTipoComplemento =
            new CampoTexto("Tipo de complemento", "Ej. corbata, chaleco...");

    // ==========================================
    // COMPONENTES DE DISFRAZ
    // ==========================================

    private final CampoTexto txtNombreDisfraz =
            new CampoTexto("Nombre", "Nombre del disfraz");

    // ==========================================
    // CONTENEDORES
    // ==========================================

    private final CardLayout cardLayout = new CardLayout();

    private final JPanel panelEspecifico =
            new JPanel(cardLayout);

    private final PanelFormulario formulario =
            new PanelFormulario();

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
        construirFormulario();
        configurarEventos();

        if (original != null) {
            cargarDatosOriginales();
        }

        setLocationRelativeTo(propietario);
    }

    // ==========================================
    // CONFIGURACIÓN DE VENTANA
    // ==========================================

    private void configurarVentana() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 760);
        setMinimumSize(new Dimension(560, 620));
        setResizable(true);
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

        construirCampos();

        formulario.configurarBotonGuardar(
                original == null
                        ? "Registrar prenda"
                        : "Guardar cambios",
                e -> guardar()
        );

        formulario.configurarBotonCancelar(
                "Cancelar",
                e -> dispose()
        );

        formulario.getBotonGuardar()
                .configurarIcono(IconosUI.guardar());

        formulario.getBotonCancelar()
                .configurarIcono(IconosUI.cancelar());

        getRootPane().setDefaultButton(
                formulario.getBotonGuardar()
        );

        principal.add(crearEncabezado(), BorderLayout.NORTH);
        principal.add(formulario, BorderLayout.CENTER);

        setContentPane(principal);
    }

    private void construirCampos() {

        int fila = 0;

        formulario.agregarCampo(
                crearTituloSeccion("Información general"),
                fila++,
                0
        );

        formulario.agregarCampo(
                crearCampoCombo("Tipo de prenda", selectorTipo),
                fila++,
                0
        );

        formulario.agregarCampo(txtReferencia, fila++, 0);
        formulario.agregarCampo(txtColor, fila++, 0);
        formulario.agregarCampo(txtMarca, fila++, 0);
        formulario.agregarCampo(txtTalla, fila++, 0);
        formulario.agregarCampo(txtValorAlquiler, fila++, 0);

        construirSeccionesEspecificas();

        formulario.agregarSeparador(fila++, 0, 1);

        formulario.agregarCampo(panelEspecifico, fila, 0);

        panelEspecifico.setOpaque(false);
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
        ));

        panel.setBackground(FONDO);
        panel.setOpaque(true);

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
    // SECCIONES ESPECÍFICAS
    // ==========================================

    private void construirSeccionesEspecificas() {

        JPanel vestidos = crearPanelEspecifico(
                "Características del vestido"
        );

        vestidos.add(
                crearCampoCombo("Tiene pedrería", cbPedreria)
        );

        vestidos.add(txtLargo);
        vestidos.add(txtNumeroPiezas);

        JPanel trajes = crearPanelEspecifico(
                "Características del traje"
        );

        trajes.add(txtTipoTraje);
        trajes.add(txtTipoComplemento);

        JPanel disfraces = crearPanelEspecifico(
                "Características del disfraz"
        );

        disfraces.add(txtNombreDisfraz);

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
    }

    private JPanel crearPanelEspecifico(String titulo) {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
        ));

        panel.setBackground(SUPERFICIE);
        panel.setOpaque(true);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BORDE_PANEL,
                new EmptyBorder(
                        ESPACIADO_MD,
                        ESPACIADO_MD,
                        ESPACIADO_MD,
                        ESPACIADO_MD
                )
        ));

        panel.add(crearTituloSeccion(titulo));
        panel.add(Box.createVerticalStrut(ESPACIADO_SM));

        return panel;
    }

    private JLabel crearTituloSeccion(String texto) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(FUENTE_SUBTITULO);
        etiqueta.setForeground(TEXTO);

        etiqueta.setBorder(new EmptyBorder(
                ESPACIADO_XS,
                0,
                ESPACIADO_SM,
                0
        ));

        return etiqueta;
    }

    private JPanel crearCampoCombo(
            String etiqueta,
            JComboBox<?> combo
    ) {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
        ));

        panel.setBackground(SUPERFICIE);
        panel.setOpaque(true);

        JLabel label = new JLabel(etiqueta);

        label.setFont(FUENTE_ETIQUETA);
        label.setForeground(TEXTO);

        combo.setFont(FUENTE_NORMAL);
        combo.setBackground(SUPERFICIE);
        combo.setForeground(TEXTO);

        combo.setPreferredSize(new Dimension(
                ANCHO_CAMPO_MINIMO,
                ALTO_CAMPO
        ));

        combo.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                ALTO_CAMPO
        ));

        panel.add(label);
        panel.add(Box.createVerticalStrut(ESPACIADO_SM));
        panel.add(combo);

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

        datos.put("referencia", txtReferencia.getText());
        datos.put("color", txtColor.getText());
        datos.put("marca", txtMarca.getText());
        datos.put("talla", txtTalla.getText());
        datos.put("valorAlquiler", txtValorAlquiler.getText());

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

            datos.put("largo", txtLargo.getText());

            datos.put(
                    "numeroPiezas",
                    txtNumeroPiezas.getText()
            );

        } else if (tipo == TipoPrenda.TRAJE) {

            datos.put(
                    "tipoTraje",
                    txtTipoTraje.getText()
            );

            datos.put(
                    "tipoComplemento",
                    txtTipoComplemento.getText()
            );

        } else if (tipo == TipoPrenda.DISFRAZ) {

            datos.put("nombre", txtNombreDisfraz.getText());
        }

        return datos;
    }

    // ==========================================
    // GUARDAR
    // ==========================================

    private void guardar() {

        if (guardando) {
            return;
        }

        TipoPrenda tipo =
                (TipoPrenda) selectorTipo.getSelectedItem();

        if (tipo == null) {
            mostrarMensaje(
                    "Selecciona un tipo de prenda.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        guardando = true;
        formulario.setBotonesHabilitados(false);

        try {

            Map<String, String> datos = obtenerDatos();

            ResultadoValidacion<Map<String, String>> resultado =
                    validarDatos(tipo, datos);

            if (!resultado.esValido()) {
                mostrarErrores(resultado.getErrores());
                return;
            }

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

        } finally {

            guardando = false;

            if (isDisplayable()) {
                formulario.setBotonesHabilitados(true);
            }
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

        String referenciaActual = original.getReferencia();

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
