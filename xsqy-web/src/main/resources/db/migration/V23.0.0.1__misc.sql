/*
 * Created 2019-1-19 13:02:08
 */
/**
 * Author:  Azige
 * Created: 2019-1-19
 */

CREATE TABLE `changlefang_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `cost_value` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `gain_value` bigint(20) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `changlefang_record` (
  `account_id` bigint(20) NOT NULL,
  `day_share` int(11) DEFAULT NULL,
  `total_share` int(11) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `changlefang_shared_record` (
  `id` bigint(20) NOT NULL,
  `day_energy` bigint(20) DEFAULT NULL,
  `day_share` int(11) DEFAULT NULL,
  `total_share` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mine_exploration_coupon_send` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `receiver_id` bigint(20) DEFAULT NULL,
  `sender_id` bigint(20) DEFAULT NULL,
  `taken` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mine_exploration_record` (
  `account_id` bigint(20) NOT NULL,
  `available_dig` int(11) DEFAULT NULL,
  `coupon_take` int(11) DEFAULT NULL,
  `in_game` bit(1) DEFAULT NULL,
  `map` varchar(1000) DEFAULT NULL,
  `mask` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tron_charge_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tx_id` varchar(255) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `charged_value` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL,
  `request_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_tx_id` (`tx_id`),
  KEY `IDX_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
