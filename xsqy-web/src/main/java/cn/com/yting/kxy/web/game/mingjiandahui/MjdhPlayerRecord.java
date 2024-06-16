/*
 * Created 2018-12-12 12:08:53
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
@Table(name = "mjdh_player_record")
@Data
@IdClass(MjdhPlayerRecord.PK.class)
@WebMessageType
public class MjdhPlayerRecord implements Serializable {

    @Id
    @Column(name = "season_id")
    private long seasonId;
    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "grade")
    private int grade;
    @Column(name = "consecutive_win_count")
    private int consecutiveWinCount;
    @Column(name = "daily_first_win")
    private boolean dailyFirstWin;
    @Column(name = "daily_battle_count")
    private int dailyBattleCount;
    @Column(name = "daily_consecutive_win_count")
    private int dailyConsecutiveWinCount;
    @Column(name = "daily_first_win_award_delivered")
    private boolean dailyFirstWinAwardDelivered;
    @Column(name = "daily_ten_battle_award_delivered")
    private boolean dailyTenBattleAwardDelivered;
    @Column(name = "daily_consecutive_win_award_available")
    private boolean dailyConsecutiveWinAwardAvailable;
    @Column(name = "daily_consecutive_win_award_delivered")
    private boolean dailyConsecutiveWinAwardDelivered;

    public int getCappedGrade() {
        if (grade > MjdhConstants.GRADE_王者) {
            return MjdhConstants.GRADE_王者;
        } else if (grade < MjdhConstants.GRADE_青铜五) {
            return MjdhConstants.GRADE_青铜五;
        } else {
            return grade;
        }
    }

    public void increaseGrade() {
        grade++;
    }

    public void decreaseGrade() {
        grade--;
    }

    public void increaseConsecutiveWinCount() {
        consecutiveWinCount++;
    }

    public void increaseDailyBattleCount() {
        dailyBattleCount++;
    }

    public void increaseDailyConsecutiveWinCount() {
        dailyConsecutiveWinCount++;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long seasonId;
        private long accountId;
    }
}
