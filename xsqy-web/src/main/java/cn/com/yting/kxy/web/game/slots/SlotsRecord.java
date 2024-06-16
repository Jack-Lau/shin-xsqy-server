/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "slots_record")
@Data
@WebMessageType
public class SlotsRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "slots")
    private String slots;
    @Column(name = "locks")
    private String locks;
    @Column(name = "like_big_prize_ids", length = 5000)
    private String likeBigPrizeIds;
    @Column(name = "taken_prize")
    private boolean takenPrize;
    @Column(name = "like_send")
    private long likeSend;
    @Column(name = "like_receive")
    private long likeReceive;

    @Setter(AccessLevel.NONE)
    private transient List<Integer> slotsList;
    @Setter(AccessLevel.NONE)
    private transient List<Integer> locksList;
    @Setter(AccessLevel.NONE)
    private transient List<Long> likeBigPrizeIdList;

    public List<Integer> getSlots() {
        if (slotsList == null) {
            slotsList = new ArrayList<>();
            if (slots != null) {
                for (int i = 0; i < slots.length(); i++) {
                    slotsList.add(Integer.parseInt(slots.charAt(i) + ""));
                }
            }
        }
        return slotsList;
    }

    public void setSlots(List<Integer> slotsList) {
        this.slotsList = slotsList;
        slots = "";
        for (int i = 0; i < slotsList.size(); i++) {
            slots += slotsList.get(i);
        }
    }

    public List<Integer> getLocks() {
        if (locksList == null) {
            locksList = new ArrayList<>();
            if (locks != null) {
                for (int i = 0; i < locks.length(); i++) {
                    locksList.add(Integer.parseInt(locks.charAt(i) + ""));
                }
            }
        }
        return locksList;
    }

    public void setLocks(List<Integer> locksList) {
        this.locksList = locksList;
        locks = "";
        for (int i = 0; i < locksList.size(); i++) {
            locks += locksList.get(i);
        }
    }

    public List<Long> getLikeBigPrizeIds() {
        if (likeBigPrizeIdList == null) {
            likeBigPrizeIdList = new ArrayList<>();
            if (likeBigPrizeIds != null && !"".equals(likeBigPrizeIds)) {
                List<String> ids = Arrays.stream(likeBigPrizeIds.split(","))
                        .collect(Collectors.toList());
                ids.forEach((id) -> {
                    likeBigPrizeIdList.add(Long.parseLong(id));
                });
            }
        }
        return likeBigPrizeIdList;
    }

    public void setLikeBigPrizeIds(List<Long> likeBigPrizeIdList) {
        this.likeBigPrizeIdList = likeBigPrizeIdList;
        likeBigPrizeIds = "";
        for (int i = 0; i < likeBigPrizeIdList.size(); i++) {
            if (i == 0) {
                likeBigPrizeIds += likeBigPrizeIdList.get(i);
            } else {
                likeBigPrizeIds += "," + likeBigPrizeIdList.get(i);
            }
        }
    }

    public int getLockCount() {
        int result = 0;
        result = getLocks().stream().map((lock) -> lock).reduce(result, Integer::sum);
        return result;
    }

}
