/*
 * Created 2018-11-6 17:38:12
 */
/**
 * Author:  Azige
 * Created: 2018-11-6
 */

ALTER TABLE pet ADD COLUMN nft_id bigint(20) DEFAULT NULL;
ALTER TABLE pet ADD COLUMN next_withdraw_time datetime DEFAULT NULL;
ALTER TABLE pet ADD UNIQUE KEY UK_nft_id (nft_id);

CREATE TABLE `pet_withdraw_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `transaction_hash` varchar(255) NOT NULL,
  `fee` bigint(20) NOT NULL,
  `request_status` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `pet_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_request_status` (`request_status`),
  KEY `IDX_account_id_request_status` (`account_id`,`request_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pet_deposit_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `transaction_hash` varchar(255) NOT NULL,
  `request_status` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `pet_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_request_status` (`request_status`),
  KEY `IDX_account_id_request_status` (`account_id`,`request_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
