/*
 * Created 2018-7-27 10:53:09
 */
package cn.com.yting.kxy.core.random;

/**
 *
 * @author Azige
 */
public enum RandomSelectType {
    /**
     * 每个元素独立抽选的方式。对于每个元素，权值即概率，根据概率命中与否决定出现或不出现，
     * 抽选结果是任意个元素的结果的集合。
     */
    INDEPENDENT,
    /**
     * 所有元素中产生一个结果抽选的方式。所有的元素的权值相加后，根据随机值命中某一个元素，
     * 抽选结果是那一个元素的结果。
     */
    DEPENDENT;
}
