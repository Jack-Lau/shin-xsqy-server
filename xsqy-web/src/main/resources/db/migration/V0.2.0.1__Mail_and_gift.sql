/*
 * Created 2018-7-27 16:22:26
 */
/**
 * Author:  Azige
 * Created: 2018-7-27
 */

CREATE TABLE `mail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `already_read` bit(1) NOT NULL DEFAULT FALSE,
  `attachment` varchar(255) NOT NULL,
  `attachment_delivered` bit(1) NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  KEY `IDX_account_id` (`account_id`),
  KEY `IDX_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gift` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gift_definition_id` bigint(20) NOT NULL,
  `code` varchar(255) NOT NULL,
  `redeemed` bit(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `redeemer_account_id` bigint(20) DEFAULT NULL,
  `redeem_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_code` (`code`),
  KEY `IDX_gift_definition_id` (`gift_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gift_generating_record` (
  `id` bigint(20) NOT NULL,
  `prototype_code` varchar(255) NOT NULL,
  `serial_code_begin` int(11) NOT NULL,
  `generated_count` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
