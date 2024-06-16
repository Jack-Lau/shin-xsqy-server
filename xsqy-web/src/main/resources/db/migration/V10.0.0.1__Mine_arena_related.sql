/*
 * Created 2018-10-20 22:59:38
 */
/**
 * Author:  Azige
 * Created: 2018-10-20
 */

CREATE TABLE `mine_arena_record` (
  `account_id` bigint(20) NOT NULL,
  `challenge_point` int(11) DEFAULT NULL,
  `last_reward_resolve_time` datetime NOT NULL,
  `resolved_reward` varchar(255) NOT NULL,
  `resolved_reward_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pit` (
  `position` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `challenged_count` int(11) NOT NULL,
  PRIMARY KEY (`position`),
  KEY `IDX_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pit_position_change_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `before_position` bigint(20) NOT NULL,
  `after_position` bigint(20) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_event_time` (`account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mine_arena_award_obtain_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `reward_text` varchar(255) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_event_time` (`account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mine_arena_challenge_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `challenger_account_id` bigint(20) NOT NULL,
  `defender_account_id` bigint(20) NOT NULL,
  `cost` bigint(20) NOT NULL,
  `challenger_position` bigint(20) NOT NULL,
  `defender_position` bigint(20) NOT NULL,
  `success` bit(1) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_challenger_account_id` (`challenger_account_id`),
  KEY `IDX_defender_account_id` (`defender_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
