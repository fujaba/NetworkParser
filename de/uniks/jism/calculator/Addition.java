package de.uniks.jism.calculator;

public class Addition implements Operator {

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public double calculate(double a, double b) {
		return a+b;
	}

	@Override
	public String getTag() {
		return "+";
	}
}
