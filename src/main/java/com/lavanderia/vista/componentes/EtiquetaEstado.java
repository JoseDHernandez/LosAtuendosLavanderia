
package com.lavanderia.vista.componentes;

import com.lavanderia.vista.estilos.EstilosUI;

import javax.swing.*;
import java.awt.*;

import static com.lavanderia.vista.estilos.EstilosUI.*;

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
        super(texto);

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
    }

    // ==========================================
    // CONFIGURACIÓN DEL ESTADO
    // ==========================================

    private void configurarEtiqueta(TipoEstado tipo) {

        switch (tipo) {

            case EXITO -> {
                setForeground(EXITO);
                setBackground(EXITO_CLARO);
            }

            case ADVERTENCIA -> {
                setForeground(ADVERTENCIA);
                setBackground(ADVERTENCIA_CLARO);
            }

            case ERROR -> {
                setForeground(EstilosUI.ERROR);
                setBackground(ERROR_CLARO);
            }

            case PRIMARIO -> {
                setForeground(PRIMARIO);
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

        setText(texto);
        tipoEstado = tipo;

        configurarEtiqueta(tipoEstado);

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
