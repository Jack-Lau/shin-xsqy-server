/*
 * Created 2018-12-22 22:50:41
 */
/**
 * Author:  Azige
 * Created: 2018-12-22
 */

CREATE TABLE `mjdh_battle_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_time` datetime NOT NULL,
  `loser_account_id` bigint(20) NOT NULL,
  `loser_after_grade` int(11) NOT NULL,
  `loser_before_grade` int(11) NOT NULL,
  `winner_account_id` bigint(20) NOT NULL,
  `winner_before_grade` int(11) NOT NULL,
  `winner_after_grade` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_winner_account_id_event_time` (`winner_account_id`,`event_time`),
  KEY `IDX_loser_account_id_event_time` (`loser_account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mjdh_dummy_record` (
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mjdh_player_record` (
  `season_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `consecutive_win_count` int(11) DEFAULT NULL,
  `daily_battle_count` int(11) DEFAULT NULL,
  `daily_consecutive_win_award_available` bit(1) DEFAULT NULL,
  `daily_consecutive_win_award_delivered` bit(1) DEFAULT NULL,
  `daily_consecutive_win_count` int(11) DEFAULT NULL,
  `daily_first_win` bit(1) DEFAULT NULL,
  `daily_first_win_award_delivered` bit(1) DEFAULT NULL,
  `daily_ten_battle_award_delivered` bit(1) DEFAULT NULL,
  PRIMARY KEY (`season_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mjdh_season` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mjdh_winner_record` (
  `season_id` bigint(20) NOT NULL,
  `ranking` int(11) NOT NULL,
  `account_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`season_id`,`ranking`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
