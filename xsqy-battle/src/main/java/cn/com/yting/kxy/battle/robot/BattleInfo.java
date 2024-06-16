/*
 * Created 2016-5-25 15:11:23
 */
package cn.com.yting.kxy.battle.robot;

/**
 *
 * @author Azige
 */
public interface BattleInfo{

    BattleInfo DUMMY = new BattleInfo() {

        @Override
        public boolean isAutomatic(){
            return true;
        }

        @Override
        public int getTurnCount(){
            return 0;
        }
    };

    boolean isAutomatic();

    int getTurnCount();
}
