/*
 * Created 2018-12-12 11:32:53
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

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
@Table(name = "mjdh_winner_record")
@Data
@IdClass(MjdhWinnerRecord.PK.class)
@WebMessageType
public class MjdhWinnerRecord implements Serializable {

    @Id
    @Column(name = "season_id")
    private long seasonId;
    @Id
    @Column(name = "ranking")
    private int ranking;
    @Column(name = "account_id")
    private long accountId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long seasonId;
        private int ranking;
    }
}
