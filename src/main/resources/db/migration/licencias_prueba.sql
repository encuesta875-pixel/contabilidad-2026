-- Crear tabla de licencias de prueba
CREATE TABLE IF NOT EXISTS licencias_prueba (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mac_address VARCHAR(100) NOT NULL UNIQUE,
    fecha_inicio DATETIME NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    bloqueada BOOLEAN DEFAULT FALSE,
    motivo_bloqueo VARCHAR(255),
    INDEX idx_mac_address (mac_address),
    INDEX idx_activa (activa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Agregar columna mac_address a la tabla suscripciones si no existe
ALTER TABLE suscripciones
ADD COLUMN IF NOT EXISTS mac_address VARCHAR(100);

-- Crear índice para mac_address en suscripciones
CREATE INDEX IF NOT EXISTS idx_suscripciones_mac_address
ON suscripciones(mac_address);
