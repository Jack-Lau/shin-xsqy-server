/*
 * Created 2018-11-30 19:53:03
 */
/**
 * Author:  Azige
 * Created: 2018-11-30
 */

CREATE TABLE `shop_commodity` (
  `commodity_id` bigint(20) NOT NULL,
  `current_price` bigint(20) DEFAULT NULL,
  `remain_count` bigint(20) DEFAULT NULL,
  `total_buy` bigint(20) DEFAULT NULL,
  `total_buy_in_period` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`commodity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `brawl_record` (
  `account_id` bigint(20) NOT NULL,
  `brawl_count` bigint(20) DEFAULT NULL,
  `current_battle_session_id` bigint(20) DEFAULT NULL,
  `current_stage` bigint(20) DEFAULT NULL,
  `reset_count` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `team_member_1` bigint(20) DEFAULT NULL,
  `team_member_2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
