/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "baccarat_bet")
@Data
@WebMessageType
public class BaccaratBet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "game_id")
    private long gameId;
    @Column(name = "bet_0")
    private long bet_0;
    @Column(name = "bet_1")
    private long bet_1;
    @Column(name = "bet_2")
    private long bet_2;
    @Column(name = "bet_3")
    private long bet_3;
    @Column(name = "bet_4")
    private long bet_4;
    @Column(name = "bet_5")
    private long bet_5;
    @Column(name = "total_gain")
    private long totalGain;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    public void addBet(int index, long amount) {
        switch (index) {
            case 0:
                bet_0 += amount;
                break;
            case 1:
                bet_1 += amount;
                break;
            case 2:
                bet_2 += amount;
                break;
            case 3:
                bet_3 += amount;
                break;
            case 4:
                bet_4 += amount;
                break;
            case 5:
                bet_5 += amount;
                break;
        }
    }

    public List<Long> getBets() {
        return Arrays.asList(bet_0, bet_1, bet_2, bet_3, bet_4, bet_5);
    }

    public long getBetsSum() {
        return bet_0 + bet_1 + bet_2 + bet_3 + bet_4 + bet_5;
    }

}
