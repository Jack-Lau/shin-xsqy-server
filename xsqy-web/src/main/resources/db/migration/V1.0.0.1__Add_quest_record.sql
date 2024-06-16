/*
 * Created 2018-8-4 12:30:48
 */
/**
 * Author:  Azige
 * Created: 2018-8-4
 */

CREATE TABLE `quest_record` (
  `account_id` bigint(20) NOT NULL,
  `quest_id` bigint(20) NOT NULL,
  `quest_status` varchar(255) NOT NULL,
  `results` varchar(255) NOT NULL,
  `objective_status` varchar(255) NOT NULL,
  `random_bac_id` bigint(20) DEFAULT NULL,
  `started_count` int(11) NOT NULL,
  PRIMARY KEY (`account_id`,`quest_id`),
  KEY `IDX_quest_id` (`quest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
