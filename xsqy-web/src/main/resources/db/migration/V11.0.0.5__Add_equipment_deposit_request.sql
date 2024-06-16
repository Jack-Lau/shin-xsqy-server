/*
 * Created 2018-10-27 12:15:51
 */
/**
 * Author:  Azige
 * Created: 2018-10-27
 */

CREATE TABLE `equipment_deposit_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `equipment_id` bigint(20) NOT NULL,
  `transaction_hash` varchar(255) NOT NULL,
  `request_status` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_request_status` (`request_status`),
  KEY `IDX_account_id_request_status` (`account_id`,`request_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
