package de.uniks.jism.calculator;


public interface Operator {
    public int getPriority();
    public double calculate( double a, double b );
    public String getTag();
}
