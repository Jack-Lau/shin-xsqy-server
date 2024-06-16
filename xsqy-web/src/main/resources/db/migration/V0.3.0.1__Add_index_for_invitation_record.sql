/*
 * Created 2018-7-30 15:48:06
 */
/**
 * Author:  Azige
 * Created: 2018-7-30
 */

ALTER TABLE invitation_record ADD KEY IDX_inviter_id_inviter_depth (inviter_id, inviter_depth);
