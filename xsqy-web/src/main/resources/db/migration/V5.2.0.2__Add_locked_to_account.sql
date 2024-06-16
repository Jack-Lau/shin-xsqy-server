/*
 * Created 2018-9-10 17:37:24
 */
/**
 * Author:  Azige
 * Created: 2018-9-10
 */

ALTER TABLE account ADD COLUMN locked bit(1) NOT NULL DEFAULT FALSE;
