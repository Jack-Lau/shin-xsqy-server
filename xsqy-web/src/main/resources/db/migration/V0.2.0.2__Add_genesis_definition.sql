/*
 * Created 2018-7-27 18:37:48
 */
/**
 * Author:  Azige
 * Created: 2018-7-27
 */

-- 为 player 表添加 genesis 字段，并将前 9999 个玩家的值设为 true
ALTER TABLE player ADD COLUMN genesis BIT(1) NOT NULL DEFAULT FALSE;
UPDATE player SET genesis = TRUE ORDER BY account_id LIMIT 9999;

-- 提高 inviter_record 中 invitation_limit 的默认值，并将“创世居民”的值进一步提高
UPDATE inviter_record SET invitation_limit = 10 WHERE invitation_limit < 10;
UPDATE inviter_record LEFT JOIN player on inviter_record.account_id = player.account_id SET invitation_limit = 30 WHERE player.genesis IS TRUE AND invitation_limit < 30;
