# Aporte individual: Abstract Factory y SQLite

Este aporte implementa la familia SQLite de los siete contratos DAO del proyecto del grupo. No es la entrega completa del curso. Base: commit 337d24b481f02505857ebd26ebd807fdb179691f de https://github.com/JoseDHernandez/LosAtuendosLavanderia.

## Abrir y probar

1. Abrir esta carpeta como proyecto Maven en NetBeans.
2. Seleccionar un JDK 25 o posterior. El proyecto se compila para Java 25.
3. Ejecutar `mvn clean test`. Se verificaron seis pruebas de integración con SQLite real en archivos temporales, sin modificar datos del negocio.
4. La fábrica crea las tablas al instanciarse. La ruta predeterminada es `database/database.db`, relativa al directorio de ejecución. Se recomienda pasar una ruta absoluta.

```java
DAOFactory persistencia = new SQLiteDAOFactory(Path.of("database/database.db"));
IClienteDAO clientes = persistencia.getClienteDAO();
IEmpleadoDAO empleados = persistencia.getEmpleadoDAO();
IPersonaDAO personas = persistencia.getPersonaDAO();
// Inyectar estos contratos en el constructor de GestorPersonas.
// Crear de la misma forma los otros gestores; no instanciar DAO concretos allí.
```

Importaciones: `com.lavanderia.conexion.*`, `com.lavanderia.dao.interfaces.*`, `java.nio.file.Path`.

## Qué corresponde al patrón

- Fábrica abstracta: DAOFactory (contrato ya existente).
- Fábrica concreta: SQLiteDAOFactory.
- Productos abstractos: IPersonaDAO, IClienteDAO, IEmpleadoDAO, IPrendaDAO, IAlquilerDAO, IDevolucionDAO e ILavanderiaDAO.
- Productos concretos: las siete clases *DAOSQLite.
- Cliente del patrón: composición de la aplicación y servicios que reciben los contratos.
- SQLiteConnection administra aperturas; no es un Singleton ni la fábrica abstracta.

La familia reúne productos de persistencia compatibles con un mismo archivo y esquema. No hace falta inventar un segundo motor para demostrar la fábrica. Cambiar de motor requeriría implementar toda otra familia y adaptar su esquema y SQL.

## Decisiones de integración

Se conservaron las firmas reales del repositorio, que tienen más DAO que el diagrama conceptual. Los nombres *SQLote de los cascarones sin uso se reemplazaron por *SQLite. Se añadió PersonaDAOSQLite, que faltaba. Personas conservan el contrato sin id: los DAO resuelven sus claves internas por identificación. `consultarPorCliente(long)` recibe el id interno, no el documento; debe resolverse en el servicio cuando la entrada es identificación.

El esquema canónico está en `src/main/resources/database/schema.sql`, incluido en el JAR. `database/schema.sql` es una copia para consulta. El archivo original `shema.sql` estaba vacío y no se utiliza. La inicialización es para bases nuevas o compatibles; no es una herramienta de migración de bases anteriores.

Fechas ISO 8601. Rangos de alquiler inclusivos, conforme al modelo del equipo que añade fecha final. Valores de catálogo enteros como el modelo; valores acordados BigDecimal se guardan en TEXT para conservar decimales. Pendientes se recuperan por fecha e id; Strategy sigue siendo responsable de seleccionar según prioridad. Consultar no modifica estados. El envío confirma de forma atómica lote, solicitudes y estado de prendas.

## Ajustes necesarios para compilar

El pom original pedía Java 27 y se actualizaron las dependencias a versiones verificadas en este aporte. Se fijaron Java 25, sqlite-jdbc 3.50.3.0 e Ikonli 12.3.1; se agregaron JUnit y complementos Maven de compilación y pruebas. En ClienteCreator y EmpleadoCreator se añadió `.obtenerValor()` al resultado del validador, corrigiendo dos errores de tipos preexistentes. Estos cambios se incluyen para revisión del grupo.

## Alcance pendiente del grupo

`Main.inicializarSistemaFacade()` sigue lanzando UnsupportedOperationException en el código original. No se presenta esta entrega como aplicación Swing operativa. El equipo debe ensamblar la fachada y los gestores, verificar las reglas de negocio y probar los flujos completos desde la interfaz. La llegada a lavandería requiere una devolución según el modelo acordado; el enunciado permite ingresar por referencia, por lo que el servicio debe resolverla al detalle/devuelta correspondiente.

La fecha de solicitud usa LocalDate.now() del sistema. Las consultas vigentes aplican fecha de inicio >= hoy y estados REGISTRADO/ACTIVO. La API actualizarEstado guarda cambios; las transiciones válidas y la sincronización de estados en los demás casos corresponden al servicio. La finalización de lavandería requiere que el gestor actualice también la prenda a disponible; no se supone automática en este aporte.

## Evidencia

Seis pruebas, cero fallos y cero errores: familia/reapertura; personas y rollback; tres subtipos de prendas; consecutivo, consultas y conflicto; rechazo de referencias y rollback de detalles; devolución y despacho con rollback. No equivale a pruebas de interfaz ni de concurrencia entre procesos.

El PDF es una sección para unificar en el documento del grupo: añadir nombres reales, portada y nombre final AA4_Diseño_nombreDelGrupo.pdf. No se inventaron integrantes, retroalimentación ni datos de portada.

## Código comentado

La explicación general y el inventario de archivos están en [EXPLICACION_DEL_CODIGO.md](EXPLICACION_DEL_CODIGO.md). Los métodos de conexión y DAO incluyen comentarios de propósito y los bloques complejos explican sus decisiones.

## Javadoc verificado

Se completó Javadoc de conexión y DAO con parámetros, retornos y excepciones declaradas. Documentación generada: `docs/javadoc/index.html`. Verificado con JDK 25, `-private -Xdoclint:all -Werror`, para los paquetes conexion, dao.sqlite y dao.interfaces: sin advertencias ni errores. Esta comprobación no abarca todos los paquetes del equipo.
