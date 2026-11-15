
package com.lavanderia.vista.componentes;

import com.lavanderia.vista.estilos.EstilosUI;

import javax.swing.*;
import java.awt.*;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/**
 * Representa visualmente un estado de la aplicación.
 *
 * Se adapta al espacio disponible, permite textos extensos
 * y conserva una presentación uniforme en los diferentes módulos.
 */
public class EtiquetaEstado extends JLabel {

    public enum TipoEstado {
        EXITO,
        ADVERTENCIA,
        ERROR,
        NEUTRO,
        PRIMARIO
    }

    private TipoEstado tipoEstado;

    public EtiquetaEstado(String texto, TipoEstado tipo) {
        super(texto != null ? texto : "");

        configurarBase();
        actualizarEstado(texto, tipo);
    }

    // ==========================================
    // CONFIGURACIÓN BASE
    // ==========================================

    private void configurarBase() {

        setFont(FUENTE_PEQUENA.deriveFont(Font.BOLD));

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setBorder(BorderFactory.createEmptyBorder(
                ESPACIADO_SM,
                ESPACIADO_MD - ESPACIADO_XS,
                ESPACIADO_SM,
                ESPACIADO_MD - ESPACIADO_XS
        ));

        setOpaque(true);

        // Permite que la etiqueta se reduzca horizontalmente.
        setMinimumSize(new Dimension(0, 0));

        // Evita que la etiqueta se expanda verticalmente sin necesidad.
        setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        setHorizontalTextPosition(SwingConstants.CENTER);

        getAccessibleContext().setAccessibleName(getText());
    }

    // ==========================================
    // CONFIGURACIÓN DEL ESTADO
    // ==========================================

    private void configurarEtiqueta(TipoEstado tipo) {

        switch (tipo) {

            case EXITO -> {
                setForeground(EstilosUI.EXITO);
                setBackground(EXITO_CLARO);
            }

            case ADVERTENCIA -> {
                setForeground(EstilosUI.ADVERTENCIA);
                setBackground(ADVERTENCIA_CLARO);
            }

            case ERROR -> {
                setForeground(EstilosUI.ERROR);
                setBackground(ERROR_CLARO);
            }

            case PRIMARIO -> {
                setForeground(EstilosUI.PRIMARIO);
                setBackground(PRIMARIO_CLARO);
            }

            case NEUTRO -> {
                setForeground(TEXTO_SECUNDARIO);
                setBackground(FONDO);
            }
        }
    }

    // ==========================================
    // ACTUALIZACIÓN DEL ESTADO
    // ==========================================

    public void actualizarEstado(
            String texto,
            TipoEstado tipo
    ) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de estado no puede ser nulo."
            );
        }

        String textoSeguro = texto != null ? texto : "";

        setText(textoSeguro);
        tipoEstado = tipo;

        configurarEtiqueta(tipoEstado);

        getAccessibleContext().setAccessibleName(textoSeguro);

        setToolTipText(textoSeguro.isBlank() ? null : textoSeguro);

        revalidate();
        repaint();
    }

    // ==========================================
    // ACTUALIZACIÓN DEL TEXTO
    // ==========================================

    @Override
    public void setText(String texto) {

        super.setText(texto);

        if (getAccessibleContext() != null) {
            getAccessibleContext().setAccessibleName(
                    texto != null ? texto : ""
            );
        }

        revalidate();
        repaint();
    }

    // ==========================================
    // CONSULTA DEL ESTADO
    // ==========================================

    public TipoEstado getTipoEstado() {
        return tipoEstado;
    }

    public boolean esEstado(TipoEstado tipo) {
        return tipoEstado == tipo;
    }
}
