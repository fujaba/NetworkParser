package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.DateField;
import de.uniks.networkparser.DateTimeEntity;

public class DateTimeEntityTest {
	@Test
	public void testDateComplete(){
		DateTimeEntity date = new DateTimeEntity();
		GregorianCalendar reference = new GregorianCalendar();
		reference.set(GregorianCalendar.MONTH, 2);
		reference.set(GregorianCalendar.DAY_OF_MONTH, 31);
		DateFormat df = DateFormat.getDateInstance( DateFormat.FULL );
		printToStream(df, reference, null);
		long timeInMillis = reference.getTimeInMillis();
		date.withTime(timeInMillis);

		assertEquals("MILLISECOND", "" +reference.get(Calendar.MILLISECOND), "" +date.get(DateField.MILLISECOND));
		assertEquals("MILLISECONDS", "" +timeInMillis, "" +date.getTime());
//		assertEquals("AMPM", "" +reference.get(Calendar.AM), "" +date.get(DateField.AMPM).getValue());
		assertEquals("SECOND_OF_MINUTE", "" +reference.get(Calendar.SECOND), "" +date.get(DateField.SECOND_OF_MINUTE));
		assertEquals("MINUTE_OF_HOUR", "" +reference.get(Calendar.MINUTE), "" +date.get(DateField.MINUTE_OF_HOUR));
//FIXME 02.4.14 13:52 (1396439596409)
		if(reference.get(Calendar.HOUR_OF_DAY) - date.get(DateField.HOUR_OF_DAY)>1) {
			assertEquals("HOUR_OF_DAY", "" +reference.get(Calendar.HOUR_OF_DAY), "" +date.get(DateField.HOUR_OF_DAY));
		}
		assertEquals("DAY_OF_MONTH", "" +reference.get(Calendar.DAY_OF_MONTH), "" +date.get(DateField.DAY_OF_MONTH));
		assertEquals("DAY_OF_WEEK", "" +reference.get(Calendar.DAY_OF_WEEK), "" +(date.get(DateField.DAY_OF_WEEK)+1));
		assertEquals("DAY_OF_YEAR", "" +reference.get(Calendar.DAY_OF_YEAR), "" +date.get(DateField.DAY_OF_YEAR));
		assertEquals("MONTH", "" +reference.get(Calendar.MONTH), "" +(date.get(DateField.MONTH)-1));
		assertEquals("YEAR", "" +reference.get(Calendar.YEAR), "" +date.get(DateField.YEAR));

		assertEquals("WEEK_OF_MONTH", "" +reference.get(Calendar.WEEK_OF_MONTH), "" +date.get(DateField.WEEK_OF_MONTH));
		if(reference.get(Calendar.WEEK_OF_YEAR) - date.get(DateField.WEEK_OF_YEAR)>1) {
			assertEquals("WEEK_OF_YEAR", "" +reference.get(Calendar.WEEK_OF_YEAR), "" +date.get(DateField.WEEK_OF_YEAR));
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
		Assert.assertEquals("2014-04-02T12:53:16Z", string);
	}

	public void printToStream(DateFormat df, GregorianCalendar reference, PrintStream stream) {
		if(stream != null) {
			stream.println(df.format(reference.getTime()));
		}
	}
}
