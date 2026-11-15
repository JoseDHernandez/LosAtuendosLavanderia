
package com.lavanderia.vista;

import com.lavanderia.vista.componentes.BotonNavegacion;
import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Ventana principal de la aplicación Lavandería - Los Atuendos.
 *
 * Responsabilidades:
 * - Administrar la estructura general de la aplicación.
 * - Gestionar el menú lateral de navegación.
 * - Presentar la información del módulo activo.
 * - Administrar el cambio de vistas mediante CardLayout.
 * - Adaptar la distribución visual al tamaño disponible de la ventana.
 *
 * La clase trabaja mediante el contrato IModuloVista,
 * evitando dependencias directas con las implementaciones
 * de los diferentes módulos.
 *
 * Las operaciones de interfaz deben ejecutarse en el EDT
 * (Event Dispatch Thread) de Swing.
 */
public class MainFrame extends JFrame {

    // =====================================================
    // 1. COMPONENTES PRINCIPALES
    // =====================================================

    /** Contenedor central de los módulos. */
    private final JPanel panelContenido;

    /** Administrador de vistas mediante tarjetas. */
    private final CardLayout cardLayout;

    /** Contenedor principal de toda la ventana. */
    private final JPanel contenedorPrincipal;

    /** Panel lateral de navegación. */
    private final JPanel menuLateral;

    /** Barra superior de información. */
    private final JPanel barraSuperior;

    /** Panel que contiene las opciones de navegación. */
    private final JPanel panelOpcionesMenu;

    /** Contenedor desplazable de las opciones del menú. */
    private final JScrollPane scrollOpcionesMenu;

    /** Etiqueta del título del módulo actual. */
    private final JLabel lblTitulo;

    /** Etiqueta de navegación secundaria. */
    private final JLabel lblModulo;

    /** Panel de información del módulo. */
    private final JPanel panelInformacionModulo;

    /** Panel del perfil del usuario. */
    private final JPanel panelPerfilUsuario;

    /** Nombre del usuario. */
    private final JLabel lblNombreUsuario;

    /** Descripción del perfil. */
    private final JLabel lblRolUsuario;

    /** Avatar del usuario. */
    private final JLabel lblAvatar;

    // =====================================================
    // 2. NAVEGACIÓN Y MÓDULOS
    // =====================================================

    /** Botones asociados a los módulos registrados. */
    private final Map<String, BotonNavegacion> botones;

    /** Módulos registrados en la aplicación. */
    private final Map<String, IModuloVista> modulos;

    /** Identificador del módulo actualmente visible. */
    private String moduloActual;

    // =====================================================
    // 3. DIMENSIONES ADAPTABLES
    // =====================================================

    /** Ancho preferido del menú lateral. */
    private int anchoMenuActual;

    /** Ancho mínimo del menú lateral. */
    private static final int ANCHO_MENU_MINIMO = 210;

    /** Ancho máximo del menú lateral. */
    private static final int ANCHO_MENU_MAXIMO = 270;

    /** Porcentaje aproximado de la ventana destinado al menú. */
    private static final double PROPORCION_MENU = 0.19;

    /** Ancho de referencia para calcular la distribución. */
    private static final int ANCHO_REFERENCIA = 1280;

    /** Separación horizontal de la ventana en modo compacto. */
    private static final int MARGEN_COMPACTO = 16;

    /** Separación horizontal de la ventana en modo amplio. */
    private static final int MARGEN_AMPLIO = 24;

    /** Indica si la ventana está utilizando una distribución compacta. */
    private boolean modoCompacto;

    // =====================================================
    // 4. CONSTRUCTOR
    // =====================================================

