/*
 * Created 2018-10-21 4:31:52
 */
/**
 * Author:  Azige
 * Created: 2018-10-21
 */

CREATE TABLE `activity_player_record` (
  `account_id` bigint(20) NOT NULL,
  `incoming_active_points` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
