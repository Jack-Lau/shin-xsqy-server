/*
 * Created 2018-7-28 16:14:16
 */
/**
 * Author:  Azige
 * Created: 2018-7-28
 */

ALTER TABLE kbdzp_record ADD COLUMN kuaibi_pool bigint(20) NOT NULL DEFAULT 10;
ALTER TABLE kbdzp_record ADD COLUMN kuaibi_pool_last_reset datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE kbdzp_shared_record ADD COLUMN public_pool bigint(20) NOT NULL DEFAULT 41600;
