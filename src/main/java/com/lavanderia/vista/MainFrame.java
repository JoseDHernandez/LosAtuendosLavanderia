
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

        this.lblTitulo = new JLabel();
        this.lblModulo = new JLabel();

        this.cardLayout = new CardLayout();
        this.panelContenido = new JPanel(cardLayout);

        this.panelContenido.setBackground(FONDO);

        this.menuLateral = crearMenuLateral();
        this.barraSuperior = crearBarraSuperior();

        registrarVistas();
        construirLayout();

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
                        "ID de módulo duplicado: " + modulo.getId()
                );
            }

            this.modulos.put(modulo.getId(), modulo);
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
     * Permite incorporar un módulo después de construir
     * la ventana principal.
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

        sidebar.setPreferredSize(new Dimension(
                ANCHO_MENU_LATERAL,
                0
        ));

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
     * Reconstruye las opciones del menú conservando
     * el módulo que se encuentra seleccionado.
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

    public void mostrarModulo(String id) {

        IModuloVista modulo = modulos.get(id);

        if (modulo == null) {
            return;
        }

        cardLayout.show(panelContenido, id);

        lblTitulo.setText(modulo.getTitulo());

        lblModulo.setText(
                "Inicio  /  " + modulo.getTitulo()
        );

        moduloActual = id;

        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {

        botones.forEach((clave, boton) ->
                boton.setActivo(clave.equals(moduloActual))
        );
    }

    // ==========================================
    // CONSULTA DE MÓDULO ACTUAL
    // ==========================================

    public String getModuloActual() {
        return moduloActual;
    }
}
