package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import de.uniks.networkparser.calculator.RegCalculator;

public class CalculatorTest {
	private RegCalculator calculator;

	@Before
	public void InitCalculator(){
		calculator = new RegCalculator().withStandard();
	}

	@Test
	public void calculation001() {
		Assert.assertEquals(7.0, calculator.calculate("4+3"), 0.0);
	}

	@Test
	public void calculation002() {
		Assert.assertEquals(14.0, calculator.calculate("5 + ((1 + 2) * 4) - 3"), 0.0);
	}

	@Test
	public void calculation003() {
		Assert.assertEquals(16.0, calculator.calculate("6+2*5"), 0.0);
	}

	@Test
	public void calculation004() {
		Assert.assertEquals(-9.0, calculator.calculate("-8/2-5"), 0.0);
	}

	@Test
	public void calculation005() {
		Assert.assertEquals(22.0, calculator.calculate("5*3+(6+1)"), 0.0);
	}

	@Test
	public void calculation006() {
		Assert.assertEquals(-3.0, calculator.calculate("-5+7-(5*1)"), 0.0);
	}

	@Test
	public void calculation007() {
		Assert.assertEquals(2.0, calculator.calculate("2-[-(7-2)+1]-4"), 0.0);
	}

	@Test
	public void calculation008() {
		Assert.assertEquals(-15.0, calculator.calculate("-5*[(-3*2)/(-3)+1]"), 0.0);
	}

	@Test
	public void calculation009() {
		Assert.assertEquals(35.0, calculator.calculate("18+[9-(-3)+5]"), 0.0);
	}

	@Test
	public void calculation010() {
		Assert.assertEquals(-20.0, calculator.calculate("-[4-(-16)]"), 0.0);
	}

	@Test
	public void calculation011() {
		Assert.assertEquals(-3.0, calculator.calculate("3-[4-(5-7)]-{9-[5-(-4)]}"), 0.0);
	}

	@Test
	public void calculation012() {
		Assert.assertEquals(-3.0, calculator.calculate("14-(8+7)-[4+2-3-(-4+5)]"), 0.0);
	}

	@Test
	public void calculation013() {
		Assert.assertEquals(-5.0, calculator.calculate("15/(-3)"), 0.0);
	}

	@Test
	public void calculation014() {
		Assert.assertEquals(-34.0, calculator.calculate("7*(-3)+[2+3(-5)]"), 0.0);
	}

	@Test
	public void calculation015() {
		Assert.assertEquals(5.0, calculator.calculate("8+10/2-4*2"), 0.0);
	}

	@Test
	public void calculation016() {
		Assert.assertEquals(-261.0, calculator.calculate("29[(-10)+1]"), 0.0);
	}

	@Test
	public void calculation017() {
		Assert.assertEquals(-19.0, calculator.calculate("(-12)*7-13(-5)"), 0.0);
	}

	@Test
	public void calculation018() {
		Assert.assertEquals(-208.0, calculator.calculate("(4-20)13"), 0.0);
	}

	@Test
	public void calculation019() {
		Assert.assertEquals(1.0, calculator.calculate("(-5)*7-9(-4)"), 0.0);
	}

	@Test
	public void calculation020() {
		Assert.assertEquals(-1.0, calculator.calculate("(-48+32)-(67-82)"), 0.0);
	}

	@Test
	public void calculation021() {
		Assert.assertEquals(10.0, calculator.calculate("-[-13+(24-68)]-(-48+95)"), 0.0);
	}

	@Test
	public void calculation022() {
		Assert.assertEquals(-96.0, calculator.calculate("12(-7)-12"), 0.0);
	}

	@Test
	public void calculation023() {
		Assert.assertEquals(65.0, calculator.calculate("48-[15-(43-38)-27]"), 0.0);
	}

	@Test
	public void calculation024() {
		Assert.assertEquals(-73.0, calculator.calculate("-32-[19-(24-46)]"), 0.0);
	}

	@Test
	public void calculation025() {
		Assert.assertEquals(-20.0, calculator.calculate("-(24-89+18)+(-91+24)"), 0.0);
	}

	@Test
	public void calculation026() {
		Assert.assertEquals(4.0, calculator.calculate("-2^2"), 0.0);
	}

	@Test
	public void calculation027() {
		Assert.assertEquals(88.0, calculator.calculate("5*2^4+4*2^2-6*2+4"), 0.0);
	}

	@Test
	public void calculation028() {
		Assert.assertEquals(6561.0, calculator.calculate("3^3*3^4*3"), 0.0);
	}

	@Test
	public void calculation029() {
		Assert.assertEquals(625.0, calculator.calculate("5^7/5^3"), 0.0);
	}

	@Test
	public void calculation030() {
		Assert.assertEquals(244140625.0, calculator.calculate("(5^3)^4"), 0.0);
	}

	@Test
	public void calculation031() {
		Assert.assertEquals(810000.0, calculator.calculate("(5*2*3)^4"), 0.0);
	}

	@Test
	public void calculation032() {
		Assert.assertEquals(43046721.0, calculator.calculate("(3^4)^4"), 0.0);
	}

	@Test
	public void calculation033() {
		Assert.assertEquals(9.0, calculator.calculate("(((2-1/5)^2)/((3-2/9)^(-1)))"), 0.00001);

		Assert.assertEquals(863.2857142, calculator.calculate("(((2-1/5)^2)/((3-2/9)^(-1))) / (((6/7)*(5/4)-(2/7)/(1/2))^3)/((1/2)-(1/3)*(1/4)/(1/5))-5(1/7)"), 0.0000001);
	}

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
