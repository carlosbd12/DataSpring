CREATE TABLE IF NOT EXISTS `measurement_types` (
                                     `measurement_type_id` bigint NOT NULL AUTO_INCREMENT,
                                     `name` varchar(120) NOT NULL,
                                     `code` varchar(60) NOT NULL,
                                     `description` varchar(255) DEFAULT NULL,
                                     `magnitude` varchar(50) NOT NULL,
                                     `unit_name` varchar(100) NOT NULL,
                                     `unit_symbol` varchar(30) NOT NULL,
                                     `is_active` tinyint(1) NOT NULL DEFAULT '1',
                                     `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`measurement_type_id`),
                                     UNIQUE KEY `code` (`code`)
);

INSERT IGNORE INTO `measurement_types` VALUES (1,'Energía activa consumida','USAGE_KWH','Consumo de energía activa','Energía','Kilovatio-hora','kWh',1,'2026-05-15 08:06:18'),(2,'Emisión de CO₂','CO2','Toneladas de CO₂ emitidas','Emisión','Toneladas de CO₂','tCO2',1,'2026-05-15 08:06:18'),(3,'Potencia reactiva inductiva','REACTIVE_POWER_LAGGING','Potencia reactiva en lagging','Potencia','Kilovoltamperio-hora reactivo','kVarh',1,'2026-05-15 08:06:18'),(4,'Potencia reactiva capacitiva','REACTIVE_POWER_LEADING','Potencia reactiva en leading','Potencia','Kilovoltamperio-hora reactivo','kVarh',1,'2026-05-15 08:06:18'),(5,'Factor de potencia inductivo','POWER_FACTOR_LAGGING','Factor de potencia en lagging','Factor','Adimensional','ø',1,'2026-05-15 08:06:18'),(6,'Factor de potencia capacitivo','POWER_FACTOR_LEADING','Factor de potencia en leading','Factor','Adimensional','ø',1,'2026-05-15 08:06:18');
