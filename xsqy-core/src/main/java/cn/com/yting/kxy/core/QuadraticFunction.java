/*
 * Created 2016-9-13 11:53:05
 */
package cn.com.yting.kxy.core;

import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author Azige
 */
public class QuadraticFunction implements DoubleUnaryOperator{

    private final double a;
    private final double b;
    private final double c;

    public QuadraticFunction(double a, double b, double c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double applyAsDouble(double x){
        return a * x * x + b * x + c;
    }

    @Override
    public String toString(){
        return String.format("QuadraticFunction{ %fx2 + %fx + %f }", a, b, c);
    }
}
