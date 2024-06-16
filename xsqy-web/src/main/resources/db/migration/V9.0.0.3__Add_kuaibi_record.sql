/*
 * Created 2018-10-13 18:45:17
 */
/**
 * Author:  Azige
 * Created: 2018-10-13
 */

CREATE TABLE `kuaibi_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `airdrop_milli_kuaibi` bigint(20) NOT NULL,
  `destroy_milli_kuaibi` bigint(20) NOT NULL,
  `maintenance_milli_kuaibi` bigint(20) NOT NULL,
  `rebate_milli_kuaibi` bigint(20) NOT NULL,
  `rebate_milli_kuaibi_from_other` bigint(20) NOT NULL,
  `rebate_milli_kuaibi_from_player_interactive` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8mb4;
