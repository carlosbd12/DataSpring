CREATE TABLE `measurements` (
                                `measurement_id` bigint NOT NULL AUTO_INCREMENT,
                                `device_id` bigint NOT NULL,
                                `measurement_type_id` bigint NOT NULL,
                                `timestamp` datetime NOT NULL,
                                `value` decimal(18,6) NOT NULL,
                                `source` varchar(100) DEFAULT NULL,
                                `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`measurement_id`),
                                UNIQUE KEY `uq_meas_unique` (`device_id`,`measurement_type_id`,`timestamp`),
                                KEY `idx_meas_device` (`device_id`),
                                KEY `idx_meas_type` (`measurement_type_id`),
                                KEY `idx_meas_ts` (`timestamp`),
                                KEY `idx_meas_device_ts` (`device_id`,`timestamp`),
                                CONSTRAINT `fk_meas_device` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                CONSTRAINT `fk_meas_type` FOREIGN KEY (`measurement_type_id`) REFERENCES `measurement_types` (`measurement_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
);