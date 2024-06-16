/*
 * Created 2019-2-14 11:46:44
 */
/**
 * Author:  Azige
 * Created: 2019-2-14
 */

ALTER TABLE player ADD COLUMN `last_online_time` datetime DEFAULT NULL;
DELETE FROM quest_record WHERE quest_id >= 730011 AND quest_id <= 730150;
DELETE FROM currency_record WHERE currency_id = 20039;

CREATE TABLE `yxjy_record` (
  `account_id` bigint(20) NOT NULL,
  `award_status` varchar(255) NOT NULL,
  `last_invitation_time` datetime DEFAULT NULL,
  `invited_account_id_1` bigint(20) DEFAULT NULL,
  `invited_account_id_2` bigint(20) DEFAULT NULL,
  `invited_account_id_3` bigint(20) DEFAULT NULL,
  `today_attended_count` int(11) NOT NULL,
  `attended_account_id_1` bigint(20) DEFAULT NULL,
  `attended_account_id_2` bigint(20) DEFAULT NULL,
  `attended_account_id_3` bigint(20) DEFAULT NULL,
  `attended_account_id_4` bigint(20) DEFAULT NULL,
  `attended_account_id_5` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
