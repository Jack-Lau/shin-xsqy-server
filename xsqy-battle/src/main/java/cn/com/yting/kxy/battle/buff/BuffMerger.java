/*
 * Created 2017-3-13 10:50:44
 */
package cn.com.yting.kxy.battle.buff;

/**
 *
 * @author Azige
 */
public interface BuffMerger{

    Buff merge(Buff origin, Buff newComer);

}
