
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class DialogoConfirmacion extends JDialog {

    private boolean confirmado = false;

    public DialogoConfirmacion(
            Window propietario,
            String titulo,
            String mensaje
    ) {
        super(propietario, titulo, ModalityType.APPLICATION_MODAL);

        configurarVentana();
        construirContenido(mensaje);
    }

    // ==========================================
    // CONFIGURACIÓN DE LA VENTANA
    // ==========================================

    private void configurarVentana() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setSize(ANCHO_DIALOGO, ALTO_DIALOGO);
        setMinimumSize(new Dimension(
                ANCHO_DIALOGO,
                ALTO_DIALOGO
        ));

        setResizable(false);
        setBackground(SUPERFICIE);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL CONTENIDO
    // ==========================================

    private void construirContenido(String mensaje) {

        JPanel contenido = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        contenido.setBackground(SUPERFICIE);

        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        );

        construirMensaje(contenido, mensaje);
        construirAcciones(contenido);

        setContentPane(contenido);

        configurarTeclas();
    }

    // ==========================================
    // MENSAJE
    // ==========================================

    private void construirMensaje(
            JPanel contenido,
            String mensaje
    ) {

        JTextArea txtMensaje = new JTextArea(
                mensaje == null ? "" : mensaje
        );

        txtMensaje.setFont(FUENTE_NORMAL);
        txtMensaje.setForeground(TEXTO);
        txtMensaje.setBackground(SUPERFICIE);

        txtMensaje.setEditable(false);
        txtMensaje.setFocusable(false);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);

        txtMensaje.setBorder(null);
        txtMensaje.setOpaque(false);

        contenido.add(txtMensaje, BorderLayout.CENTER);
    }

    // ==========================================
    // BOTONES DE ACCIÓN
    // ==========================================

    private void construirAcciones(JPanel contenido) {

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        ESPACIADO_SM,
                        0
                )
        );

        acciones.setBackground(SUPERFICIE);

        BotonSecundario btnCancelar =
                new BotonSecundario("Cancelar");

        BotonPrincipal btnConfirmar =
                new BotonPrincipal("Confirmar");

        btnCancelar.addActionListener(e -> cancelar());

        btnConfirmar.addActionListener(e -> confirmar());

        acciones.add(btnCancelar);
        acciones.add(btnConfirmar);

        contenido.add(acciones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnConfirmar);
    }

    // ==========================================
    // TECLAS DE ACCESO
    // ==========================================

    private void configurarTeclas() {

        getRootPane().registerKeyboardAction(
                e -> cancelar(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // ==========================================
    // ACCIONES
    // ==========================================

    private void confirmar() {

        confirmado = true;
        dispose();
    }

    private void cancelar() {

        confirmado = false;
        dispose();
    }

    // ==========================================
    // VISUALIZACIÓN
    // ==========================================

    public boolean mostrarDialogo() {

        confirmado = false;

        pack();

        setSize(
                ANCHO_DIALOGO,
                ALTO_DIALOGO
        );

        setLocationRelativeTo(getOwner());

        setVisible(true);

        return confirmado;
    }
}
