/*
 * Created 2016-4-20 10:56:24
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.record.ActionRecord;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Azige
 */
public class BattleResult {

    /**
     * @return the unitInitInfo
     */
    public List<UnitInitInfo> getUnitInitInfo() {
        return unitInitInfo;
    }

    /**
     * @param unitInitInfo the unitInitInfo to set
     */
    public void setUnitInitInfo(List<UnitInitInfo> unitInitInfo) {
        this.unitInitInfo = unitInitInfo;
    }

    /**
     * @return the battleStartAction
     */
    public List<ActionRecord> getBattleStartAction() {
        return battleStartAction;
    }

    /**
     * @param battleStartAction the battleStartAction to set
     */
    public void setBattleStartAction(List<ActionRecord> battleStartAction) {
        this.battleStartAction = battleStartAction;
    }

    /**
     * @return the turnInfo
     */
    public List<TurnInfo> getTurnInfo() {
        return turnInfo;
    }

    /**
     * @param turnInfo the turnInfo to set
     */
    public void setTurnInfo(List<TurnInfo> turnInfo) {
        this.turnInfo = turnInfo;
    }

    /**
     * @return the statistics
     */
    public BattleStatistics getStatistics() {
        return statistics;
    }

    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(BattleStatistics statistics) {
        this.statistics = statistics;
    }

    public static class TurnInfo {

        private int turnCount;
        private boolean battleEnd;
        private List<UnitStatus> unitStatus = new ArrayList<>();
        private List<UnitStatus> endOfTurnUnitStatus = new ArrayList<>();
        private List<ActionRecord> actionRecord = new ArrayList<>();

        /**
         * @return the turnCount
         */
        public int getTurnCount() {
            return turnCount;
        }

        /**
         * @param turnCount the turnCount to set
         */
        public void setTurnCount(int turnCount) {
            this.turnCount = turnCount;
        }

        /**
         * @return the battleEnd
         */
        public boolean isBattleEnd() {
            return battleEnd;
        }

        /**
         * @param battleEnd the battleEnd to set
         */
        public void setBattleEnd(boolean battleEnd) {
            this.battleEnd = battleEnd;
        }

        /**
         * @return the unitStatus
         */
        public List<UnitStatus> getUnitStatus() {
            return unitStatus;
        }

        /**
         * @param unitStatus the unitStatus to set
         */
        public void setUnitStatus(List<UnitStatus> unitStatus) {
            this.unitStatus = unitStatus;
        }

        public List<UnitStatus> getEndOfTurnUnitStatus() {
            return endOfTurnUnitStatus;
        }

        public void setEndOfTurnUnitStatus(List<UnitStatus> endOfTurnUnitStatus) {
            this.endOfTurnUnitStatus = endOfTurnUnitStatus;
        }

        /**
         * @return the actionRecord
         */
        public List<ActionRecord> getActionRecord() {
            return actionRecord;
        }

        /**
         * @param actionRecord
         */
        public void setActionRecord(List<ActionRecord> actionRecord) {
            this.actionRecord = actionRecord;
        }

    }

    public static class BattleStatistics {

        private int turnCount;
        private Stance winStance;
        private Party redParty;
        private Party blueParty;

        /**
         * @return the turnCount
         */
        public int getTurnCount() {
            return turnCount;
        }

        /**
         * @param turnCount the turnCount to set
         */
        public void setTurnCount(int turnCount) {
            this.turnCount = turnCount;
        }

        /**
         * @return the redParty
         */
        public Party getRedParty() {
            return redParty;
        }

        /**
         * @param redParty the redParty to set
         */
        public void setRedParty(Party redParty) {
            this.redParty = redParty;
        }

        /**
         * @return the blueParty
         */
        public Party getBlueParty() {
            return blueParty;
        }

        /**
         * @param blueParty the blueParty to set
         */
        public void setBlueParty(Party blueParty) {
            this.blueParty = blueParty;
        }

        /**
         * @return the winStance
         */
        public Stance getWinStance() {
            return winStance;
        }

        /**
         * @param winStance the winStance to set
         */
        public void setWinStance(Stance winStance) {
            this.winStance = winStance;
        }

    }

    private List<UnitInitInfo> unitInitInfo = new ArrayList<>();
    private List<ActionRecord> battleStartAction = new ArrayList<>();
    private List<TurnInfo> turnInfo = new ArrayList<>();
    private BattleStatistics statistics = new BattleStatistics();

}
