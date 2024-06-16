/*
 * Created 2018-10-31 16:58:35
 */
/**
 * Author:  Azige
 * Created: 2018-10-31
 */

ALTER TABLE currency_record ADD KEY IDX_currency_id (currency_id);

CREATE TABLE `ranking_record` (
  `ranking_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `object_id` bigint(20) NOT NULL,
  `ranking_value_1` bigint(20) NOT NULL,
  `ranking_value_2` bigint(20) NOT NULL,
  `ranking_value_3` bigint(20) NOT NULL,
  `ranking_value_4` bigint(20) NOT NULL,
  `ranking_value_5` bigint(20) NOT NULL,
  `last_modified` datetime NOT NULL,
  PRIMARY KEY (`ranking_id`,`account_id`,`object_id`),
  KEY `IDX_ranking` (`ranking_id`,`ranking_value_1`,`ranking_value_2`,`ranking_value_3`,`ranking_value_4`,`ranking_value_5`,`last_modified`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
