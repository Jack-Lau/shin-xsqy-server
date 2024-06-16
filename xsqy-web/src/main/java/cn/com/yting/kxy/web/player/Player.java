/*
 * Created 2018-6-26 16:02:36
 */
package cn.com.yting.kxy.web.player;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Data
@Table(name = "player")
@WebMessageType
public class Player implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "player_name", nullable = false, unique = true)
    private String playerName;
    @Column(name = "prefab_id", nullable = false)
    private int prefabId;
    @Column(name = "genesis", nullable = false)
    private boolean genesis;
    @Column(name = "player_level", nullable = false)
    private int playerLevel;
    @Column(name = "samsara_count", nullable = false)
    private int samsaraCount;
    @Column(name = "serial_number", nullable = false)
    private long serialNumber;
    @Column(name = "fc", nullable = false)
    private long fc;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "last_login_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    @Column(name = "last_online_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastOnlineTime;
    @Column(name = "online_time_count", nullable = false)
    private long onlineTimeCount;

    public void updateLastOnlineTime(Date now) {
        if (getLastOnlineTime() != null) {
            if (TimeUtils.toOffsetTime(getLastOnlineTime()).getDayOfMonth() != TimeUtils.toOffsetTime(now).getDayOfMonth()) {
                onlineTimeCount = 0;
            } else {
                long diff = now.getTime() - getLastOnlineTime().getTime();
                if (diff > 300_000) {
                    diff = 300_000;
                }
                onlineTimeCount += diff;
            }
        }

        this.lastOnlineTime = now;
    }

    public void increasePlayerLevel() {
        playerLevel++;
    }

    /**
     * @return the accountId
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return the prefabId
     */
    public int getPrefabId() {
        return prefabId;
    }

    /**
     * @return the genesis
     */
    public boolean isGenesis() {
        return genesis;
    }

    /**
     * @return the playerLevel
     */
    public int getPlayerLevel() {
        return playerLevel;
    }

    /**
     * @return the samsaraCount
     */
    public int getSamsaraCount() {
        return samsaraCount;
    }

    /**
     * @return the serialNumber
     */
    public long getSerialNumber() {
        return serialNumber;
    }

    /**
     * @return the fc
     */
    public long getFc() {
        return fc;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @return the lastLoginTime
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @return the lastOnlineTime
     */
    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    /**
     * @return the onlineTimeCount
     */
    public long getOnlineTimeCount() {
        return onlineTimeCount;
    }
}
