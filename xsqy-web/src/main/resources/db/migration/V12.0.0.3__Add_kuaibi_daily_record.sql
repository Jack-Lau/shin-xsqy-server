/*
 * Created 2018-11-3 12:00:00
 */
/**
 * Author:  Azige
 * Created: 2018-11-3
 */

DROP TABLE IF EXISTS `kuaibi_daily_record`;
CREATE TABLE `kuaibi_daily_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `airdrop_milli_kuaibi` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL,
  `destroy_milli_kuaibi` bigint(20) NOT NULL,
  `maintenance_milli_kuaibi` bigint(20) NOT NULL,
  `rebate_milli_kuaibi_from_other` bigint(20) NOT NULL,
  `rebate_milli_kuaibi_from_player_interactive` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

INSERT INTO `kuaibi_daily_record`
VALUES ('1', '0', CURRENT_TIMESTAMP, 90000, 10000, 100000, 5000);
