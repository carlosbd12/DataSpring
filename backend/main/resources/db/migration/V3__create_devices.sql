CREATE TABLE `devices` (
                           `device_id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(150) NOT NULL,
                           `device_type` varchar(50) NOT NULL,
                           `location` varchar(150) DEFAULT NULL,
                           `status` varchar(30) NOT NULL DEFAULT 'ACTIVE',
                           `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`device_id`)
);

INSERT INTO `devices` VALUES (1,'Analizador Principal','ANALYZER','Planta 1','ACTIVE','2026-05-15 08:06:18');
