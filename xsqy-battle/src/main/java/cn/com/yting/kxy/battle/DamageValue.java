/*
 * Created 2016-4-15 11:33:49
 */
package cn.com.yting.kxy.battle;

/**
 *
 * @author Azige
 */
public class DamageValue {

    public static final DamageValue ZERO = new DamageValue(0, 0);

    private final double hp;
    private final double sp;

    public DamageValue(double hp, double sp) {
        this.hp = hp;
        this.sp = sp;
    }

    public double getHp() {
        return hp;
    }

    public double getSp() {
        return sp;
    }

    @Override
    public String toString() {
        return "{" + "hp=" + hp + ", sp=" + sp + '}';
    }

    public DamageValue plus(double addend) {
        return new DamageValue(hp + addend, sp + addend);
    }

    public DamageValue plus(DamageValue another) {
        return new DamageValue(hp + another.hp, sp + another.sp);
    }

    public DamageValue multiply(double factor) {
        return new DamageValue(hp * factor, sp * factor);
    }

    /**
     * 对伤害值进行规整化。 如果伤害值不为0，则最小值为1。 所有数值四舍五入为整数。
     *
     * @return
     */
    public DamageValue normalize() {
        double[] values = {hp, sp};
        for (int i = 0; i < values.length; i++) {
            double value = values[i];
            if (value > 0 && value < 1) {
                value = 1;
            }
            values[i] = Math.round(value);
        }
        return new DamageValue(values[0], values[1]);
    }

    public static DamageValue hpOnly(double hp) {
        return new DamageValue(hp, 0);
    }

    public static DamageValue spOnly(double sp) {
        return new DamageValue(0, sp);
    }
}
