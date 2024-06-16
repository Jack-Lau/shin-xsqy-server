/*
 * Created 2018-12-27 17:03:04
 */
/**
 * Author:  Azige
 * Created: 2018-12-27
 */

CREATE TABLE `consignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seller_account_id` bigint(20) NOT NULL,
  `goods_type` varchar(255) NOT NULL,
  `goods_object_id` bigint(20) NOT NULL,
  `goods_definition_id` bigint(20) NOT NULL,
  `price` bigint(20) NOT NULL,
  `previous_price` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `deadline` datetime DEFAULT NULL,
  `sold` bit(1) NOT NULL,
  `deal_time` datetime DEFAULT NULL,
  `buyer_account_id` bigint(20) DEFAULT NULL,
  `goods_delivered` bit(1) NOT NULL,
  `payment_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_seller_account_id` (`seller_account_id`),
  KEY `IDX_buyer_account_id` (`buyer_account_id`),
  KEY `IDX_sold_deadline_goods_type` (`sold`,`deadline`,`goods_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `consignment_marker` (
  `account_id` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`,`consignment_id`),
  CONSTRAINT `FK_marker_consignment_id` FOREIGN KEY (`consignment_id`) REFERENCES `consignment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `consignment_equipment_info` (
  `consignment_id` bigint(20) NOT NULL,
  `color` int(11) NOT NULL,
  `part` int(11) NOT NULL,
  `patk` int(11) NOT NULL,
  `matk` int(11) NOT NULL,
  `fc` bigint(20) NOT NULL,
  `max_enhance_level` int(11) NOT NULL,
  PRIMARY KEY (`consignment_id`),
  CONSTRAINT `FK_equipment_info_consignment_id` FOREIGN KEY (`consignment_id`) REFERENCES `consignment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `consignment_equipment_effect` (
  `consignment_id` bigint(20) NOT NULL,
  `effect_id` bigint(20) NOT NULL,
  PRIMARY KEY (`consignment_id`,`effect_id`),
  CONSTRAINT `FK_equipment_effect_consignment_id` FOREIGN KEY (`consignment_id`) REFERENCES `consignment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `consignment_pet_info` (
  `consignment_id` bigint(20) NOT NULL,
  `aptitude_hp` int(11) NOT NULL,
  `aptitude_atk` int(11) NOT NULL,
  `aptitude_pdef` int(11) NOT NULL,
  `aptitude_mdef` int(11) NOT NULL,
  `aptitude_spd` int(11) NOT NULL,
  `pet_rank` int(11) NOT NULL,
  `max_pet_rank` int(11) NOT NULL,
  PRIMARY KEY (`consignment_id`),
  CONSTRAINT `FK_pet_info_consignment_id` FOREIGN KEY (`consignment_id`) REFERENCES `consignment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `consignment_pet_ability` (
  `consignment_id` bigint(20) NOT NULL,
  `ability_id` bigint(20) NOT NULL,
  PRIMARY KEY (`consignment_id`,`ability_id`),
  CONSTRAINT `FK_pet_ability_consignment_id` FOREIGN KEY (`consignment_id`) REFERENCES `consignment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
