package com.lavanderia.database;

import com.lavanderia.conexion.SQLiteConnection;
import com.lavanderia.modelo.enums.EstadoPrenda;
import com.lavanderia.modelo.interfaces.IDisfraz;
import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.modelo.interfaces.IPersona;
import com.lavanderia.modelo.interfaces.IPrenda;
import com.lavanderia.modelo.interfaces.ITrajeCaballero;
import com.lavanderia.modelo.interfaces.IVestidoDama;
import com.lavanderia.patrones.builder.DisfrazBuilder;
import com.lavanderia.patrones.builder.TrajeBuilder;
import com.lavanderia.patrones.builder.VestidoBuilder;
import com.lavanderia.patrones.factory.ClienteCreator;
import com.lavanderia.patrones.factory.EmpleadoCreator;
import com.lavanderia.patrones.factory.PersonaFactory;
import com.lavanderia.validacion.persona.ClienteValidator;
import com.lavanderia.validacion.persona.EmpleadoValidator;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Carga datos de demostración en la base SQLite del sistema.
 *
 * <p>Las referencias, identificaciones y números de alquiler del seed son claves de negocio
 * estables. Por ello, las inserciones usan {@code INSERT OR IGNORE} y pueden ejecutarse varias
 * veces sin duplicar registros.</p>
 */
public final class DataSeeder {

    private final SQLiteConnection database;

    /**
     * Usa la base de datos predeterminada {@code database/database.db}.
     */
    public DataSeeder() {
        this(new SQLiteConnection());
    }

    /**
     * Usa una ruta SQLite específica, útil para pruebas o instalaciones alternativas.
     *
     * @param databaseFile archivo SQLite
     */
    public DataSeeder(Path databaseFile) {
        this(new SQLiteConnection(databaseFile));
    }

    private DataSeeder(SQLiteConnection database) {
        this.database = database;
    }

    /**
     * Inicializa el esquema y carga el conjunto inicial de personas, prendas, alquileres y
     * solicitudes de lavandería.
     *
     * @throws IllegalStateException si la base no puede inicializarse o poblarse
     */
    public void seedDatabase() {
        database.inicializar();

        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            connection.setAutoCommit(false);
            try {
                seedPeople(connection);
                seedGarments(connection);
                seedRentalAndLaundry(connection);
                connection.commit();
                System.out.println("Seed de Los Atuendos completado correctamente.");
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        } catch (SQLException exception) {
            System.err.println("Error al ejecutar el seed de Los Atuendos: "
                    + exception.getMessage());
            throw new IllegalStateException("No se pudo poblar la base de datos.", exception);
        }
    }

    private void seedPeople(Connection connection) throws SQLException {
        PersonaFactory factory = new PersonaFactory(
                new ClienteCreator(new ClienteValidator()),
                new EmpleadoCreator(new EmpleadoValidator()));

        List<IPersona> clients = List.of(
                factory.crearCliente(Map.of(
                        "identificacion", "10001",
                        "nombre", "Ana Torres",
                        "direccion", "Calle 10 #20-30",
                        "telefono", "3001112233",
                        "correo", "ana.torres@example.com")),
                factory.crearCliente(Map.of(
                        "identificacion", "10002",
                        "nombre", "Luis Perez",
                        "direccion", "Carrera 15 #40-12",
                        "telefono", "3002223344",
                        "correo", "luis.perez@example.com")));
        IEmpleado employee = factory.crearEmpleado(Map.of(
                "identificacion", "20001",
                "nombre", "Marta Gomez",
                "direccion", "Calle 50 #8-20",
                "telefono", "3003334455",
                "correo", "marta.gomez@example.com",
                "cargo", "Administradora"));

        for (IPersona client : clients) {
            insertPerson(connection, client);
            insertRole(connection, "CLIENTE", client);
        }
        insertPerson(connection, employee);
        insertRole(connection, "EMPLEADO", employee);
        System.out.println("Personas y roles iniciales insertados/verificados.");
    }

