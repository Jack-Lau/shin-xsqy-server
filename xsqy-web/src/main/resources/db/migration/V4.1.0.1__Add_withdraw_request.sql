/*
 * Created 2018-8-30 12:19:31
 */
/**
 * Author:  Azige
 * Created: 2018-8-30
 */

CREATE TABLE `withdraw_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `transaction_hash` varchar(255) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `fee` bigint(20) NOT NULL,
  `request_status` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDE_request_status` (`request_status`),
  KEY `IDX_account_id_request_status` (`account_id`,`request_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
