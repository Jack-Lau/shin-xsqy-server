/*
 * Created 2018-11-15 11:44:54
 */
/**
 * Author:  Azige
 * Created: 2018-11-15
 */

ALTER TABLE kbdzp_record ADD COLUMN total_gain_milliKC bigint(20) NOT NULL DEFAULT 0;

INSERT INTO mail (account_id, title, content, create_time, attachment)
SELECT a.id, "称号解锁", "亲爱的游侠：恭喜您的累计邀请人数达到30人，成功解锁称号“有人替我撑腰”，请您查收~", CURRENT_TIMESTAMP, "20005:1"
FROM account a
JOIN player p ON a.id = p.account_id
JOIN (SELECT inviter_id, COUNT(*) AS c FROM invitation_record WHERE inviter_depth = 1 GROUP BY inviter_id HAVING c >= 30) t1 ON a.id = t1.inviter_id
WHERE p.player_level >= 30;

INSERT INTO quest_record (account_id, quest_id, quest_status, results, objective_status, random_bac_id, started_count)
SELECT account_id, 710090, "IN_PROGRESS", "", "F", NULL, 0
FROM quest_record
WHERE quest_id = 700104 AND results = "A";
