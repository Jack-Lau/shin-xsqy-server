/*
 * Created 2018-8-30 19:17:44
 */
/**
 * Author:  Azige
 * Created: 2018-8-30
 */

-- 将现有的块币乘以 1000，即转换成毫块币为单位的值
UPDATE currency_record SET amount = amount * 1000 WHERE currency_id = 151;
