/*
 * Created 2018-11-16 17:15:54
 */
/**
 * Author:  Azige
 * Created: 2018-11-16
 */

CREATE TABLE `auction_record` (
  `account_id` bigint(20) NOT NULL,
  `liked_today` int(11) NOT NULL,
  `liked_today_limit` int(11) NOT NULL,
  `stock_kuaibi` bigint(20) NOT NULL,
  `locked_kuaibi` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `commodity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `definition_id` bigint(20) NOT NULL,
  `queue_number` int(11) NOT NULL,
  `commodity_status` varchar(255) NOT NULL,
  `last_bid` bigint(20) NOT NULL,
  `last_bidder_account_id` bigint(20) DEFAULT NULL,
  `deadline` datetime DEFAULT NULL,
  `broadcast_published` bit(1) NOT NULL,
  `delivered` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_commodity_status` (`commodity_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `commodity_player_record` (
  `commodity_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `bidded` bit(1) NOT NULL,
  `like_count` int(11) NOT NULL,
  PRIMARY KEY (`commodity_id`,`account_id`),
  KEY `IDX_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `auction_shared_record` (
  `id` bigint(20) NOT NULL,
  `paused_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
