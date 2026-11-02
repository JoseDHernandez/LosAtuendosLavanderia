
package com.lavanderia.vista.paneles;

import com.lavanderia.modelo.enums.EstadoAlquiler;
import com.lavanderia.modelo.enums.EstadoSolicitud;

import com.lavanderia.patrones.facade.ISistemaFacade;

import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelInicio extends JPanel implements IModuloVista {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ISistemaFacade sistemaFacade;

    private JLabel lblTotalPrendas;
    private JLabel lblAlquileresActivos;
    private JLabel lblTotalClientes;
    private JLabel lblPendientesLavanderia;

    // ==========================================
    // CONSTANTES VISUALES
    // ==========================================

    private static final Color COLOR_ENCABEZADO =
            new Color(32, 57, 72);

    private static final Color COLOR_BORDE =
            new Color(225, 231, 235);

    private static final Color COLOR_AZUL =
            new Color(43, 103, 145);

    private static final Color COLOR_VERDE =
            new Color(67, 119, 66);

    private static final Color COLOR_MORADO =
            new Color(119, 83, 132);

    private static final Color COLOR_NARANJA =
            new Color(157, 105, 43);

    private static final Font FUENTE_INDICADOR =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 36);

    private static final Font FUENTE_TARJETA =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 13);

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelInicio(ISistemaFacade sistemaFacade) {

        this.sistemaFacade = sistemaFacade;

        configurarPanel();
        inicializarComponentes();
        actualizarIndicadores();
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout());
        setBackground(FONDO);
    }

    // ==========================================
    // COMPONENTES PRINCIPALES
    // ==========================================

    private void inicializarComponentes() {

        JPanel contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );

        contenido.setBackground(FONDO);

        contenido.setBorder(
                new EmptyBorder(25, 28, 28, 28)
        );

        // Encabezado
        contenido.add(crearEncabezado());

        contenido.add(
                Box.createVerticalStrut(25)
        );

        // Título de indicadores
        contenido.add(crearTituloSeccion(
                "Resumen general",
                "Indicadores principales del negocio"
        ));

        contenido.add(
                Box.createVerticalStrut(14)
        );

        // Tarjetas
        contenido.add(crearPanelIndicadores());

        contenido.add(
                Box.createVerticalStrut(25)
        );

        // Información inferior
        contenido.add(crearPanelInformativo());

        JScrollPane scroll = new JScrollPane(contenido);

        scroll.setBorder(null);
        scroll.setBackground(FONDO);

        scroll.getViewport().setBackground(FONDO);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel encabezado = new JPanel(
                new BorderLayout()
        );

        encabezado.setBackground(COLOR_ENCABEZADO);

        encabezado.setBorder(
                new EmptyBorder(28, 30, 28, 30)
        );

        JPanel textos = new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel etiqueta = new JLabel(
                "PANEL DE ADMINISTRACIÓN"
        );

        etiqueta.setFont(
                new Font(FUENTE_PRINCIPAL, Font.BOLD, 11)
        );

        etiqueta.setForeground(
                new Color(184, 207, 219)
        );

        JLabel bienvenida = new JLabel(
                "Bienvenido a Los Atuendos"
        );

        bienvenida.setFont(
                new Font(FUENTE_PRINCIPAL, Font.BOLD, 25)
        );

        bienvenida.setForeground(Color.WHITE);

        JLabel descripcion = new JLabel(
                "Gestiona el inventario, los alquileres y la lavandería."
        );

        descripcion.setFont(FUENTE_NORMAL);

        descripcion.setForeground(
                new Color(216, 228, 235)
        );

        textos.add(etiqueta);

        textos.add(
                Box.createVerticalStrut(8)
        );

        textos.add(bienvenida);

        textos.add(
                Box.createVerticalStrut(7)
        );

        textos.add(descripcion);

        // Fecha actual
        JLabel fecha = new JLabel(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd 'de' MMMM 'de' yyyy",
                                new Locale("es", "CO")
                        )
                )
        );

        fecha.setForeground(
                new Color(216, 228, 235)
        );

        fecha.setFont(FUENTE_PEQUENA);

        fecha.setHorizontalAlignment(SwingConstants.RIGHT);

        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(fecha, BorderLayout.EAST);

        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 145)
        );

        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);

        return encabezado;
    }

    // ==========================================
    // TÍTULO DE SECCIÓN
    // ==========================================

    private JPanel crearTituloSeccion(
            String titulo,
            String descripcion
    ) {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JLabel lblTitulo = crearEtiqueta(
                titulo,
                FUENTE_SECCION,
                TEXTO
        );

        JLabel lblDescripcion = crearEtiqueta(
                descripcion,
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        panel.add(lblTitulo);

        panel.add(
                Box.createVerticalStrut(4)
        );

        panel.add(lblDescripcion);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    // ==========================================
    // PANEL DE INDICADORES
    // ==========================================

    private JPanel crearPanelIndicadores() {

        JPanel indicadores = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );

        indicadores.setOpaque(false);

        indicadores.add(crearTarjeta(
                "Prendas registradas",
                "0",
                "Inventario general",
                COLOR_AZUL,
                1
        ));

        indicadores.add(crearTarjeta(
                "Alquileres activos",
                "0",
                "Servicios en curso",
                COLOR_VERDE,
                2
        ));

        indicadores.add(crearTarjeta(
                "Clientes registrados",
                "0",
                "Personas registradas",
                COLOR_MORADO,
                3
        ));

        indicadores.add(crearTarjeta(
                "Pendientes de lavandería",
                "0",
                "Solicitudes por atender",
                COLOR_NARANJA,
                4
        ));

        indicadores.setPreferredSize(
                new Dimension(1000, 165)
        );

        indicadores.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 165)
        );

        indicadores.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return indicadores;
    }

    // ==========================================
    // TARJETAS
    // ==========================================

    private JPanel crearTarjeta(
            String titulo,
            String valor,
            String descripcion,
            Color colorIndicador,
            int indicador
    ) {

        JPanel tarjeta = new JPanel(
                new BorderLayout(0, 12)
        );

        tarjeta.setBackground(SUPERFICIE);

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_BORDE, 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        // Título
        JLabel lblTitulo = new JLabel(titulo);

        lblTitulo.setFont(FUENTE_TARJETA);

        lblTitulo.setForeground(TEXTO_SECUNDARIO);

        // Valor
        JLabel lblValor = new JLabel(valor);

        lblValor.setFont(FUENTE_INDICADOR);

        lblValor.setForeground(colorIndicador);

        // Descripción
        JLabel lblDescripcion = new JLabel(descripcion);

        lblDescripcion.setFont(
                new Font(FUENTE_PRINCIPAL, Font.PLAIN, 11)
        );

        lblDescripcion.setForeground(
                TEXTO_SECUNDARIO
        );

        // Contenido central
        JPanel centro = new JPanel();

        centro.setOpaque(false);

        centro.setLayout(
                new BoxLayout(centro, BoxLayout.Y_AXIS)
        );

        centro.add(lblValor);

        centro.add(
                Box.createVerticalStrut(5)
        );

        centro.add(lblDescripcion);

        tarjeta.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        tarjeta.add(
                centro,
                BorderLayout.CENTER
        );

        // Guardar referencias de indicadores
        switch (indicador) {

            case 1 ->
                    lblTotalPrendas = lblValor;

            case 2 ->
                    lblAlquileresActivos = lblValor;

            case 3 ->
                    lblTotalClientes = lblValor;

            case 4 ->
                    lblPendientesLavanderia = lblValor;
        }

        return tarjeta;
    }

    // ==========================================
    // PANEL INFORMATIVO
    // ==========================================

    private JPanel crearPanelInformativo() {

        JPanel informacion = new JPanel(
                new BorderLayout(0, 14)
        );

        informacion.setBackground(SUPERFICIE);

        informacion.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_BORDE, 1, true),
                        new EmptyBorder(20, 22, 20, 22)
                )
        );

        JLabel titulo = crearEtiqueta(
                "Actividad del negocio",
                FUENTE_SECCION,
                TEXTO
        );

        JLabel mensaje = new JLabel(
                "<html>Desde el menú lateral puedes acceder a los "
                        + "diferentes módulos del sistema.<br><br>"
                        + "Administra tus clientes y empleados, controla "
                        + "el inventario de prendas, registra alquileres "
                        + "y supervisa las solicitudes de lavandería.</html>"
        );

        mensaje.setFont(FUENTE_NORMAL);

        mensaje.setForeground(TEXTO_SECUNDARIO);

        informacion.add(
                titulo,
                BorderLayout.NORTH
        );

        informacion.add(
                mensaje,
                BorderLayout.CENTER
        );

        informacion.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 150)
        );

        informacion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return informacion;
    }

    // ==========================================
    // CARGAR INDICADORES
    // ==========================================

    public void actualizarIndicadores() {

        try {

            // Total de prendas
            int totalPrendas = sistemaFacade
                    .getGestorPrendas()
                    .listarPrendas()
                    .size();

            // Total de clientes
            int totalClientes = sistemaFacade
                    .getGestorPersonas()
                    .listarClientes()
                    .size();

            // Alquileres activos
            long alquileresActivos = sistemaFacade
                    .getGestorAlquiler()
                    .listarTodos()
                    .stream()
                    .filter(alquiler ->
                            alquiler.getEstado()
                                    == EstadoAlquiler.ACTIVO
                    )
                    .count();

            // Solicitudes pendientes de lavandería
            long pendientesLavanderia = sistemaFacade
                    .getGestorLavanderia()
                    .listarSolicitudes()
                    .stream()
                    .filter(solicitud ->
                            solicitud.getEstado()
                                    == EstadoSolicitud.PENDIENTE
                    )
                    .count();

            // Actualizar las tarjetas
            lblTotalPrendas.setText(
                    String.valueOf(totalPrendas)
            );

            lblTotalClientes.setText(
                    String.valueOf(totalClientes)
            );

            lblAlquileresActivos.setText(
                    String.valueOf(alquileresActivos)
            );

            lblPendientesLavanderia.setText(
                    String.valueOf(pendientesLavanderia)
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cargar los indicadores:\n"
                            + e.getMessage(),
                    "Error de consulta",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // ==========================================
    // CONTRATO IMODULOVISTA
    // ==========================================

    @Override
    public String getId() {
        return "inicio";
    }

    @Override
    public String getTitulo() {
        return "Inicio";
    }

    @Override
    public Icon getIcono() {
        return IconosUI.inicio();
    }

    @Override
    public JComponent getVista() {
        return this;
    }
}