    /**
     * Construye la ventana principal.
     *
     * @param listaModulos Módulos que se registrarán.
     * @param moduloInicial Identificador del módulo inicial.
     */
    public MainFrame(
            List<IModuloVista> listaModulos,
            String moduloInicial) {

        super("Los Atuendos | Sistema de Gestión");

        this.modulos = new LinkedHashMap<>();
        this.botones = new LinkedHashMap<>();

        // Registrar y validar los módulos.
        registrarModulos(listaModulos);

        // Comprobar que exista el módulo inicial.
        if (moduloInicial == null
                || !this.modulos.containsKey(moduloInicial)) {

            throw new IllegalArgumentException(
                    "El módulo inicial no está registrado: "
                            + moduloInicial
            );
        }

        // Configurar propiedades generales de la ventana.
        configurarVentana();

        // Inicializar etiquetas.
        lblTitulo = new JLabel();
        lblModulo = new JLabel();

        // Inicializar información del usuario.
        lblNombreUsuario = new JLabel("Administrador");
        lblRolUsuario = new JLabel("Gestión del sistema");
        lblAvatar = new JLabel("AD");

        // Inicializar contenedor central.
        cardLayout = new CardLayout();

        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(FONDO);
        panelContenido.setOpaque(true);

        // Inicializar panel de navegación.
        panelOpcionesMenu = crearOpcionesMenu();

        scrollOpcionesMenu = crearScrollMenu();

        // Construir menú lateral.
        menuLateral = crearMenuLateral();

        // Construir barra superior.
        panelInformacionModulo = crearInformacionModulo();
        panelPerfilUsuario = crearPerfilUsuario();

        barraSuperior = crearBarraSuperior();

        // Inicializar contenedor principal.
        contenedorPrincipal = new JPanel(new BorderLayout());

        contenedorPrincipal.setBackground(FONDO);
        contenedorPrincipal.setOpaque(true);

        // Registrar vistas.
        registrarVistas();

        // Construir distribución.
        construirLayout();

        // Configurar adaptación a cambios de tamaño.
        configurarAdaptabilidad();

        // Mostrar módulo inicial.
        mostrarModulo(moduloInicial);

        // Centrar la ventana.
        setLocationRelativeTo(null);
    }

    // =====================================================
    // 5. REGISTRO Y VALIDACIÓN DE MÓDULOS
    // =====================================================

    /**
     * Registra los módulos recibidos mediante sus interfaces.
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

            if (modulos.containsKey(modulo.getId())) {

                throw new IllegalArgumentException(
                        "ID de módulo duplicado: "
                                + modulo.getId()
                );
            }

            modulos.put(modulo.getId(), modulo);
        }
    }

    /**
     * Comprueba la validez de un módulo.
     */
    private void validarModulo(IModuloVista modulo) {

        if (modulo == null) {

            throw new IllegalArgumentException(
                    "No se puede registrar un módulo nulo."
            );
        }

        if (modulo.getId() == null
                || modulo.getId().isBlank()) {

            throw new IllegalArgumentException(
                    "El módulo contiene un identificador inválido."
            );
        }

        if (modulo.getTitulo() == null
                || modulo.getTitulo().isBlank()) {

            throw new IllegalArgumentException(
                    "El módulo debe proporcionar un título válido."
            );
        }

        if (modulo.getVista() == null) {

            throw new IllegalArgumentException(
                    "El módulo no proporciona una vista válida: "
                            + modulo.getId()
            );
        }
    }

    // =====================================================
    // 6. REGISTRO DINÁMICO DE MÓDULOS
    // =====================================================

    /**
     * Incorpora un módulo después de construir la ventana.
     *
     * La operación debe invocarse desde el EDT.
     *
     * @param modulo Nuevo módulo.
     */
    public void agregarModulo(IModuloVista modulo) {

        if (!SwingUtilities.isEventDispatchThread()) {

            throw new IllegalStateException(
                    "agregarModulo debe ejecutarse en el EDT."
            );
        }

        validarModulo(modulo);

        if (modulos.containsKey(modulo.getId())) {

            throw new IllegalArgumentException(
                    "El módulo ya está registrado: "
                            + modulo.getId()
            );
        }

        modulos.put(modulo.getId(), modulo);

        // Agregar la vista al contenedor CardLayout.
        panelContenido.add(
                modulo.getVista(),
                modulo.getId()
        );

        // Crear el botón de navegación.
        agregarBotonNavegacion(modulo);

        // Actualizar distribución visual.
        actualizarDistribucion();

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // =====================================================
    // 7. CONFIGURACIÓN DE LA VENTANA
    // =====================================================

    /**
     * Configura dimensiones iniciales y mínimas según
     * el espacio disponible en la pantalla.
     *
     * Evita establecer dimensiones mínimas superiores
     * a la resolución disponible.
     */
    private void configurarVentana() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(
                ANCHO_MINIMO_ADAPTADO(),
                ALTO_MINIMO_ADAPTADO()
        ));

        Rectangle pantalla =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();

        int anchoDisponible = pantalla.width;
        int altoDisponible = pantalla.height;

        int anchoInicial = Math.min(
                ANCHO_VENTANA,
                anchoDisponible
        );

