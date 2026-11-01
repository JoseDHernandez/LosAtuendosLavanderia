package com.lavanderia;

import com.lavanderia.vista.MainFrame;
import com.lavanderia.vista.interfaces.IModuloVista;
import com.lavanderia.vista.paneles.PanelInicio;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                        "No se pudo establecer el Look and Feel."
                );
            }

            List<IModuloVista> modulos = List.of(
                    new PanelInicio()
            );

            MainFrame ventana = new MainFrame(
                    modulos,
                    "inicio"
            );

            ventana.setVisible(true);
        });
    }
}