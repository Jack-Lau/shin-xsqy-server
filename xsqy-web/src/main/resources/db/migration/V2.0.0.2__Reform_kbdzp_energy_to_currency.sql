/*
 * Created 2018-8-10 15:48:18
 */
/**
 * Author:  Azige
 * Created: 2018-8-10
 */

-- 将块币大转盘记录中的能量值转换为货币记录，删除块币大转盘记录的字段以及消耗日志表
DELETE FROM currency_record WHERE currency_id = 152;
INSERT INTO currency_record SELECT account_id, 152, energy FROM kbdzp_record;
ALTER TABLE kbdzp_record DROP COLUMN energy;
DROP TABLE kbdzp_energy_consumption_log;

-- 修正货币变化记录的索引
ALTER TABLE currency_change_log DROP KEY IDX_account_id_currency_id;
ALTER TABLE currency_change_log DROP KEY IDX_event_time;
ALTER TABLE currency_change_log ADD KEY IDX_account_id_currency_id_event_time (account_id, currency_id, event_time);
