# Los Atuendos

Sistema de gestión para un negocio de alquiler de vestuario.

## Tecnologías

- Java 25
- Java Swing
- Maven
- SQLite
- JDBC

## Arquitectura

Aplicación modular organizada mediante interfaces,
separación de responsabilidades y patrones de diseño.

## Patrones de diseño

- Abstract Factory: Persistencia.
- Factory Method: Creación de personas.
- Builder: Construcción de prendas.
- Facade: Gestión de alquileres.
- Strategy: Prioridades de lavandería.

## Base de datos

SQLite local mediante JDBC.

## Ejecución

Ejecutar la clase com.lavanderia.Main.
## Aporte de persistencia

Ver [guía de integración](docs/LEEME_APORTE.md). Compilación y pruebas: `mvn clean test`. El arranque Swing aún requiere integrar los gestores en Main.
