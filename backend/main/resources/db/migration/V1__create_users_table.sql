CREATE DATABASE IF NOT EXISTS datasenseweb;

USE datasenseweb;

DROP TABLE IF EXISTS users;

CREATE TABLE usuarios (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          username VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL,
                          nombre VARCHAR(150) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          role VARCHAR(50) NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          fecha_creacion DATETIME NOT NULL,
                          ultimo_acceso DATETIME NULL,
                          PRIMARY KEY (id),
                          UNIQUE (username),
                          UNIQUE (email)
);

INSERT INTO usuarios (
    username,
    email,
    nombre,
    password_hash,
    role,
    activo,
    fecha_creacion,
    ultimo_acceso
) VALUES (
             'admin',
             'admin@tudominio.com',
             'Administrador',
             'admin',
             'ADMIN_PLATAFORMA',
             true,
             NOW(),
             NULL
         );

SELECT * FROM usuarios;