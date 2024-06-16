/*
 * Created 2018-11-12 17:11:24
 */
package cn.com.yting.kxy.web.auction;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "commodity_player_record")
@IdClass(CommodityPlayerRecord.PK.class)
@Data
@WebMessageType
public class CommodityPlayerRecord implements Serializable {

    @Id
    @Column(name = "commodity_id")
    private long commodityId;
    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "like_count", nullable = false)
    private int likeCount;
    @Column(name = "bidded", nullable = false)
    private boolean bidded;

    public void increaseLikeCount() {
        likeCount++;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PK implements Serializable {

        private long commodityId;
        private long accountId;
    }
}
