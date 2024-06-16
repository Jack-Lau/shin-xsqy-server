/*
 * Created 2019-1-11 21:28:02
 */
/**
 * Author:  Azige
 * Created: 2019-1-11
 */

CREATE TABLE `player_name_used` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `used_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gold_tower_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `finish_floor` bigint(20) DEFAULT NULL,
  `gain_milli_kc` bigint(20) DEFAULT NULL,
  `remain_challenge_count` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `perk_ring` (
  `account_id` bigint(20) NOT NULL,
  `progress` int(11) NOT NULL,
  `perk_selection_1` varchar(255) NOT NULL,
  `perk_selection_2` varchar(255) NOT NULL,
  `perk_selection_3` varchar(255) NOT NULL,
  `perk_selection_4` varchar(255) NOT NULL,
  `perk_selection_5` varchar(255) NOT NULL,
  `perk_selection_6` varchar(255) NOT NULL,
  `perk_selection_7` varchar(255) NOT NULL,
  `perk_selection_8` varchar(255) NOT NULL,
  `perk_selection_9` varchar(255) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
