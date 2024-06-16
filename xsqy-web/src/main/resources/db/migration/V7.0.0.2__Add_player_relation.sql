/*
 * Created 2018-9-23 0:45:35
 */
/**
 * Author:  Azige
 * Created: 2018-9-23
 */

CREATE TABLE `player_relation` (
  `account_id` bigint(20) NOT NULL,
  `title_id` bigint(20) DEFAULT NULL,
  `hand_equipment_id` bigint(20) DEFAULT NULL,
  `body_equipment_id` bigint(20) DEFAULT NULL,
  `waist_equipment_id` bigint(20) DEFAULT NULL,
  `foot_equipment_id` bigint(20) DEFAULT NULL,
  `head_equipment_id` bigint(20) DEFAULT NULL,
  `neck_equipment_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
