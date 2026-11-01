package com.lavanderia.vista.paneles;

import com.lavanderia.vista.interfaces.IModuloVista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelInicio extends JPanel implements IModuloVista {

    private static final Color FONDO =
            new Color(245, 247, 250);

    private static final Color TEXTO =
            new Color(31, 41, 55);

    private static final Color SECUNDARIO =
            new Color(107, 114, 128);

    private static final Color AZUL =
            new Color(37, 99, 235);

    public PanelInicio() {

        setLayout(new BorderLayout());
        setBackground(FONDO);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(
                contenido, BoxLayout.Y_AXIS
        ));
        contenido.setBackground(FONDO);

        contenido.setBorder(new EmptyBorder(
                28, 30, 30, 30
        ));

        // Bienvenida
        JLabel bienvenida = new JLabel(
                "Bienvenido a Los Atuendos"
        );

        bienvenida.setFont(new Font(
                "Segoe UI", Font.BOLD, 25
        ));
        bienvenida.setForeground(TEXTO);

        JLabel descripcion = new JLabel(
                "Administra tus prendas, clientes, alquileres y lavandería."
        );

        descripcion.setFont(new Font(
                "Segoe UI", Font.PLAIN, 14
        ));
        descripcion.setForeground(SECUNDARIO);

        contenido.add(bienvenida);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(descripcion);

        contenido.add(Box.createVerticalStrut(30));

        // Indicadores
        JPanel indicadores = new JPanel(
                new GridLayout(1, 4, 16, 0)
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

        contenido.add(indicadores);

        contenido.add(Box.createVerticalStrut(25));

        // Panel informativo
        JPanel informacion = new JPanel(new BorderLayout());

        informacion.setBackground(Color.WHITE);
        informacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(229, 231, 235)
                ),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titulo = new JLabel("Panel de administración");

        titulo.setFont(new Font(
                "Segoe UI", Font.BOLD, 17
        ));
        titulo.setForeground(TEXTO);

        JLabel mensaje = new JLabel(
                "<html>Desde el menú lateral puedes acceder a los diferentes " +
                        "módulos del sistema.<br><br>" +
                        "Selecciona una sección para comenzar a gestionar " +
                        "la información del negocio.</html>"
        );

        mensaje.setFont(new Font(
                "Segoe UI", Font.PLAIN, 13
        ));
        mensaje.setForeground(SECUNDARIO);

        informacion.add(titulo, BorderLayout.NORTH);
        informacion.add(mensaje, BorderLayout.CENTER);

        informacion.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 180)
        );

        informacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenido.add(informacion);

        add(contenido, BorderLayout.NORTH);
    }

    private JPanel crearTarjeta(
            String titulo,
            String valor,
            String categoria
    ) {

        JPanel tarjeta = new JPanel();

        tarjeta.setLayout(new BoxLayout(
                tarjeta, BoxLayout.Y_AXIS
        ));

        tarjeta.setBackground(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(229, 231, 235)
                ),
                new EmptyBorder(15, 16, 15, 16)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12
        ));
        lblTitulo.setForeground(SECUNDARIO);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font(
                "Segoe UI", Font.BOLD, 27
        ));
        lblValor.setForeground(AZUL);

        JLabel lblCategoria = new JLabel(categoria);
        lblCategoria.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11
        ));
        lblCategoria.setForeground(SECUNDARIO);

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblValor);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(lblCategoria);

        return tarjeta;
    }

    @Override
    public String getId() {
        return "inicio";
    }

    @Override
    public String getTitulo() {
        return "Inicio";
    }

    @Override
    public String getIcono() {
        return "⌂";
    }

    @Override
    public JComponent getVista() {
        return this;
    }
}