    private void seedGarments(Connection connection) throws SQLException {
        List<IPrenda> garments = List.of(
                new TrajeBuilder()
                        .conReferencia("TRAJE-001")
                        .conColor("Negro")
                        .conMarca("Los Atuendos")
                        .conTalla("M")
                        .conValorAlquiler(180000)
                        .conEstado(EstadoPrenda.ALQUILADA)
                        .conTipoTraje("Esmoquin")
                        .conTipoComplemento("Corbatin")
                        .build(),
                new TrajeBuilder()
                        .conReferencia("TRAJE-002")
                        .conColor("Azul marino")
                        .conMarca("Los Atuendos")
                        .conTalla("L")
                        .conValorAlquiler(150000)
                        .conEstado(EstadoPrenda.DISPONIBLE)
                        .conTipoTraje("Formal")
                        .conTipoComplemento("Corbata")
                        .build(),
                new VestidoBuilder()
                        .conReferencia("VESTIDO-001")
                        .conColor("Rojo")
                        .conMarca("Gala Atelier")
                        .conTalla("S")
                        .conValorAlquiler(220000)
                        .conEstado(EstadoPrenda.EN_LAVANDERIA)
                        .conPedreria(true)
                        .conLargo("Largo")
                        .conNumeroPiezas(2)
                        .build(),
                new VestidoBuilder()
                        .conReferencia("VESTIDO-002")
                        .conColor("Blanco")
                        .conMarca("Gala Atelier")
                        .conTalla("M")
                        .conValorAlquiler(260000)
                        .conEstado(EstadoPrenda.EN_LAVANDERIA)
                        .conPedreria(true)
                        .conLargo("Cola")
                        .conNumeroPiezas(1)
                        .build(),
                new DisfrazBuilder()
                        .conReferencia("DISFRAZ-001")
                        .conColor("Multicolor")
                        .conMarca("Los Atuendos")
                        .conTalla("M")
                        .conValorAlquiler(90000)
                        .conEstado(EstadoPrenda.DISPONIBLE)
                        .conNombre("Pirata")
                        .build());

        for (IPrenda garment : garments) {
            insertGarment(connection, garment);
        }
        System.out.println("Prendas iniciales insertadas/verificadas: " + garments.size() + ".");
    }

