/*
 * Created 2018-9-23 0:51:11
 */
/**
 * Author:  Azige
 * Created: 2018-9-23
 */

CREATE TABLE `price_record` (
  `id` bigint(20) NOT NULL,
  `current_value` bigint(20) DEFAULT NULL,
  `used_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
