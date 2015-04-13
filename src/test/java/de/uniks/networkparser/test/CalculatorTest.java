package de.uniks.networkparser.test;


import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.calculator.RegCalculator;

public class CalculatorTest {
	@Test
	public void testAdd(){
		RegCalculator calculator= new RegCalculator();
		calculator.withStandard();
		Assert.assertEquals(3.0, calculator.calculate("1+2"), 0.01);
		
		Assert.assertEquals(4.0, calculator.calculate("(1+2)+1"), 0.01);
		
		calculator.withConstants("COUNT", 42);
		Assert.assertEquals(42.0, calculator.calculate("(COUNT+1)-1"), 0.01);
	}
	@Test
	public void testMulti(){
		RegCalculator calculator= new RegCalculator();
		calculator.withStandard();
		
		Assert.assertEquals(42.0, calculator.calculate("2*21"), 0.01);
		
		Assert.assertEquals(42.0, calculator.calculate("126/3"), 0.01);
		
		Assert.assertEquals(1.0, calculator.calculate("5*5%3"), 0.01);
		
		Assert.assertEquals(20.0, calculator.calculate("5+5*3"), 0.01);
	}
	@Test
	public void testFunction(){
		RegCalculator calculator= new RegCalculator();
		calculator.withStandard();
		
		Assert.assertEquals(2.0, calculator.calculate("min(2,42)"), 0.01);
		
		Assert.assertEquals(42.0, calculator.calculate("max(2,42)"), 0.01);
		
		Assert.assertEquals(42.0, calculator.calculate("max(max(2,42),1)"), 0.01);
		
		
		Assert.assertEquals(42.0, calculator.calculate("max(1,2,42),1)"), 0.01);
	}
	@Test
	public void testSimple(){
		RegCalculator calculator= new RegCalculator();
		calculator.withStandard();
		Assert.assertEquals(5.0, calculator.calculate("5"), 0.01);
		Assert.assertEquals(5.0, calculator.calculate(" +5"), 0.01);
		Assert.assertEquals(-5.0, calculator.calculate("-5"), 0.01);

		Assert.assertEquals(5.0, calculator.calculate("(+5)"), 0.01);
		Assert.assertEquals(-5.0, calculator.calculate("(-5)"), 0.01);
		
		Assert.assertEquals(-42.0, calculator.calculate("-(42)"), 0.01);
		Assert.assertEquals(42.0, calculator.calculate(" +(42)"), 0.01);
		
		Assert.assertEquals(1.0, calculator.calculate("2+-1"), 0.01);
		Assert.assertEquals(3.0, calculator.calculate("2--1"), 0.01);
		Assert.assertEquals(1.0, calculator.calculate("2+(-1)"), 0.01);
		
	}
}
