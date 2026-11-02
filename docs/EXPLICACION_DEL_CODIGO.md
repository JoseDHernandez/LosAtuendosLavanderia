# Cómo entender el código de Los Atuendos

Esta guía explica el proyecto completo a grandes rasgos y desarrolla con más detalle tu aporte: Abstract Factory, conexión SQLite y DAO. El código de tu aporte tiene comentarios por método y en las decisiones más importantes. Los demás módulos ya traían documentación del equipo. Al final hay un inventario de todos los archivos Java para ubicarlos.

## 1. Qué busca hacer la aplicación

La aplicación administra un negocio de alquiler de ropa. Maneja clientes, empleados, prendas, reservas, devoluciones y envíos de ropa a lavandería. SQLite conserva la información en un archivo local para que no desaparezca cuando se cierra el programa.

El proyecto separa responsabilidades. Una pantalla recoge información, un validador comprueba los datos, un gestor coordina una operación y un DAO ejecuta las consultas a la base. Las entidades representan la información que pasa entre esas partes.

Recorrido general previsto:

```text
Usuario → formulario Swing → gestor → interfaz DAO → DAO SQLite → archivo .db
Usuario ← pantalla          ← objetos Java         ← resultados SQL
```

Este es el diseño de colaboración. El arranque completo aún requiere conectar los gestores y la fachada en Main; las pruebas realizadas verifican directamente la persistencia, sin abrir las pantallas.

## 2. Qué hace cada carpeta

| Carpeta o archivo | Responsabilidad |
| --- | --- |
| `Main.java` | Punto de entrada: prepara el aspecto visual e intenta construir la fachada y las pantallas. Su método de ensamblaje sigue pendiente. |
| `modelo/interfaces` | Define qué información ofrece cada entidad, por ejemplo los datos de una persona o las fechas de un alquiler. |
| `modelo/persona` | Representa Persona, Cliente y Empleado. |
| `modelo/prenda` | Representa la prenda común y sus subtipos: vestido, traje y disfraz. |
| `modelo/alquiler` | Representa el servicio y cada prenda incluida en él. |
| `modelo/lavanderia` | Representa devolución, solicitud de lavado y lote de envío. |
| `modelo/enums` | Limita estados y prioridades a valores conocidos. |
| `validacion` | Comprueba entradas y devuelve datos válidos o errores por campo. |
| `patrones/factory` | Construye clientes y empleados mediante Factory Method. |
| `patrones/builder` | Construye los distintos tipos de prenda paso a paso. |
| `patrones/facade` | Agrupa el acceso a los gestores del sistema. |
| `patrones/strategy` | Permite elegir cómo seleccionar prendas para lavandería. |
| `servicio` | Coordina casos de uso, validación, construcción y consultas a DAO. |
| `dao/interfaces` | Define las operaciones disponibles de persistencia sin especificar cómo se hacen. |
| `dao/sqlite` | Implementa esas operaciones con SQL y transforma filas en objetos Java. |
| `conexion` | Configura la familia de DAO y abre las conexiones SQLite. |
| `vista` | Contiene ventanas, formularios, tablas, botones y estilos de Swing. |
| `src/main/resources/database/schema.sql` | Define las tablas que la aplicación crea al inicializar SQLite. |
| `database/schema.sql` | Copia del esquema para consultar o ejecutar manualmente. |
| `database/seed.sql` | Datos de ejemplo opcionales. No se ejecutan automáticamente. |
| `pom.xml` | Configura Java, bibliotecas, compilación y pruebas mediante Maven. |
| `src/test/java` | Pruebas automatizadas de la persistencia. |

## 3. Tu patrón: Abstract Factory

Piensa en la fábrica como el lugar donde la aplicación pide las herramientas para guardar y consultar datos. El servicio pide un contrato como IClienteDAO y recibe una herramienta que lo cumple, ClienteDAOSQLite.

Los participantes reales son:

| Papel del patrón | Código |
| --- | --- |
| Fábrica abstracta | `DAOFactory` |
| Fábrica concreta | `SQLiteDAOFactory` |
| Productos abstractos | Los siete contratos `I...DAO` |
| Productos concretos | Las siete clases `...DAOSQLite` |
| Consumidor | El ensamblaje que obtiene los DAO y los servicios que los reciben |

La familia incluye personas, clientes, empleados, prendas, alquileres, devoluciones y lavandería. Todos los productos creados por una fábrica usan un proveedor configurado con el mismo archivo SQLite.

