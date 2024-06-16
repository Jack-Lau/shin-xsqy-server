/*
 * Created 2018-11-12 17:56:54
 */
package cn.com.yting.kxy.web.auction;

import java.io.Serializable;

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
@Table(name = "auction_record")
@Data
@WebMessageType
public class AuctionRecord implements Serializable {

    @Id
    private long accountId;
    @Column(name = "liked_today", nullable = false)
    private int likedToday;
    @Column(name = "liked_today_limit", nullable = false)
    private int likedTodayLimit;
    @Column(name = "stock_yb", nullable = false)
    private long stockYb;
    @Column(name = "locked_yb", nullable = false)
    private long lockedYb;

    public void increaseLikedToday() {
        likedToday++;
    }

    public void increaseStockYb(long value) {
        stockYb += value;
    }

    public void decreaseStockYb(long value) {
        stockYb -= value;
    }

    public void increaseLockedYb(long value) {
        lockedYb += value;
    }

    public void decreaseLockedYb(long value) {
        lockedYb -= value;
    }

}
