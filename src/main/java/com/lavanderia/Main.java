
package com.lavanderia;

import com.lavanderia.conexion.DAOFactory;
import com.lavanderia.conexion.SQLiteDAOFactory;

import com.lavanderia.dao.interfaces.*;
import com.lavanderia.modelo.interfaces.*;

import com.lavanderia.patrones.facade.ISistemaFacade;
import com.lavanderia.patrones.facade.SistemaFacade;

import com.lavanderia.patrones.factory.IPersonaFactory;
import com.lavanderia.patrones.factory.PersonaFactory;
import com.lavanderia.patrones.factory.ClienteCreator;
import com.lavanderia.patrones.factory.EmpleadoCreator;

import com.lavanderia.patrones.strategy.IEstrategiaPrioridadLavanderia;
import com.lavanderia.patrones.strategy.EstrategiaPrioridad;

import com.lavanderia.servicio.*;
import com.lavanderia.servicio.interfaces.*;

import com.lavanderia.validacion.persona.ClienteValidator;
import com.lavanderia.validacion.persona.EmpleadoValidator;
import com.lavanderia.validacion.persona.IPersonaValidator;

import com.lavanderia.validacion.prenda.IPrendaValidator;
import com.lavanderia.validacion.prenda.PrendaValidator;

import com.lavanderia.validacion.alquiler.IAlquilerValidator;
import com.lavanderia.validacion.alquiler.AlquilerValidator;

import com.lavanderia.validacion.devolucion.IDevolucionValidator;
import com.lavanderia.validacion.devolucion.DevolucionValidator;

import com.lavanderia.validacion.lavanderia.ISolicitudLavanderiaValidator;
import com.lavanderia.validacion.lavanderia.SolicitudLavanderiaValidator;

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

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            configurarLookAndFeel();

            try {

                // 1. Inicializar el sistema
                ISistemaFacade sistemaFacade =
                        inicializarSistemaFacade();

                // 2. Validadores especializados para las vistas
                IPersonaValidator clienteValidator =
                        new ClienteValidator();

                IPersonaValidator empleadoValidator =
                        new EmpleadoValidator();

                IPrendaValidator prendaValidator =
                        new PrendaValidator();

                IAlquilerValidator alquilerValidator =
                        new AlquilerValidator();

                // 3. Registrar módulos
                List<IModuloVista> modulos = List.of(

                        new PanelInicio(sistemaFacade),

                        new PanelClientes(
                                sistemaFacade,
                                clienteValidator
                        ),

                        new PanelEmpleados(
                                sistemaFacade,
                                empleadoValidator
                        ),

                        new PanelPrendas(
                                sistemaFacade,
                                prendaValidator
                        ),

                        new PanelAlquileres(
                                sistemaFacade,
                                alquilerValidator
                        ),

                        new PanelDevoluciones(
                                sistemaFacade
                        ),

                        new PanelLavanderia(
                                sistemaFacade
                        )
                );

                // 4. Crear ventana principal
                MainFrame ventana = new MainFrame(
                        modulos,
                        "inicio"
                );

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

    private static ISistemaFacade inicializarSistemaFacade() {

        // 1. Fábrica abstracta de persistencia
        DAOFactory daoFactory = new SQLiteDAOFactory();

        // 2. Obtener los DAO
        IPersonaDAO personaDAO = daoFactory.getPersonaDAO();
        IClienteDAO clienteDAO = daoFactory.getClienteDAO();
        IEmpleadoDAO empleadoDAO = daoFactory.getEmpleadoDAO();
        IPrendaDAO prendaDAO = daoFactory.getPrendaDAO();
        IAlquilerDAO alquilerDAO = daoFactory.getAlquilerDAO();
        IDevolucionDAO devolucionDAO = daoFactory.getDevolucionDAO();
        ILavanderiaDAO lavanderiaDAO = daoFactory.getLavanderiaDAO();

        // 3. Crear validadores especializados
        IPersonaValidator clienteValidator =
                new ClienteValidator();

        IPersonaValidator empleadoValidator =
                new EmpleadoValidator();

        IPrendaValidator prendaValidator =
                new PrendaValidator();

        IAlquilerValidator alquilerValidator =
                new AlquilerValidator();

        IDevolucionValidator devolucionValidator =
                new DevolucionValidator();

        ISolicitudLavanderiaValidator lavanderiaValidator =
                new SolicitudLavanderiaValidator();

        // 4. Factory Method: creación de personas
        ClienteCreator clienteCreator =
                new ClienteCreator(clienteValidator);

        EmpleadoCreator empleadoCreator =
                new EmpleadoCreator(empleadoValidator);

        IPersonaFactory personaFactory =
                new PersonaFactory(
                        clienteCreator,
                        empleadoCreator
                );

        // 5. Gestor de personas
        IGestorPersonas gestorPersonas =
                new GestorPersonas(
                        personaFactory,
                        personaDAO,
                        clienteDAO,
                        empleadoDAO,
                        clienteValidator,
                        empleadoValidator
                );

        // 6. Gestor de prendas
        IGestorPrendas gestorPrendas =
                new GestorPrendas(
                        prendaDAO,
                        prendaValidator
                );

        // 7. Gestor de alquileres
        IGestorAlquiler gestorAlquiler =
                new GestorAlquiler(
                        alquilerDAO,
                        clienteDAO,
                        empleadoDAO,
                        prendaDAO,
                        alquilerValidator
                );

        // 8. Gestor de lavandería
        IEstrategiaPrioridadLavanderia estrategia =
                new EstrategiaPrioridad();

        IGestorLavanderia gestorLavanderia =
                new GestorLavanderia(
                        lavanderiaDAO,
                        lavanderiaValidator,
                        estrategia
                );

        // 9. Gestor de devoluciones
        IGestorDevoluciones gestorDevoluciones =
                new GestorDevoluciones(
                        devolucionDAO,
                        devolucionValidator,
                        gestorLavanderia
                );

        // 10. Construir la fachada
        return new SistemaFacade(
                gestorPersonas,
                gestorPrendas,
                gestorAlquiler,
                gestorLavanderia,
                gestorDevoluciones
        );
    }
}