```java
DAOFactory fabrica = new SQLiteDAOFactory(Path.of("database/database.db"));
IClienteDAO clientes = fabrica.getClienteDAO();
```

La primera línea selecciona SQLite en el lugar de composición. La segunda obtiene el DAO usando una interfaz. Después se puede llamar `clientes.guardar(cliente)` sin escribir SQL ni mencionar ClienteDAOSQLite desde el gestor.

Crear otra implementación de DAOFactory permitiría proporcionar otra familia, pero habría que implementar sus DAO, sus consultas y su esquema. Cambiar de motor no se consigue cambiando solamente el nombre de una clase.

## 4. Los archivos de conexión, uno por uno

**DAOFactory.java.** Es una interfaz: declara qué DAO puede proporcionar una fábrica. No contiene consultas SQL ni guarda clientes. Sus siete métodos establecen el contrato común.

**SQLiteDAOFactory.java.** Implementa DAOFactory. El constructor prepara SQLiteConnection e inicializa el esquema. Cada método `get...DAO()` construye el producto correspondiente y le entrega el proveedor de conexiones. Compartir el proveedor significa compartir configuración; cada operación abre su propia conexión.

**SQLiteConnection.java.** Guarda la ruta del archivo `.db`. `getConnection()` crea el directorio si hace falta, abre JDBC, activa las claves foráneas y configura una espera de cinco segundos ante bloqueos. `inicializar()` carga el esquema incluido en los recursos y crea las tablas que faltan. No elimina información existente ni transforma automáticamente una base antigua con otra estructura.

**PersistenciaException.java.** Permite comunicar un fallo de acceso a datos conservando la causa original. Por ejemplo, un documento duplicado produce un error SQL que se envuelve en esta excepción. La capa que atiende al usuario debe capturarla y presentar un mensaje adecuado; no significa que todos los errores queden resueltos automáticamente.

## 5. SQLiteDAO: las herramientas comunes

Esta clase abstracta evita repetir el manejo de conexiones y consultas en cada DAO. No es un producto adicional de DAOFactory ni representa una tabla.

| Método o elemento | Qué hace |
| --- | --- |
| `bd` | Referencia al proveedor de conexiones recibido por constructor. |
| `Trabajo<T>` | Contrato interno para una operación que usa una conexión y devuelve un resultado. |
| `Mapeador<T>` | Contrato interno para convertir una fila SQL en un objeto. |
| `ejecutar()` | Abre una conexión, ejecuta el trabajo, cierra recursos y convierte errores SQL a PersistenciaException. |
| `transaccion()` | Ejecuta el trabajo con confirmación manual: confirma si todo sale bien y revierte si falla. |
| `preparar()` | Crea una sentencia SQL y asigna por separado los valores de los marcadores `?`. |
| `cambiar()` | Ejecuta una escritura y devuelve cuántas filas afectó. |
| `insertar()` | Inserta y recupera el id generado en esa conexión. |
| `consultar()` | Recorre las filas del resultado y usa un mapeador para crear una lista de objetos. |
| `exigirUno()` | Falla cuando una actualización no afectó exactamente una fila. |

Ejemplo: guardar un cliente necesita insertar PERSONA y CLIENTE. Si solo se guardara PERSONA y fallara CLIENTE, quedarían datos incompletos. La transacción hace que ambas inserciones se confirmen juntas o se deshagan juntas.

## 6. Los siete DAO concretos

### PersonaDAOSQLite

Hace consultas comunes de personas: verifica si una identificación existe y busca a la persona por documento. La sobrecarga de `existeIdentificacion` excluye el documento actual durante una edición. Para buscar una persona intenta primero el rol cliente y después empleado. El registro de cada rol se delega a su DAO especializado.

### ClienteDAOSQLite y EmpleadoDAOSQLite

`guardar()` crea la fila común PERSONA y la fila del rol dentro de una transacción. Empleado añade el cargo. `mapear()` convierte una fila de la consulta en Cliente o Empleado. `listarTodos()` devuelve los registros ordenados; `buscarPorIdentificacion()` devuelve un resultado opcional; `actualizar()` modifica una persona existente y, para empleados, su cargo.

El documento de identidad y el id interno no son iguales. El primero lo conoce el usuario; el segundo identifica relaciones dentro de la base.

### PrendaDAOSQLite

