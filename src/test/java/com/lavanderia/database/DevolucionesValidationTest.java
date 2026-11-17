package com.lavanderia.database;

import com.lavanderia.conexion.SQLiteConnection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DevolucionesValidationTest {

    @Test
    void activeRentalsHaveValidReferencesForReturns() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         SELECT a.id_alquiler, a.id_cliente, d.id_prenda
                         FROM ALQUILER a
                         JOIN DETALLE_ALQUILER d ON d.id_alquiler = a.id_alquiler
                         WHERE a.estado = 'ACTIVO'
                         """);
                 ResultSet resultSet = statement.executeQuery()) {
                boolean foundActiveRental = false;
                while (resultSet.next()) {
                    foundActiveRental = true;
                    Object rentalId = resultSet.getObject("id_alquiler");
                    Object clientId = resultSet.getObject("id_cliente");
                    Object garmentId = resultSet.getObject("id_prenda");

                    assertNotNull(rentalId, "El ID del alquiler no puede ser nulo.");
                    assertNotNull(clientId, "El ID del cliente no puede ser nulo.");
                    assertNotNull(garmentId, "El ID de la prenda no puede ser nulo.");
                    assertTrue(((Number) rentalId).longValue() > 0);
                    assertTrue(((Number) clientId).longValue() > 0);
                    assertTrue(((Number) garmentId).longValue() > 0);
                }
                assertTrue(foundActiveRental, "Debe existir al menos un alquiler activo.");
            } catch (SQLException exception) {
                fail("No se pudieron validar los alquileres activos.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void nonexistentRentalDetailIsRejectedByForeignKey() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         INSERT INTO DEVOLUCION
                             (id_detalle, fecha_devolucion, observaciones, estado_revision)
                         VALUES (?, ?, ?, ?)
                         """)) {
                statement.setLong(1, 0);
                statement.setString(2, "2026-01-20");
                statement.setString(3, "Detalle inexistente");
                statement.setString(4, "PENDIENTE");
                assertThrows(SQLException.class, statement::executeUpdate,
                        "No debe registrarse una devolución para un detalle inexistente.");
            } catch (SQLException exception) {
                fail("No se pudo preparar la validación de la clave foránea.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void incompleteReturnFieldsAreRejectedBySchema() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new SQLiteConnection(databaseFile).getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         INSERT INTO DEVOLUCION
                             (id_detalle, fecha_devolucion, observaciones, estado_revision)
                         VALUES (?, ?, ?, ?)
                         """)) {
                statement.setLong(1, detailId(connection));
                statement.setObject(2, null);
                statement.setString(3, "Datos incompletos");
                statement.setObject(4, null);
                assertThrows(SQLException.class, statement::executeUpdate,
                        "La fecha y el estado de revisión son obligatorios.");
            } catch (SQLException exception) {
                fail("No se pudo validar el rechazo de campos incompletos.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    private static long detailId(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id_detalle FROM DETALLE_ALQUILER LIMIT 1");
             ResultSet resultSet = statement.executeQuery()) {
            assertTrue(resultSet.next(), "Debe existir un detalle de alquiler sembrado.");
            return resultSet.getLong(1);
        }
    }

    private static Path temporaryDatabase() {
        try {
            return Files.createTempFile("los-atuendos-devoluciones-", ".db");
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
