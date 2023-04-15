-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               10.11.1-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

-- Dumping data for table smart_garage.password_reset_tokens: ~1 rows (approximately)
/*!40000 ALTER TABLE `password_reset_tokens`
    DISABLE KEYS */;
INSERT INTO `password_reset_tokens` (`id`, `token`, `user_id`, `expire_date`)
VALUES (2, '$2a$10$MFw5OizZsUqq4KgIn8LhdujzWt0jQB0cFTt1M79N4tFSrbwjkpIn2', 16, '2023-04-15 12:58:43');
/*!40000 ALTER TABLE `password_reset_tokens`
    ENABLE KEYS */;

-- Dumping data for table smart_garage.repairs: ~6 rows (approximately)
/*!40000 ALTER TABLE `repairs`
    DISABLE KEYS */;
INSERT INTO `repairs` (`repair_id`, `name`, `price`, `is_deleted`)
VALUES (1, 'Oil change', 30.99, 1),
       (2, 'Oil Filter Change', 19.99, 0),
       (3, 'Brake fuild change', 89.99, 0),
       (4, 'Front windshield change', 349.99, 0),
       (5, 'Oil change', 35.99, 0);
/*!40000 ALTER TABLE `repairs`
    ENABLE KEYS */;

-- Dumping data for table smart_garage.roles: ~3 rows (approximately)
/*!40000 ALTER TABLE `roles`
    DISABLE KEYS */;
INSERT INTO `roles` (`role_id`, `name`)
VALUES (3, 'ROLE_ADMIN'),
       (1, 'ROLE_CUSTOMER'),
       (2, 'ROLE_EMPLOYEE');
/*!40000 ALTER TABLE `roles`
    ENABLE KEYS */;

-- Dumping data for table smart_garage.users: ~9 rows (approximately)
/*!40000 ALTER TABLE `users`
    DISABLE KEYS */;
INSERT INTO `users` (`user_id`, `username`, `email`, `phone_number`, `password`, `first_name`, `last_name`)
VALUES (1, 'grPeatrov', 'gpetrov@asdas.com', '0000000000',
        '$2a$10$pcO9WPG2BtMqKDlmQSNu9Oeik2MV6PxBe6n8dulJlhgIGaqbK0efe', 'Georg2oii', 'Patyetroiv'),
       (2, 'stoqnT', 'stoqnT@adfasdas.com', '1111111111',
        '$2a$10$tJ8uS1sGlCCDuSTzO2n34eXntvh23v1shzIyExJM1o3R/diy.Llui', 'Stoqn', 'Todorov'),
       (3, 'iliqM', 'iliqm@asdas.com', '2222222222', '$2a$10$FugXPYBL9amKVGqmqLz3du7jfIY14Pwv7sMb7EJz4WkR5KzKb2p5S',
        'Iliq', 'Marinov'),
       (4, 'strahilY', 'strahily@asdas.com', '3333333333',
        '$2a$10$u8KLUjr/iUI8RZzg9XvQDu9SYSf9Ew1cf/aBQIdKFgZknns8M7Mvy', 'Strahil', 'Yordanov'),
       (5, 'peshoI', 'peshoi@asda.com', '4444444444', '$2a$10$D1UJthx5yga.YQeVc05yauHB26czo64BtpBv.WXQgCRSrPoy99Dcy',
        'Petur', 'Iliev'),
       (6, 'jGM2002', 'gmihaylajov@gmail.com', '1234567890',
        '$2a$10$RZx7xickGI8YShk8UDZapulXeJuNHMfyTD8A3AYoEbe2JcbiCU5ji', NULL, NULL),
       (13, 'deleted13', 'gmihalyasdaalajov@gmail.com', '0284767890', 'bBYuMPe0', NULL, NULL),
       (14, 'deleted14', 'gmihalyasdasdaalajov@gmail.com', '0284787890', '&A@hzxgd', NULL, NULL),
       (15, 'TfOsmxSOXPcUXvKHxfEN', 'email@bestemail.abc', '0888888888',
        '$2a$10$/N.p5fwPWXVoGrgM/wKl5ueSiMBmccMPfTT5NnLnTB.v/wPISao/y', NULL, NULL),
       (16, 'KNeGPLUPaMKZTJxvAQLo', 'jorko2023@gmail.com', '0887123456',
        '$2a$10$aF26dgOpPD5QIjErc1nqdOBL3l9JSYlKvhV0yiUMziXEI1YYjsklC', NULL, NULL);
/*!40000 ALTER TABLE `users`
    ENABLE KEYS */;

-- Dumping data for table smart_garage.users_roles: ~9 rows (approximately)
/*!40000 ALTER TABLE `users_roles`
    DISABLE KEYS */;
INSERT INTO `users_roles` (`user_id`, `role_id`)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (3, 3),
       (4, 1),
       (5, 3),
       (13, 1),
       (14, 1),
       (15, 1),
       (16, 1);
/*!40000 ALTER TABLE `users_roles`
    ENABLE KEYS */;

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

-- Dumping data for table smart_garage.visits: ~6 rows (approximately)
/*!40000 ALTER TABLE `visits`
    DISABLE KEYS */;
INSERT INTO `visits` (`visit_id`, `date`, `vehicle_id`, `user_id`)
VALUES (1, '2023-04-06 12:24:33', 1, 1),
       (2, '2021-06-17 14:02:22', 2, 1),
       (4, '2023-04-12 11:23:55', 2, 2),
       (5, '2023-04-12 11:45:56', 3, 3),
       (7, '2023-04-12 12:23:45', 4, 4);
/*!40000 ALTER TABLE `visits`
    ENABLE KEYS */;

-- Dumping data for table smart_garage.visits_repairs: ~8 rows (approximately)
/*!40000 ALTER TABLE `visits_repairs`
    DISABLE KEYS */;
INSERT INTO `visits_repairs` (`visit_id`, `repair_id`)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (4, 2),
       (4, 5),
       (5, 4),
       (7, 2),
       (7, 5);
/*!40000 ALTER TABLE `visits_repairs`
    ENABLE KEYS */;

/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES = IFNULL(@OLD_SQL_NOTES, 1) */;