Guarda los datos comunes en PRENDA y los específicos en VESTIDO_DAMA, TRAJE_CABALLERO o DISFRAZ. `especializar()` elige la tabla según la interfaz del objeto. `mapear()` inspecciona las columnas recuperadas y reconstruye el subtipo correcto. Los LEFT JOIN permiten consultar todos los tipos sin perder los que no tienen fila en las otras especializaciones.

Ofrece registro, comprobación de referencia, búsqueda por referencia, listado, filtro por talla y actualización. Al actualizar conserva el id base y reemplaza los datos de especialización dentro de una transacción. El filtro por talla devuelve objetos con su subtipo; la agrupación visual por tipo corresponde a la presentación.

### AlquilerDAOSQLite

Guarda un servicio completo: cabecera y detalles. Antes de calcular el consecutivo y comprobar reservas adquiere el bloqueo de escritura mediante una sentencia que no cambia filas. Comprueba el rango de fechas, obtiene el siguiente consecutivo y resuelve los documentos de cliente y empleado a sus claves internas.

Para cada detalle comprueba que la prenda exista, esté disponible y no tenga una reserva registrada o activa que se cruce con el período solicitado. Guarda el importe acordado sin convertir BigDecimal a double. Si falla una prenda, se revierte todo el servicio.

Después de guardar, consulta el servicio para devolverlo con sus identificadores generados. Las consultas permiten buscar por consecutivo, cliente, fecha y conflictos. La consulta de vigentes exige inicio desde hoy y estado registrado o activo. Los rangos son inclusivos: compartir un día también cuenta como cruce.

### DevolucionDAOSQLite

Guarda la devolución de un detalle de alquiler: fecha, observaciones y resultado de revisión. La restricción UNIQUE de id_detalle impide registrar dos devoluciones para la misma línea. Permite consultar por id de devolución o detalle, listar y actualizar. Reconstruye también el detalle y la prenda relacionados.

### LavanderiaDAOSQLite

Registra solicitudes pendientes asociadas a devoluciones. Consultar pendientes solo lee información y usa fecha e id como orden de llegada. La estrategia del servicio decide cuáles se envían según la prioridad.

`confirmarEnvio()` comprueba cantidad y cupo, crea un lote enviado, vincula cada solicitud, la marca en lavado y cambia el estado de las prendas a EN_LAVANDERIA. Todo ocurre en una transacción. Si una solicitud ya fue enviada, la condición del UPDATE no se cumple y se revierte la operación.

También permite buscar solicitudes por devolución, guardar estados y modificar fecha o prioridad de solicitudes aún pendientes. Marcar una solicitud como lista no actualiza por sí solo todos los datos relacionados: el servicio debe coordinar la disponibilidad de la prenda.

## 7. Cómo se relaciona tu código con los otros patrones

**Factory Method:** PersonaCreator declara crearPersona; ClienteCreator y EmpleadoCreator construyen el tipo correspondiente después de validar. Aquí se crean entidades del negocio.

**Builder:** PrendaBuilder reúne atributos comunes; VestidoBuilder, TrajeBuilder y DisfrazBuilder añaden atributos específicos. Los métodos `con...` permiten construir una prenda por pasos hasta llamar `build()`.

**Facade:** SistemaFacade reúne los cinco gestores y ofrece acceso a ellos mediante sus contratos. En esta versión funciona como un punto de acceso a servicios; no contiene toda la lógica de alquiler.

**Strategy:** IEstrategiaPrioridadLavanderia define la selección. EstrategiaFIFO usa llegada; EstrategiaPrioridad da preferencia por prioridad. GestorLavanderia utiliza la estrategia elegida y el DAO persiste el despacho.

Tu Abstract Factory construye las herramientas de persistencia. Factory Method y Builder construyen los objetos que esas herramientas guardan.

## 8. Modelo, validación, servicios y pantallas

**Modelo.** Persona agrupa identificación, nombre, dirección, teléfono y correo. Empleado añade cargo. Prenda reúne referencia, color, marca, talla, valor y estado. Sus subtipos añaden detalles propios. Alquiler tiene cliente, empleado, fechas, estado y una lista de DetalleAlquiler. Cada detalle vincula una prenda con el precio pactado. Devolucion registra el retorno; SolicitudLavanderia registra el turno de lavado; LoteLavanderia registra la tanda.

