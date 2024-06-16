/*
 * Created 2018-10-25 15:42:28
 */
/**
 * Author:  Azige
 * Created: 2018-10-25
 */

ALTER TABLE equipment ADD COLUMN nft_id bigint(20);
ALTER TABLE equipment ADD COLUMN `number` int(11);
ALTER TABLE equipment ADD COLUMN next_withdraw_time datetime;
ALTER TABLE equipment ADD UNIQUE KEY IDX_nft_id (nft_id);
