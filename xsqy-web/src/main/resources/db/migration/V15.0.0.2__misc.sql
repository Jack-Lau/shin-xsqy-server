/*
 * Created 2018-11-24 20:35:28
 */
/**
 * Author:  Azige
 * Created: 2018-11-24
 */

ALTER TABLE player ADD COLUMN last_login_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE `friend_record` (
  `account_id` bigint(20) NOT NULL,
  `friend_ids` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `secret_shop_record` (
  `account_id` bigint(20) NOT NULL,
  `not_take_prizes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `secret_shop_shared_record` (
  `id` bigint(20) NOT NULL,
  `kc_pack_remain_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `currency_change_statistic` (
  `currency_id` bigint(20) NOT NULL,
  `statistic_date` date NOT NULL,
  `total_gain` bigint(20) NOT NULL,
  `total_drain` bigint(20) NOT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`currency_id`,`statistic_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
