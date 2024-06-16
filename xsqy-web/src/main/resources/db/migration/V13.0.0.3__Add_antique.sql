/*
 * Created 2018-11-9 21:10:36
 */
/**
 * Author:  Azige
 * Created: 2018-11-9
 */

CREATE TABLE `antique_record` (
  `account_id` bigint(20) NOT NULL,
  `last_public_award_obtain_time` datetime NOT NULL,
  `part` varchar(255) DEFAULT NULL,
  `progress` int(11) NOT NULL,
  `public_award_obtain_count` int(11) NOT NULL,
  `repair_count` int(11) NOT NULL,
  `started` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `antique_shared_record` (
  `id` bigint(20) NOT NULL,
  `last_public_award_create_time` datetime DEFAULT NULL,
  `public_award_account_id` bigint(20) DEFAULT NULL,
  `public_award_remain_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
