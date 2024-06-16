/*
 * Created 2018-12-14 19:08:56
 */
/**
 * Author:  Azige
 * Created: 2018-12-14
 */

CREATE TABLE `broadcast_record` (
  `broadcast_id` bigint(20) NOT NULL,
  `today_send_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`broadcast_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `player_location` (
  `account_id` bigint(20) NOT NULL,
  `direction` int(11) DEFAULT NULL,
  `map_id` int(11) DEFAULT NULL,
  `x_pos` int(11) DEFAULT NULL,
  `y_pos` int(11) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE gold_tower_record ADD COLUMN taken_wipe_out_award bit(1) DEFAULT NULL;
ALTER TABLE gold_tower_record ADD COLUMN up_to_target_floor bit(1) DEFAULT NULL;
ALTER TABLE gold_tower_record ADD COLUMN wipe_out_battle_session_id bigint(20) DEFAULT NULL;
ALTER TABLE gold_tower_record ADD COLUMN wipe_out_battle_win bit(1) DEFAULT NULL;
