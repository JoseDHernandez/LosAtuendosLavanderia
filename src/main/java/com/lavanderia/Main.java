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

import javax.swing.*;
import java.util.List;

/**
 * Clase principal y punto de entrada de la aplicación "Los Atuendos".
 * Se encarga del arranque del sistema, configuración visual (Look and Feel),
 * inicialización de la fachada de servicios, instanciación de validadores
 * y registro de los módulos de la interfaz gráfica Swing.
 *
 * @author Sistema de Lavandería
 * @version 1.0
 */
public class Main {

    /**
     * Método principal de arranque del aplicativo.
     * Ejecuta la construcción de la interfaz gráfica en el hilo de despacho de eventos (EDT).
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // Delegar la ejecución de la UI al Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {

            // Configurar el estilo visual nativo del sistema operativo
            configurarLookAndFeel();

            try {

                // 1. Inicializar la fachada del sistema (capa de servicios y persistencia)
                ISistemaFacade sistemaFacade =
                        inicializarSistemaFacade();

                // 2. Crear los validadores de negocio reutilizables
                IPersonaValidator validadorPersonas =
                        new PersonaValidator();

                IPrendaValidator validadorPrendas =
                        new PrendaValidator();

                IAlquilerValidator validadorAlquileres =
                        new AlquilerValidator();

                // 3. Registrar los módulos de la aplicación que componen las vistas
                // NOTA: PanelLavanderia no se encuentra registrado actualmente debido a estar incompleto.
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
                        )
                );

                // 4. Crear y configurar la ventana principal con los módulos registrados
                MainFrame ventana = new MainFrame(
                        modulos,
                        "inicio"
                );

                // Centrar la ventana en la pantalla del usuario
                ventana.setLocationRelativeTo(null);
                // Hacer visible la ventana
                ventana.setVisible(true);

            } catch (Exception e) {

                // Mostrar mensaje gráfico en caso de fallo crítico en la inicialización
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible iniciar el sistema:\n"
                                + e.getMessage(),
                        "Error de inicialización",
                        JOptionPane.ERROR_MESSAGE
                );

                // Imprimir traza del error en la consola para depuración
                e.printStackTrace();
            }
        });
    }

    /**
     * Configura el aspecto visual (Look and Feel) de la aplicación según el sistema operativo anfitrión.
     * Si falla, captura la excepción y mantiene el estilo por defecto de Java Swing.
     */
    private static void configurarLookAndFeel() {

        try {

            // Establecer el estilo nativo de la plataforma (Windows, macOS o Linux)
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception e) {

            // Registrar advertencia si el Look and Feel nativo no pudo cargarse
            System.err.println(
                    "No se pudo establecer el Look and Feel."
            );
        }
    }

    /**
     * Construye la fachada del sistema y ensambla sus dependencias (fábricas, DAOs y gestores).
     *
     * <p><b>Estado actual de desarrollo:</b>
     * Pendiente conectar la fábrica SQLite, los DAO y los cinco gestores del sistema.
     * Actualmente arroja una excepción para indicar que falta la infraestructura.</p>
     *
     * @return Instancia configurada de {@link ISistemaFacade}.
     * @throws UnsupportedOperationException Mientras la inicialización de los gestores esté pendiente.
     */
    private static ISistemaFacade inicializarSistemaFacade() {

        // Aquí se debe construir SistemaFacade utilizando sus cinco gestores:
        // GestorPersonas, GestorPrendas, GestorAlquiler, GestorLavanderia y GestorDevoluciones.

        // Lanzar excepción indicando que el módulo de persistencia y gestores aún no está conectado
        throw new UnsupportedOperationException(
                "La inicialización de los gestores está pendiente."
        );
    }
}
