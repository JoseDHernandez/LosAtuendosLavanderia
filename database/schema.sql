-- Datos comunes de personas. identificacion es el documento único del negocio.
CREATE TABLE IF NOT EXISTS PERSONA(id_persona INTEGER PRIMARY KEY AUTOINCREMENT, identificacion TEXT NOT NULL UNIQUE, nombre TEXT NOT NULL, direccion TEXT NOT NULL, telefono TEXT NOT NULL, correo TEXT NOT NULL);
-- Rol cliente: reutiliza el id de PERSONA y permite que ALQUILER exija un cliente registrado.
CREATE TABLE IF NOT EXISTS CLIENTE(id_persona INTEGER PRIMARY KEY REFERENCES PERSONA(id_persona));
-- Rol empleado: reutiliza el id de PERSONA y añade el cargo.
CREATE TABLE IF NOT EXISTS EMPLEADO(id_persona INTEGER PRIMARY KEY REFERENCES PERSONA(id_persona), cargo TEXT NOT NULL);
-- Inventario común. referencia es única y el id interno mantiene las relaciones.
CREATE TABLE IF NOT EXISTS PRENDA(id_prenda INTEGER PRIMARY KEY AUTOINCREMENT, referencia TEXT NOT NULL UNIQUE, color TEXT NOT NULL, marca TEXT NOT NULL, talla TEXT NOT NULL, valor_alquiler INTEGER NOT NULL CHECK(valor_alquiler>=0), estado TEXT NOT NULL CHECK(estado IN ('DISPONIBLE','ALQUILADA','EN_LAVANDERIA','EN_MANTENIMIENTO')));
-- Especialización de PRENDA: pedrería, largo y número de piezas.
CREATE TABLE IF NOT EXISTS VESTIDO_DAMA(id_prenda INTEGER PRIMARY KEY REFERENCES PRENDA(id_prenda), tiene_pedreria INTEGER NOT NULL CHECK(tiene_pedreria IN (0,1)), largo TEXT NOT NULL, numero_piezas INTEGER NOT NULL CHECK(numero_piezas>0));
-- Especialización de PRENDA: tipo de traje y complemento.
CREATE TABLE IF NOT EXISTS TRAJE_CABALLERO(id_prenda INTEGER PRIMARY KEY REFERENCES PRENDA(id_prenda), tipo_traje TEXT NOT NULL, tipo_complemento TEXT NOT NULL);
-- Especialización de PRENDA: nombre del disfraz.
CREATE TABLE IF NOT EXISTS DISFRAZ(id_prenda INTEGER PRIMARY KEY REFERENCES PRENDA(id_prenda), nombre TEXT NOT NULL);
-- Cabecera del servicio: consecutivo, fechas, cliente, empleado y estado.
CREATE TABLE IF NOT EXISTS ALQUILER(id_alquiler INTEGER PRIMARY KEY AUTOINCREMENT, numero_consecutivo INTEGER NOT NULL UNIQUE, fecha_solicitud TEXT NOT NULL, fecha_inicio TEXT NOT NULL, fecha_fin_prevista TEXT NOT NULL CHECK(fecha_fin_prevista>=fecha_inicio), id_cliente INTEGER NOT NULL REFERENCES CLIENTE(id_persona), id_empleado INTEGER NOT NULL REFERENCES EMPLEADO(id_persona), estado TEXT NOT NULL CHECK(estado IN ('REGISTRADO','ACTIVO','FINALIZADO','CANCELADO')));
-- Relaciona cada alquiler con sus prendas y guarda el valor pactado como texto decimal exacto.
CREATE TABLE IF NOT EXISTS DETALLE_ALQUILER(id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, id_alquiler INTEGER NOT NULL REFERENCES ALQUILER(id_alquiler), id_prenda INTEGER NOT NULL REFERENCES PRENDA(id_prenda), valor_acordado TEXT NOT NULL, UNIQUE(id_alquiler,id_prenda));
-- Una devolución por detalle. UNIQUE impide devolver dos veces la misma línea.
CREATE TABLE IF NOT EXISTS DEVOLUCION(id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT, id_detalle INTEGER NOT NULL UNIQUE REFERENCES DETALLE_ALQUILER(id_detalle), fecha_devolucion TEXT NOT NULL, observaciones TEXT NOT NULL, estado_revision TEXT NOT NULL CHECK(estado_revision IN ('PENDIENTE','SIN_NOVEDAD','CON_NOVEDAD')));
-- Agrupa las solicitudes enviadas en una tanda con fecha y cupo.
CREATE TABLE IF NOT EXISTS LOTE_LAVANDERIA(id_lote INTEGER PRIMARY KEY AUTOINCREMENT, fecha_envio TEXT NOT NULL, cupo INTEGER NOT NULL CHECK(cupo>0), estado TEXT NOT NULL CHECK(estado IN ('ABIERTO','ENVIADO','FINALIZADO','CANCELADO')));
-- Turno de lavado asociado a una devolución. id_lote es null antes del envío.
CREATE TABLE IF NOT EXISTS SOLICITUD_LAVANDERIA(id_solicitud INTEGER PRIMARY KEY AUTOINCREMENT, id_devolucion INTEGER NOT NULL UNIQUE REFERENCES DEVOLUCION(id_devolucion), id_lote INTEGER REFERENCES LOTE_LAVANDERIA(id_lote), fecha_recepcion TEXT NOT NULL, prioridad TEXT NOT NULL CHECK(prioridad IN ('NORMAL','DELICADA','MANCHADA','ESPECIAL')), estado TEXT NOT NULL CHECK(estado IN ('PENDIENTE','EN_LAVADO','LISTA','CANCELADA')));
CREATE INDEX IF NOT EXISTS ix_alquiler_fecha ON ALQUILER(fecha_inicio);
CREATE INDEX IF NOT EXISTS ix_solicitud_pendiente ON SOLICITUD_LAVANDERIA(estado,fecha_recepcion,id_solicitud);
