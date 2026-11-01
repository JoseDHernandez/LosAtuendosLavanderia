package com.lavanderia.vista;

import com.lavanderia.vista.interfaces.IModuloVista;
import com.lavanderia.vista.componentes.BotonNavegacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class MainFrame extends JFrame {

    // Paleta visual
    private static final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private static final Color COLOR_BLANCO =
            Color.WHITE;

    private static final Color COLOR_PRIMARIO =
            new Color(37, 99, 235);

    private static final Color COLOR_TEXTO =
            new Color(31, 41, 55);

    private static final Color COLOR_SECUNDARIO =
            new Color(107, 114, 128);

    private static final Color COLOR_BORDE =
            new Color(229, 231, 235);

    // Componentes principales
    private final JPanel panelContenido;
    private final JPanel menuLateral;
    private final JPanel barraSuperior;

    private final CardLayout cardLayout;

    private final JLabel lblTitulo;
    private final JLabel lblModulo;

    // Navegación
    private final Map<String, BotonNavegacion> botones;
    private final Map<String, IModuloVista> modulos;

    private String moduloActual;

    public MainFrame(
            java.util.List<IModuloVista> modulos,
            String moduloInicial
    ) {

        super("Los Atuendos | Sistema de Gestión");

        this.modulos = new LinkedHashMap<>();
        this.botones = new LinkedHashMap<>();

        // Registrar módulos
        for (IModuloVista modulo : modulos) {

            if (this.modulos.containsKey(modulo.getId())) {
                throw new IllegalArgumentException(
                        "ID de módulo duplicado: " + modulo.getId()
                );
            }

            this.modulos.put(modulo.getId(), modulo);
        }

        if (!this.modulos.containsKey(moduloInicial)) {
            throw new IllegalArgumentException(
                    "El módulo inicial no está registrado."
            );
        }

        // Configuración de ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Layout principal
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(COLOR_FONDO);

        // Navegación lateral
        menuLateral = crearMenuLateral();

        // Área de contenido
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(COLOR_FONDO);

        // Barra superior
        lblTitulo = new JLabel();
        lblModulo = new JLabel();

        barraSuperior = crearBarraSuperior();

        // Incorporar módulos
        this.modulos.forEach((id, modulo) -> {
            panelContenido.add(
                    modulo.getVista(),
                    id
            );
        });

        // Estructura general
        JPanel areaPrincipal = new JPanel(new BorderLayout());
        areaPrincipal.setBackground(COLOR_FONDO);

        areaPrincipal.add(barraSuperior, BorderLayout.NORTH);
        areaPrincipal.add(panelContenido, BorderLayout.CENTER);

        contenedor.add(menuLateral, BorderLayout.WEST);
        contenedor.add(areaPrincipal, BorderLayout.CENTER);

        setContentPane(contenedor);

        // Abrir pantalla inicial
        mostrarModulo(moduloInicial);
    }

    // ==========================================
    // MENÚ LATERAL
    // ==========================================

    private JPanel crearMenuLateral() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(COLOR_BLANCO);
        sidebar.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1, COLOR_BORDE
                )
        );

        // Encabezado del menú
        JPanel encabezado = new JPanel();
        encabezado.setBackground(COLOR_BLANCO);
        encabezado.setLayout(new BoxLayout(
                encabezado, BoxLayout.Y_AXIS
        ));

        encabezado.setBorder(new EmptyBorder(
                28, 22, 28, 22
        ));

        JLabel nombre = new JLabel("LOS ATUENDOS");
        nombre.setFont(new Font(
                "Segoe UI", Font.BOLD, 20
        ));
        nombre.setForeground(COLOR_PRIMARIO);

        JLabel descripcion = new JLabel(
                "Gestión de lavandería y alquiler"
        );
        descripcion.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11
        ));
        descripcion.setForeground(COLOR_SECUNDARIO);

        encabezado.add(nombre);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(descripcion);

        // Opciones de navegación
        JPanel opciones = new JPanel();
        opciones.setBackground(COLOR_BLANCO);

        opciones.setLayout(new BoxLayout(
                opciones, BoxLayout.Y_AXIS
        ));

        opciones.setBorder(new EmptyBorder(
                8, 12, 8, 12
        ));

        JLabel lblGeneral = crearEtiquetaSeccion("GENERAL");
        opciones.add(lblGeneral);
        opciones.add(Box.createVerticalStrut(10));

        // Crear botones según los módulos registrados
        for (IModuloVista modulo : modulos.values()) {

            BotonNavegacion boton = new BotonNavegacion(
                    modulo.getIcono(),
                    modulo.getTitulo()
            );

            boton.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 46)
            );

            boton.setAlignmentX(Component.LEFT_ALIGNMENT);

            boton.addActionListener(e ->
                    mostrarModulo(modulo.getId())
            );

            botones.put(modulo.getId(), boton);

            opciones.add(boton);
            opciones.add(Box.createVerticalStrut(5));
        }

        // Pie del menú
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(COLOR_BLANCO);
        pie.setBorder(new EmptyBorder(15, 20, 20, 20));

        JLabel version = new JLabel("Los Atuendos · v1.0.0");
        version.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11
        ));
        version.setForeground(COLOR_SECUNDARIO);

        pie.add(version, BorderLayout.WEST);

        sidebar.add(encabezado, BorderLayout.NORTH);
        sidebar.add(opciones, BorderLayout.CENTER);
        sidebar.add(pie, BorderLayout.SOUTH);

        return sidebar;
    }

    // ==========================================
    // BARRA SUPERIOR
    // ==========================================

    private JPanel crearBarraSuperior() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(COLOR_BLANCO);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, COLOR_BORDE
                ),
                new EmptyBorder(18, 28, 18, 28)
        ));

        // Títulos
        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(new BoxLayout(
                informacion, BoxLayout.Y_AXIS
        ));

        lblTitulo.setFont(new Font(
                "Segoe UI", Font.BOLD, 19
        ));
        lblTitulo.setForeground(COLOR_TEXTO);

        lblModulo.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12
        ));
        lblModulo.setForeground(COLOR_SECUNDARIO);

        informacion.add(lblTitulo);
        informacion.add(Box.createVerticalStrut(4));
        informacion.add(lblModulo);

        // Perfil
        JPanel perfil = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 0
        ));

        perfil.setOpaque(false);

        JLabel avatar = new JLabel("AD");
        avatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        avatar.setOpaque(true);
        avatar.setBackground(new Color(224, 237, 252));
        avatar.setForeground(COLOR_PRIMARIO);
        avatar.setFont(new Font(
                "Segoe UI", Font.BOLD, 12
        ));

        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel usuario = new JPanel();
        usuario.setOpaque(false);
        usuario.setLayout(new BoxLayout(
                usuario, BoxLayout.Y_AXIS
        ));

        JLabel nombre = new JLabel("Administrador");
        nombre.setFont(new Font(
                "Segoe UI", Font.BOLD, 12
        ));
        nombre.setForeground(COLOR_TEXTO);

        JLabel rol = new JLabel("Gestión del sistema");
        rol.setFont(new Font(
                "Segoe UI", Font.PLAIN, 10
        ));
        rol.setForeground(COLOR_SECUNDARIO);

        usuario.add(nombre);
        usuario.add(rol);

        perfil.add(avatar);
        perfil.add(usuario);

        header.add(informacion, BorderLayout.WEST);
        header.add(perfil, BorderLayout.EAST);

        return header;
    }

    // ==========================================
    // NAVEGACIÓN
    // ==========================================

    public void mostrarModulo(String id) {

        IModuloVista modulo = modulos.get(id);

        if (modulo == null) {
            return;
        }

        cardLayout.show(panelContenido, id);

        // Actualizar encabezado
        lblTitulo.setText(modulo.getTitulo());

        lblModulo.setText(
                "Inicio  /  " + modulo.getTitulo()
        );

        // Actualizar botones
        botones.forEach((clave, boton) ->
                boton.setActivo(clave.equals(id))
        );

        moduloActual = id;
    }

    public String getModuloActual() {
        return moduloActual;
    }

    // ==========================================
    // COMPONENTES AUXILIARES
    // ==========================================

    private JLabel crearEtiquetaSeccion(String texto) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(new Font(
                "Segoe UI", Font.BOLD, 10
        ));

        etiqueta.setForeground(COLOR_SECUNDARIO);
        etiqueta.setBorder(new EmptyBorder(
                8, 10, 5, 0
        ));

        return etiqueta;
    }
}