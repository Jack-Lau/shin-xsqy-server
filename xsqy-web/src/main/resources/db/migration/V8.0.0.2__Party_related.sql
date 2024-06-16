/*
 * Created 2018-9-26 16:58:28
 */
/**
 * Author:  Azige
 * Created: 2018-9-26
 */

-- 为 player 的 fc 添加索引以便按 fc 查询
ALTER TABLE player ADD KEY IDX_fc (fc);

CREATE TABLE `party_record` (
  `account_id` bigint(20) NOT NULL,
  `candidate_supporters` varchar(1000) NOT NULL,
  `high_level_candidate` bit(1) NOT NULL,
  `last_reward_resolve_time` datetime NOT NULL,
  `support_reward` bigint(20) NOT NULL,
  `today_reward_delivered` bit(1) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `support_relation` (
  `inviter_account_id` bigint(20) NOT NULL,
  `supporter_account_id` bigint(20) NOT NULL,
  `deadline` datetime NOT NULL,
  `release_cool_down` datetime DEFAULT NULL,
  `released` bit(1) NOT NULL,
  `release_cooldown` datetime DEFAULT NULL,
  PRIMARY KEY (`inviter_account_id`,`supporter_account_id`),
  KEY `IDX_supporter_account_id` (`supporter_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `support_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inviter_account_id` bigint(20) NOT NULL,
  `supporter_account_id` bigint(20) NOT NULL,
  `event_time` datetime DEFAULT NULL,
  `fee` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_supporter_account_id_event_time` (`supporter_account_id`,`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
