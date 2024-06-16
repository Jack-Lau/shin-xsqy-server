/*
 * Created 2017-3-14 12:33:48
 */
package cn.com.yting.kxy.battle.buff;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;

/**
 *
 * @author Azige
 */
public final class BuffMergers {

    private static final BuffMerger REJECTOR = (origin, newComer) -> origin;
    private static final BuffMerger OVERRIDER = (origin, newComer) -> newComer;
    private static final BuffMerger PARAMETERSPACE_MERGER = (origin, newComer)
            -> origin.getPrototype().createBuff(newComer.getActor(),
                    newComer.getSourceId(),
                    new AggregateParameterSpace(origin.getParameterSpace(), newComer.getParameterSpace()),
                    newComer.getCountdown(),
                    origin.getEffectValue() + newComer.getEffectValue());
    private static final BuffMerger COUNTDOWN_MERGER = (origin, newComer)
            -> origin.getPrototype().createBuff(newComer.getActor(),
                    newComer.getSourceId(),
                    origin.getEffectValue() > newComer.getEffectValue() ? origin.getParameterSpace() : newComer.getParameterSpace(),
                    origin.getCountdown() + newComer.getCountdown(),
                    origin.getEffectValue() > newComer.getEffectValue() ? origin.getEffectValue() : newComer.getEffectValue());
    private static final BuffMerger MAXIMIZER = (origin, newComer)
            -> origin.getPrototype().createBuff(newComer.getActor(),
                    newComer.getSourceId(),
                    origin.getEffectValue() > newComer.getEffectValue() ? origin.getParameterSpace() : newComer.getParameterSpace(),
                    origin.getCountdown() > newComer.getCountdown() ? origin.getCountdown() : newComer.getCountdown(),
                    origin.getEffectValue() > newComer.getEffectValue() ? origin.getEffectValue() : newComer.getEffectValue());

    private BuffMergers() {
    }

    /**
     * 后者无效
     *
     * @return
     */
    public static BuffMerger rejector() {
        return REJECTOR;
    }

    /**
     * 取后者
     *
     * @return
     */
    public static BuffMerger overrider() {
        return OVERRIDER;
    }

    /**
     * 效果值相加，倒计时取后者
     *
     * @return
     */
    public static BuffMerger parameterMerger() {
        return PARAMETERSPACE_MERGER;
    }

    /**
     * 效果值取最大，倒计时相加
     *
     * @return
     */
    public static BuffMerger countdownMerger() {
        return COUNTDOWN_MERGER;
    }

    /**
     * 效果值取最大，倒计时取最大
     *
     * @return
     */
    public static BuffMerger maximizer() {
        return MAXIMIZER;
    }

}
