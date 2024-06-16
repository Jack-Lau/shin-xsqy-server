/*
 * Created 2018-11-24 15:37:04
 */
/**
 * Author:  Azige
 * Created: 2018-11-24
 */

CREATE TABLE `impartation_record` (
  `account_id` bigint(20) NOT NULL,
  `impartation_role` varchar(255) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `disciple_record` (
  `account_id` bigint(20) NOT NULL,
  `master_account_id` bigint(20) NOT NULL,
  `discipline_phase` varchar(255) NOT NULL,
  `create_date` date NOT NULL,
  `deadline` date NOT NULL,
  `daily_practice_generated` bit(1) NOT NULL,
  `disciple_confirmed` bit(1) NOT NULL,
  `master_confirmed` bit(1) NOT NULL,
  `confirmation_date` date DEFAULT NULL,
  `kuaibi_pool` bigint(20) NOT NULL,
  `disciple_last_kuaibi_delivery` datetime DEFAULT NULL,
  `master_last_kuaibi_delivery` datetime DEFAULT NULL,
  `player_level_at_midnight` int(11) NOT NULL,
  `yesterday_yuanbao_pool` bigint(20) NOT NULL,
  `today_yuanbao_pool` bigint(20) NOT NULL,
  `yesterday_exp_pool` bigint(20) NOT NULL,
  `today_exp_pool` bigint(20) NOT NULL,
  `last_yuanbao_exp_delivery` datetime NOT NULL,
  PRIMARY KEY (`account_id`),
  KEY `IDX_master_account_id` (`master_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `discipline_request` (
  `account_id` bigint(20) NOT NULL,
  `master_account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`,`master_account_id`),
  KEY `IDX_master_account_id` (`master_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daily_practice_record` (
  `account_id` bigint(20) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `daily_practices_status` varchar(255) NOT NULL,
  `progress` int(11) NOT NULL,
  PRIMARY KEY (`account_id`,`definition_id`),
  KEY `IDX_definition_id` (`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
