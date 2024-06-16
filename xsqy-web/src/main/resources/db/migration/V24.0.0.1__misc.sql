/*
 * Created 2019-1-24 12:49:32
 */
/**
 * Author:  Azige
 * Created: 2019-1-24
 */

ALTER TABLE player ADD COLUMN online_time_count bigint(20) NOT NULL DEFAULT 0;
ALTER TABLE pet ADD COLUMN legendary bit(1) NOT NULL DEFAULT FALSE;
UPDATE secret_shop_shared_record SET kc_pack_remain_count = 1000000;
UPDATE price_record SET current_value = 50 WHERE id = 4400003;

CREATE TABLE `fxjl_record` (
  `account_id` bigint(20) NOT NULL,
  `award_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `fxjl_shared_record` (
  `id` bigint(20) NOT NULL,
  `quest_id_1` bigint(20) NOT NULL,
  `quest_id_2` bigint(20) NOT NULL,
  `quest_id_3` bigint(20) NOT NULL,
  `quest_id_4` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `zxjl_record` (
  `account_id` bigint(20) NOT NULL,
  `award_1_delivered` bit(1) NOT NULL,
  `award_2_delivered` bit(1) NOT NULL,
  `award_3_delivered` bit(1) NOT NULL,
  `award_4_delivered` bit(1) NOT NULL,
  `award_5_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `legendary_pet_generation_record` (
  `definition_id` bigint(20) NOT NULL,
  `available_count` int(11) NOT NULL,
  `redeemed_count` int(11) NOT NULL,
  `serial_number` int(11) NOT NULL,
  PRIMARY KEY (`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
