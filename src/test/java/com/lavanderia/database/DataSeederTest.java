package com.lavanderia.database;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DataSeederTest {

    @Test
    void testSeedInsertsDataCorrectly() {
        Path databaseFile = temporaryDatabase();
        try {
            new DataSeeder(databaseFile).seedDatabase();

            try (Connection connection = new com.lavanderia.conexion.SQLiteConnection(databaseFile)
                    .getConnection()) {
                assertTableContainsData(connection, "PERSONA");
                assertTableContainsData(connection, "PRENDA");
            } catch (SQLException exception) {
                fail("La prueba del seed falló debido a una excepción SQL.", exception);
            }
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    @Test
    void testSeedIsIdempotent() {
        Path databaseFile = temporaryDatabase();
        try {
            DataSeeder seeder = new DataSeeder(databaseFile);

            assertDoesNotThrow(() -> {
                seeder.seedDatabase();
                seeder.seedDatabase();
            }, "El seed debe ser idempotente.");
        } finally {
            deleteDatabase(databaseFile);
        }
    }

    private static void assertTableContainsData(Connection connection, String tableName)
            throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            assertTrue(resultSet.next(), "La consulta de " + tableName + " debe retornar un resultado.");
            assertTrue(resultSet.getInt(1) > 0,
                    "La tabla " + tableName + " debe contener registros.");
        }
    }

    private static Path temporaryDatabase() {
        try {
            return Files.createTempFile("los-atuendos-seed-", ".db");
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("No se pudo crear la base temporal de prueba.", exception);
        }
    }

    private static void deleteDatabase(Path databaseFile) {
        try {
            Files.deleteIfExists(databaseFile);
        } catch (java.io.IOException exception) {
            fail("No se pudo eliminar la base temporal de prueba.", exception);
        }
    }
}
