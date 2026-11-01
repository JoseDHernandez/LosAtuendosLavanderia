
package com.lavanderia.vista.componentes;

import com.lavanderia.vista.estilos.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;

import static com.lavanderia.vista.estilos.EstilosUI.*;

public class SelectorFecha extends JPanel {

    private final JLabel lblEtiqueta;
    private final JSpinner selector;
    private final JLabel lblError;

    public SelectorFecha(String etiqueta) {

        lblEtiqueta = new JLabel(etiqueta);

        selector = new JSpinner(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        Calendar.DAY_OF_MONTH
                )
        );

        lblError = new JLabel(" ");

        configurarPanel();
        configurarEtiqueta();
        configurarSelector();
        configurarError();

        add(lblEtiqueta);
        add(selector);
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
    // CONFIGURACIÓN DEL SELECTOR
    // ==========================================

    private void configurarSelector() {

        JSpinner.DateEditor editor = new JSpinner.DateEditor(
                selector,
                "dd/MM/yyyy"
        );

        selector.setEditor(editor);
        selector.setFont(FUENTE_NORMAL);

        selector.setBorder(BORDE_CAMPO);
        selector.setAlignmentX(Component.LEFT_ALIGNMENT);

        JFormattedTextField campo =
                editor.getTextField();

        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setCaretColor(PRIMARIO);

        campo.setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_MD,
                ESPACIADO_SM,
                ESPACIADO_MD
        ));

        campo.setPreferredSize(
                new Dimension(
                        ANCHO_CAMPO_MINIMO,
                        ALTO_CAMPO
                )
        );

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        ALTO_CAMPO
                )
        );
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
    // ACCESO A LA FECHA
    // ==========================================

    public Date getFecha() {
        return (Date) selector.getValue();
    }

    public void setFecha(Date fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula."
            );
        }

        SpinnerDateModel modelo =
                (SpinnerDateModel) selector.getModel();

        if (modelo.getStart() != null
                && fecha.before((Date) modelo.getStart())) {
            throw new IllegalArgumentException(
                    "La fecha es anterior al límite permitido."
            );
        }

        if (modelo.getEnd() != null
                && fecha.after((Date) modelo.getEnd())) {
            throw new IllegalArgumentException(
                    "La fecha supera el límite permitido."
            );
        }

        selector.setValue(fecha);
        limpiarError();
    }

    // ==========================================
    // LÍMITES DE FECHAS
    // ==========================================

    public void setFechaMinima(Date fechaMinima) {

        SpinnerDateModel modelo =
                (SpinnerDateModel) selector.getModel();

        if (fechaMinima != null
                && modelo.getEnd() != null
                && fechaMinima.after((Date) modelo.getEnd())) {
            throw new IllegalArgumentException(
                    "La fecha mínima no puede superar la máxima."
            );
        }

        if (fechaMinima != null
                && fechaMinima.after(getFecha())) {
            throw new IllegalArgumentException(
                    "La fecha mínima no puede superar la fecha seleccionada."
            );
        }

        modelo.setStart(fechaMinima);
    }

    public void setFechaMaxima(Date fechaMaxima) {

        SpinnerDateModel modelo =
                (SpinnerDateModel) selector.getModel();

        if (fechaMaxima != null
                && modelo.getStart() != null
                && fechaMaxima.before((Date) modelo.getStart())) {
            throw new IllegalArgumentException(
                    "La fecha máxima no puede ser anterior a la mínima."
            );
        }

        if (fechaMaxima != null
                && fechaMaxima.before(getFecha())) {
            throw new IllegalArgumentException(
                    "La fecha máxima no puede ser anterior a la fecha seleccionada."
            );
        }

        modelo.setEnd(fechaMaxima);
    }

    // ==========================================
    // VALIDACIONES VISUALES
    // ==========================================

    public void mostrarError(String mensaje) {

        if (mensaje == null || mensaje.isBlank()) {
            limpiarError();
            return;
        }

        lblError.setText(mensaje);

        revalidate();
        repaint();
    }

    public void limpiarError() {

        lblError.setText(" ");

        revalidate();
        repaint();
    }

    // ==========================================
    // CONFIGURACIÓN GENERAL
    // ==========================================

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        lblEtiqueta.setEnabled(enabled);
        selector.setEnabled(enabled);
        lblError.setEnabled(enabled);
    }

    public JSpinner getSelector() {
        return selector;
    }
}
