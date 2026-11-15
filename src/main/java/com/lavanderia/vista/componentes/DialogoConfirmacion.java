
package com.lavanderia.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Componente visual para solicitar confirmación de una acción.
 *
 * Se adapta al tamaño del mensaje y a las dimensiones de pantalla,
 * permitiendo mostrar textos extensos sin recortarlos.
 */
public class DialogoConfirmacion extends JDialog {

    private boolean confirmado = false;

    private JTextArea txtMensaje;
    private BotonSecundario btnCancelar;
    private BotonPrincipal btnConfirmar;
    private JPanel contenido;
    private JPanel acciones;

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
        setBackground(SUPERFICIE);

        // El tamaño máximo se limita al área disponible de la pantalla.
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        int anchoMaximo = Math.max(ANCHO_DIALOGO,
                pantalla.width - ESPACIADO_XL * 2);

        int altoMaximo = Math.max(ALTO_DIALOGO,
                pantalla.height - ESPACIADO_XL * 2);

        setMinimumSize(new Dimension(
                Math.min(ANCHO_DIALOGO, anchoMaximo),
                Math.min(ALTO_DIALOGO, altoMaximo)
        ));

        setSize(ANCHO_DIALOGO, ALTO_DIALOGO);

        // El usuario puede ampliar el diálogo si el contenido lo requiere.
        setResizable(true);
    }

    // ==========================================
    // CONSTRUCCIÓN DEL CONTENIDO
    // ==========================================

    private void construirContenido(String mensaje) {

        contenido = new JPanel(new BorderLayout(
                0,
                ESPACIADO_LG
        ));

        contenido.setBackground(SUPERFICIE);

        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG,
                        ESPACIADO_LG
                )
        );

        construirMensaje(mensaje);
        construirAcciones();

        setContentPane(contenido);

        configurarTeclas();
    }

    // ==========================================
    // MENSAJE
    // ==========================================

    private void construirMensaje(String mensaje) {

        txtMensaje = new JTextArea(
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

        txtMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        // El scroll permite consultar mensajes extensos sin aumentar
        // indefinidamente el tamaño de la ventana.
        JScrollPane scrollMensaje = new JScrollPane(
                txtMensaje,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollMensaje.setBorder(null);
        scrollMensaje.setOpaque(false);
        scrollMensaje.getViewport().setOpaque(false);

        scrollMensaje.setMinimumSize(new Dimension(0, 80));
        scrollMensaje.setPreferredSize(new Dimension(
                ANCHO_DIALOGO - ESPACIADO_XL * 2,
                ALTO_DIALOGO - ESPACIADO_XL * 3
        ));

        contenido.add(scrollMensaje, BorderLayout.CENTER);
    }

    // ==========================================
    // BOTONES DE ACCIÓN
    // ==========================================

    private void construirAcciones() {

        acciones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                ESPACIADO_SM,
                0
        ));

        acciones.setBackground(SUPERFICIE);

        btnCancelar = new BotonSecundario("Cancelar");
        btnConfirmar = new BotonPrincipal("Confirmar");

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

    /**
     * Muestra el diálogo centrado respecto a su ventana propietaria.
     *
     * @return true si el usuario confirma; false si cancela o cierra.
     */
    public boolean mostrarDialogo() {

        confirmado = false;

        pack();

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        int ancho = Math.min(
                ANCHO_DIALOGO,
                pantalla.width - ESPACIADO_XL * 2
        );

        int alto = Math.min(
                ALTO_DIALOGO,
                pantalla.height - ESPACIADO_XL * 2
        );

        setSize(
                Math.max(ANCHO_MINIMO, ancho),
                Math.max(ALTO_MINIMO, alto)
        );

        setLocationRelativeTo(getOwner());

        setVisible(true);

        return confirmado;
    }

    // ==========================================
    // ACCESO AL ESTADO
    // ==========================================

    public boolean isConfirmado() {
        return confirmado;
    }
}
