
package com.lavanderia.vista.componentes;

import com.lavanderia.vista.estilos.EstilosUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class CampoTexto extends JPanel {

    private final JLabel lblEtiqueta;
    private final JTextField txtCampo;
    private final JLabel lblError;

    private final Border bordeNormal;
    private final Border bordeError;

    private boolean tieneError;

    public CampoTexto(String etiqueta, String placeholder) {

        lblEtiqueta = new JLabel(etiqueta);
        txtCampo = new JTextField();
        lblError = new JLabel(" ");

        bordeNormal = BorderFactory.createCompoundBorder(
                BORDE_CAMPO,
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_MD,
                        ESPACIADO_SM,
                        ESPACIADO_MD
                )
        );

        bordeError = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.ERROR, 1),
                BorderFactory.createEmptyBorder(
                        ESPACIADO_SM,
                        ESPACIADO_MD,
                        ESPACIADO_SM,
                        ESPACIADO_MD
                )
        );

        configurarPanel();
        configurarEtiqueta();
        configurarCampo();
        configurarError();

        configurarPlaceholder(placeholder);
        configurarEventos();

        add(lblEtiqueta);
        add(txtCampo);
        add(lblError);

        lblEtiqueta.setVisible(
                etiqueta != null && !etiqueta.isBlank()
        );
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SUPERFICIE);
        setOpaque(true);
    }

    // ==========================================
    // CONFIGURACIÓN DE LA ETIQUETA
    // ==========================================

    private void configurarEtiqueta() {

        lblEtiqueta.setFont(FUENTE_ETIQUETA);
        lblEtiqueta.setForeground(TEXTO);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEtiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        ESPACIADO_SM,
                        0
                )
        );
    }

    // ==========================================
    // CONFIGURACIÓN DEL CAMPO
    // ==========================================

    private void configurarCampo() {

        txtCampo.setFont(FUENTE_NORMAL);
        txtCampo.setForeground(TEXTO);
        txtCampo.setBackground(SUPERFICIE);
        txtCampo.setCaretColor(PRIMARIO);
        txtCampo.setBorder(bordeNormal);

        txtCampo.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        txtCampo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_CAMPO
                )
        );

        txtCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ==========================================
    // CONFIGURACIÓN DEL ERROR
    // ==========================================

    private void configurarError() {

        lblError.setFont(FUENTE_PEQUENA);
        lblError.setForeground(EstilosUI.ERROR);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblError.setBorder(
                BorderFactory.createEmptyBorder(
                        ESPACIADO_XS,
                        ESPACIADO_XS / 2,
                        0,
                        0
                )
        );

        lblError.setPreferredSize(
                new Dimension(0, ESPACIADO_LG)
        );

        lblError.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ESPACIADO_LG
                )
        );
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        txtCampo.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        limpiarError();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        limpiarError();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        limpiarError();
                    }
                }
        );
    }

    // ==========================================
    // PLACEHOLDER
    // ==========================================

    private void configurarPlaceholder(String placeholder) {

        if (placeholder != null && !placeholder.isBlank()) {
            txtCampo.putClientProperty(
                    "JTextField.placeholderText",
                    placeholder
            );
        }
    }

    // ==========================================
    // ACCESO A LA INFORMACIÓN
    // ==========================================

    public String getText() {
        return txtCampo.getText().trim();
    }

    public String getTextSinTrim() {
        return txtCampo.getText();
    }

    public void setText(String texto) {
        txtCampo.setText(texto != null ? texto : "");
        limpiarError();
    }

    public JTextField getCampo() {
        return txtCampo;
    }

    public void solicitarFoco() {
        txtCampo.requestFocusInWindow();
    }

    // ==========================================
    // CONFIGURACIÓN DEL CAMPO
    // ==========================================

    public void setEditable(boolean editable) {

        txtCampo.setEditable(editable);

        txtCampo.setBackground(
                editable ? SUPERFICIE : FONDO
        );
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        if (lblEtiqueta != null) {
            lblEtiqueta.setEnabled(enabled);
        }

        if (txtCampo != null) {
            txtCampo.setEnabled(enabled);
        }

        if (lblError != null) {
            lblError.setEnabled(enabled);
        }
    }

    public void setEtiqueta(String texto) {

        lblEtiqueta.setText(texto);

        lblEtiqueta.setVisible(
                texto != null && !texto.isBlank()
        );

        revalidate();
        repaint();
    }

    // ==========================================
    // VALIDACIONES VISUALES
    // ==========================================

    public void mostrarError(String mensaje) {

        if (mensaje == null || mensaje.isBlank()) {
            limpiarError();
            return;
        }

        tieneError = true;

        lblError.setText(mensaje);
        txtCampo.setBorder(bordeError);

        revalidate();
        repaint();
    }

    public void limpiarError() {

        tieneError = false;

        lblError.setText(" ");
        txtCampo.setBorder(bordeNormal);

        revalidate();
        repaint();
    }

    public boolean tieneError() {
        return tieneError;
    }

    // ==========================================
    // LIMPIEZA
    // ==========================================

    public void limpiar() {

        txtCampo.setText("");
        limpiarError();
    }
}
