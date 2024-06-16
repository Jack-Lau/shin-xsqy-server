/*
 * Created 2018-10-13 18:18:16
 */
/**
 * Author:  Azige
 * Created: 2018-10-13
 */

CREATE TABLE `pet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `pet_name` varchar(255) NOT NULL,
  `rank` int(11) NOT NULL,
  `rank_progress` int(11) NOT NULL,
  `max_rank` int(11) NOT NULL,
  `max_ability_capacity` int(11) NOT NULL,
  `abilities_text` varchar(1000) NOT NULL,
  `aptitude_atk` int(11) NOT NULL,
  `aptitude_hp` int(11) NOT NULL,
  `aptitude_mdef` int(11) NOT NULL,
  `aptitude_pdef` int(11) NOT NULL,
  `aptitude_spd` int(11) NOT NULL,
  `number` int(11) DEFAULT NULL,
  `sorting_index` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id_sorting_index` (`account_id`,`sorting_index` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pet_gacha_ranking_record` (
  `account_id` bigint(20) NOT NULL,
  `point` bigint(20) NOT NULL,
  `last_modified` datetime NOT NULL,
  PRIMARY KEY (`account_id`),
  KEY `IDX_point` (`point` DESC, `last_modified` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pet_gacha_ranking_award_record` (
  `ranking` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `final_point` bigint(20) NOT NULL,
  `award` varchar(255) NOT NULL,
  `delivered` bit(1) NOT NULL,
  PRIMARY KEY (`ranking`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pet_gacha_ranking_shared_record` (
  `id` bigint(20) NOT NULL,
  `remaining_yingting` int(11) NOT NULL,
  `next_yingting_number` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
