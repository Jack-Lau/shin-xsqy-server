/*
 * Created 2018-10-29 12:53:29
 */
/**
 * Author:  Azige
 * Created: 2018-10-29
 */

UPDATE player_relation pr LEFT JOIN equipment e ON pr.hand_equipment_id = e.id SET pr.hand_equipment_id = NULL WHERE pr.hand_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN equipment e ON pr.body_equipment_id = e.id SET pr.body_equipment_id = NULL WHERE pr.body_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN equipment e ON pr.foot_equipment_id = e.id SET pr.foot_equipment_id = NULL WHERE pr.foot_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN equipment e ON pr.head_equipment_id = e.id SET pr.head_equipment_id = NULL WHERE pr.head_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN equipment e ON pr.neck_equipment_id = e.id SET pr.neck_equipment_id = NULL WHERE pr.neck_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN equipment e ON pr.waist_equipment_id = e.id SET pr.waist_equipment_id = NULL WHERE pr.waist_equipment_id IS NOT NULL AND e.id IS NULL;
UPDATE player_relation pr LEFT JOIN pet p ON pr.battle_pet_id_1 = p.id SET pr.battle_pet_id_1 = NULL WHERE pr.battle_pet_id_1 IS NOT NULL AND p.id IS NULL;
UPDATE player_relation pr LEFT JOIN pet p ON pr.battle_pet_id_2 = p.id SET pr.battle_pet_id_2 = NULL WHERE pr.battle_pet_id_2 IS NOT NULL AND p.id IS NULL;
UPDATE player_relation pr LEFT JOIN pet p ON pr.battle_pet_id_3 = p.id SET pr.battle_pet_id_3 = NULL WHERE pr.battle_pet_id_3 IS NOT NULL AND p.id IS NULL;

ALTER TABLE player_relation ADD CONSTRAINT FK_hand_equipment_id FOREIGN KEY (hand_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_body_equipment_id FOREIGN KEY (body_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_foot_equipment_id FOREIGN KEY (foot_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_head_equipment_id FOREIGN KEY (head_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_neck_equipment_id FOREIGN KEY (neck_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_waist_equipment_id FOREIGN KEY (waist_equipment_id) REFERENCES equipment (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_battle_pet_id_1 FOREIGN KEY (battle_pet_id_1) REFERENCES pet (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_battle_pet_id_2 FOREIGN KEY (battle_pet_id_2) REFERENCES pet (id) ON DELETE SET NULL;
ALTER TABLE player_relation ADD CONSTRAINT FK_battle_pet_id_3 FOREIGN KEY (battle_pet_id_3) REFERENCES pet (id) ON DELETE SET NULL;
