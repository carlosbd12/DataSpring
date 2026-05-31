CREATE TABLE IF NOT EXISTS mediciones_energia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha_hora DATETIME NOT NULL,
    consumo_kwh DOUBLE NOT NULL,
    co2_kg DOUBLE NOT NULL,
    estado_semana VARCHAR(30) NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    tipo_carga VARCHAR(30) NOT NULL,
    hora TINYINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_mediciones_energia_fecha_hora ON mediciones_energia(fecha_hora);