        int altoInicial = Math.min(
                ALTO_VENTANA,
                altoDisponible
        );

        setSize(
                Math.max(getMinimumSize().width, anchoInicial),
                Math.max(getMinimumSize().height, altoInicial)
        );

        setLocationRelativeTo(null);
    }

    /**
     * Calcula un ancho mínimo compatible con la pantalla.
     */
    private int ANCHO_MINIMO_ADAPTADO() {

        Rectangle pantalla =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();

        return Math.min(ANCHO_MINIMO, pantalla.width);
    }

    /**
     * Calcula un alto mínimo compatible con la pantalla.
     */
    private int ALTO_MINIMO_ADAPTADO() {

        Rectangle pantalla =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();

        return Math.min(ALTO_MINIMO, pantalla.height);
    }

    // =====================================================
    // 8. CONSTRUCCIÓN DEL LAYOUT GENERAL
    // =====================================================

    /**
     * Ensambla el menú lateral y el área principal.
     */
    private void construirLayout() {

        JPanel areaPrincipal = new JPanel(
                new BorderLayout()
        );

        areaPrincipal.setBackground(FONDO);
        areaPrincipal.setOpaque(true);

        // Encabezado superior.
        areaPrincipal.add(
                barraSuperior,
                BorderLayout.NORTH
        );

        // Contenido dinámico.
        areaPrincipal.add(
                panelContenido,
                BorderLayout.CENTER
        );

        // Menú lateral.
        contenedorPrincipal.add(
                menuLateral,
                BorderLayout.WEST
        );

        // Área de trabajo.
        contenedorPrincipal.add(
                areaPrincipal,
                BorderLayout.CENTER
        );

        setContentPane(contenedorPrincipal);
    }

    // =====================================================
    // 9. MENÚ LATERAL
    // =====================================================

    /**
     * Construye el menú lateral con una distribución
     * vertical flexible.
     */
    private JPanel crearMenuLateral() {

        JPanel sidebar = new JPanel(
                new BorderLayout()
        );

        sidebar.setBackground(SUPERFICIE);
        sidebar.setOpaque(true);

        // Ancho inicial adaptable.
        anchoMenuActual = calcularAnchoMenu();

        sidebar.setPreferredSize(
                new Dimension(anchoMenuActual, 0)
        );

        // Borde divisorio.
        sidebar.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1, BORDE
                )
        );

        // Encabezado corporativo.
        sidebar.add(
                crearEncabezadoMenu(),
                BorderLayout.NORTH
        );

        // Opciones desplazables.
        sidebar.add(
                scrollOpcionesMenu,
                BorderLayout.CENTER
        );

        // Pie del menú.
        sidebar.add(
                crearPieMenu(),
                BorderLayout.SOUTH
        );

        return sidebar;
    }

    // =====================================================
    // 10. ENCABEZADO DEL MENÚ
    // =====================================================

    /**
     * Construye la identidad corporativa.
     */
    private JPanel crearEncabezadoMenu() {

        JPanel encabezado = new JPanel();

        encabezado.setBackground(SUPERFICIE);
        encabezado.setOpaque(true);

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        encabezado.setBorder(
                new EmptyBorder(
                        26,
                        ESPACIADO_LG,
                        24,
                        ESPACIADO_LG
                )
        );

        JLabel nombre = crearEtiqueta(
                "LOS ATUENDOS",
                FUENTE_LOGO,
                PRIMARIO
        );

        JLabel descripcion = crearEtiqueta(
                "Gestión de lavandería y alquiler",
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(nombre);

        encabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        encabezado.add(descripcion);

        return encabezado;
    }

    // =====================================================
    // 11. OPCIONES DE NAVEGACIÓN
    // =====================================================

    /**
     * Crea el contenedor vertical de opciones.
     */
    private JPanel crearOpcionesMenu() {

        JPanel opciones = new JPanel();

        opciones.setBackground(SUPERFICIE);
        opciones.setOpaque(true);

        opciones.setLayout(
                new BoxLayout(
                        opciones,
                        BoxLayout.Y_AXIS
                )
        );

        opciones.setBorder(
                new EmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM + ESPACIADO_XS,
                        ESPACIADO_SM,
                        ESPACIADO_SM + ESPACIADO_XS
                )
        );

        JLabel lblGeneral = crearEtiqueta(
                "GENERAL",
                FUENTE_ETIQUETA,
                TEXTO_SECUNDARIO
        );

        lblGeneral.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblGeneral.setBorder(
                new EmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_XS,
                        0
                )
        );

        opciones.add(lblGeneral);

        opciones.add(
                Box.createVerticalStrut(ESPACIADO_SM)
        );

        // Registrar botones iniciales.
        for (IModuloVista modulo : modulos.values()) {

            agregarBotonAContenedor(
                    opciones,
                    modulo
            );
        }

        return opciones;
    }

    /**
     * Construye un botón de navegación y lo agrega
     * al contenedor indicado.
     */
    private void agregarBotonAContenedor(
            JPanel contenedor,
            IModuloVista modulo) {

        BotonNavegacion boton = new BotonNavegacion(
                modulo.getIcono(),
                modulo.getTitulo()
        );

        configurarBotonNavegacion(
                boton,
                modulo
        );

        botones.put(
                modulo.getId(),
                boton
        );

        contenedor.add(boton);

        contenedor.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );
    }

    /**
     * Configura el comportamiento y las propiedades
     * del botón de navegación.
     */
    private void configurarBotonNavegacion(
            BotonNavegacion boton,
            IModuloVista modulo) {

        boton.setAlignmentX(Component.LEFT_ALIGNMENT);

        boton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_BOTON
                )
        );

        boton.setMinimumSize(
                new Dimension(
                        0,
                        ALTO_BOTON
                )
        );

        // El texto completo puede consultarse mediante tooltip.
        boton.setToolTipText(modulo.getTitulo());

        boton.getAccessibleContext().setAccessibleName(
                modulo.getTitulo()
        );

        boton.addActionListener(e ->
                mostrarModulo(modulo.getId())
        );
    }

    /**
     * Agrega un nuevo botón al menú existente.
     */
    private void agregarBotonNavegacion(
            IModuloVista modulo) {

        agregarBotonAContenedor(
                panelOpcionesMenu,
                modulo
        );

        panelOpcionesMenu.revalidate();
        panelOpcionesMenu.repaint();
    }

    // =====================================================
    // 12. SCROLL DEL MENÚ
    // =====================================================

    /**
     * Construye el contenedor desplazable de opciones.
     */
    private JScrollPane crearScrollMenu() {

        JScrollPane scroll = new JScrollPane(
                panelOpcionesMenu
        );

        scroll.setBorder(null);

        scroll.setBackground(SUPERFICIE);

        scroll.getViewport().setBackground(SUPERFICIE);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        scroll.setViewportBorder(null);

        return scroll;
    }

    // =====================================================
    // 13. RECONSTRUCCIÓN DEL MENÚ
    // =====================================================

    /**
     * Reconstruye las opciones sin reemplazar el contenedor
     * desplazable ni alterar el estado de navegación.
     */
    private void reconstruirMenuLateral() {

        panelOpcionesMenu.removeAll();

        botones.clear();

        JLabel lblGeneral = crearEtiqueta(
                "GENERAL",
                FUENTE_ETIQUETA,
                TEXTO_SECUNDARIO
        );

        lblGeneral.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblGeneral.setBorder(
                new EmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_SM,
                        ESPACIADO_XS,
                        0
                )
        );

        panelOpcionesMenu.add(lblGeneral);

        panelOpcionesMenu.add(
                Box.createVerticalStrut(ESPACIADO_SM)
        );

        for (IModuloVista modulo : modulos.values()) {

            agregarBotonAContenedor(
                    panelOpcionesMenu,
                    modulo
            );
        }

        actualizarEstadoBotones();

        panelOpcionesMenu.revalidate();
        panelOpcionesMenu.repaint();
    }

    // =====================================================
    // 14. PIE DEL MENÚ
    // =====================================================

    /**
     * Construye el pie del menú lateral.
     */
    private JPanel crearPieMenu() {

        JPanel pie = new JPanel(
                new BorderLayout()
        );

        pie.setBackground(SUPERFICIE);
        pie.setOpaque(true);

        pie.setBorder(
                new EmptyBorder(
                        ESPACIADO_MD,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
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

    // =====================================================
    // 15. BARRA SUPERIOR
    // =====================================================

    /**
     * Construye el encabezado principal.
     */
    private JPanel crearBarraSuperior() {

        JPanel header = new JPanel(
                new BorderLayout(
                        ESPACIADO_MD,
                        0
                )
        );

        header.setBackground(SUPERFICIE);
        header.setOpaque(true);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDE
                        ),
                        new EmptyBorder(
                                ESPACIADO_MD,
                                ESPACIADO_LG,
                                ESPACIADO_MD,
                                ESPACIADO_LG
                        )
                )
        );

        header.add(
                panelInformacionModulo,
                BorderLayout.CENTER
        );

        header.add(
                panelPerfilUsuario,
                BorderLayout.EAST
        );

        return header;
    }

    // =====================================================
    // 16. INFORMACIÓN DEL MÓDULO
    // =====================================================

    /**
     * Construye el bloque de título y breadcrumb.
     */
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

        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblModulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(lblTitulo);

        informacion.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );

        informacion.add(lblModulo);

        return informacion;
    }

    // =====================================================
    // 17. PERFIL DEL USUARIO
    // =====================================================

    /**
     * Construye el perfil de usuario de la barra superior.
     */
    private JPanel crearPerfilUsuario() {

        JPanel perfil = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        perfil.setOpaque(false);

        // Avatar.
        lblAvatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblAvatar.setVerticalAlignment(
                SwingConstants.CENTER
        );

        lblAvatar.setOpaque(true);

        lblAvatar.setBackground(PRIMARIO_CLARO);
        lblAvatar.setForeground(PRIMARIO);
        lblAvatar.setFont(FUENTE_ETIQUETA);

        lblAvatar.setPreferredSize(
                new Dimension(
                        TAMANO_AVATAR,
                        TAMANO_AVATAR
                )
        );

        // Información del usuario.
        JPanel usuario = new JPanel();

        usuario.setOpaque(false);

        usuario.setLayout(
                new BoxLayout(
                        usuario,
                        BoxLayout.Y_AXIS
                )
        );

        lblNombreUsuario.setFont(FUENTE_ETIQUETA);
        lblNombreUsuario.setForeground(TEXTO);

        lblRolUsuario.setFont(FUENTE_PEQUENA);
        lblRolUsuario.setForeground(TEXTO_SECUNDARIO);

        usuario.add(lblNombreUsuario);
        usuario.add(lblRolUsuario);

        perfil.add(lblAvatar);
        perfil.add(usuario);

        return perfil;
    }

    // =====================================================
    // 18. REGISTRO DE VISTAS
    // =====================================================

    /**
     * Agrega todas las vistas al CardLayout.
     */
    private void registrarVistas() {

        modulos.forEach((id, modulo) -> {

            panelContenido.add(
                    modulo.getVista(),
                    id
            );
        });
    }

    // =====================================================
    // 19. NAVEGACIÓN ENTRE MÓDULOS
    // =====================================================

    /**
     * Muestra el módulo solicitado.
     *
     * @param id Identificador del módulo.
     */
    public void mostrarModulo(String id) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(() ->
                    mostrarModulo(id)
            );

            return;
        }

        IModuloVista modulo = modulos.get(id);

        if (modulo == null) {
            return;
        }

        // Mostrar la tarjeta correspondiente.
        cardLayout.show(
                panelContenido,
                id
        );

        // Actualizar título.
        lblTitulo.setText(
                modulo.getTitulo()
        );

        // Actualizar ruta de navegación.
        lblModulo.setText(
                "Inicio  /  " + modulo.getTitulo()
        );

        // Actualizar módulo actual.
        moduloActual = id;

        // Actualizar resaltado de navegación.
        actualizarEstadoBotones();

        barraSuperior.revalidate();
        barraSuperior.repaint();

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // =====================================================
    // 20. ESTADO DE LOS BOTONES
    // =====================================================

    /**
     * Actualiza el estado visual de los botones.
     */
    private void actualizarEstadoBotones() {

        botones.forEach((clave, boton) -> {

            boton.setActivo(
                    clave.equals(moduloActual)
            );
        });
    }

    // =====================================================
    // 21. ADAPTABILIDAD DE LA VENTANA
    // =====================================================

    /**
     * Registra el listener encargado de adaptar la distribución
     * cuando cambia el tamaño de la ventana.
     */
    private void configurarAdaptabilidad() {

        addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent evento) {

                        actualizarDistribucion();
                    }
                }
        );

        // Aplicar distribución inicial.
        actualizarDistribucion();
    }

    /**
     * Calcula y aplica las dimensiones de los elementos
     * principales según el ancho disponible.
     */
    private void actualizarDistribucion() {

        if (getWidth() <= 0) {
            return;
        }

        int anchoVentana = getWidth();

        // Calcular el ancho del menú.
        int nuevoAnchoMenu = calcularAnchoMenu();

        // Actualizar menú solo cuando exista un cambio.
        if (nuevoAnchoMenu != anchoMenuActual) {

            anchoMenuActual = nuevoAnchoMenu;

            menuLateral.setPreferredSize(
                    new Dimension(
                            anchoMenuActual,
                            0
                    )
            );
        }

        // Determinar si se utiliza distribución compacta.
        boolean nuevoModoCompacto =
                anchoVentana < ANCHO_REFERENCIA;

        if (nuevoModoCompacto != modoCompacto) {

            modoCompacto = nuevoModoCompacto;

            actualizarEspaciadoEncabezado();
        }

        // Actualizar el tamaño máximo de los botones.
        Dimension dimensionBoton = new Dimension(
                Integer.MAX_VALUE,
                ALTO_BOTON
        );

        for (BotonNavegacion boton : botones.values()) {

            boton.setMaximumSize(dimensionBoton);
        }

        menuLateral.revalidate();
        menuLateral.repaint();

        barraSuperior.revalidate();
        barraSuperior.repaint();
    }

    /**
     * Calcula dinámicamente el ancho del menú lateral.
     *
     * Mantiene un rango mínimo y máximo para evitar
     * que el menú ocupe demasiado espacio.
     */
    private int calcularAnchoMenu() {

        int anchoVentana = getWidth();

        if (anchoVentana <= 0) {
            return ANCHO_MENU_LATERAL;
        }

        int anchoCalculado = (int) (
                anchoVentana * PROPORCION_MENU
        );

        return Math.max(
                ANCHO_MENU_MINIMO,
                Math.min(
                        anchoCalculado,
                        ANCHO_MENU_MAXIMO
                )
        );
    }

    /**
     * Ajusta los márgenes de la barra superior según
     * el espacio horizontal disponible.
     */
    private void actualizarEspaciadoEncabezado() {

        int margen = modoCompacto
                ? MARGEN_COMPACTO
                : MARGEN_AMPLIO;

        barraSuperior.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDE
                        ),
                        new EmptyBorder(
                                ESPACIADO_MD,
                                margen,
                                ESPACIADO_MD,
                                margen
                        )
                )
        );

        // En ventanas estrechas se oculta la descripción
        // secundaria del perfil, conservando el avatar
        // y el nombre del usuario.
        lblRolUsuario.setVisible(!modoCompacto);

        barraSuperior.revalidate();
        barraSuperior.repaint();
    }

    // =====================================================
    // 22. CONFIGURACIÓN DEL PERFIL
    // =====================================================

    /**
     * Permite actualizar la información visual del usuario.
     *
     * @param nombre Nombre del usuario.
     * @param rol Descripción o cargo.
     * @param iniciales Iniciales del avatar.
     */
    public void configurarPerfilUsuario(
            String nombre,
            String rol,
            String iniciales) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(() ->
                    configurarPerfilUsuario(
                            nombre,
                            rol,
                            iniciales
                    )
            );

            return;
        }

        lblNombreUsuario.setText(
                nombre == null || nombre.isBlank()
                        ? "Usuario"
                        : nombre
        );

        lblRolUsuario.setText(
                rol == null ? "" : rol
        );

        lblAvatar.setText(
                iniciales == null || iniciales.isBlank()
                        ? "US"
                        : iniciales
        );

        panelPerfilUsuario.revalidate();
        panelPerfilUsuario.repaint();
    }

    // =====================================================
    // 23. CONSULTA DEL MÓDULO ACTUAL
    // =====================================================

    /**
     * Obtiene el identificador del módulo actualmente visible.
     *
     * @return Identificador del módulo.
     */
    public String getModuloActual() {
        return moduloActual;
    }

    // =====================================================
    // 24. CONSULTA DE MÓDULOS
    // =====================================================

    /**
     * Comprueba si un módulo está registrado.
     *
     * @param id Identificador del módulo.
     * @return true si existe.
     */
    public boolean contieneModulo(String id) {
        return modulos.containsKey(id);
    }

    /**
     * Obtiene la cantidad de módulos registrados.
     *
     * @return Número de módulos.
     */
    public int getCantidadModulos() {
        return modulos.size();
    }
}
