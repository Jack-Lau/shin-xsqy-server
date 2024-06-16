/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

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
@Table(name = "friend_record")
@Data
public class FriendRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "friend_ids", length = 2000)
    private String friendIds;

    @Setter(AccessLevel.NONE)
    private transient List<Long> friendIdList;

    public List<Long> getFriends() {
        if (friendIdList == null) {
            friendIdList = new ArrayList<>();
            if (friendIds != null && !"".equals(friendIds)) {
                List<String> idStrList = Arrays.stream(this.friendIds.split(","))
                        .collect(Collectors.toList());
                idStrList.forEach((id) -> {
                    friendIdList.add(Long.parseLong(id));
                });
            }
        }
        return friendIdList;
    }

    public void setFriends(List<Long> friendIdList) {
        this.friendIdList = friendIdList;
        friendIds = "";
        this.friendIdList.forEach((id) -> {
            if (friendIds.length() < 1) {
                friendIds += "" + id;
            } else {
                friendIds += "," + id;
            }
        });
    }

}
