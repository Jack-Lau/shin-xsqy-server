/*
 * Created 2018-9-6 17:46:53
 */
/**
 * Author:  Azige
 * Created: 2018-9-6
 */

CREATE TABLE `yibenwanli_shared_record` (
  `id` bigint(20) NOT NULL,
  `pool` bigint(20) NOT NULL,
  `total_ticket_count` int(11) NOT NULL,
  `last_purchase_account_id` bigint(20) DEFAULT NULL,
  `deadline_time` datetime DEFAULT NULL,
  `paused_time` datetime DEFAULT NULL,
  `closed` bit(1) NOT NULL,
  `next_season_time` datetime DEFAULT NULL,
  `next_season_init_pool` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `yibenwanli_record` (
  `account_id` bigint(20) NOT NULL,
  `ticket_count` int(11) NOT NULL,
  `last_purchase_time` datetime NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `yibenwanli_conclusion_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `final_pool` bigint(20) NOT NULL,
  `total_ticket_count` bigint(20) NOT NULL,
  `award_for_last_one` bigint(20) NOT NULL,
  `award_per_ticket` bigint(20) NOT NULL,
  `last_purchase_account_id` bigint(20) NOT NULL,
  `event_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
