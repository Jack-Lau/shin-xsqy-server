/*
 * Created 2015-12-17 19:28:33
 */
package cn.com.yting.kxy.core.random;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.random.resource.StochasticModel;
import cn.com.yting.kxy.core.resource.ResourceContext;

/**
 *
 * @author Azige
 */
public class NormalRandomGenerator {

    private final List<Double> model;

    public NormalRandomGenerator(List<Double> model) {
        this.model = new ArrayList<>(model);
    }

    public double generate() {
        Random random = RandomProvider.getRandom();
        int randomValue = random.nextInt(model.size());
        double lowerLimit = randomValue == 0 ? 0 : model.get(randomValue - 1);
        double upperLimit = model.get(randomValue);
        return scale(random.nextDouble(), lowerLimit, upperLimit);
    }

    public int generateRanged(int lowerLimit, int upperLimit) {
        return (int) Math.round(scale(generate(), lowerLimit, upperLimit));
    }

    public double generateRanged(double lowerLimit, double upperLimit) {
        return (int) Math.round(scale(generate(), lowerLimit, upperLimit));
    }

    private static double scale(double value, double lowerLimit, double upperLimit) {
        return value * (upperLimit - lowerLimit) + lowerLimit;
    }

    public static NormalRandomGenerator createByStochasticModel(ResourceContext resourceContext, int index){
        List<Double> list = resourceContext.getLoader(StochasticModel.class).getAll().entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey()))
            .map(entry -> entry.getValue().getValueByIndex(index))
            .collect(Collectors.toList());
        return new NormalRandomGenerator(list);
    }
}
