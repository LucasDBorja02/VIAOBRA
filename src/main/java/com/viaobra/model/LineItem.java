package com.viaobra.model;

public class LineItem {
    public long id;
    public long projectId;

    public LineType kind;
    public String category;
    public String name;

    public double unitCost;
    public double quantity;

    public String recurrence;
    public int monthStart;
    public int monthEnd;

    public double totalValue() {
        return unitCost * quantity;
    }
}
