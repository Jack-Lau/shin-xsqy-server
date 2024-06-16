/*
 * Created 2018-11-3 11:51:14
 */
/**
 * Author:  Azige
 * Created: 2018-11-3
 */

CREATE TABLE `award_pool_record` (
  `id` bigint(20) NOT NULL,
  `pool_value` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `award_pool_player_record` (
  `pool_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `pool_value` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
