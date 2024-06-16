/*
 * Created 2018-8-21 11:24:04
 */
/**
 * Author:  Azige
 * Created: 2018-8-21
 */

CREATE TABLE `account_passcode` (
  `account_id` bigint(20) NOT NULL,
  `passcode_type` varchar(255) NOT NULL,
  `passcode` varchar(255) NOT NULL,
  PRIMARY KEY (`account_id`,`passcode_type`),
  KEY `IDX_passcode_type_passcode` (`passcode_type`,`passcode`),
  CONSTRAINT `FK_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO account_passcode SELECT id, passcode_type, passcode FROM account;
ALTER TABLE account DROP COLUMN passcode;
ALTER TABLE account DROP COLUMN passcode_type;
