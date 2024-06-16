/*
 * Created 2018-10-21 4:25:55
 */
/**
 * Author:  Azige
 * Created: 2018-10-21
 */

CREATE TABLE `activity_record` (
  `account_id` bigint(20) NOT NULL,
  `activity_id` bigint(20) NOT NULL,
  `progress` int(11) NOT NULL,
  `completed` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
