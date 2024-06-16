/*
 * Created 2018-7-30 16:22:41
 */
/**
 * Author:  Azige
 * Created: 2018-7-30
 */

ALTER TABLE gift DROP KEY IDX_gift_definition_id;
ALTER TABLE gift ADD KEY IDX_gift_definition_id_redeemer_account_id (gift_definition_id, redeemer_account_id);
