
package com.lavanderia.vista;

import com.lavanderia.vista.componentes.BotonNavegacion;
import com.lavanderia.vista.interfaces.IModuloVista;
import com.lavanderia.vista.paneles.PanelInicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Ventana principal de la aplicación Los Atuendos.
 *
 * Gestiona:
 * - Menú lateral de navegación.
 * - Barra superior.
 * - Navegación entre módulos mediante CardLayout.
 * - Actualización automática de los módulos que requieren
 *   refrescar su información al volver a mostrarse.
 */
public class MainFrame extends JFrame {

    // ==========================================
    // COMPONENTES PRINCIPALES
    // ==========================================

    private final JPanel panelContenido;
    private final JPanel menuLateral;
    private final JPanel barraSuperior;

    private final CardLayout cardLayout;

    private final JLabel lblTitulo;
    private final JLabel lblModulo;

    // ==========================================
    // NAVEGACIÓN
    // ==========================================

    private final Map<String, BotonNavegacion> botones;
    private final Map<String, IModuloVista> modulos;

    private String moduloActual;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public MainFrame(
            List<IModuloVista> modulos,
            String moduloInicial) {

        super("Los Atuendos | Sistema de Gestión");

        this.modulos = new LinkedHashMap<>();
        this.botones = new LinkedHashMap<>();

        registrarModulos(modulos);

        if (!this.modulos.containsKey(moduloInicial)) {
            throw new IllegalArgumentException(
                    "El módulo inicial no está registrado: "
                            + moduloInicial
            );
        }

        configurarVentana();

        lblTitulo = new JLabel();
        lblModulo = new JLabel();

        cardLayout = new CardLayout();

        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(FONDO);

        menuLateral = crearMenuLateral();
        barraSuperior = crearBarraSuperior();

        registrarVistas();

        construirLayout();

        // Mostrar la pantalla inicial.
        mostrarModulo(moduloInicial);
    }

    // ==========================================
    // REGISTRO DE MÓDULOS
    // ==========================================

    private void registrarModulos(
            List<IModuloVista> listaModulos) {

        if (listaModulos == null || listaModulos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe registrarse al menos un módulo."
            );
        }