**Validación.** Los validadores revisan datos de su módulo. ResultadoValidacion contiene datos válidos o errores por campo. `obtenerValor()` entrega los datos o lanza ValidacionException. Las restricciones de SQLite vuelven a proteger integridad y unicidad, aunque ya se haya validado antes.

**Servicios.** GestorPersonas coordina clientes y empleados. GestorPrendas coordina creación y edición de prendas. GestorAlquiler consulta disponibilidad y registra servicios. GestorDevoluciones valida retornos y coordina solicitudes de lavado cuando corresponde. GestorLavanderia selecciona y confirma tandas. Reciben dependencias por constructor para poder trabajar con contratos.

**Vistas.** MainFrame organiza la ventana y la navegación. Los Panel... muestran módulos; los Formulario... recogen entradas. Los componentes comunes evitan repetir botones, tablas y campos. EstilosUI e IconosUI centralizan apariencia. Una pantalla debe pedir operaciones a los servicios, sin escribir SQL.

## 9. Palabras y símbolos que vas a encontrar

| Expresión | Significado práctico |
| --- | --- |
| `interface` | Contrato de métodos que una implementación debe cumplir. |
| `implements` | Indica que una clase cumple una interfaz. |
| `extends` | Herencia: reutiliza o amplía una clase o interfaz. |
| `private final` | Referencia privada que se asigna una vez; no vuelve inmutable todo el objeto referenciado. |
| `public / protected` | Acceso público / acceso para el paquete y las subclases, según las reglas de Java. |
| `static` | Elemento de la clase que no requiere una instancia para invocarlo. |
| `Optional<T>` | Puede contener un resultado o estar vacío. |
| `List<T>` | Colección de elementos del tipo T. |
| `<T>` | Tipo genérico: permite reutilizar una herramienta con distintos tipos. |
| `c -> ...` | Lambda: bloque que se entrega para ejecutarlo después usando la conexión c. |
| `Clase::mapear` | Referencia a un método que convertirá cada fila. |
| `try (...)` | Cierra automáticamente los recursos declarados cuando termina el bloque. |
| `SQLException` | Error informado por JDBC o por las comprobaciones de persistencia. |
| `commit / rollback` | Confirmar una transacción / deshacer sus cambios. |
| `JOIN` | Reúne datos de tablas relacionadas. |
| `PRIMARY KEY / FOREIGN KEY` | Identificador de fila / referencia a una fila de otra tabla. |
| `UNIQUE / NOT NULL / CHECK` | Evita duplicados / exige un valor / exige una condición. |
| `?` en SQL preparado | Posición donde se coloca un parámetro, separado del texto SQL. |

## 10. Pruebas y estado real

SQLiteDAOFactoryTest usa un directorio temporal diferente para cada prueba. Crea datos de ejemplo, ejecuta una operación y verifica resultados esperados. Se cubren familia y reapertura, personas y duplicados, subtipos de prendas, alquiler y conflictos, referencias inexistentes y reversión, y devolución y envío atómico.

Ejecutar `mvn clean test` recompila y prueba el proyecto. Estas pruebas no comprueban que todos los botones funcionen ni reemplazan las pruebas del sistema integrado. Main conserva pendiente el ensamblaje; el equipo debe resolverlo antes de entregar la aplicación como operativa.

## 11. Orden recomendado de lectura

1. DAOFactory: qué herramientas puede pedir el sistema.
2. SQLiteDAOFactory: qué implementaciones entrega.
3. SQLiteConnection: dónde y cómo se abren los datos.
4. SQLiteDAO: cómo se reutiliza la ejecución SQL.
5. ClienteDAOSQLite: el ejemplo más sencillo de registro transaccional.
6. PrendaDAOSQLite y AlquilerDAOSQLite: especializaciones y relaciones.
7. DevolucionDAOSQLite y LavanderiaDAOSQLite: retorno y despacho.
8. SQLiteDAOFactoryTest: evidencia ejecutable de los resultados esperados.
9. Gestores y vistas: cómo se utilizará la persistencia desde la aplicación.

## 12. Inventario de todos los archivos Java

Cada entrada resume la responsabilidad declarada en la documentación del archivo; no certifica que todo el flujo esté integrado.

### .

- **Main.java:** Clase principal y punto de entrada de la aplicación "Los Atuendos".

### conexion

