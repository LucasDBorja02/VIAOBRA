package com.viaobra.service;

import com.viaobra.model.LineItem;
import com.viaobra.model.LineType;
import com.viaobra.model.Project;
import com.viaobra.model.Scenario;
import com.viaobra.model.Stage;

import java.util.List;

public class FeasibilityService {

    public record Result(
            int horizonMonths,
            double capex,
            double opexTotal,
            double revenueTotal,
            double contingencyValue,
            double taxValue,
            double profit,
            double npv,
            double irrApprox,
            int paybackMonth,
            int breakEvenMonth,
            int score,
            String verdict
    ) {}

    public Result evaluate(Project p, List<LineItem> items, List<Stage> stages, Scenario sc) {
        int base = Math.max(1, p.months);
        int delay = Math.max(0, sc.delayMonths);
        int horizon = base + delay;

        double[] cashflow = new double[horizon + 1];

        double capex = 0.0;
        double opex = 0.0;
        double rev = 0.0;

        double capexOnceCosts = 0.0;

        for (LineItem it : items) {
            boolean once = "ONCE".equalsIgnoreCase(it.recurrence);
            double value = it.totalValue();

            if (it.kind == LineType.COST) value *= Math.max(0.0, sc.costMultiplier);
            else value *= Math.max(0.0, sc.revenueMultiplier);

            int start = clamp(it.monthStart, 1, base);
            int end = clamp(it.monthEnd, 1, base);

            if (it.kind == LineType.REVENUE) {
                start = clamp(start + delay, 1, horizon);
                end = clamp(end + delay, 1, horizon);
            }

            if (once) {
                if (it.kind == LineType.COST) {
                    capexOnceCosts += value;
                    capex += value;
                } else {
                    cashflow[start] += value;
                    rev += value;
                }
            } else {
                if (end < start) { int tmp = start; start = end; end = tmp; }
                for (int m = start; m <= end; m++) {
                    if (it.kind == LineType.COST) {
                        cashflow[m] -= value;
                        opex += value;
                    } else {
                        cashflow[m] += value;
                        rev += value;
                    }
                }
            }
        }

        if (capexOnceCosts > 0) {
            if (stages != null && !stages.isEmpty()) {
                double totalPct = 0.0;
                for (Stage s : stages) totalPct += Math.max(0.0, s.capexPercent);
                if (totalPct <= 0.0) totalPct = 1.0;

                for (Stage s : stages) {
                    double pct = Math.max(0.0, s.capexPercent) / totalPct;
                    double alloc = capexOnceCosts * pct;

                    int a = clamp(s.monthStart, 1, base);
                    int b = clamp(s.monthEnd, 1, base);
                    if (b < a) { int tmp = a; a = b; b = tmp; }

                    int span = Math.max(1, b - a + 1);
                    double perMonth = alloc / span;
                    for (int m = a; m <= b; m++) {
                        cashflow[m] -= perMonth;
                    }
                }
            } else {
                cashflow[1] -= capexOnceCosts;
            }
        }

        double contingency = (capex + opex) * clamp01(p.contingencyRate);
        cashflow[1] -= contingency;

        double grossProfit = rev - (capex + opex + contingency);
        double tax = grossProfit > 0 ? grossProfit * clamp01(p.taxRate) : 0.0;
        cashflow[horizon] -= tax;

        double profit = grossProfit - tax;

        double npv = npv(cashflow, clampNonNeg(p.discountRateMonth));
        int payback = paybackMonth(cashflow);
        int breakeven = breakEvenMonth(cashflow);
        double irr = irrApprox(cashflow);

        int score = score(p, profit, npv, payback, irr, horizon);
        String verdict = score >= 70 ? "VIÁVEL" : (score >= 45 ? "ATENÇÃO" : "INVIÁVEL");

        return new Result(horizon, capex, opex, rev, contingency, tax, profit, npv, irr, payback, breakeven, score, verdict);
    }

    private static double npv(double[] cf, double rate) {
        double s = 0.0;
        for (int t = 0; t < cf.length; t++) {
            s += cf[t] / Math.pow(1.0 + rate, t);
        }
        return s;
    }

    private static double irrApprox(double[] cf) {
        boolean hasPos = false, hasNeg = false;
        for (double v : cf) { if (v > 0) hasPos = true; if (v < 0) hasNeg = true; }
        if (!(hasPos && hasNeg)) return 0.0;

        double lo = -0.9;
        double hi =  2.0;
        for (int i = 0; i < 90; i++) {
            double mid = (lo + hi) / 2.0;
            double val = npv(cf, mid);
            if (val > 0) lo = mid; else hi = mid;
        }
        return (lo + hi) / 2.0;
    }

    private static int paybackMonth(double[] cf) {
        double acc = 0.0;
        for (int t = 0; t < cf.length; t++) {
            acc += cf[t];
            if (acc >= 0.0 && t > 0) return t;
        }
        return -1;
    }

    private static int breakEvenMonth(double[] cf) {
        return paybackMonth(cf);
    }

    private static int score(Project p, double profit, double npv, int payback, double irr, int horizon) {
        int s = 0;

        if (profit > 0) s += 30;
        else if (profit == 0) s += 10;

        if (npv > 0) s += 25;
        else if (npv == 0) s += 10;

        if (payback > 0) {
            double ratio = (double) payback / Math.max(1, horizon);
            if (ratio <= 0.35) s += 25;
            else if (ratio <= 0.60) s += 15;
            else s += 8;
        }

        if (irr > p.discountRateMonth) s += 20;
        else if (irr > 0) s += 10;

        return Math.min(100, s);
    }

    private static int clamp(int v, int a, int b) { return Math.max(a, Math.min(b, v)); }
    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }
    private static double clampNonNeg(double v) { return Math.max(0.0, v); }
}
