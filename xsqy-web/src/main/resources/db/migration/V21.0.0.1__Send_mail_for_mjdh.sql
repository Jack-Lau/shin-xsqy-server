/*
 * Created 2019-1-4 12:51:59
 */
/**
 * Author:  Azige
 * Created: 2019-1-4
 */

INSERT INTO mail (account_id, title, content, create_time, already_read, attachment, attachment_delivered, attachment_source)
SELECT account_id, "王者决战赛季补偿", "亲爱的游侠：以下是对王者决战第一赛季出现的问题发放的补偿，请您查收~对您造成的不便，我们深感抱歉！",
CURRENT_TIMESTAMP, FALSE, "165:100", FALSE, 1006
FROM mjdh_player_record WHERE season_id = 1 AND grade > 10;
