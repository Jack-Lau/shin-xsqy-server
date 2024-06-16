/*
 * Created 2018-8-10 18:04:38
 */
/**
 * Author:  Azige
 * Created: 2018-8-10
 */

-- 添加等级和转生次数
ALTER TABLE player ADD COLUMN player_level int(11) NOT NULL DEFAULT 0;
ALTER TABLE player ADD COLUMN samsara_count int(11) NOT NULL DEFAULT 0;