    private void seedRentalAndLaundry(Connection connection) throws SQLException {
        resetSeedRentalData(connection);

        long clientId = idByIdentification(connection, "10001");
        long secondClientId = idByIdentification(connection, "10002");
        long employeeId = idByIdentification(connection, "20001");
        long rentedGarmentId = idByReference(connection, "TRAJE-001");
        long secondRentedGarmentId = idByReference(connection, "TRAJE-002");
        long returnedGarmentId = idByReference(connection, "VESTIDO-001");
        long secondReturnedGarmentId = idByReference(connection, "VESTIDO-002");

        execute(connection,
                """
                INSERT OR IGNORE INTO ALQUILER
                    (numero_consecutivo, fecha_solicitud, fecha_inicio, fecha_fin_prevista,
                     id_cliente, id_empleado, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                9001, "2026-01-10", "2026-01-15", "2026-01-18",
                clientId, employeeId, "ACTIVO");
        addRentalDetail(connection, 9001, rentedGarmentId, "180000.00");

        execute(connection,
                """
                INSERT OR IGNORE INTO ALQUILER
                    (numero_consecutivo, fecha_solicitud, fecha_inicio, fecha_fin_prevista,
                     id_cliente, id_empleado, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                9005, "2026-01-11", "2026-01-16", "2026-01-19",
                secondClientId, employeeId, "ACTIVO");
        addRentalDetail(connection, 9005, secondRentedGarmentId, "150000.00");

        execute(connection,
                """
                INSERT OR IGNORE INTO ALQUILER
                    (numero_consecutivo, fecha_solicitud, fecha_inicio, fecha_fin_prevista,
                     id_cliente, id_empleado, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                9003, "2026-01-02", "2026-01-05", "2026-01-08",
                clientId, employeeId, "FINALIZADO");
        addRentalDetail(connection, 9003, returnedGarmentId, "220000.00");

        execute(connection,
                """
                INSERT OR IGNORE INTO ALQUILER
                    (numero_consecutivo, fecha_solicitud, fecha_inicio, fecha_fin_prevista,
                     id_cliente, id_empleado, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                9006, "2026-01-03", "2026-01-06", "2026-01-09",
                secondClientId, employeeId, "FINALIZADO");
        addRentalDetail(connection, 9006, secondReturnedGarmentId, "260000.00");

        long detailId = detailId(connection, 9003, returnedGarmentId);
        execute(connection,
                """
                INSERT OR IGNORE INTO DEVOLUCION
                    (id_detalle, fecha_devolucion, observaciones, estado_revision)
                VALUES (?, ?, ?, ?)
                """,
                detailId, "2026-01-08", "Requiere lavado delicado.", "PENDIENTE");
        long returnId = returnId(connection, detailId);

        insertPendingLaundryRequest(
                connection, returnId, returnedGarmentId, "2026-01-08", "DELICADA");

        long secondDetailId = detailId(connection, 9006, secondReturnedGarmentId);
        execute(connection,
                """
                INSERT OR IGNORE INTO DEVOLUCION
                    (id_detalle, fecha_devolucion, observaciones, estado_revision)
                VALUES (?, ?, ?, ?)
                """,
                secondDetailId, "2026-01-09", "Limpieza especial por pedreria.", "PENDIENTE");
        long secondReturnId = returnId(connection, secondDetailId);

        insertPendingLaundryRequest(
                connection, secondReturnId, secondReturnedGarmentId, "2026-01-09", "ESPECIAL");
        System.out.println("Alquileres, devolución y solicitudes de lavandería pendientes "
                + "insertados/verificados: " + countPendingLaundryRequests(connection) + ".");
    }

    /**
     * Elimina únicamente el conjunto de alquileres reservado para los datos de demostración.
     * Se respeta el orden de las claves foráneas para poder reparar una base ya utilizada sin
     * borrar alquileres reales ni requerir eliminar el archivo SQLite.
     */
    private static void resetSeedRentalData(Connection connection) throws SQLException {
        int[] seedRentalNumbers = {9001, 9002, 9003, 9004, 9005, 9006};
        String placeholders = String.join(",", Arrays.stream(seedRentalNumbers)
                .mapToObj(number -> "?")
                .toList());

        execute(connection,
                "DELETE FROM SOLICITUD_LAVANDERIA WHERE id_devolucion IN ("
                        + "SELECT v.id_devolucion FROM DEVOLUCION v "
                        + "JOIN DETALLE_ALQUILER d ON d.id_detalle = v.id_detalle "
                        + "JOIN ALQUILER a ON a.id_alquiler = d.id_alquiler "
                        + "WHERE a.numero_consecutivo IN (" + placeholders + "))",
                toObjects(seedRentalNumbers));
        execute(connection,
                "DELETE FROM DEVOLUCION WHERE id_detalle IN ("
                        + "SELECT d.id_detalle FROM DETALLE_ALQUILER d "
                        + "JOIN ALQUILER a ON a.id_alquiler = d.id_alquiler "
                        + "WHERE a.numero_consecutivo IN (" + placeholders + "))",
                toObjects(seedRentalNumbers));
        execute(connection,
                "DELETE FROM DETALLE_ALQUILER WHERE id_alquiler IN ("
                        + "SELECT id_alquiler FROM ALQUILER "
                        + "WHERE numero_consecutivo IN (" + placeholders + "))",
                toObjects(seedRentalNumbers));
        execute(connection,
                "DELETE FROM ALQUILER WHERE numero_consecutivo IN (" + placeholders + ")",
                toObjects(seedRentalNumbers));
    }

    private static Object[] toObjects(int[] values) {
        return Arrays.stream(values).boxed().toArray();
    }

    private void insertPendingLaundryRequest(
            Connection connection,
            long returnId,
            long garmentId,
            String receptionDate,
            String priority) throws SQLException {
        execute(connection,
                """
                INSERT OR IGNORE INTO SOLICITUD_LAVANDERIA
                    (id_devolucion, id_lote, fecha_recepcion, prioridad, estado)
                SELECT ?, NULL, ?, ?, 'PENDIENTE'
                WHERE EXISTS (
                    SELECT 1
                    FROM DEVOLUCION v
                    JOIN DETALLE_ALQUILER d ON d.id_detalle = v.id_detalle
                    JOIN PRENDA p ON p.id_prenda = d.id_prenda
                    WHERE v.id_devolucion = ? AND p.id_prenda = ?
                )
                """,
                returnId, receptionDate, priority, returnId, garmentId);
    }

    private static long countPendingLaundryRequests(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT COUNT(*)
                FROM SOLICITUD_LAVANDERIA
                WHERE estado = 'PENDIENTE' AND id_lote IS NULL
                """);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private void insertPerson(Connection connection, IPersona person) throws SQLException {
        execute(connection,
                """
                INSERT OR IGNORE INTO PERSONA
                    (identificacion, nombre, direccion, telefono, correo)
                VALUES (?, ?, ?, ?, ?)
                """,
                person.getIdentificacion(), person.getNombre(), person.getDireccion(),
                person.getTelefono(), person.getCorreo());
    }

    private void insertRole(Connection connection, String role, IPersona person) throws SQLException {
        if ("CLIENTE".equals(role)) {
            execute(connection,
                    "INSERT OR IGNORE INTO CLIENTE (id_persona) "
                            + "SELECT id_persona FROM PERSONA WHERE identificacion = ?",
                    person.getIdentificacion());
        } else {
            execute(connection,
                    "INSERT OR IGNORE INTO EMPLEADO (id_persona, cargo) "
                            + "SELECT id_persona, ? FROM PERSONA WHERE identificacion = ?",
                    ((IEmpleado) person).getCargo(), person.getIdentificacion());
        }
    }

    private void insertGarment(Connection connection, IPrenda garment) throws SQLException {
        execute(connection,
                """
                INSERT OR IGNORE INTO PRENDA
                    (referencia, color, marca, talla, valor_alquiler, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                garment.getReferencia(), garment.getColor(), garment.getMarca(),
                garment.getTalla(), garment.getValorAlquiler(), garment.getEstado().name());
        long id = idByReference(connection, garment.getReferencia());

        if (garment instanceof ITrajeCaballero suit) {
            execute(connection,
                    "INSERT OR IGNORE INTO TRAJE_CABALLERO "
                            + "(id_prenda, tipo_traje, tipo_complemento) VALUES (?, ?, ?)",
                    id, suit.getTipoTraje(), suit.getTipoComplemento());
        } else if (garment instanceof IVestidoDama dress) {
            execute(connection,
                    "INSERT OR IGNORE INTO VESTIDO_DAMA "
                            + "(id_prenda, tiene_pedreria, largo, numero_piezas) VALUES (?, ?, ?, ?)",
                    id, dress.isTienePedreria() ? 1 : 0, dress.getLargo(),
                    dress.getNumeroPiezas());
        } else if (garment instanceof IDisfraz costume) {
            execute(connection,
                    "INSERT OR IGNORE INTO DISFRAZ (id_prenda, nombre) VALUES (?, ?)",
                    id, costume.getNombre());
        }
    }

    private void addRentalDetail(Connection connection, int rentalNumber, long garmentId,
                                 String agreedValue) throws SQLException {
        long rentalId = idByRentalNumber(connection, rentalNumber);
        execute(connection,
                "INSERT OR IGNORE INTO DETALLE_ALQUILER "
                        + "(id_alquiler, id_prenda, valor_acordado) VALUES (?, ?, ?)",
                rentalId, garmentId, agreedValue);
    }

    private static void execute(Connection connection, String sql, Object... values)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.executeUpdate();
        }
    }

