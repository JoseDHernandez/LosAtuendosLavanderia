
package com.lavanderia.vista.paneles.prendas;

import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;
import com.lavanderia.modelo.interfaces.IDisfraz;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.prenda.IPrendaValidator;
import com.lavanderia.vista.componentes.BotonSecundario;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PanelPrendas extends PanelModulo {

    // ==========================================
    // DEPENDENCIAS
    // ==========================================

    private final IGestorPrendas gestorPrendas;
    private final IPrendaValidator validador;

    // ==========================================
    // COMPONENTES
    // ==========================================

    private final PanelTabla panelTabla;

    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;
    private final BotonSecundario botonActualizar;

    // ==========================================
    // DATOS
    // ==========================================

    private List<IPrenda> prendas;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelPrendas(
            ISistemaFacade sistemaFacade,
            IPrendaValidator validador
    ) {

        super(
                "prendas",
                "Prendas",
                "Consulta y administra el inventario de vestidos, trajes y disfraces.",
                IconosUI.prendas()
        );

        Objects.requireNonNull(
                sistemaFacade,
                "La fachada del sistema es obligatoria."
        );

        this.gestorPrendas = Objects.requireNonNull(
                sistemaFacade.getGestorPrendas(),
                "El gestor de prendas es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de prendas es obligatorio."
        );

        this.prendas = new ArrayList<>();

        this.panelTabla = new PanelTabla(
                "Buscar por referencia, tipo, color, marca o talla..."
        );

        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario("Editar");
        this.botonActualizar = new BotonSecundario("Actualizar");

        inicializarComponentes();
        configurarEventos();
        cargarPrendas();
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        // Acción principal del encabezado.
        configurarAccion(
                "Nueva prenda",
                e -> abrirFormularioRegistro()
        );

        // Configurar tabla.
        panelTabla.setModelo(modeloTabla);

        panelTabla.getTablaDatos()
                .setOrdenamientoHabilitado(true);

        // Botón editar.
        botonEditar.configurarIcono(IconosUI.editar());
        botonEditar.setEnabled(false);

        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );

        // Botón actualizar.
        botonActualizar.configurarIcono(IconosUI.actualizar());

        botonActualizar.addActionListener(
                e -> cargarPrendas()
        );

        // Agregar acciones secundarias al panel común.
        panelTabla.agregarAccion(botonEditar);
        panelTabla.agregarAccion(botonActualizar);

        agregarContenido(panelTabla);
    }

    // ==========================================
    // MODELO DE TABLA
    // ==========================================

    private DefaultTableModel crearModeloTabla() {

        return new DefaultTableModel(
                new Object[]{
                        "Referencia",
                        "Tipo",
                        "Color",
                        "Marca",
                        "Talla",
                        "Valor alquiler",
                        "Estado"
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
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        // Búsqueda dinámica.
        panelTabla.getCampoBusqueda().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrarPrendas();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrarPrendas();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrarPrendas();
                    }
                }
        );

        // Habilitar edición cuando exista selección.
        panelTabla.getTablaDatos()
                .getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        botonEditar.setEnabled(
                                panelTabla.getTablaDatos()
                                        .getTabla()
                                        .getSelectedRow() != -1
                        );
                    }
                });

        // Doble clic para editar.
        panelTabla.getTablaDatos()
                .getTabla()
                .addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            abrirFormularioEdicion();
                        }
                    }
                });
    }

    // ==========================================
    // FORMULARIO DE REGISTRO
    // ==========================================

    private void abrirFormularioRegistro() {

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioPrendas formulario = new FormularioPrendas(
                propietario,
                gestorPrendas,
                validador,
                this::cargarPrendas
        );

        formulario.mostrar();
    }

    // ==========================================
    // FORMULARIO DE EDICIÓN
    // ==========================================

    private void abrirFormularioEdicion() {

        IPrenda prenda = obtenerPrendaSeleccionada();

        if (prenda == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una prenda para editar.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Window propietario =
                SwingUtilities.getWindowAncestor(this);

        FormularioPrendas formulario = new FormularioPrendas(
                propietario,
                gestorPrendas,
                validador,
                prenda,
                this::cargarPrendas
        );

        formulario.mostrar();
    }

    // ==========================================
    // PRENDA SELECCIONADA
    // ==========================================

    private IPrenda obtenerPrendaSeleccionada() {

        int filaModelo = panelTabla.getTablaDatos()
                .getFilaSeleccionada();

        if (filaModelo == -1) {
            return null;
        }

        String referencia = String.valueOf(
                modeloTabla.getValueAt(filaModelo, 0)
        );

        return prendas.stream()
                .filter(Objects::nonNull)
                .filter(prenda ->
                        referencia.equals(prenda.getReferencia())
                )
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // CONSULTA DE PRENDAS
    // ==========================================

    private void cargarPrendas() {

        try {

            List<IPrenda> resultado =
                    gestorPrendas.listarPrendas();

            if (resultado == null) {

                throw new IllegalStateException(
                        "El gestor devolvió una lista de prendas nula."
                );
            }

            prendas = new ArrayList<>(resultado);

            // Mantener el texto de búsqueda actual.
            filtrarPrendas();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar las prendas.\n"
                            + obtenerMensajeError(ex)
            );
        }
    }

    // ==========================================
    // FILTRAR PRENDAS
    // ==========================================

    private void filtrarPrendas() {

        String texto = panelTabla.getTextoBusqueda()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {

            actualizarTabla(prendas);
            return;
        }

        List<IPrenda> resultados = prendas.stream()
                .filter(Objects::nonNull)
                .filter(prenda ->
                        contiene(prenda.getReferencia(), texto)
                                || contiene(obtenerTipo(prenda), texto)
                                || contiene(prenda.getColor(), texto)
                                || contiene(prenda.getMarca(), texto)
                                || contiene(prenda.getTalla(), texto)
                                || contiene(
                                String.valueOf(prenda.getEstado()),
                                texto
                        )
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
    // IDENTIFICAR TIPO DE PRENDA
    // ==========================================

    private String obtenerTipo(IPrenda prenda) {

        if (prenda instanceof IVestidoDama) {
            return "Vestido";
        }

        if (prenda instanceof ITrajeCaballero) {
            return "Traje";
        }

        if (prenda instanceof IDisfraz) {
            return "Disfraz";
        }

        return "Desconocido";
    }

    // ==========================================
    // ACTUALIZAR TABLA
    // ==========================================

    private void actualizarTabla(List<IPrenda> listaPrendas) {

        modeloTabla.setRowCount(0);

        if (listaPrendas != null) {

            for (IPrenda prenda : listaPrendas) {

                if (prenda == null) {
                    continue;
                }

                modeloTabla.addRow(new Object[]{
                        prenda.getReferencia(),
                        obtenerTipo(prenda),
                        prenda.getColor(),
                        prenda.getMarca(),
                        prenda.getTalla(),
                        prenda.getValorAlquiler(),
                        prenda.getEstado()
                });
            }
        }

        // Evitar conservar una selección de datos anteriores.
        panelTabla.getTablaDatos().limpiarSeleccion();

        botonEditar.setEnabled(false);
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Gestión de prendas",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String obtenerMensajeError(RuntimeException ex) {

        return ex.getMessage() == null
                ? "Ocurrió un error inesperado."
                : ex.getMessage();
    }
}
