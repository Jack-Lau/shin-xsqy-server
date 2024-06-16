/*
 * Created 2018-9-15 10:54:28
 */
/**
 * Author:  Azige
 * Created: 2018-9-15
 */

CREATE TABLE `school_record` (
  `account_id` bigint(20) NOT NULL,
  `school_id` bigint(20) NOT NULL,
  `ability_1_level` int(11) NOT NULL,
  `ability_2_level` int(11) NOT NULL,
  `ability_3_level` int(11) NOT NULL,
  `ability_4_level` int(11) NOT NULL,
  `ability_5_level` int(11) NOT NULL,
  `ability_6_level` int(11) NOT NULL,
  `ability_7_level` int(11) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
