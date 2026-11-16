
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Panel principal del sistema Los Atuendos.
 *
 * Presenta los indicadores generales del negocio y una introducción
 * a los módulos disponibles. Su distribución se adapta al ancho
 * de la ventana y las consultas se ejecutan fuera del EDT.
 */
public class PanelInicio extends JPanel implements IModuloVista {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ISistemaFacade sistemaFacade;

    private JLabel lblTotalPrendas;
    private JLabel lblAlquileresActivos;
    private JLabel lblTotalClientes;
    private JLabel lblPendientesLavanderia;

    private JPanel panelIndicadores;
    private JPanel contenido;

    private SwingWorker<Indicadores, Void> tareaIndicadores;

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

    private static final Color COLOR_FECHA =
            new Color(216, 228, 235);

    private static final Font FUENTE_INDICADOR =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 32);

    private static final Font FUENTE_TARJETA =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 13);

    private static final int ESPACIO_CONTENIDO = ESPACIADO_LG;
    private static final int ANCHO_TARJETA_MINIMO = 220;
    private static final int ALTO_TARJETA = 150;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PanelInicio(ISistemaFacade sistemaFacade) {

        if (sistemaFacade == null) {
            throw new IllegalArgumentException(
                    "La fachada del sistema no puede ser nula."
            );
        }

        this.sistemaFacade = sistemaFacade;

        configurarPanel();
        inicializarComponentes();
        configurarAdaptabilidad();

        actualizarIndicadores();
    }

    // ==========================================
    // CONFIGURACIÓN GENERAL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout());
        setBackground(FONDO);
        setOpaque(true);

        setMinimumSize(new Dimension(0, 0));
    }

    // ==========================================
    // COMPONENTES PRINCIPALES
    // ==========================================

    private void inicializarComponentes() {

        contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );

        contenido.setBackground(FONDO);
        contenido.setBorder(
                new EmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        );

        contenido.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Encabezado principal.
        contenido.add(crearEncabezado());
        contenido.add(Box.createVerticalStrut(ESPACIADO_LG));

        // Título de los indicadores.
        contenido.add(crearTituloSeccion(
                "Resumen general",
                "Indicadores principales del negocio"
        ));

        contenido.add(Box.createVerticalStrut(ESPACIADO_MD));

        // Tarjetas de indicadores.
        panelIndicadores = crearPanelIndicadores();
        contenido.add(panelIndicadores);

        contenido.add(Box.createVerticalStrut(ESPACIADO_LG));

        // Información complementaria.
        contenido.add(crearPanelInformativo());

        JScrollPane scroll = new JScrollPane(
                contenido,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setBorder(null);
        scroll.setBackground(FONDO);
        scroll.getViewport().setBackground(FONDO);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // El contenido se ajusta al ancho del área visible.
        scroll.getViewport().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                actualizarDistribucionTarjetas();
            }
        });

        add(scroll, BorderLayout.CENTER);
    }

    // ==========================================
    // ADAPTABILIDAD
    // ==========================================

    private void configurarAdaptabilidad() {

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                actualizarDistribucionTarjetas();
            }
        });
    }

    /**
     * Cambia la cantidad de columnas según el ancho disponible.
     *
     * Cuatro columnas para ventanas amplias.
     * Dos columnas para espacios intermedios.
     * Una columna para ventanas estrechas.
     */
    private void actualizarDistribucionTarjetas() {

        if (panelIndicadores == null) {
            return;
        }

        int ancho = panelIndicadores.getParent() != null
                ? panelIndicadores.getParent().getWidth()
                : getWidth();

        if (ancho <= 0) {
            return;
        }

        int columnas = Math.clamp(ancho / ANCHO_TARJETA_MINIMO,
                1, 4);

        GridLayout layout = (GridLayout) panelIndicadores.getLayout();

        if (layout.getColumns() != columnas) {

            layout.setColumns(columnas);
            layout.setRows(0);

            panelIndicadores.revalidate();
            panelIndicadores.repaint();
        }
    }

    // ==========================================
    // ENCABEZADO
    // ==========================================

    private JPanel crearEncabezado() {

        JPanel encabezado = new JPanel(new BorderLayout(
                ESPACIADO_MD,
                ESPACIADO_SM
        ));

        encabezado.setBackground(COLOR_ENCABEZADO);

        encabezado.setBorder(
                new EmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        );

        JPanel textos = new JPanel();

        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel etiqueta = new JLabel("PANEL DE ADMINISTRACIÓN");

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
                "<html>Gestiona el inventario, los alquileres "
                        + "y la lavandería.</html>"
        );

        descripcion.setFont(FUENTE_NORMAL);
        descripcion.setForeground(COLOR_FECHA);

        textos.add(etiqueta);
        textos.add(Box.createVerticalStrut(ESPACIADO_SM));
        textos.add(bienvenida);
        textos.add(Box.createVerticalStrut(ESPACIADO_XS));
        textos.add(descripcion);

        JLabel fecha = new JLabel(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd 'de' MMMM 'de' yyyy",
                                new Locale("es", "CO")
                        )
                )
        );

        fecha.setForeground(COLOR_FECHA);
        fecha.setFont(FUENTE_PEQUENA);
        fecha.setHorizontalAlignment(SwingConstants.RIGHT);
        fecha.setVerticalAlignment(SwingConstants.TOP);

        JPanel panelFecha = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                0,
                0
        ));

        panelFecha.setOpaque(false);
        panelFecha.add(fecha);

        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(panelFecha, BorderLayout.EAST);

        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

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

        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(ESPACIADO_XS));
        panel.add(lblDescripcion);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    // ==========================================
    // PANEL DE INDICADORES
    // ==========================================

    private JPanel crearPanelIndicadores() {

        JPanel indicadores = new JPanel(
                new GridLayout(0, 4, ESPACIADO_MD, ESPACIADO_MD)
        );

        indicadores.setOpaque(false);
        indicadores.setAlignmentX(Component.LEFT_ALIGNMENT);

        indicadores.add(crearTarjeta(
                "Prendas registradas",
                "—",
                "Inventario general",
                COLOR_AZUL,
                1
        ));

        indicadores.add(crearTarjeta(
                "Alquileres activos",
                "—",
                "Servicios en curso",
                COLOR_VERDE,
                2
        ));

        indicadores.add(crearTarjeta(
                "Clientes registrados",
                "—",
                "Personas registradas",
                COLOR_MORADO,
                3
        ));

        indicadores.add(crearTarjeta(
                "Pendientes de lavandería",
                "—",
                "Solicitudes por atender",
                COLOR_NARANJA,
                4
        ));

        indicadores.setPreferredSize(new Dimension(0, ALTO_TARJETA));

        indicadores.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

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

        JPanel tarjeta = new JPanel(new BorderLayout(
                0,
                ESPACIADO_SM
        ));

        tarjeta.setBackground(SUPERFICIE);

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_BORDE, 1, true),
                        new EmptyBorder(
                                ESPACIADO_MD,
                                ESPACIADO_MD,
                                ESPACIADO_MD,
                                ESPACIADO_MD
                        )
                )
        );

        tarjeta.setMinimumSize(new Dimension(
                ANCHO_TARJETA_MINIMO,
                ALTO_TARJETA
        ));

        tarjeta.setPreferredSize(new Dimension(
                ANCHO_TARJETA_MINIMO,
                ALTO_TARJETA
        ));

        // Título de la tarjeta.
        JLabel lblTitulo = new JLabel(
                "<html><div style='width:100%;'>"
                        + escaparHTML(titulo)
                        + "</div></html>"
        );

        lblTitulo.setFont(FUENTE_TARJETA);
        lblTitulo.setForeground(TEXTO_SECUNDARIO);

        // Valor del indicador.
        JLabel lblValor = new JLabel(valor);

        lblValor.setFont(FUENTE_INDICADOR);
        lblValor.setForeground(colorIndicador);

        // Descripción.
        JLabel lblDescripcion = new JLabel(
                "<html><div style='width:100%;'>"
                        + escaparHTML(descripcion)
                        + "</div></html>"
        );

        lblDescripcion.setFont(FUENTE_PEQUENA);
        lblDescripcion.setForeground(TEXTO_SECUNDARIO);

        JPanel centro = new JPanel();

        centro.setOpaque(false);
        centro.setLayout(
                new BoxLayout(centro, BoxLayout.Y_AXIS)
        );

        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(lblValor);
        centro.add(Box.createVerticalStrut(ESPACIADO_XS));
        centro.add(lblDescripcion);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(centro, BorderLayout.CENTER);

        // Guardar referencias para actualizar los indicadores.
        switch (indicador) {

            case 1 -> lblTotalPrendas = lblValor;

            case 2 -> lblAlquileresActivos = lblValor;

            case 3 -> lblTotalClientes = lblValor;

            case 4 -> lblPendientesLavanderia = lblValor;

            default -> throw new IllegalArgumentException(
                    "Indicador de tarjeta no reconocido."
            );
        }

        return tarjeta;
    }

    // ==========================================
    // PANEL INFORMATIVO
    // ==========================================

    private JPanel crearPanelInformativo() {

        JPanel informacion = new JPanel(new BorderLayout(
                0,
                ESPACIADO_MD
        ));

        informacion.setBackground(SUPERFICIE);

        informacion.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_BORDE, 1, true),
                        new EmptyBorder(
                                ESPACIADO_MD,
                                ESPACIADO_LG,
                                ESPACIADO_MD,
                                ESPACIADO_LG
                        )
                )
        );

        JLabel titulo = crearEtiqueta(
                "Actividad del negocio",
                FUENTE_SECCION,
                TEXTO
        );

        JLabel mensaje = new JLabel(
                "<html><div style='width:100%;'>"
                        + "Desde el menú lateral puedes acceder a los "
                        + "diferentes módulos del sistema.<br><br>"
                        + "Administra tus clientes y empleados, controla "
                        + "el inventario de prendas, registra alquileres "
                        + "y supervisa las solicitudes de lavandería."
                        + "</div></html>"
        );

        mensaje.setFont(FUENTE_NORMAL);
        mensaje.setForeground(TEXTO_SECUNDARIO);

        informacion.add(titulo, BorderLayout.NORTH);
        informacion.add(mensaje, BorderLayout.CENTER);

        informacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        return informacion;
    }

    // ==========================================
    // CONSULTA DE INDICADORES
    // ==========================================

    /**
     * Agrupa los datos consultados para actualizar las tarjetas
     * en una única operación sobre el EDT.
     */
    private record Indicadores(
            int totalPrendas,
            int totalClientes,
            long alquileresActivos,
            long pendientesLavanderia
    ) {}

    /**
     * Consulta los indicadores en segundo plano para evitar que
     * la interfaz se congele mientras se accede a la persistencia.
     */
    public void actualizarIndicadores() {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::actualizarIndicadores);
            return;
        }

        if (tareaIndicadores != null && !tareaIndicadores.isDone()) {
            tareaIndicadores.cancel(true);
        }

        establecerIndicadores("…");

        tareaIndicadores = new SwingWorker<>() {

            @Override
            protected Indicadores doInBackground() {

                int totalPrendas = sistemaFacade
                        .getGestorPrendas()
                        .listarPrendas()
                        .size();

                int totalClientes = sistemaFacade
                        .getGestorPersonas()
                        .listarClientes()
                        .size();

                long alquileresActivos = sistemaFacade
                        .getGestorAlquiler()
                        .listarTodos()
                        .stream()
                        .filter(alquiler ->
                                alquiler.getEstado() == EstadoAlquiler.ACTIVO
                        )
                        .count();

                long pendientesLavanderia = sistemaFacade
                        .getGestorLavanderia()
                        .listarSolicitudes()
                        .stream()
                        .filter(solicitud ->
                                solicitud.getEstado() == EstadoSolicitud.PENDIENTE
                        )
                        .count();

                return new Indicadores(
                        totalPrendas,
                        totalClientes,
                        alquileresActivos,
                        pendientesLavanderia
                );
            }

            @Override
            protected void done() {

                if (isCancelled()) {
                    return;
                }

                try {

                    Indicadores datos = get();

                    lblTotalPrendas.setText(
                            String.valueOf(datos.totalPrendas())
                    );

                    lblTotalClientes.setText(
                            String.valueOf(datos.totalClientes())
                    );

                    lblAlquileresActivos.setText(
                            String.valueOf(datos.alquileresActivos())
                    );

                    lblPendientesLavanderia.setText(
                            String.valueOf(datos.pendientesLavanderia())
                    );

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    establecerIndicadores("—");

                } catch (ExecutionException e) {

                    establecerIndicadores("—");

                    Throwable causa = e.getCause() != null
                            ? e.getCause()
                            : e;

                    JOptionPane.showMessageDialog(
                            PanelInicio.this,
                            "No fue posible cargar los indicadores:\n"
                                    + causa.getMessage(),
                            "Error de consulta",
                            JOptionPane.ERROR_MESSAGE
                    );

                    causa.printStackTrace();
                }
            }
        };

        tareaIndicadores.execute();
    }

    /**
     * Establece un valor temporal mientras se realiza la consulta.
     */
    private void establecerIndicadores(String valor) {

        if (lblTotalPrendas != null) {
            lblTotalPrendas.setText(valor);
        }

        if (lblAlquileresActivos != null) {
            lblAlquileresActivos.setText(valor);
        }

        if (lblTotalClientes != null) {
            lblTotalClientes.setText(valor);
        }

        if (lblPendientesLavanderia != null) {
            lblPendientesLavanderia.setText(valor);
        }
    }

    // ==========================================
    // UTILIDADES VISUALES
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

    private String escaparHTML(String texto) {

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
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