    private static long idByIdentification(Connection connection, String identification)
            throws SQLException {
        return queryId(connection, "SELECT id_persona FROM PERSONA WHERE identificacion = ?",
                identification);
    }

    private static long idByReference(Connection connection, String reference) throws SQLException {
        return queryId(connection, "SELECT id_prenda FROM PRENDA WHERE referencia = ?", reference);
    }

    private static long idByRentalNumber(Connection connection, int rentalNumber)
            throws SQLException {
        return queryId(connection, "SELECT id_alquiler FROM ALQUILER WHERE numero_consecutivo = ?",
                rentalNumber);
    }

    private static long detailId(Connection connection, int rentalNumber, long garmentId)
            throws SQLException {
        return queryId(connection,
                "SELECT id_detalle FROM DETALLE_ALQUILER "
                        + "WHERE id_alquiler = ? AND id_prenda = ?",
                idByRentalNumber(connection, rentalNumber), garmentId);
    }

    private static long returnId(Connection connection, long detailId) throws SQLException {
        return queryId(connection, "SELECT id_devolucion FROM DEVOLUCION WHERE id_detalle = ?",
                detailId);
    }

    private static long queryId(Connection connection, String sql, Object... values)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("No se encontró el registro del seed para: " + sql);
                }
                return result.getLong(1);
            }
        }
    }
}
