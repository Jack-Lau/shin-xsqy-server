/*
 * Created 2018-12-8 21:40:54
 */
/**
 * Author:  Azige
 * Created: 2018-12-8
 */

CREATE TABLE `slots_big_prize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `award_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `slots_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `big_prize_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `receiver_id` bigint(20) DEFAULT NULL,
  `sender_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `slots_record` (
  `account_id` bigint(20) NOT NULL,
  `like_big_prize_ids` varchar(5000) DEFAULT NULL,
  `like_send` bigint(20) DEFAULT NULL,
  `like_receive` bigint(20) DEFAULT NULL,
  `locks` varchar(255) DEFAULT NULL,
  `slots` varchar(255) DEFAULT NULL,
  `taken_prize` bit(1) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gold_tower_record` (
  `account_id` bigint(20) NOT NULL,
  `max_finish_floor` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `brawl_record` ADD COLUMN `team_max_fc` bigint(20) DEFAULT NULL;
