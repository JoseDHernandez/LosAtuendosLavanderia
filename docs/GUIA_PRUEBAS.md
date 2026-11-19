# Guía de pruebas

## Requisitos

- JDK 25, requerido por `pom.xml`.
- Maven disponible en la terminal (`mvn -version`).
- Para las pruebas de interfaz, un entorno de escritorio con Java Swing.

## Ejecutar las pruebas automatizadas

Abre PowerShell en la carpeta raíz del proyecto y ejecuta:

```powershell
mvn test
```

Para recompilar desde cero antes de probar:

```powershell
mvn clean test
```

Para ejecutar solamente las pruebas nuevas de unicidad:

```powershell
mvn -Dtest=ReglasUnicidadTest test
```

Maven informa el resultado al final. También genera un reporte por clase en
`target/surefire-reports/`. La suite comprobada en este proyecto terminó con
21 pruebas, 0 fallos y 0 errores; 6 corresponden a
`ReglasUnicidadTest`.

Las pruebas nuevas crean una base SQLite temporal con JUnit (`@TempDir`).
Las pruebas de integración existentes también utilizan archivos temporales.
Por eso, ejecutar `mvn test` no debería insertar datos en la base de trabajo.
Los mensajes del `DataSeeder` que aparecen en la consola durante la suite son
salida de `DataSeederTest`; esa prueba crea y elimina su propia base temporal.

## Pruebas manuales desde la aplicación

### Precaución con la base de datos

La aplicación iniciada desde `com.lavanderia.Main` usa por defecto
`database/database.db`. Al iniciar, `Main` también ejecuta `DataSeeder` y
puede agregar datos de demostración. Para hacer pruebas sin alterar los datos
que quieras conservar, trabaja en una copia desechable del proyecto o realiza
una copia de seguridad de `database/database.db` antes de abrir la aplicación.
No confundas el lanzamiento de `Main` con `mvn test`: son ejecuciones distintas.

### Abrir la aplicación

1. Abre el proyecto en VS Code o en tu IDE.
2. Selecciona un JDK 25.
3. Abre `src/main/java/com/lavanderia/Main.java` y ejecuta `Main`.
4. Comprueba que aparece la ventana principal y que puedes cambiar entre los
   módulos de Clientes, Empleados, Prendas, Alquileres y Devoluciones.

### Casos sencillos

Usa identificaciones y referencias que no existan ya en la base de datos de
prueba. Los valores propuestos son ejemplos; cámbialos si ya están registrados.

| Caso | Pasos | Resultado esperado |
|---|---|---|
| Registrar un cliente | En Clientes, registra identificación `901001`, nombre `Ana Prueba`, dirección `Calle 1`, teléfono `3001234567` y correo `prueba@example.com`. | Se guarda y aparece en la lista. |
| Repetir correo | Registra otro cliente con identificación `901002` y el mismo correo `prueba@example.com`. | Se guarda; el correo no es único. |
| Repetir identificación | Intenta registrar otro cliente con identificación `901001`. | Se rechaza porque la identificación ya existe. |
| Validar datos de persona | Intenta guardar un cliente con correo `correo-invalido` o teléfono `abc`. | Se muestran errores de validación y no se guarda. |
| Registrar empleados | Registra dos empleados con identificaciones distintas (`902001` y `902002`), el mismo correo y cargo `Asesor`. | Ambos se guardan; el correo puede repetirse. |
| Identificación compartida | Intenta registrar un empleado con identificación `901001`, usada por el cliente anterior. | Se rechaza: la identificación es única entre clientes y empleados. |
| Registrar una prenda | En Prendas, selecciona Disfraz y registra referencia `MAN-PRB-01`, color `Azul`, marca `Prueba`, talla `M`, valor `10000` y nombre `Pirata`. | Se guarda y aparece en el catálogo. Si el formulario no solicita estado, se usa `DISPONIBLE`. |
| Repetir referencia | Intenta registrar otro disfraz con referencia `MAN-PRB-01`. | Se rechaza porque la referencia ya está registrada. |
| Validar referencia | Intenta usar una referencia con espacios o de más de 20 caracteres. | Se rechaza por formato inválido. |
| Validar alquiler | Con un cliente, empleado y prenda disponibles, crea un alquiler y selecciona una fecha final posterior a la inicial. Luego prueba con fecha final igual o anterior a la inicial. | El rango correcto se acepta; un rango que no termina después del inicio se rechaza. |
| Registrar devolución | Para un alquiler existente, selecciona su detalle, indica fecha, observaciones y estado de revisión. | Se registra la devolución. Con `CON_NOVEDAD` se solicita lavandería; con `SIN_NOVEDAD` o `PENDIENTE` se cancela una solicitud previa asociada. |

Al finalizar, puedes volver a consultar las listas para confirmar altas y
rechazos. Si trabajaste en una copia desechable, cierra la aplicación antes de
descartar esa copia.

## Qué cubren las pruebas automatizadas nuevas

`ReglasUnicidadTest` utiliza los métodos reales de `GestorPersonas` y
`GestorPrendas`, conectados a los DAO SQLite:

- Correo repetido en clientes y en empleados.
- Identificación repetida dentro del mismo tipo y entre cliente y empleado.
- Referencia de prenda repetida.
