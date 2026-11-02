
package com.lavanderia.vista.paneles.prendas;

import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.IVestidoDama;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;
import com.lavanderia.modelo.interfaces.IDisfraz;
import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.prenda.IPrendaValidator;
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

/** Presenta el módulo de prendas y sus acciones en la interfaz Swing. */
public class PanelPrendas extends PanelModulo {

    private final IGestorPrendas gestorPrendas;
    private final IPrendaValidator validador;

    private final CampoBusqueda campoBusqueda;
    private final TablaDatos tablaPrendas;
    private final DefaultTableModel modeloTabla;

    private final BotonSecundario botonEditar;

    private List<IPrenda> prendas;

    public PanelPrendas(
            ISistemaFacade sistemaFacade,
            IPrendaValidator validador) {

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

        this.campoBusqueda = new CampoBusqueda(
                "Buscar por referencia, tipo, color, marca o talla..."
        );

        this.tablaPrendas = new TablaDatos();
        this.modeloTabla = crearModeloTabla();

        this.botonEditar = new BotonSecundario("Editar prenda");

        inicializarComponentes();
        configurarEventos();
        cargarPrendas();
    }

    // ==========================================
    // INICIALIZACIÓN DE COMPONENTES
    // ==========================================

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_MD)
        );

        panelPrincipal.setOpaque(false);

        // Barra de herramientas.
        JPanel panelHerramientas = new JPanel(
                new BorderLayout()
        );

        panelHerramientas.setOpaque(false);

        JLabel etiquetaResultados = crearEtiqueta(
                "Prendas registradas",
                FUENTE_SECCION,
                TEXTO
        );

        // Botón registrar.
        BotonPrincipal botonNuevo =
                new BotonPrincipal("Nueva prenda");

        botonNuevo.setIcon(IconosUI.agregar());

        botonNuevo.addActionListener(
                e -> abrirFormularioRegistro()
        );

        // Botón editar.
        botonEditar.setIcon(IconosUI.editar());
        botonEditar.addActionListener(
                e -> abrirFormularioEdicion()
        );
        botonEditar.setEnabled(false);

        // Botón actualizar.
        BotonSecundario botonActualizar =
                new BotonSecundario("Actualizar");

        botonActualizar.setIcon(IconosUI.actualizar());

        botonActualizar.addActionListener(
                e -> cargarPrendas()
        );

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

        // Tabla.
        tablaPrendas.setModelo(modeloTabla);
        tablaPrendas.setOrdenamientoHabilitado(true);

        panelPrincipal.add(
                panelHerramientas,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                tablaPrendas,
                BorderLayout.CENTER
        );

        agregarContenido(panelPrincipal);
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
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // ==========================================
    // APERTURA DE FORMULARIOS
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

    private void abrirFormularioEdicion() {

        IPrenda prenda = obtenerPrendaSeleccionada();

        if (prenda == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una prenda para editar.",
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
    // OBTENER PRENDA SELECCIONADA
    // ==========================================

    private IPrenda obtenerPrendaSeleccionada() {

        int filaVista = tablaPrendas.getFilaSeleccionada();

        if (filaVista == -1) {
            return null;
        }

        // Convertir índice visual al índice del modelo.
        int filaModelo = tablaPrendas.getTabla()
                .convertRowIndexToModel(filaVista);

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
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        campoBusqueda.addDocumentListener(new DocumentListener() {

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
        });

        // Habilitar edición al seleccionar una fila.
        tablaPrendas.getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        botonEditar.setEnabled(
                                tablaPrendas.getFilaSeleccionada() != -1
                        );
                    }
                });

        // Doble clic para editar.
        tablaPrendas.getTabla().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            abrirFormularioEdicion();
                        }
                    }
                }
        );
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

            // Conserva el filtro de búsqueda actual.
            filtrarPrendas();

        } catch (RuntimeException ex) {

            mostrarError(
                    "No fue posible cargar las prendas.\n"
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // FILTRO DE PRENDAS
    // ==========================================

    private void filtrarPrendas() {

        String texto = campoBusqueda.getText()
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
            String texto) {

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
    // ACTUALIZACIÓN DE LA TABLA
    // ==========================================

    private void actualizarTabla(List<IPrenda> listaPrendas) {

        modeloTabla.setRowCount(0);

        if (listaPrendas == null) {
            botonEditar.setEnabled(false);
            return;
        }

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

        // Evitar mantener una selección sobre datos anteriores.
        tablaPrendas.getTabla().clearSelection();
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
