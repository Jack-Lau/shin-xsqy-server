/*
 * Created 2018-8-21 18:45:03
 */
/**
 * Author:  Azige
 * Created: 2018-8-21
 */

ALTER TABLE player ADD COLUMN serial_number bigint(20) DEFAULT NULL;
SET @count := 0;
UPDATE player SET serial_number = (@count := @count + 1) ORDER BY account_id;
ALTER TABLE player MODIFY COLUMN serial_number bigint(20) NOT NULL;
