/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "idle_mine_record")
@Data
@WebMessageType
public class IdleMineRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "available_mine_queue_count", nullable = false)
    private int availableMineQueueCount;

    @Column(name = "mine_queue_team_id_1")
    private Long mineQueueTeamId_1;
    @Column(name = "mine_queue_map_id_1")
    private Long mineQueueMapId_1;
    @Column(name = "mine_queue_finish_time_1")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueFinishTime_1;
    @Column(name = "mine_queue_last_balance_time_1")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueLastBalanceTime_1;
    //
    @Column(name = "mine_queue_team_id_2")
    private Long mineQueueTeamId_2;
    @Column(name = "mine_queue_map_id_2")
    private Long mineQueueMapId_2;
    @Column(name = "mine_queue_finish_time_2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueFinishTime_2;
    @Column(name = "mine_queue_last_balance_time_2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueLastBalanceTime_2;
    //
    @Column(name = "mine_queue_team_id_3")
    private Long mineQueueTeamId_3;
    @Column(name = "mine_queue_map_id_3")
    private Long mineQueueMapId_3;
    @Column(name = "mine_queue_finish_time_3")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueFinishTime_3;
    @Column(name = "mine_queue_last_balance_time_3")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mineQueueLastBalanceTime_3;

    @Column(name = "idle_mine_reward")
    private String idleMineReward;

    @Setter(AccessLevel.NONE)
    private transient List<IdleMineQueue> idleMineQueueList;
    @Setter(AccessLevel.NONE)
    private transient List<IdleMineReward> idleMineRewardList;

    public List<IdleMineQueue> getIdleMineQueueList() {
        if (idleMineQueueList == null) {
            idleMineQueueList = new ArrayList<>();
            idleMineQueueList.add(new IdleMineQueue(mineQueueTeamId_1, mineQueueMapId_1, mineQueueFinishTime_1, mineQueueLastBalanceTime_1));
            idleMineQueueList.add(new IdleMineQueue(mineQueueTeamId_2, mineQueueMapId_2, mineQueueFinishTime_2, mineQueueLastBalanceTime_2));
            idleMineQueueList.add(new IdleMineQueue(mineQueueTeamId_3, mineQueueMapId_3, mineQueueFinishTime_3, mineQueueLastBalanceTime_3));
        }
        return idleMineQueueList;
    }

    public void setIdleMineQueueList(List<IdleMineQueue> queues) {
        idleMineQueueList = queues;
        mineQueueTeamId_1 = queues.get(0).getTeamId();
        mineQueueMapId_1 = queues.get(0).getMapId();
        mineQueueFinishTime_1 = queues.get(0).getFinishTime();
        mineQueueLastBalanceTime_1 = queues.get(0).getLastBalanceTime();
        //
        mineQueueTeamId_2 = queues.get(1).getTeamId();
        mineQueueMapId_2 = queues.get(1).getMapId();
        mineQueueFinishTime_2 = queues.get(1).getFinishTime();
        mineQueueLastBalanceTime_2 = queues.get(1).getLastBalanceTime();
        //
        mineQueueTeamId_3 = queues.get(2).getTeamId();
        mineQueueMapId_3 = queues.get(2).getMapId();
        mineQueueFinishTime_3 = queues.get(2).getFinishTime();
        mineQueueLastBalanceTime_3 = queues.get(2).getLastBalanceTime();
    }

    public List<IdleMineReward> getIdleMineRewardList() {
        if (idleMineRewardList == null) {
            idleMineRewardList = new ArrayList<>();
            if (this.idleMineReward != null) {
                List<String> rewardStrList = Arrays.stream(this.idleMineReward.split(","))
                        .collect(Collectors.toList());
                for (String str : rewardStrList) {
                    idleMineRewardList.add(IdleMineReward.fromString(str));
                }
            }
        }
        return idleMineRewardList;
    }

    public void addIdleMineReward(List<IdleMineReward> rewards) {
        List<IdleMineReward> rewardList = getIdleMineRewardList();
        for (IdleMineReward reward : rewards) {
            boolean combined = false;
            for (int i = 0; i < rewardList.size(); i++) {
                IdleMineReward oldReward = rewardList.get(i);
                if (reward.getCurrencyId() == oldReward.getCurrencyId()) {
                    oldReward.setCurrencyAmount(oldReward.getCurrencyAmount() + reward.getCurrencyAmount());
                    rewardList.set(i, oldReward);
                    combined = true;
                    break;
                }
            }
            if (!combined) {
                rewardList.add(reward);
            }
        }
        //
        idleMineRewardList = rewardList;
        idleMineReward = null;
        for (int i = 0; i < idleMineRewardList.size(); i++) {
            if (i == 0) {
                idleMineReward = idleMineRewardList.get(i).toString();
            } else {
                idleMineReward += "," + idleMineRewardList.get(i).toString();
            }
        }
    }

    public void clearIdleMineReward() {
        this.idleMineReward = null;
        if (this.idleMineRewardList != null) {
            this.idleMineRewardList.clear();
        }
    }

    @Data
    public static class IdleMineQueue {

        private Long teamId;
        private Long mapId;
        private Date finishTime;
        private Date lastBalanceTime;

        public IdleMineQueue() {

        }

        public IdleMineQueue(Long teamId, Long mapId, Date finishTime, Date lastBalanceTime) {
            this.teamId = teamId;
            this.mapId = mapId;
            this.finishTime = finishTime;
            this.lastBalanceTime = lastBalanceTime;
        }

    }

    @Data
    public static class IdleMineReward {

        private long id;
        private long currencyId;
        private long currencyAmount;

        public IdleMineReward() {

        }

        public IdleMineReward(long id, long currencyId, long currencyAmount) {
            this.id = id;
            this.currencyId = currencyId;
            this.currencyAmount = currencyAmount;
        }

        public static IdleMineReward fromString(String str) {
            IdleMineReward reward = new IdleMineReward();
            List<String> elements = Arrays.stream(str.split("-"))
                    .collect(Collectors.toList());
            reward.setId(Long.parseLong(elements.get(0)));
            reward.setCurrencyId(Long.parseLong(elements.get(1)));
            reward.setCurrencyAmount(Long.parseLong(elements.get(2)));
            return reward;
        }

        @Override
        public String toString() {
            return id + "-" + currencyId + "-" + currencyAmount;
        }

    }

}
