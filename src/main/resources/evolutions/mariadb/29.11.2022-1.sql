CREATE TABLE `user_sessions` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `session_id` varchar(100) NOT NULL,
                                 `user_name` varchar(100) NOT NULL,
                                 `state` varchar(100) NOT NULL,
                                 `time_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;