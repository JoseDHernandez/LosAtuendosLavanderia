
package com.lavanderia;

import com.lavanderia.patrones.facade.ISistemaFacade;

import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.validacion.persona.PersonaValidator;

import com.lavanderia.validacion.prenda.IPrendaValidator;
import com.lavanderia.validacion.prenda.PrendaValidator;

import com.lavanderia.validacion.alquiler.IAlquilerValidator;
import com.lavanderia.validacion.alquiler.AlquilerValidator;

import com.lavanderia.vista.MainFrame;
import com.lavanderia.vista.interfaces.IModuloVista;

import com.lavanderia.vista.paneles.PanelInicio;
import com.lavanderia.vista.paneles.clientes.PanelClientes;
import com.lavanderia.vista.paneles.empleados.PanelEmpleados;
import com.lavanderia.vista.paneles.prendas.PanelPrendas;
import com.lavanderia.vista.paneles.alquileres.PanelAlquileres;
import com.lavanderia.vista.paneles.devoluciones.PanelDevoluciones;
import com.lavanderia.vista.paneles.lavanderia.PanelLavanderia;

import javax.swing.*;
import java.util.List;

/**
 * Clase principal y punto de entrada de la aplicación "Los Atuendos".
 *
 * Se encarga del arranque del sistema, configuración visual,
 * inicialización de la fachada de servicios, instanciación de
 * validadores y registro de los módulos de la interfaz gráfica Swing.
 *
 * @author Sistema de Lavandería
 * @version 1.0
 */
public class Main {

    /**
     * Método principal de arranque del aplicativo.
     *
     * Ejecuta la construcción de la interfaz gráfica
     * en el hilo de despacho de eventos (EDT).
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            configurarLookAndFeel();

            try {

                // 1. Inicializar la fachada del sistema.
                ISistemaFacade sistemaFacade =
                        inicializarSistemaFacade();

                // 2. Crear los validadores de negocio.
                IPersonaValidator validadorPersonas =
                        new PersonaValidator();

                IPrendaValidator validadorPrendas =
                        new PrendaValidator();

                IAlquilerValidator validadorAlquileres =
                        new AlquilerValidator();

                // 3. Registrar los módulos de la aplicación.
                List<IModuloVista> modulos = List.of(

                        new PanelInicio(),

                        new PanelClientes(
                                sistemaFacade,
                                validadorPersonas
                        ),

                        new PanelEmpleados(
                                sistemaFacade,
                                validadorPersonas
                        ),

                        new PanelPrendas(
                                sistemaFacade,
                                validadorPrendas
                        ),

                        new PanelAlquileres(
                                sistemaFacade,
                                validadorAlquileres
                        ),

                        new PanelDevoluciones(
                                sistemaFacade
                        ),

                        new PanelLavanderia(
                                sistemaFacade
                        )
                );

                // 4. Crear la ventana principal.
                MainFrame ventana = new MainFrame(
                        modulos,
                        "inicio"
                );

                // 5. Mostrar la aplicación.
                ventana.setLocationRelativeTo(null);
                ventana.setVisible(true);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible iniciar el sistema:\n"
                                + e.getMessage(),
                        "Error de inicialización",
                        JOptionPane.ERROR_MESSAGE
                );

                e.printStackTrace();
            }
        });
    }

    /**
     * Configura el aspecto visual de la aplicación
     * según el sistema operativo anfitrión.
     */
    private static void configurarLookAndFeel() {

        try {

            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception e) {

            System.err.println(
                    "No se pudo establecer el Look and Feel."
            );
        }
    }

    /**
     * Construye la fachada del sistema y ensambla sus dependencias.
     *
     * Pendiente conectar la fábrica SQLite, los DAO
     * y los cinco gestores del sistema.
     *
     * @return Instancia configurada de ISistemaFacade.
     */
    private static ISistemaFacade inicializarSistemaFacade() {

        // Pendiente implementar la inicialización de:
        // - GestorPersonas
        // - GestorPrendas
        // - GestorAlquiler
        // - GestorLavanderia
        // - GestorDevoluciones

        throw new UnsupportedOperationException(
                "La inicialización de los gestores está pendiente."
        );
    }
}
