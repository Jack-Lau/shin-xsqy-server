/*
 * Created 2018-9-29 21:43:38
 */
/**
 * Author:  Azige
 * Created: 2018-9-29
 */

CREATE TABLE `gold_tower_challenge` (
  `account_id` bigint(20) NOT NULL,
  `available_challenge_count` int(11) NOT NULL,
  `available_treasure_count` int(11) NOT NULL,
  `current_battle_session_id` bigint(20) NOT NULL,
  `current_room_id` bigint(20) NOT NULL,
  `finish_last_floor_time` datetime NOT NULL,
  `is_current_room_challenge_success` bit(1) NOT NULL,
  `is_in_challenge` bit(1) NOT NULL,
  `last_floor_count` bigint(20) NOT NULL,
  `total_contribution` bigint(20) NOT NULL,
  `total_fragment` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gold_tower_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `challenge_param_1` varchar(255) DEFAULT NULL,
  `challenge_param_2` varchar(255) DEFAULT NULL,
  `challenge_param_3` varchar(255) DEFAULT NULL,
  `floor_id` bigint(20) NOT NULL,
  `prototype_id` bigint(20) NOT NULL,
  `treasure_count` int(11) NOT NULL,
  `waypoint_color_1` bigint(20) DEFAULT NULL,
  `waypoint_color_2` bigint(20) DEFAULT NULL,
  `waypoint_color_3` bigint(20) DEFAULT NULL,
  `waypoint_color_4` bigint(20) DEFAULT NULL,
  `waypoint_1` bigint(20) DEFAULT NULL,
  `waypoint_2` bigint(20) DEFAULT NULL,
  `waypoint_3` bigint(20) DEFAULT NULL,
  `waypoint_4` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gold_tower_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `challenge_player_count` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `total_contribution` bigint(20) NOT NULL,
  `total_fragment` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
