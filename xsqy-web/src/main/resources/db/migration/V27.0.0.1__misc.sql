/*
 * Created 2019-3-1 20:49:09
 */
/**
 * Author:  Azige
 * Created: 2019-3-1
 */

CREATE TABLE `baccarat_bet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `bet_0` bigint(20) DEFAULT NULL,
  `bet_1` bigint(20) DEFAULT NULL,
  `bet_2` bigint(20) DEFAULT NULL,
  `bet_3` bigint(20) DEFAULT NULL,
  `bet_4` bigint(20) DEFAULT NULL,
  `bet_5` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `game_id` bigint(20) DEFAULT NULL,
  `total_gain` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `baccarat_game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blue_point_1` bigint(20) DEFAULT NULL,
  `blue_point_2` bigint(20) DEFAULT NULL,
  `lottery_time` datetime NOT NULL,
  `red_point_1` bigint(20) DEFAULT NULL,
  `red_point_2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
