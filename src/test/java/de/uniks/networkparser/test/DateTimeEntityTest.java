package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.DateTimeEntity;

public class DateTimeEntityTest {
	@Test
	public void testDateComplete(){
		DateTimeEntity date = new DateTimeEntity();
		GregorianCalendar reference = new GregorianCalendar();
		reference.set(GregorianCalendar.MONTH, 1);
		reference.set(GregorianCalendar.DAY_OF_MONTH, 5);
		reference.set(GregorianCalendar.YEAR, 2018);
		DateFormat df = DateFormat.getDateInstance( DateFormat.FULL );
		printToStream(df, reference, null);
		long timeInMillis = reference.getTimeInMillis();
		date.withTime(timeInMillis);

		assertEquals("" +reference.get(Calendar.MILLISECOND), "" +date.get(DateTimeEntity.MILLISECOND), "MILLISECOND");
		assertEquals("" +timeInMillis, "" +date.getTime(), "MILLISECONDS");
//		assertEquals("AMPM", "" +reference.get(Calendar.AM), "" +date.get(DateField.AMPM).getValue());
		assertEquals("" +reference.get(Calendar.SECOND), "" +date.get(DateTimeEntity.SECOND_OF_MINUTE), "SECOND_OF_MINUTE");
		assertEquals("" +reference.get(Calendar.MINUTE), "" +date.get(DateTimeEntity.MINUTE_OF_HOUR), "MINUTE_OF_HOUR");
//FIXME 02.4.14 13:52 (1396439596409)
		if(reference.get(Calendar.HOUR_OF_DAY) - date.get(DateTimeEntity.HOUR_OF_DAY)>1) {
			assertEquals("" +reference.get(Calendar.HOUR_OF_DAY), "" +date.get(DateTimeEntity.HOUR_OF_DAY), "HOUR_OF_DAY");
		}
		assertEquals("" +reference.get(Calendar.DAY_OF_MONTH), "" +date.get(DateTimeEntity.DAY_OF_MONTH), "DAY_OF_MONTH");
		assertEquals("" +reference.get(Calendar.DAY_OF_WEEK), "" +(date.get(DateTimeEntity.DAY_OF_WEEK)+1), "DAY_OF_WEEK");
		assertEquals("" +reference.get(Calendar.DAY_OF_YEAR), "" +date.get(DateTimeEntity.DAY_OF_YEAR), "DAY_OF_YEAR");
		assertEquals("" +reference.get(Calendar.MONTH), "" +(date.get(DateTimeEntity.MONTH)-1), "MONTH");
		assertEquals("" +reference.get(Calendar.YEAR), "" +date.get(DateTimeEntity.YEAR), "YEAR");

		assertEquals("" +reference.get(Calendar.WEEK_OF_MONTH), "" +date.get(DateTimeEntity.WEEK_OF_MONTH), "WEEK_OF_MONTH");
		if(reference.get(Calendar.WEEK_OF_YEAR) - date.get(DateTimeEntity.WEEK_OF_YEAR)>1) {
			assertEquals("" +reference.get(Calendar.WEEK_OF_YEAR), "" +date.get(DateTimeEntity.WEEK_OF_YEAR), "WEEK_OF_YEAR");
		}

//		case MILLISECONDS:
//		case MILLISECOND_OF_DAY:
//		case MILLISECOND_OF_YEAR:
//		case TIMEZONE:
	}

	@Test
	public void testToString() {
		DateTimeEntity date = new DateTimeEntity();
		date.withValue(1396439596409L);
		String string = date.toString("yyyy-mm-dd'T'HZ:MM:SS'Z'");
		assertEquals("2014-04-02T12:53:16Z", string);
	}

	public void printToStream(DateFormat df, GregorianCalendar reference, PrintStream stream) {
		if(stream != null) {
			stream.println(df.format(reference.getTime()));
		}
	}
}