        for (IModuloVista modulo : listaModulos) {

            validarModulo(modulo);

            if (this.modulos.containsKey(modulo.getId())) {
                throw new IllegalArgumentException(
                        "ID de módulo duplicado: "
                                + modulo.getId()
                );
            }

            this.modulos.put(
                    modulo.getId(),
                    modulo
            );
        }
    }

    private void validarModulo(IModuloVista modulo) {

        if (modulo == null
                || modulo.getId() == null
                || modulo.getId().isBlank()) {

            throw new IllegalArgumentException(
                    "El módulo contiene un identificador inválido."
            );
        }

        if (modulo.getVista() == null) {
            throw new IllegalArgumentException(
                    "El módulo no proporciona una vista válida: "
                            + modulo.getId()
            );
        }
    }

    /**
     * Permite incorporar módulos después de construir la ventana.
     */
    public void agregarModulo(IModuloVista modulo) {

        validarModulo(modulo);

        if (modulos.containsKey(modulo.getId())) {
            throw new IllegalArgumentException(
                    "El módulo ya está registrado: "
                            + modulo.getId()
            );
        }

        modulos.put(
                modulo.getId(),
                modulo
        );

        panelContenido.add(
                modulo.getVista(),
                modulo.getId()
        );

        reconstruirMenuLateral();

        menuLateral.revalidate();
        menuLateral.repaint();

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==========================================
    // CONFIGURACIÓN DE VENTANA
    // ==========================================

    private void configurarVentana() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(
                ANCHO_VENTANA,
                ALTO_VENTANA
        );

        setMinimumSize(
                new Dimension(
                        ANCHO_MINIMO,
                        ALTO_MINIMO
                )
        );

        setLocationRelativeTo(null);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL LAYOUT
    // ==========================================

    private void construirLayout() {

        JPanel contenedor = new JPanel(
                new BorderLayout()
        );

        contenedor.setBackground(FONDO);

        JPanel areaPrincipal = new JPanel(
                new BorderLayout()
        );

        areaPrincipal.setBackground(FONDO);

        areaPrincipal.add(
                barraSuperior,
                BorderLayout.NORTH
        );

        areaPrincipal.add(
                panelContenido,
                BorderLayout.CENTER
        );

        contenedor.add(
                menuLateral,
                BorderLayout.WEST
        );

        contenedor.add(
                areaPrincipal,
                BorderLayout.CENTER
        );

        setContentPane(contenedor);
    }

    // ==========================================
    // MENÚ LATERAL
    // ==========================================

    private JPanel crearMenuLateral() {

        JPanel sidebar = new JPanel(
                new BorderLayout()
        );

        sidebar.setPreferredSize(
                new Dimension(
                        ANCHO_MENU_LATERAL,
                        0
                )
        );

        sidebar.setBackground(SUPERFICIE);

        sidebar.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1, BORDE
                )
        );

        sidebar.add(
                crearEncabezadoMenu(),
                BorderLayout.NORTH
        );

        sidebar.add(
                crearOpcionesMenu(),
                BorderLayout.CENTER
        );

        sidebar.add(
                crearPieMenu(),
                BorderLayout.SOUTH
        );

        return sidebar;
    }

    // ==========================================
    // ENCABEZADO DEL MENÚ
    // ==========================================

    private JPanel crearEncabezadoMenu() {

        JPanel encabezado = new JPanel();

        encabezado.setBackground(SUPERFICIE);

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        encabezado.setBorder(
                new EmptyBorder(
                        28, 22, 28, 22
                )
        );

        JLabel nombre = crearEtiqueta(
                "LOS ATUENDOS",
                new Font(
                        FUENTE_PRINCIPAL,
                        Font.BOLD,
                        20
                ),
                PRIMARIO
        );

        JLabel descripcion = crearEtiqueta(
                "Gestión de lavandería y alquiler",
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        encabezado.add(nombre);

        encabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        encabezado.add(descripcion);

        return encabezado;
    }

    // ==========================================
    // OPCIONES DE NAVEGACIÓN
    // ==========================================

    private JPanel crearOpcionesMenu() {

        JPanel opciones = new JPanel();

        opciones.setBackground(SUPERFICIE);

        opciones.setLayout(
                new BoxLayout(
                        opciones,
                        BoxLayout.Y_AXIS
                )
        );

        opciones.setBorder(
                new EmptyBorder(
                        ESPACIADO_SM,
                        12,
                        ESPACIADO_SM,
                        12
                )
        );

        JLabel lblGeneral = crearEtiqueta(
                "GENERAL",
                FUENTE_ETIQUETA,
                TEXTO_SECUNDARIO
        );

        lblGeneral.setBorder(
                new EmptyBorder(
                        ESPACIADO_SM,
                        10,
                        5,
                        0
                )
        );

        opciones.add(lblGeneral);

        opciones.add(
                Box.createVerticalStrut(ESPACIADO_SM)
        );

        for (IModuloVista modulo : modulos.values()) {

            BotonNavegacion boton = new BotonNavegacion(
                    modulo.getIcono(),
                    modulo.getTitulo()
            );

            boton.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            ALTO_BOTON
                    )
            );

            boton.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            boton.addActionListener(
                    e -> mostrarModulo(modulo.getId())
            );

            botones.put(
                    modulo.getId(),
                    boton
            );

            opciones.add(boton);

            opciones.add(
                    Box.createVerticalStrut(5)
            );
        }

        return opciones;
    }

    /**
     * Reconstruye el menú lateral conservando el módulo activo.
     */
    private void reconstruirMenuLateral() {

        JPanel nuevasOpciones = crearOpcionesMenu();

        menuLateral.remove(1);

        menuLateral.add(
                nuevasOpciones,
                BorderLayout.CENTER
        );

        actualizarEstadoBotones();
    }

    // ==========================================
    // PIE DEL MENÚ
    // ==========================================

    private JPanel crearPieMenu() {

        JPanel pie = new JPanel(
                new BorderLayout()
        );

        pie.setBackground(SUPERFICIE);

        pie.setBorder(
                new EmptyBorder(
                        15, 20, 20, 20
                )
        );

        JLabel version = crearEtiqueta(
                "Los Atuendos · v1.0.0",
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        pie.add(
                version,
                BorderLayout.WEST
        );

        return pie;
    }

    // ==========================================
    // BARRA SUPERIOR
    // ==========================================

    private JPanel crearBarraSuperior() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(SUPERFICIE);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDE
                        ),
                        new EmptyBorder(
                                18,
                                ESPACIADO_LG,
                                18,
                                ESPACIADO_LG
                        )
                )
        );

        header.add(
                crearInformacionModulo(),
                BorderLayout.WEST
        );

        header.add(
                crearPerfilUsuario(),
                BorderLayout.EAST
        );

        return header;
    }

    // ==========================================
    // INFORMACIÓN DEL MÓDULO
    // ==========================================

    private JPanel crearInformacionModulo() {

        JPanel informacion = new JPanel();

        informacion.setOpaque(false);

        informacion.setLayout(
                new BoxLayout(
                        informacion,
                        BoxLayout.Y_AXIS
                )
        );

        lblTitulo.setFont(FUENTE_SUBTITULO);
        lblTitulo.setForeground(TEXTO);

        lblModulo.setFont(FUENTE_PEQUENA);
        lblModulo.setForeground(TEXTO_SECUNDARIO);

        informacion.add(lblTitulo);

        informacion.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        informacion.add(lblModulo);

        return informacion;
    }

    // ==========================================
    // PERFIL DEL USUARIO
    // ==========================================

    private JPanel crearPerfilUsuario() {

        JPanel perfil = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                )
        );

        perfil.setOpaque(false);

        JLabel avatar = new JLabel("AD");

        avatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        avatar.setOpaque(true);

        avatar.setBackground(PRIMARIO_CLARO);
        avatar.setForeground(PRIMARIO);
        avatar.setFont(FUENTE_ETIQUETA);

        avatar.setPreferredSize(
                new Dimension(
                        TAMANO_AVATAR,
                        TAMANO_AVATAR
                )
        );

        JPanel usuario = new JPanel();

        usuario.setOpaque(false);

        usuario.setLayout(
                new BoxLayout(
                        usuario,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel nombre = crearEtiqueta(
                "Administrador",
                FUENTE_ETIQUETA,
                TEXTO
        );

        JLabel rol = crearEtiqueta(
                "Gestión del sistema",
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        usuario.add(nombre);
        usuario.add(rol);

        perfil.add(avatar);
        perfil.add(usuario);

        return perfil;
    }

    // ==========================================
    // REGISTRO DE VISTAS
    // ==========================================

    private void registrarVistas() {

        modulos.forEach((id, modulo) -> {

            panelContenido.add(
                    modulo.getVista(),
                    id
            );
        });
    }

    // ==========================================
    // NAVEGACIÓN ENTRE MÓDULOS
    // ==========================================

    /**
     * Cambia la pantalla activa.
     *
     * Si el usuario regresa al módulo de Inicio desde otra pantalla,
     * se actualizan nuevamente los indicadores del negocio.
     *
     * @param id Identificador del módulo que se desea mostrar.
     */
    public void mostrarModulo(String id) {

        IModuloVista modulo = modulos.get(id);

        if (modulo == null) {
            return;
        }

        // Guardar la pantalla anterior antes de cambiar.
        String moduloAnterior = moduloActual;

        // Mostrar la vista solicitada.
        cardLayout.show(
                panelContenido,
                id
        );

        // Actualizar el encabezado.
        lblTitulo.setText(
                modulo.getTitulo()
        );

        lblModulo.setText(
                "Inicio  /  " + modulo.getTitulo()
        );

        // Actualizar el módulo activo.
        moduloActual = id;

        // Actualizar el estado visual del menú.
        actualizarEstadoBotones();

        // ------------------------------------------
        // ACTUALIZACIÓN AUTOMÁTICA DEL INICIO
        // ------------------------------------------

        /*
         * Solo actualizamos cuando se regresa a Inicio
         * desde otro módulo.
         *
         * No repetimos la consulta cuando el usuario
         * ya se encuentra en Inicio.
         *
         * Tampoco duplicamos la consulta inicial,
         * porque PanelInicio ya la realiza en su constructor.
         */
        if ("inicio".equals(id)
                && moduloAnterior != null
                && !moduloAnterior.equals("inicio")
                && modulo instanceof PanelInicio panelInicio) {

            panelInicio.actualizarIndicadores();
        }

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==========================================
    // ESTADO DE LOS BOTONES
    // ==========================================

    private void actualizarEstadoBotones() {

        botones.forEach((clave, boton) ->
                boton.setActivo(
                        clave.equals(moduloActual)
                )
        );
    }

    // ==========================================
    // CONSULTA DEL MÓDULO ACTUAL
    // ==========================================

    public String getModuloActual() {
        return moduloActual;
    }

    // ==========================================
    // UTILIDADES
    // ==========================================

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
}