- **DAOFactory.java:** Fábrica abstracta (patrón Abstract Factory) para la creación de DAOs (Data Access Objects).
- **PersistenciaException.java:** Error de infraestructura; conserva la causa para el registro técnico.
- **SQLiteConnection.java:** Proveedor sin conexión global compartida.
- **SQLiteDAOFactory.java:** Abstract Factory concreta: entrega una familia de siete DAO sobre el mismo archivo SQLite.

### dao/interfaces

- **IAlquilerDAO.java:** Contrato de acceso a datos (DAO) para la entidad Alquiler.
- **IClienteDAO.java:** Contrato de acceso a datos (DAO) para la persistencia de clientes.
- **IDevolucionDAO.java:** Contrato de acceso a datos (DAO) para el registro de devoluciones de prendas.
- **IEmpleadoDAO.java:** Contrato de acceso a datos (DAO) para la gestión de empleados del negocio.
- **ILavanderiaDAO.java:** Contrato de acceso a datos (DAO) para el flujo de lavandería industrial/externa.
- **IPersonaDAO.java:** Contrato de acceso a datos (DAO) para validaciones y consultas transversales de personas.
- **IPrendaDAO.java:** Contrato de acceso a datos (DAO) para el catálogo e inventario de prendas.

### dao/sqlite

- **AlquilerDAOSQLite.java:** Alquiler y detalles en una transacción, con bloqueo previo a comprobar disponibilidad.
- **ClienteDAOSQLite.java:** Persistencia transaccional de persona y rol cliente.
- **DevolucionDAOSQLite.java:** Persistencia de devolución con unicidad por detalle.
- **EmpleadoDAOSQLite.java:** Persistencia transaccional de persona y rol empleado.
- **LavanderiaDAOSQLite.java:** El despacho confirma lote, solicitudes y prendas en una única transacción.
- **PersonaDAOSQLite.java:** Consultas transversales sin duplicar la persistencia de cada rol.
- **PrendaDAOSQLite.java:** Catálogo con tabla base y especialización; inserciones y cambios atómicos.
- **SQLiteDAO.java:** Utilidades JDBC internas: parámetros, cierre de recursos y transacciones.

### modelo/alquiler

- **Alquiler.java:** Entidad principal que modela un contrato o servicio de Alquiler de vestuario.
- **DetalleAlquiler.java:** Entidad concreta que representa un elemento o línea de detalle dentro de un contrato de alquiler.

### modelo/enums

- **EstadoAlquiler.java:** Representa los estados del ciclo de vida de un alquiler de prendas.
- **EstadoLote.java:** Representa los estados del ciclo de vida de un lote de lavandería industrial.
- **EstadoPrenda.java:** Representa los posibles estados de disponibilidad y ubicación de una prenda.
- **EstadoRevision.java:** Representa el resultado del peritaje o revisión técnica de una prenda devuelta.
- **EstadoSolicitud.java:** Representa el estado de una solicitud individual de lavado para una prenda devuelta.
- **Prioridad.java:** Nivel de urgencia y tratamiento asignado a una solicitud de lavado.

### modelo/interfaces

- **IAlquiler.java:** Contrato que define el comportamiento y estructura de un contrato de Alquiler.
- **ICliente.java:** Contrato que identifica a un cliente dentro del dominio del sistema.
- **IDetalleAlquiler.java:** Contrato que modela una línea de detalle dentro de un contrato de alquiler.
- **IDevolucion.java:** Contrato que representa el registro de retorno físico de una prenda alquilada.
- **IDisfraz.java:** Contrato especializado para prendas de tipo Disfraz.
- **IEmpleado.java:** Contrato que representa a un empleado del negocio en el modelo de dominio.
- **ILoteLavanderia.java:** Contrato que representa un lote o tanda de prendas agrupadas para su despacho a lavandería.
- **IPersona.java:** Contrato base para cualquier persona en el sistema (clientes y empleados).
- **IPrenda.java:** Contrato general para cualquier prenda gestionada en el inventario de vestuario.
- **ISolicitudLavanderia.java:** Contrato para solicitudes individuales de lavado generadas a partir de una devolución con novedad.
- **ITrajeCaballero.java:** Contrato para trajes de caballero dentro del catálogo de vestuario.
- **IVestidoDama.java:** Contrato para vestidos de dama dentro del catálogo de vestuario.

### modelo/lavanderia

