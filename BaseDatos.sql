-- =====================================================
-- Script de Base de Datos - Sistema Bancario
-- =====================================================
-- Base de datos: banking_db
-- Descripción: Script completo para crear la base de datos,
--              tablas y datos de ejemplo
-- =====================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;


-- =====================================================
-- CREAR TABLAS
-- =====================================================

-- Tabla: personas
-- Descripción: Tabla base para almacenar información de personas
--              Utiliza herencia JOINED con la tabla clientes
CREATE TABLE personas (
    persona_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    edad INT,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    INDEX idx_identificacion (identificacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: clientes
-- Descripción: Tabla que extiende personas con información específica de clientes
--              Utiliza herencia JOINED (persona_id como FK y PK)
CREATE TABLE clientes (
    persona_id BIGINT PRIMARY KEY,
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_cliente_id (cliente_id),
    FOREIGN KEY (persona_id) REFERENCES personas(persona_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: cuentas
-- Descripción: Almacena información de las cuentas bancarias
CREATE TABLE cuentas (
    cuenta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial DECIMAL(19,2) NOT NULL,
    saldo_disponible DECIMAL(19,2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id VARCHAR(50) NOT NULL,
    INDEX idx_numero_cuenta (numero_cuenta),
    INDEX idx_cliente_id (cliente_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: movimientos
-- Descripción: Almacena los movimientos (depósitos y retiros) de las cuentas
CREATE TABLE movimientos (
    movimiento_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    saldo DECIMAL(19,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    INDEX idx_fecha (fecha),
    INDEX idx_cuenta_id (cuenta_id),
    INDEX idx_tipo_movimiento (tipo_movimiento),
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;








-- =====================================================
-- INSERTAR DATOS DE EJEMPLO
-- =====================================================

-- Insertar Personas y Clientes
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono) VALUES
('Jose Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785'),
('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NNUU', '097548965'),
('Juan Osorio', 'Masculino', 42, '1122334455', '13 junio y Equinoccial', '098874587');

-- Obtener los persona_id generados para insertar en clientes
SET @persona1_id = (SELECT persona_id FROM personas WHERE identificacion = '1234567890');
SET @persona2_id = (SELECT persona_id FROM personas WHERE identificacion = '0987654321');
SET @persona3_id = (SELECT persona_id FROM personas WHERE identificacion = '1122334455');

INSERT INTO clientes (persona_id, cliente_id, contrasena, estado) VALUES
(@persona1_id, 'CLI001', '1234', TRUE),
(@persona2_id, 'CLI002', '5678', TRUE),
(@persona3_id, 'CLI003', '9012', TRUE);

-- Insertar Cuentas
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
('478758', 'Ahorros', 2000.00, 2000.00, TRUE, 'CLI001'),
('225487', 'Corriente', 100.00, 100.00, TRUE, 'CLI002'),
('495878', 'Ahorros', 0.00, 0.00, TRUE, 'CLI001'),
('496825', 'Ahorros', 540.00, 540.00, TRUE, 'CLI002'),
('585545', 'Corriente', 1000.00, 1000.00, TRUE, 'CLI003');

-- Obtener los cuenta_id generados para insertar movimientos
SET @cuenta1_id = (SELECT cuenta_id FROM cuentas WHERE numero_cuenta = '478758');
SET @cuenta2_id = (SELECT cuenta_id FROM cuentas WHERE numero_cuenta = '225487');
SET @cuenta3_id = (SELECT cuenta_id FROM cuentas WHERE numero_cuenta = '495878');
SET @cuenta4_id = (SELECT cuenta_id FROM cuentas WHERE numero_cuenta = '496825');
SET @cuenta5_id = (SELECT cuenta_id FROM cuentas WHERE numero_cuenta = '585545');

-- Insertar Movimientos
-- Movimientos para cuenta 478758 (CLI001)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2022-02-01 10:00:00', 'DEPOSITO', 600.00, 2600.00, @cuenta1_id),
('2022-02-02 14:30:00', 'RETIRO', 575.00, 2025.00, @cuenta1_id),
('2022-02-15 09:15:00', 'DEPOSITO', 100.00, 2125.00, @cuenta1_id);

-- Movimientos para cuenta 225487 (CLI002)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2022-02-05 11:20:00', 'DEPOSITO', 200.00, 300.00, @cuenta2_id),
('2022-02-10 16:45:00', 'RETIRO', 50.00, 250.00, @cuenta2_id);

-- Movimientos para cuenta 495878 (CLI001)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2022-02-20 08:00:00', 'DEPOSITO', 500.00, 500.00, @cuenta3_id);

-- Movimientos para cuenta 496825 (CLI002)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2022-02-12 13:30:00', 'DEPOSITO', 100.00, 640.00, @cuenta4_id),
('2022-02-18 15:00:00', 'RETIRO', 100.00, 540.00, @cuenta4_id);

-- Movimientos para cuenta 585545 (CLI003)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2022-02-25 10:30:00', 'DEPOSITO', 500.00, 1500.00, @cuenta5_id);

-- Actualizar saldos disponibles en cuentas según los movimientos
UPDATE cuentas SET saldo_disponible = 2125.00 WHERE numero_cuenta = '478758';
UPDATE cuentas SET saldo_disponible = 250.00 WHERE numero_cuenta = '225487';
UPDATE cuentas SET saldo_disponible = 500.00 WHERE numero_cuenta = '495878';
UPDATE cuentas SET saldo_disponible = 540.00 WHERE numero_cuenta = '496825';
UPDATE cuentas SET saldo_disponible = 1500.00 WHERE numero_cuenta = '585545';

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Verificar datos insertados
SELECT '=== PERSONAS ===' AS '';
SELECT * FROM personas;

SELECT '=== CLIENTES ===' AS '';
SELECT c.*, p.nombre, p.identificacion 
FROM clientes c 
JOIN personas p ON c.persona_id = p.persona_id;

SELECT '=== CUENTAS ===' AS '';
SELECT * FROM cuentas;

SELECT '=== MOVIMIENTOS ===' AS '';
SELECT m.*, c.numero_cuenta 
FROM movimientos m 
JOIN cuentas c ON m.cuenta_id = c.cuenta_id 
ORDER BY m.fecha;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

