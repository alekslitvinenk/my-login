-- dockovpn.users definition

CREATE TABLE `users` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `first_name` varchar(100) DEFAULT NULL,
                         `last_name` varchar(100) DEFAULT NULL,
                         `email` varchar(100) NOT NULL,
                         `user_name` varchar(100) NOT NULL,
                         `user_password` varchar(100) NOT NULL,
                         `email_verified` tinyint(1) NOT NULL,
                         `auth_method` varchar(100) NOT NULL DEFAULT 'LOGIN_FORM',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;