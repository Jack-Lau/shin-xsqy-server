/*
 * Created 2018-12-4 16:17:28
 */
/**
 * Author:  Azige
 * Created: 2018-12-4
 */

CREATE TABLE `exchange_record` (
  `account_id` bigint(20) NOT NULL,
  `last_kuaibi_withdraw_time` datetime DEFAULT NULL,
  `kuaibi_withdraw_count` bigint(20) NOT NULL,
  `last_kuaibi_deposit_time` datetime DEFAULT NULL,
  `kuaibi_deposit_count` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `exchange_shared_record` (
  `id` bigint(20) NOT NULL,
  `kuaibi_withdraw_count` bigint(20) NOT NULL,
  `kuaibi_deposit_count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
