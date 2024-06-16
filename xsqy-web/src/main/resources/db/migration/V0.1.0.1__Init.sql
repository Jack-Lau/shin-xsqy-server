/*
 * Created 2018-7-5 16:27:49
 */
/**
 * Author:  Azige
 * Created: 2018-7-5
 */

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `passcode` varchar(255) NOT NULL,
  `passcode_type` varchar(255) NOT NULL,
  `display_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `phone_activation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(255) NOT NULL,
  `activation_code` int(11) NOT NULL,
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_activation_code` (`activation_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `currency_record` (
  `account_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `amount` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`,`currency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `currency_change_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `before_amount` bigint(20) NOT NULL,
  `after_amount` bigint(20) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_currency_id` (`account_id`,`currency_id`),
  KEY `IDX_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `scheduled_task_record` (
  `task_name` varchar(255) NOT NULL,
  `last_execution` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inviter_record` (
  `account_id` bigint(20) NOT NULL,
  `invitation_code` varchar(255) NOT NULL,
  `invitation_limit` int(11) NOT NULL,
  `last_reward_resolve_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `today_kbdzp_energy_reward` int(11) NOT NULL,
  `today_kuaibi_reward` int(11) NOT NULL,
  `today_reward_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UK_invitation_code` (`invitation_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `invitation_record` (
  `account_id` bigint(20) NOT NULL,
  `inviter_id` bigint(20) NOT NULL,
  `inviter_depth` int(11) NOT NULL,
  PRIMARY KEY (`account_id`,`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `invitation_reward_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `invitee_id` bigint(20) NOT NULL,
  `kbdzp_energy_reward` int(11) NOT NULL,
  `kuaibi_reward` int(11) NOT NULL,
  `event_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_event_time` (`account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `kbdzp_record` (
  `account_id` bigint(20) NOT NULL,
  `energy` int(11) NOT NULL,
  `booster1` bit(1) NOT NULL,
  `booster2` bit(1) NOT NULL,
  `recover_ref_time` datetime NOT NULL,
  `invitee_bonus_available` bit(1) NOT NULL,
  `invitee_bonus_delivered` bit(1) NOT NULL,
  `pending_award` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `kbdzp_energy_consumption_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `consumption_value` int(11) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_event_time` (`account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `kbdzp_shared_record` (
  `id` bigint(20) NOT NULL,
  `booster1_activation_code` varchar(255) DEFAULT NULL,
  `booster2_activation_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `player` (
  `account_id` bigint(20) NOT NULL,
  `player_name` varchar(255) NOT NULL,
  `prefab_id` int(11) NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UK_player_name` (`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
