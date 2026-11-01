
package com.lavanderia.vista.paneles;

import com.lavanderia.vista.estilos.IconosUI;
import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class PanelInicio extends JPanel implements IModuloVista {

    public PanelInicio() {

        configurarPanel();
        inicializarComponentes();
    }
    // Tipografía adicional
    public static final Font FUENTE_INDICADOR =
            new Font(FUENTE_PRINCIPAL, Font.BOLD, 27);

    // Márgenes específicos
    public static final Border MARGEN_DASHBOARD =
            new EmptyBorder(28, 30, 30, 30);
    // ==========================================
    // CONFIGURACIÓN DEL PANEL
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

        contenido.setLayout(new BoxLayout(
                contenido, BoxLayout.Y_AXIS
        ));

        contenido.setBackground(FONDO);
        contenido.setBorder(MARGEN_DASHBOARD);

        // Bienvenida
        JLabel bienvenida = crearEtiqueta(
                "Bienvenido a Los Atuendos",
                FUENTE_TITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                "Administra tus prendas, clientes, alquileres y lavandería.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        contenido.add(bienvenida);
        contenido.add(Box.createVerticalStrut(ESPACIADO_SM));
        contenido.add(descripcion);

        contenido.add(Box.createVerticalStrut(ESPACIADO_XL));

        // Indicadores
        JPanel indicadores = crearPanelIndicadores();

        contenido.add(indicadores);

        contenido.add(Box.createVerticalStrut(ESPACIADO_LG));

        // Panel informativo
        JPanel informacion = crearPanelInformativo();

        contenido.add(informacion);

        add(contenido, BorderLayout.NORTH);
    }

    // ==========================================
    // INDICADORES
    // ==========================================

    private JPanel crearPanelIndicadores() {

        JPanel indicadores = new JPanel(
                new GridLayout(1, 4, ESPACIADO_MD, 0)
        );

        indicadores.setOpaque(false);

        indicadores.add(crearTarjeta(
                "Prendas registradas",
                "—",
                "Inventario"
        ));

        indicadores.add(crearTarjeta(
                "Alquileres activos",
                "—",
                "Servicios"
        ));

        indicadores.add(crearTarjeta(
                "Clientes registrados",
                "—",
                "Personas"
        ));

        indicadores.add(crearTarjeta(
                "Pendientes de lavandería",
                "—",
                "Lavandería"
        ));

        indicadores.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 125)
        );

        indicadores.setAlignmentX(Component.LEFT_ALIGNMENT);

        return indicadores;
    }

    // ==========================================
    // TARJETAS DE INDICADORES
    // ==========================================

    private JPanel crearTarjeta(
            String titulo,
            String valor,
            String categoria
    ) {

        JPanel tarjeta = new JPanel();

        tarjeta.setLayout(new BoxLayout(
                tarjeta, BoxLayout.Y_AXIS
        ));

        tarjeta.setBackground(SUPERFICIE);

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BORDE_PANEL,
                new EmptyBorder(
                        ESPACIADO_MD,
                        ESPACIADO_MD,
                        ESPACIADO_MD,
                        ESPACIADO_MD
                )
        ));

        JLabel lblTitulo = crearEtiqueta(
                titulo,
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        JLabel lblValor = crearEtiqueta(
                valor,
                FUENTE_INDICADOR,
                PRIMARIO
        );

        JLabel lblCategoria = crearEtiqueta(
                categoria,
                FUENTE_PEQUENA,
                TEXTO_SECUNDARIO
        );

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(ESPACIADO_SM));
        tarjeta.add(lblValor);
        tarjeta.add(Box.createVerticalStrut(ESPACIADO_XS));
        tarjeta.add(lblCategoria);

        return tarjeta;
    }

    // ==========================================
    // PANEL INFORMATIVO
    // ==========================================

    private JPanel crearPanelInformativo() {

        JPanel informacion = new JPanel(new BorderLayout());

        informacion.setBackground(SUPERFICIE);

        informacion.setBorder(BorderFactory.createCompoundBorder(
                BORDE_PANEL,
                new EmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        ));

        JLabel titulo = crearEtiqueta(
                "Panel de administración",
                FUENTE_SECCION,
                TEXTO
        );

        JLabel mensaje = new JLabel(
                "<html>Desde el menú lateral puedes acceder a los diferentes " +
                        "módulos del sistema.<br><br>" +
                        "Selecciona una sección para comenzar a gestionar " +
                        "la información del negocio.</html>"
        );

        mensaje.setFont(FUENTE_PEQUENA);
        mensaje.setForeground(TEXTO_SECUNDARIO);

        informacion.add(titulo, BorderLayout.NORTH);
        informacion.add(mensaje, BorderLayout.CENTER);

        informacion.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 180)
        );

        informacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        return informacion;
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
