package com.lavanderia.vista;

import com.lavanderia.vista.componentes.BotonNavegacion;
import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Ventana principal (JFrame) de la aplicación de escritorio "Los Atuendos".
 * Implementa la arquitectura visual tipo dashboard moderno:
 * <ul>
 *   <li>Menú lateral izquierdo con marca corporativa, opciones de navegación y pie con versión.</li>
 *   <li>Barra superior con título del módulo activo, ruta tipo migas de pan (breadcrumb) y perfil de usuario.</li>
 *   <li>Área central dinámica gestionada mediante {@link CardLayout} para la alternancia entre módulos ({@link IModuloVista}).</li>
 * </ul>
 */
public class MainFrame extends JFrame {

    // ==========================================
    // COMPONENTES PRINCIPALES
    // ==========================================

    /** Panel central que aloja las vistas de los módulos intercambiables con CardLayout. */
    private final JPanel panelContenido;

    /** Panel lateral contenedor del menú de navegación. */
    private final JPanel menuLateral;

    /** Barra superior con título de pantalla activa y perfil. */
    private final JPanel barraSuperior;

    /** Administrador de diseño por tarjetas para alternar las pantallas. */
    private final CardLayout cardLayout;

    /** Etiqueta que despliega el título principal del módulo en uso. */
    private final JLabel lblTitulo;

    /** Etiqueta secundaria que muestra la ruta de navegación (ej. "Inicio / Clientes"). */
    private final JLabel lblModulo;

    // ==========================================
    // NAVEGACIÓN
    // ==========================================

    /** Mapa asociativo de botones del menú lateral indexados por el ID de cada módulo. */
    private final Map<String, BotonNavegacion> botones;

    /** Mapa de módulos registrados indexados por su identificador único. */
    private final Map<String, IModuloVista> modulos;

    /** Identificador del módulo actualmente visible en pantalla. */
    private String moduloActual;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    /**
     * Construye e inicializa la ventana principal con la lista de módulos y la pantalla inicial.
     *
     * @param modulos Lista de módulos a registrar ({@link IModuloVista}).
     * @param moduloInicial Identificador del módulo que debe mostrarse al arrancar.
     * @throws IllegalArgumentException Si la lista de módulos es nula o vacía, o si el módulo inicial no existe.
     */
    public MainFrame(
            List<IModuloVista> modulos,
            String moduloInicial) {

        super("Los Atuendos | Sistema de Gestión");

        this.modulos = new LinkedHashMap<>();
        this.botones = new LinkedHashMap<>();

        // Registrar y validar los módulos proporcionados
        registrarModulos(modulos);

        // Validar que el módulo inicial exista en la colección registrada
        if (!this.modulos.containsKey(moduloInicial)) {
            throw new IllegalArgumentException(
                    "El módulo inicial no está registrado: "
                            + moduloInicial
            );
        }

        // Configurar propiedades de tamaño y centrado del JFrame
        configurarVentana();

        this.lblTitulo = new JLabel();
        this.lblModulo = new JLabel();

        // Configurar panel de contenido con CardLayout
        this.cardLayout = new CardLayout();
        this.panelContenido = new JPanel(cardLayout);
        this.panelContenido.setBackground(FONDO);

        // Construir secciones estructurales de la interfaz
        this.menuLateral = crearMenuLateral();
        this.barraSuperior = crearBarraSuperior();

        // Registrar los componentes visuales de cada módulo en el CardLayout
        registrarVistas();

        // Ensamblar la distribución global de la ventana
        construirLayout();

        // Desplegar el módulo inicial configurado
        mostrarModulo(moduloInicial);
    }

    // ==========================================
    // REGISTRO DE MÓDULOS
    // ==========================================

