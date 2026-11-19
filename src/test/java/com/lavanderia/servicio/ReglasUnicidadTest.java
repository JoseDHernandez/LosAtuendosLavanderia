package com.lavanderia.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lavanderia.conexion.DAOFactory;
import com.lavanderia.conexion.SQLiteDAOFactory;
import com.lavanderia.dao.interfaces.IClienteDAO;
import com.lavanderia.dao.interfaces.IEmpleadoDAO;
import com.lavanderia.dao.interfaces.IPersonaDAO;
import com.lavanderia.dao.interfaces.IPrendaDAO;
import com.lavanderia.patrones.factory.ClienteCreator;
import com.lavanderia.patrones.factory.EmpleadoCreator;
import com.lavanderia.patrones.factory.IPersonaFactory;
import com.lavanderia.patrones.factory.PersonaFactory;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.servicio.interfaces.IGestorPrendas;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.persona.ClienteValidator;
import com.lavanderia.validacion.persona.EmpleadoValidator;
import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.validacion.prenda.PrendaValidator;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReglasUnicidadTest {

    @TempDir
    Path directorioTemporal;

    private IGestorPersonas gestorPersonas;
    private IGestorPrendas gestorPrendas;

    @BeforeEach
    void prepararSistema() {
        DAOFactory persistencia =
                new SQLiteDAOFactory(directorioTemporal.resolve("reglas.db"));

        IPersonaDAO personaDAO = persistencia.getPersonaDAO();
        IClienteDAO clienteDAO = persistencia.getClienteDAO();
        IEmpleadoDAO empleadoDAO = persistencia.getEmpleadoDAO();
        IPrendaDAO prendaDAO = persistencia.getPrendaDAO();

        IPersonaValidator validadorCliente = new ClienteValidator();
        IPersonaValidator validadorEmpleado = new EmpleadoValidator();

        ClienteCreator creadorCliente =
                new ClienteCreator(validadorCliente);
        EmpleadoCreator creadorEmpleado =
                new EmpleadoCreator(validadorEmpleado);
        IPersonaFactory fabricaPersonas =
                new PersonaFactory(creadorCliente, creadorEmpleado);

        gestorPersonas = new GestorPersonas(
                fabricaPersonas,
                personaDAO,
                clienteDAO,
                empleadoDAO,
                validadorCliente,
                validadorEmpleado
        );

        gestorPrendas = new GestorPrendas(
                prendaDAO,
                new PrendaValidator()
        );
    }

    @Test
    void correoPuedeRepetirseEnClientes() {
        gestorPersonas.registrarCliente(
                datosCliente("10101", "Ana Perez", "correo@example.com")
        );
        gestorPersonas.registrarCliente(
                datosCliente("20202", "Luis Gomez", "correo@example.com")
        );

        assertEquals(2, gestorPersonas.listarClientes().size());
    }

    @Test
    void correoPuedeRepetirseEnEmpleados() {
        gestorPersonas.registrarEmpleado(
                datosEmpleado("30303", "Maria Diaz", "empleado@example.com")
        );
        gestorPersonas.registrarEmpleado(
                datosEmpleado("40404", "Pedro Rojas", "empleado@example.com")
        );

        assertEquals(2, gestorPersonas.listarEmpleados().size());
    }

    @Test
    void identificacionNoPuedeRepetirseEnClientes() {
        gestorPersonas.registrarCliente(
                datosCliente("50505", "Ana Perez", "ana@example.com")
        );

        assertThrows(
                ValidacionException.class,
                () -> gestorPersonas.registrarCliente(
                        datosCliente("50505", "Otra Persona", "otra@example.com")
                )
        );
    }

    @Test
    void identificacionNoPuedeRepetirseEnEmpleados() {
        gestorPersonas.registrarEmpleado(
                datosEmpleado("60606", "Maria Diaz", "maria@example.com")
        );

        assertThrows(
                ValidacionException.class,
                () -> gestorPersonas.registrarEmpleado(
                        datosEmpleado("60606", "Otro Empleado", "otro@example.com")
                )
        );
    }

    @Test
    void identificacionNoPuedeRepetirseEntreClienteYEmpleado() {
        gestorPersonas.registrarCliente(
                datosCliente("70707", "Ana Perez", "ana@example.com")
        );

        assertThrows(
                ValidacionException.class,
                () -> gestorPersonas.registrarEmpleado(
                        datosEmpleado("70707", "Maria Diaz", "maria@example.com")
                )
        );
    }

    @Test
    void referenciaDePrendaNoPuedeRepetirse() {
        gestorPrendas.registrarDisfraz(
                datosPrenda("DISF-001")
        );

        assertThrows(
                ValidacionException.class,
                () -> gestorPrendas.registrarDisfraz(
                        datosPrenda("DISF-001")
                )
        );
    }

    private Map<String, String> datosCliente(
            String identificacion,
            String nombre,
            String correo) {

        Map<String, String> datos = new LinkedHashMap<>();
        datos.put("identificacion", identificacion);
        datos.put("nombre", nombre);
        datos.put("direccion", "Calle Principal");
        datos.put("telefono", "3001234567");
        datos.put("correo", correo);
        return datos;
    }

    private Map<String, String> datosEmpleado(
            String identificacion,
            String nombre,
            String correo) {

        Map<String, String> datos = datosCliente(
                identificacion,
                nombre,
                correo
        );
        datos.put("cargo", "Asesor");
        return datos;
    }

    private Map<String, String> datosPrenda(String referencia) {
        Map<String, String> datos = new LinkedHashMap<>();
        datos.put("referencia", referencia);
        datos.put("color", "Rojo");
        datos.put("marca", "Marca Prueba");
        datos.put("talla", "M");
        datos.put("valorAlquiler", "10000");
        datos.put("estado", "DISPONIBLE");
        datos.put("nombre", "Pirata");
        return datos;
    }
}