- **Devolucion.java:** Entidad concreta que modela la Devolución de una prenda previamente alquilada.
- **LoteLavanderia.java:** Entidad concreta que representa un Lote de Lavandería (tanda de prendas agrupadas).
- **SolicitudLavanderia.java:** Entidad que modela una Solicitud individual de Lavandería para una prenda retornada con novedades.

### modelo/persona

- **Cliente.java:** Entidad de dominio que representa a un Cliente en el sistema de alquiler de vestuario.
- **Empleado.java:** Entidad de dominio que representa a un Empleado dentro de la organización.
- **Persona.java:** Clase base abstracta que representa a una persona en el sistema.

### modelo/prenda

- **Disfraz.java:** Entidad concreta que representa un Disfraz en el catálogo de prendas.
- **Prenda.java:** Clase abstracta que define las propiedades y comportamientos comunes a todas las prendas.
- **TrajeCaballero.java:** Entidad concreta que representa un Traje de Caballero en el inventario.
- **VestidoDama.java:** Entidad concreta que representa un Vestido de Dama en el inventario.

### patrones/builder

- **DisfrazBuilder.java:** Constructor concreto (Builder) para instanciar objetos Disfraz.
- **IPrendaBuilder.java:** Contrato base para el patrón de diseño Builder aplicado a la construcción de prendas.
- **PrendaBuilder.java:** Implementación abstracta base del patrón Builder para entidades derivadas de Prenda.
- **TrajeBuilder.java:** Constructor concreto (Builder) para instanciar objetos TrajeCaballero.
- **VestidoBuilder.java:** Constructor concreto (Builder) para instanciar objetos VestidoDama.

### patrones/facade

- **ISistemaFacade.java:** Contrato de la Fachada del Sistema (patrón Facade).
- **SistemaFacade.java:** Implementación de la Fachada del Sistema (patrón Facade).

### patrones/factory

- **ClienteCreator.java:** Creador concreto (ConcreteCreator) para clientes dentro del patrón Factory Method.
- **EmpleadoCreator.java:** Creador concreto (ConcreteCreator) para empleados dentro del patrón Factory Method.
- **IPersonaFactory.java:** Contrato de fábrica para la instanciación de personas (patrón Abstract Factory / Factory Method).
- **PersonaCreator.java:** Creador abstracto (Creator) del patrón Factory Method para la instanciación de personas.
- **PersonaFactory.java:** Fábrica concreta que implementa IPersonaFactory.

### patrones/strategy

- **EstrategiaFIFO.java:** Estrategia concreta que despacha solicitudes de lavandería siguiendo el principio FIFO (First In, First Out / Primero en llegar, primero en ser atendido).
- **EstrategiaPrioridad.java:** Estrategia concreta que despacha solicitudes de lavandería considerando el nivel de urgencia/prioridad.
- **IEstrategiaPrioridadLavanderia.java:** Contrato de Estrategia (patrón Strategy) para la priorización y selección de prendas en lavandería.

### servicio

- **GestorAlquiler.java:** Servicio de lógica de negocio para la gestión de contratos de Alquiler.
- **GestorDevoluciones.java:** Servicio de lógica de negocio para la gestión de devoluciones y peritaje de vestuario.
- **GestorLavanderia.java:** Servicio de lógica de negocio para la gestión de lavandería de prendas.
- **GestorPersonas.java:** Servicio de lógica de negocio para la gestión integral de Personas (Clientes y Empleados).
- **GestorPrendas.java:** Servicio de lógica de negocio para la administración del catálogo e inventario de Prendas.

### servicio/interfaces

- **IGestorAlquiler.java:** Contrato de servicio para la gestión de contratos de alquiler de prendas.
- **IGestorDevoluciones.java:** Contrato de servicio para la gestión de devoluciones e inspecciones de prendas alquiladas.
- **IGestorLavanderia.java:** Contrato de servicio para el control de lavandería de prendas del negocio.
- **IGestorPersonas.java:** Contrato de servicio para la administración de personas (clientes y empleados).
- **IGestorPrendas.java:** Contrato de servicio para la gestión del inventario y catálogo de prendas.

### validacion

- **ResultadoValidacion.java:** Contenedor inmutable que encapsula el resultado de un proceso de validación.
- **ValidacionException.java:** Excepción personalizada no verificada (unchecked) lanzada cuando ocurren fallos en las reglas de validación.

### validacion/alquiler

- **AlquilerValidator.java:** Validador concreto de las solicitudes de alquiler de vestuario.
- **IAlquilerValidator.java:** Contrato para la validación de datos de entrada en la solicitud de alquileres.

