/*
 * Created 2018-9-11 11:52:10
 */
/**
 * Author:  Azige
 * Created: 2018-9-11
 */

ALTER TABLE yibenwanli_conclusion_log ADD COLUMN award_for_lucky_one bigint(20) NOT NULL DEFAULT 0;
ALTER TABLE yibenwanli_conclusion_log ADD COLUMN lucky_one_account_id bigint(20) NOT NULL DEFAULT -1;
