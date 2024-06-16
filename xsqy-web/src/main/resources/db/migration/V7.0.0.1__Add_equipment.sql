/*
 * Created 2018-9-20 18:43:26
 */
/**
 * Author:  Azige
 * Created: 2018-9-20
 */

CREATE TABLE `equipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `base_parameters_text` varchar(2000) NOT NULL,
  `base_fc` int(11) NOT NULL,
  `effects_text` varchar(2000) NOT NULL,
  `enhance_level` int(11) NOT NULL,
  `max_enhance_level` int(11) NOT NULL,
  `highest_enhance_level_ever` int(11) NOT NULL,
  `creator_name` varchar(255),
  PRIMARY KEY (`id`),
  KEY `IDX_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
