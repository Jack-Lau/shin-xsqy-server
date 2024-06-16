/*
 * Created 2018-10-26 16:34:24
 */
/**
 * Author:  Azige
 * Created: 2018-10-26
 */

INSERT INTO quest_record (account_id, quest_id, quest_status, results, objective_status, random_bac_id, started_count)
SELECT account_id, 710124, "IN_PROGRESS", "", "F", NULL, 0
FROM quest_record
WHERE quest_id = 700121 AND results = "A";