### validacion/devolucion

- **DevolucionValidator.java:** Validador concreto para objetos de devolución de prendas.
- **IDevolucionValidator.java:** Contrato de validador para inspecciones y devoluciones de prendas.

### validacion/lavanderia

- **ISolicitudLavanderiaValidator.java:** Contrato de validador para solicitudes y tandas de lavandería.
- **SolicitudLavanderiaValidator.java:** Validador concreto para las operaciones y cupos del flujo de lavandería industrial.

### validacion/persona

- **ClienteValidator.java:** Validador especializado para la entidad Cliente.
- **EmpleadoValidator.java:** Validador especializado para la entidad Empleado.
- **IPersonaValidator.java:** Contrato de validador para datos personales y de contacto en el sistema.
- **PersonaValidator.java:** Validador base para atributos personales comunes a clientes y empleados.

### validacion/prenda

- **IPrendaValidator.java:** Contrato de validador para prendas de vestir según su tipología específica.
- **PrendaValidator.java:** Validador concreto polimórfico para prendas de vestir (vestidos, trajes y disfraces).

### vista

- **MainFrame.java:** Ventana principal (JFrame) de la aplicación de escritorio "Los Atuendos".

### vista/componentes

- **BotonNavegacion.java:** Componente de botón personalizado para las opciones del menú lateral de navegación.
- **BotonPrincipal.java:** Botón con el estilo destinado a la acción principal.
- **BotonSecundario.java:** Botón con el estilo de acciones secundarias.
- **CampoBusqueda.java:** Componente reutilizable para ingresar criterios de búsqueda.
- **CampoTexto.java:** Componente reutilizable para ingresar texto.
- **DialogoConfirmacion.java:** Componente visual para solicitar confirmación de una acción.
- **EtiquetaEstado.java:** Representa visualmente un estado de la aplicación.
- **SelectorFecha.java:** Componente para recoger o seleccionar fechas desde la interfaz.
- **TablaDatos.java:** Componente reutilizable para mostrar datos en una tabla.

### vista/estilos

- **EstilosUI.java:** Constantes de diseño y utilidades de estilo visual para la interfaz gráfica Swing.
- **IconosUI.java:** Catálogo y fábrica centralizada de iconos vectoriales para la interfaz Swing.

### vista/interfaces

- **IModuloVista.java:** Contrato que define un módulo o pantalla principal navegable dentro de la interfaz gráfica Swing.

### vista/paneles

- **PanelInicio.java:** Muestra el módulo inicial de la aplicación.

### vista/paneles/alquileres

- **FormularioAlquileres.java:** Recoge los datos del formulario de alquileres y coordina las acciones de esa pantalla.
- **PanelAlquileres.java:** Presenta el módulo de alquileres y sus acciones en la interfaz Swing.

### vista/paneles/clientes

- **FormularioClientes.java:** Recoge los datos del formulario de clientes y coordina las acciones de esa pantalla.
- **PanelClientes.java:** Presenta el módulo de clientes y sus acciones en la interfaz Swing.

### vista/paneles/comunes

- **PanelEncabezado.java:** Presenta el encabezado visual de un módulo.
- **PanelFormulario.java:** Contenedor común para organizar campos y acciones de un formulario.
- **PanelModulo.java:** Constructor para módulos sin botón de acción en el encabezado.
- **PanelTabla.java:** Contenedor visual común para presentar datos tabulares.

### vista/paneles/devoluciones

- **FormularioDevolucion.java:** Recoge los datos del formulario de devolucion y coordina las acciones de esa pantalla.
- **PanelDevoluciones.java:** Presenta el módulo de devoluciones y sus acciones en la interfaz Swing.

### vista/paneles/empleados

- **FormularioEmpleados.java:** Recoge los datos del formulario de empleados y coordina las acciones de esa pantalla.
- **PanelEmpleados.java:** Presenta el módulo de empleados y sus acciones en la interfaz Swing.

### vista/paneles/lavanderia

- **PanelLavanderia.java:** Presenta el módulo de lavanderia y sus acciones en la interfaz Swing.

### vista/paneles/prendas

- **FormularioPrendas.java:** Recoge los datos del formulario de prendas y coordina las acciones de esa pantalla.
- **PanelPrendas.java:** Presenta el módulo de prendas y sus acciones en la interfaz Swing.