    /**
     * Valida y almacena la lista de módulos en el mapa interno asegurando unicidad de IDs.
     *
     * @param listaModulos Lista de módulos a examinar.
     */
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
                        "ID de módulo duplicado: " + modulo.getId()
                );
            }

            this.modulos.put(modulo.getId(), modulo);
        }
    }

    /**
     * Valida que un módulo no sea nulo, contenga un ID válido y provea una vista gráfica no nula.
     *
     * @param modulo Instancia de módulo a validar.
     */
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
     * Permite incorporar un módulo dinámicamente después de haber construido la ventana principal.
     * Actualiza el menú lateral y repinta el contenedor.
     *
     * @param modulo Nuevo módulo a registrar.
     */
    public void agregarModulo(IModuloVista modulo) {

        validarModulo(modulo);

        if (modulos.containsKey(modulo.getId())) {
            throw new IllegalArgumentException(
                    "El módulo ya está registrado: "
                            + modulo.getId()
            );
        }

        modulos.put(modulo.getId(), modulo);

        // Añadir la vista al CardLayout
        panelContenido.add(
                modulo.getVista(),
                modulo.getId()
        );

        // Reconstruir los botones del menú lateral para incluir la nueva opción
        reconstruirMenuLateral();

        menuLateral.revalidate();
        menuLateral.repaint();

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==========================================
    // CONFIGURACIÓN DE VENTANA
    // ==========================================

    /**
     * Configura el comportamiento de cierre, dimensiones por defecto y mínimas, y centrado en pantalla.
     */
    private void configurarVentana() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(ANCHO_VENTANA, ALTO_VENTANA);

        setMinimumSize(new Dimension(
                ANCHO_MINIMO,
                ALTO_MINIMO
        ));

        setLocationRelativeTo(null);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL LAYOUT
    // ==========================================

    /**
     * Ensambla el contenedor principal con el menú lateral al oeste y el área de trabajo al centro.
     */
    private void construirLayout() {

        JPanel contenedor = new JPanel(
                new BorderLayout()
        );

        contenedor.setBackground(FONDO);

        JPanel areaPrincipal = new JPanel(
                new BorderLayout()
        );

        areaPrincipal.setBackground(FONDO);

        // Barra superior de encabezado
        areaPrincipal.add(
                barraSuperior,
                BorderLayout.NORTH
        );

        // Panel de contenido central dinámico
        areaPrincipal.add(
                panelContenido,
                BorderLayout.CENTER
        );

        // Menú de navegación lateral
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

    /**
     * Construye el panel lateral (sidebar) con ancho fijo, borde divisorio y estructura norte/centro/sur.
     *
     * @return Panel lateral ensamblado.
     */
    private JPanel crearMenuLateral() {

        JPanel sidebar = new JPanel(
                new BorderLayout()
        );

        sidebar.setPreferredSize(new Dimension(
                ANCHO_MENU_LATERAL,
                0
        ));

        sidebar.setBackground(SUPERFICIE);

        // Borde derecho de división sutil
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

    /**
     * Crea el membrete corporativo superior del menú lateral (Logotipo y subtítulo del sistema).
     *
     * @return Panel de encabezado del menú lateral.
     */
    private JPanel crearEncabezadoMenu() {

        JPanel encabezado = new JPanel();

        encabezado.setBackground(SUPERFICIE);

        encabezado.setLayout(new BoxLayout(
                encabezado,
                BoxLayout.Y_AXIS
        ));

        encabezado.setBorder(new EmptyBorder(
                28, 22, 28, 22
        ));

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

    /**
     * Crea la botonera vertical con las opciones de cada módulo registrado.
     *
     * @return Panel con los botones de navegación.
     */
    private JPanel crearOpcionesMenu() {

        JPanel opciones = new JPanel();

        opciones.setBackground(SUPERFICIE);

        opciones.setLayout(new BoxLayout(
                opciones,
                BoxLayout.Y_AXIS
        ));

        opciones.setBorder(new EmptyBorder(
                ESPACIADO_SM,
                12,
                ESPACIADO_SM,
                12
        ));

        JLabel lblGeneral = crearEtiqueta(
                "GENERAL",
                FUENTE_ETIQUETA,
                TEXTO_SECUNDARIO
        );

        lblGeneral.setBorder(new EmptyBorder(
                ESPACIADO_SM,
                10,
                5,
                0
        ));

        opciones.add(lblGeneral);

        opciones.add(
                Box.createVerticalStrut(ESPACIADO_SM)
        );

        // Iterar los módulos y crear su botón interactivo
        for (IModuloVista modulo : modulos.values()) {

            BotonNavegacion boton = new BotonNavegacion(
                    modulo.getIcono(),
                    modulo.getTitulo()
            );

            boton.setMaximumSize(new Dimension(
                    Integer.MAX_VALUE,
                    ALTO_BOTON
            ));

            boton.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Al presionar el botón, conmutar hacia la pantalla del módulo
            boton.addActionListener(e ->
                    mostrarModulo(modulo.getId())
            );

            botones.put(modulo.getId(), boton);

            opciones.add(boton);

            opciones.add(
                    Box.createVerticalStrut(5)
            );
        }

        return opciones;
    }

    /**
     * Reconstruye las opciones del menú conservando el módulo que se encuentra seleccionado.
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

    /**
     * Crea el pie inferior del menú lateral con la versión del software.
     *
     * @return Panel de pie de página.
     */
    private JPanel crearPieMenu() {

        JPanel pie = new JPanel(
                new BorderLayout()
        );

        pie.setBackground(SUPERFICIE);

        pie.setBorder(new EmptyBorder(
                15, 20, 20, 20
        ));

        JLabel version = crearEtiqueta(
                "Los Atuendos · v1.0.0",
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        pie.add(version, BorderLayout.WEST);

        return pie;
    }

    // ==========================================
    // BARRA SUPERIOR
    // ==========================================

    /**
     * Construye la barra horizontal superior con información del módulo y perfil de usuario.
     *
     * @return Panel de barra superior.
     */
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

    /**
     * Crea el bloque textual con el título y la ruta de navegación (breadcrumb) en la cabecera.
     *
     * @return Panel informativo.
     */
    private JPanel crearInformacionModulo() {

        JPanel informacion = new JPanel();

        informacion.setOpaque(false);

        informacion.setLayout(new BoxLayout(
                informacion,
                BoxLayout.Y_AXIS
        ));

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

    /**
     * Construye el widget con el avatar y rol del usuario en la esquina superior derecha.
     *
     * @return Panel de perfil.
     */
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

        avatar.setPreferredSize(new Dimension(
                TAMANO_AVATAR,
                TAMANO_AVATAR
        ));

        JPanel usuario = new JPanel();

        usuario.setOpaque(false);

        usuario.setLayout(new BoxLayout(
                usuario,
                BoxLayout.Y_AXIS
        ));

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

    /**
     * Agrega las vistas de todos los módulos registrados al CardLayout del panel central.
     */
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
     * Conmuta la vista activa hacia el módulo indicado por su identificador.
     *
     * @param id Clave identificadora del módulo a desplegar.
     */
    public void mostrarModulo(String id) {

        IModuloVista modulo = modulos.get(id);

        if (modulo == null) {
            return;
        }

        // Mostrar la tarjeta correspondiente en CardLayout
        cardLayout.show(panelContenido, id);

        // Actualizar textos informativos de la cabecera
        lblTitulo.setText(modulo.getTitulo());

        lblModulo.setText(
                "Inicio  /  " + modulo.getTitulo()
        );

        moduloActual = id;

        // Actualizar el estado visual activo en los botones del menú
        actualizarEstadoBotones();
    }

    /**
     * Actualiza el resaltado visual de los botones del menú lateral marcando solo el activo.
     */
    private void actualizarEstadoBotones() {

        botones.forEach((clave, boton) ->
                boton.setActivo(clave.equals(moduloActual))
        );
    }

    // ==========================================
    // CONSULTA DE MÓDULO ACTUAL
    // ==========================================

    /**
     * Obtiene el identificador del módulo actualmente seleccionado.
     *
     * @return Clave del módulo activo.
     */
    public String getModuloActual() {
        return moduloActual;
    }
}
