/*
 * Created 2018-10-27 20:57:10
 */
/**
 * Author:  Azige
 * Created: 2018-10-27
 */

CREATE TABLE `idle_mine_record` (
  `account_id` bigint(20) NOT NULL,
  `available_mine_queue_count` int(11) NOT NULL,
  `idle_mine_reward` varchar(255) DEFAULT NULL,
  `mine_queue_finish_time_1` datetime DEFAULT NULL,
  `mine_queue_finish_time_2` datetime DEFAULT NULL,
  `mine_queue_finish_time_3` datetime DEFAULT NULL,
  `mine_queue_last_balance_time_1` datetime DEFAULT NULL,
  `mine_queue_last_balance_time_2` datetime DEFAULT NULL,
  `mine_queue_last_balance_time_3` datetime DEFAULT NULL,
  `mine_queue_map_id_1` bigint(20) DEFAULT NULL,
  `mine_queue_map_id_2` bigint(20) DEFAULT NULL,
  `mine_queue_map_id_3` bigint(20) DEFAULT NULL,
  `mine_queue_team_id_1` bigint(20) DEFAULT NULL,
  `mine_queue_team_id_2` bigint(20) DEFAULT NULL,
  `mine_queue_team_id_3` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
