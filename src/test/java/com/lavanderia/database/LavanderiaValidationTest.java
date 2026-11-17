package com.lavanderia.database;

import com.lavanderia.conexion.SQLiteConnection;
import com.lavanderia.dao.sqlite.DevolucionDAOSQLite;
import com.lavanderia.modelo.lavanderia.SolicitudLavanderia;
import com.lavanderia.patrones.strategy.EstrategiaPrioridad;
import com.lavanderia.validacion.ValidacionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class LavanderiaValidationTest {

    private static final Set<String> VALID_PRIORITIES =
            Set.of("NORMAL", "DELICADA", "MANCHADA", "ESPECIAL");

    @Test
    void pendingLaundryRequestsHaveValidPriorityAndState() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         SELECT prioridad, estado
                         FROM SOLICITUD_LAVANDERIA
                         WHERE estado = 'PENDIENTE'
                         """);
                 ResultSet resultSet = statement.executeQuery()) {
                boolean foundPendingRequest = false;
                while (resultSet.next()) {
                    foundPendingRequest = true;
                    String priority = resultSet.getString("prioridad");
                    String state = resultSet.getString("estado");

                    assertNotNull(priority, "La prioridad no puede ser nula.");
                    assertNotNull(state, "El estado no puede ser nulo.");
                    assertTrue(VALID_PRIORITIES.contains(priority),
                            "La prioridad debe cumplir las reglas del esquema.");
                    assertTrue("PENDIENTE".equals(state),
                            "La solicitud debe estar pendiente antes de procesarse.");
                }
                assertTrue(foundPendingRequest,
                        "Debe existir al menos una solicitud de lavandería pendiente.");
            } catch (SQLException exception) {
                fail("No se pudieron validar las solicitudes de lavandería.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void invalidGarmentStateIsRejectedBySchema() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         INSERT INTO PRENDA
                             (referencia, color, marca, talla, valor_alquiler, estado)
                         VALUES (?, ?, ?, ?, ?, ?)
                         """)) {
                statement.setString(1, "INVALIDA-001");
                statement.setString(2, "Negro");
                statement.setString(3, "Prueba");
                statement.setString(4, "M");
                statement.setLong(5, 1);
                statement.setString(6, "NO_EXISTE");
                assertThrows(SQLException.class, statement::executeUpdate,
                        "Un estado de prenda no soportado debe ser rechazado.");
            } catch (SQLException exception) {
                fail("No se pudo validar el estado inválido de la prenda.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void invalidOrNullPriorityIsHandledWithoutStrategyCrash() {
        assertTrue(new EstrategiaPrioridad()
                .seleccionarPrendas(null, 1)
                .isEmpty(),
                "Una lista nula de pendientes debe producir una lista vacía.");

        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();
            var returnRecord = new DevolucionDAOSQLite(new SQLiteConnection(databaseFile))
                    .buscarPorId(1)
                    .orElseThrow();

            assertThrows(ValidacionException.class,
                    () -> new SolicitudLavanderia(
                            returnRecord,
                            null,
                            LocalDate.of(2026, 1, 20)),
                    "Una prioridad nula debe rechazarse antes de procesar la solicitud.");
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void invalidPriorityIsRejectedBySchema() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         UPDATE SOLICITUD_LAVANDERIA
                         SET prioridad = ?
                         WHERE estado = 'PENDIENTE'
                         """)) {
                statement.setString(1, "ALTA");
                assertThrows(SQLException.class, statement::executeUpdate,
                        "La prioridad no definida por el esquema debe ser rechazada.");
            } catch (SQLException exception) {
                fail("No se pudo validar la prioridad inválida.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    private static Path temporaryDatabase() {
        try {
            return Files.createTempFile("los-atuendos-lavanderia-", ".db");
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear la base temporal.", exception);
        }
    }

    private static void deleteDatabase(Path databaseFile) {
        try {
            Files.deleteIfExists(databaseFile);
        } catch (IOException exception) {
            fail("No se pudo eliminar la base temporal.", exception);
        }
    }
}
