/*
 * Created 2018-12-30 22:52:03
 */
/**
 * Author:  Azige
 * Created: 2018-12-30
 */

CREATE TABLE `fashion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `definition_id` bigint(20) NOT NULL,
  `dye_id` bigint(20) DEFAULT NULL,
  `next_withdraw_time` datetime DEFAULT NULL,
  `nft_id` bigint(20) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nft_id` (`nft_id`),
  KEY `IDX_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `fashion_dye` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `definition_id` bigint(20) NOT NULL,
  `dye_name` varchar(255) DEFAULT NULL,
  `part_1_brightness` int(11) DEFAULT NULL,
  `part_1_color` int(11) DEFAULT NULL,
  `part_1_saturation` int(11) DEFAULT NULL,
  `part_2_brightness` int(11) DEFAULT NULL,
  `part_2_color` int(11) DEFAULT NULL,
  `part_2_saturation` int(11) DEFAULT NULL,
  `part_3_brightness` int(11) DEFAULT NULL,
  `part_3_color` int(11) DEFAULT NULL,
  `part_3_saturation` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id` (`account_id`),
  KEY `IDX_definition_id` (`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `fashion_granting_stats` (
  `definition_id` bigint(20) NOT NULL,
  `granted_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `secret_shop_prize_granting_stats` (
  `id` bigint(20) NOT NULL,
  `granted_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE player_relation ADD COLUMN fashion_id bigint DEFAULT NULL;
