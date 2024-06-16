/*
 * Created 2018-11-8 16:51:25
 */
/**
 * Author:  Azige
 * Created: 2018-11-8
 */

CREATE TABLE `title` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_definition_id` (`account_id`,`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `title_granting_stats` (
  `title_id` bigint(20) NOT NULL,
  `granted_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`title_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
