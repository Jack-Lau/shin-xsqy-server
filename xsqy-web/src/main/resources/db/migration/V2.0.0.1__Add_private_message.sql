/*
 * Created 2018-8-7 15:46:38
 */
/**
 * Author:  Azige
 * Created: 2018-8-7
 */

CREATE TABLE `private_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sender_account_id` bigint(20) NOT NULL,
  `receiver_account_id` bigint(20) NOT NULL,
  `conversation` varchar(255) NOT NULL,
  `content` varchar(5000) NOT NULL,
  `event_time` datetime NOT NULL,
  `already_read` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_receiver_account_id_sender_account_id` (`receiver_account_id`,`sender_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
