/*
 * Created 2018-8-9 17:06:25
 */
package cn.com.yting.kxy.web.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "player_relation")
@Data
@WebMessageType
public class PlayerRelation implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "title_id")
    private Long titleId;
    @Column(name = "fashion_id")
    private Long fashionId;
    @Column(name = "hand_equipment_id")
    private Long handEquipmentId;
    @Column(name = "body_equipment_id")
    private Long bodyEquipmentId;
    @Column(name = "waist_equipment_id")
    private Long waistEquipmentId;
    @Column(name = "foot_equipment_id")
    private Long footEquipmentId;
    @Column(name = "head_equipment_id")
    private Long headEquipmentId;
    @Column(name = "neck_equipment_id")
    private Long neckEquipmentId;
    @Column(name = "battle_pet_id_1")
    private Long battlePetId1;
    @Column(name = "battle_pet_id_2")
    private Long battlePetId2;
    @Column(name = "battle_pet_id_3")
    private Long battlePetId3;

    public List<Long> toEquipmentIds() {
        return Arrays.asList(
                handEquipmentId,
                bodyEquipmentId,
                waistEquipmentId,
                footEquipmentId,
                headEquipmentId,
                neckEquipmentId
        );
    }

    public void importBattlePetIdList(List<Long> petIds) {
        if (petIds.size() < 3) {
            petIds = new ArrayList<>(petIds);
            while (petIds.size() < 3) {
                petIds.add(null);
            }
        }

        battlePetId1 = petIds.get(0);
        battlePetId2 = petIds.get(1);
        battlePetId3 = petIds.get(2);
    }

    public List<Long> toBattlePetIds() {
        return Arrays.asList(
                battlePetId1,
                battlePetId2,
                battlePetId3
        );
    }
}
