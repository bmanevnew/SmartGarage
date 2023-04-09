-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.5.18-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


-- Dumping database structure for smart_garage
CREATE DATABASE IF NOT EXISTS `smart_garage` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `smart_garage`;

-- Dumping structure for table smart_garage.repairs
CREATE TABLE IF NOT EXISTS `repairs`
(
    `repair_id` bigint(20)    NOT NULL,
    `name`      varchar(32)   NOT NULL,
    `price`     double(10, 2) NOT NULL,
    `is_active` tinyint(1)    NOT NULL DEFAULT 1,
    PRIMARY KEY (`repair_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.repairs: ~4 rows (approximately)
/*!40000 ALTER TABLE `repairs`
    DISABLE KEYS */;
INSERT INTO `repairs` (`repair_id`, `name`, `price`, `is_active`)
VALUES (1, 'Oil change', 123.00, 1),
       (2, 'Oil Filter Change', 234.00, 1),
       (3, 'Brake fuild change', 134.00, 1),
       (4, 'Front windshield change', 250.00, 1);
/*!40000 ALTER TABLE `repairs`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.roles
CREATE TABLE IF NOT EXISTS `roles`
(
    `role_id` bigint(20)  NOT NULL AUTO_INCREMENT,
    `name`    varchar(32) NOT NULL,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `roles_pk` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.roles: ~4 rows (approximately)
/*!40000 ALTER TABLE `roles`
    DISABLE KEYS */;
INSERT INTO `roles` (`role_id`, `name`)
VALUES (3, 'admin'),
       (1, 'customer'),
       (4, 'deleted'),
       (2, 'employee');
/*!40000 ALTER TABLE `roles`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.users
CREATE TABLE IF NOT EXISTS `users`
(
    `user_id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `username`     varchar(20)  NOT NULL,
    `email`        varchar(255) NOT NULL,
    `phone_number` char(10)     NOT NULL,
    `password`     varchar(256) NOT NULL,
    `first_name`   varchar(32) DEFAULT NULL,
    `last_name`    varchar(32) DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `users_pk` (`username`),
    UNIQUE KEY `users_pk2` (`email`),
    UNIQUE KEY `users_pk3` (`phone_number`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.users: ~8 rows (approximately)
/*!40000 ALTER TABLE `users`
    DISABLE KEYS */;
INSERT INTO `users` (`user_id`, `username`, `email`, `phone_number`, `password`, `first_name`, `last_name`)
VALUES (1, 'grPeatrov', 'gpetrov@asdas.com', '0000000000', 'Password11!', 'Georg2oii', 'Patyetroiv'),
       (2, 'stoqnT', 'stoqnT@adfasdas.com', '1111111111', 'Password1!', 'Stoqn', 'Todorov'),
       (3, 'iliqM', 'iliqm@asdas.com', '2222222222', 'Password1!', 'Iliq', 'Marinov'),
       (4, 'strahilY', 'strahily@asdas.com', '3333333333', 'Password1!', 'Strahil', 'Yordanov'),
       (5, 'peshoI', 'peshoi@asda.com', '4444444444', 'Password1!', 'Petur', 'Iliev'),
       (6, 'jGM2002', 'gmihaylajov@gmail.com', '1234567890', 'bnS9Q7b3', NULL, NULL),
       (13, 'deleted13', 'gmihalyasdaalajov@gmail.com', '0284767890', 'bBYuMPe0', NULL, NULL),
       (14, 'deleted14', 'gmihalyasdasdaalajov@gmail.com', '0284787890', '&A@hzxgd', NULL, NULL);
/*!40000 ALTER TABLE `users`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.users_roles
CREATE TABLE IF NOT EXISTS `users_roles`
(
    `user_id` bigint(20) NOT NULL,
    `role_id` bigint(20) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `users_roles_roles_role_id_fk` (`role_id`),
    CONSTRAINT `users_roles_roles_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
    CONSTRAINT `users_roles_users_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.users_roles: ~12 rows (approximately)
/*!40000 ALTER TABLE `users_roles`
    DISABLE KEYS */;
INSERT INTO `users_roles` (`user_id`, `role_id`)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (3, 3),
       (4, 1),
       (4, 4),
       (5, 1),
       (5, 2),
       (13, 1),
       (13, 4),
       (14, 1),
       (14, 4);
/*!40000 ALTER TABLE `users_roles`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.vehicles
CREATE TABLE IF NOT EXISTS `vehicles`
(
    `vehicle_id`      bigint(20)  NOT NULL AUTO_INCREMENT,
    `license_plate`   varchar(10) NOT NULL,
    `vin`             char(17)    NOT NULL,
    `production_year` smallint(6) NOT NULL,
    `model`           varchar(32) NOT NULL,
    `brand`           varchar(32) NOT NULL,
    `user_id`         bigint(20)  NOT NULL,
    PRIMARY KEY (`vehicle_id`),
    UNIQUE KEY `vehicles_pk2` (`license_plate`),
    UNIQUE KEY `vehicles_pk3` (`vin`),
    KEY `vehicles_users_user_id_fk` (`user_id`),
    CONSTRAINT `vehicles_users_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `check_production_year` CHECK (`production_year` >= 1886)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.vehicles: ~5 rows (approximately)
/*!40000 ALTER TABLE `vehicles`
    DISABLE KEYS */;
INSERT INTO `vehicles` (`vehicle_id`, `license_plate`, `vin`, `production_year`, `model`, `brand`, `user_id`)
VALUES (1, 'PB 3456 AM', '1GCEK14T73Z193939', 2003, 'Silverado', 'Chevrolet', 1),
       (2, 'PB 6789 PC', '3VWJZ7AJ5EM311290', 2014, 'Jetta', 'Volkswagen', 2),
       (3, 'PB 9012 XH', '1C6RR7FT2HS532317', 2017, 'Ram', 'Dodge', 3),
       (4, 'PB 3456 TB', '3FA6P0H73ER369767', 2014, 'Fusion', 'Ford', 4),
       (5, 'PB 7890 ZP', 'JF1GV7E60DG013454', 2013, 'WRX', 'Subaru', 5);
/*!40000 ALTER TABLE `vehicles`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.visits
CREATE TABLE IF NOT EXISTS `visits`
(
    `visit_id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `date`       date       NOT NULL,
    `vehicle_id` bigint(20) NOT NULL,
    PRIMARY KEY (`visit_id`),
    KEY `visits_vehicles_vehicle_id_fk` (`vehicle_id`),
    CONSTRAINT `visits_vehicles_vehicle_id_fk` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.visits: ~3 rows (approximately)
/*!40000 ALTER TABLE `visits`
    DISABLE KEYS */;
INSERT INTO `visits` (`visit_id`, `date`, `vehicle_id`)
VALUES (1, '2023-04-06', 1),
       (2, '2021-06-17', 2),
       (3, '2020-04-02', 3);
/*!40000 ALTER TABLE `visits`
    ENABLE KEYS */;

-- Dumping structure for table smart_garage.visits_repairs
CREATE TABLE IF NOT EXISTS `visits_repairs`
(
    `visit_id`  bigint(20) NOT NULL,
    `repair_id` bigint(20) NOT NULL,
    PRIMARY KEY (`visit_id`, `repair_id`),
    KEY `visits_repairs_repairs_repair_id_fk` (`repair_id`),
    CONSTRAINT `visits_repairs_repairs_repair_id_fk` FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`),
    CONSTRAINT `visits_repairs_visits_visit_id_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`visit_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;

-- Dumping data for table smart_garage.visits_repairs: ~4 rows (approximately)
/*!40000 ALTER TABLE `visits_repairs`
    DISABLE KEYS */;
INSERT INTO `visits_repairs` (`visit_id`, `repair_id`)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 4);
/*!40000 ALTER TABLE `visits_repairs`
    ENABLE KEYS */;

/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES = IFNULL(@OLD_SQL_NOTES, 1) */;
