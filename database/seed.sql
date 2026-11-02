-- Datos de demostración opcionales. Ejecutar una sola vez sobre una base vacía.
INSERT INTO PERSONA(identificacion,nombre,direccion,telefono,correo) VALUES ('101','Cliente Demo','Calle 1','3001234567','cliente@example.com');
INSERT INTO CLIENTE SELECT id_persona FROM PERSONA WHERE identificacion='101';
