/*
 * Created 2018-9-8 16:35:03
 */
/**
 * Author:  Azige
 * Created: 2018-9-8
 */

ALTER TABLE account ADD COLUMN create_time datetime DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE account MODIFY COLUMN create_time datetime NOT NULL;
ALTER TABLE player ADD COLUMN create_time datetime DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE player MODIFY COLUMN create_time datetime NOT NULL;
