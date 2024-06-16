/*
 * Created 2015-12-22 12:21:57
 */
package cn.com.yting.kxy.battle.buff;

/**
 * 用于测试的空 buff 实现
 *
 * @author Azige
 */
public class SimpleBuffPrototype extends BuffPrototype{

    public SimpleBuffPrototype(String name){
        super(-1, name, Type.默认, BuffDecayType.TURN_END, BuffMergers.overrider());
    }
    
}
