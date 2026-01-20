package com.viaobra.model;

public class Scenario {
    public double costMultiplier;
    public double revenueMultiplier;
    public int delayMonths;

    public Scenario(double costMultiplier, double revenueMultiplier, int delayMonths) {
        this.costMultiplier = costMultiplier;
        this.revenueMultiplier = revenueMultiplier;
        this.delayMonths = delayMonths;
    }
}
