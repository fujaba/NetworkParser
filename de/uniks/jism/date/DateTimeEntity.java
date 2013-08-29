package de.uniks.jism.date;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.text.SimpleDateFormat;
import java.util.Date;

import de.uniks.jism.DefaultTextItems;
import de.uniks.jism.TextItems;

public class DateTimeEntity  {
	private DateTimeFields fields = new DateTimeFields();
	private TextItems items;

	/**
	 * Month of the Year Default is German
	 */
	public String[] monthOfYear = new String[] { DefaultTextItems.JANUARY, DefaultTextItems.FEBRUARY,
			DefaultTextItems.MARCH, DefaultTextItems.APRIL, DefaultTextItems.MAY, DefaultTextItems.JUNE, 
			DefaultTextItems.JULY, DefaultTextItems.AUGUST, DefaultTextItems.SEPTEMBER, DefaultTextItems.OCTOBER,
			DefaultTextItems.NOVEMBER, DefaultTextItems.DECEMBER};

	/**
	 * Days of the week
	 */
	public String[] weekDays = new String[] {DefaultTextItems.SUNDAY, DefaultTextItems.MONDAY, DefaultTextItems.TUESDAY,
			DefaultTextItems.WEDNESDAY, DefaultTextItems.THURSDAY, DefaultTextItems.FRIDAY, DefaultTextItems.SATURDAY };
	private boolean isInitConstants = false;

	/**
	 * Setter with milliseconds
	 * 
	 * @param milliseconds
	 *            milliseconds since 01.01.1970
	 */
	public DateTimeEntity withValue(long milliseconds){
		fields.withTime(milliseconds);
		return this;
	}

	/**
	 * Setter with day, month and year
	 * 
	 * @param year
	 *            year of the date
	 * @param month
	 *            month of the date
	 * @param day
	 *            day of the date
	 */
	public DateTimeEntity withValue(int year, int month, int day) {
		this.withYear(year);
		this.withMonth(month);
		this.withDate(day);
		return this;
	}

	/**
	 * Setter with date-String
	 * 
	 * @param date
	 *            date as String
	 */
	public DateTimeEntity withValue(String date) {
		
		Integer day = Integer.valueOf( date.substring(0, 1) );
		Integer month = Integer.valueOf( date.substring(3, 4) );
		Integer year = Integer.valueOf( date.substring(6, 9) );
		
		this.withYear(year);
		this.withMonth(month);
		this.withDate(day);
		return this;
	}

	/**
	 * Setter with date
	 * 
	 * @param date
	 *            with new date
	 */
	public DateTimeEntity withValue(java.util.Date date) {
		withValue(date.getTime());
		return this;
	}

	/**
	 * Set new TimeStamp
	 * 
	 * @param date
	 *            a new Date
	 * @return 
	 */
	public DateTimeEntity withNewDate(long date) {
		this.fields.withTime(date * DateTimeFields.ONE_SECOND);
		return this;
	}

	public DateTimeEntity withTextItems(TextItems items) {
		this.items = items;
		return this;
	}

	/**
	 * Init the Date object
	 */
	private void initDate() {
		if (items != null && !isInitConstants) {
			// Month
			for (int i = 0; i < 12; i++) {
				monthOfYear[i] = items.getText(monthOfYear[i], this, null);
			}
			// Weekdays
			for (int i = 0; i < 7; i++) {
				weekDays[i] = items.getText(weekDays[i], this, null);
			}
			isInitConstants = true;
		}
	}

//
//	public long get(String field) {
//		return get(field, this);
//	}
//
//	public static long get(String field, DateTimeEntity reference) {
//		if (DateTimeFields.MILLISECONDS.equalsIgnoreCase(field)) {
//			return reference.getTime();
//		} else if (DateTimeFields.MILLISECOND_OF_DAY.equalsIgnoreCase(field)) {
//			return get(DateTimeFields.MILLISECONDS, reference)
//					% DateTimeFields.ONE_DAY;
//		} else if (DateTimeFields.SECONDS.equalsIgnoreCase(field)) {
//			return get(DateTimeFields.MILLISECONDS, reference)
//					/ DateTimeFields.ONE_SECOND;
//		} else if (DateTimeFields.SECOND_OF_MINUTE.equalsIgnoreCase(field)) {
//			return (get(DateTimeFields.MILLISECOND_OF_DAY, reference) / DateTimeFields.ONE_SECOND)
//					% DateTimeFields.ONE_MINUTE;
//		} else if (DateTimeFields.SECOND_OF_DAY.equalsIgnoreCase(field)) {
//			return (get(DateTimeFields.MILLISECOND_OF_DAY, reference) / DateTimeFields.ONE_SECOND);
//		} else if (DateTimeFields.SECOND_OF_YEAR.equalsIgnoreCase(field)) {
//			long milli = get(DateTimeFields.MILLISECONDS, reference)/ DateTimeFields.ONE_SECOND;
//			return milli - reference.getYearSeconds(reference.getYear());
//		} else if (DateTimeFields.MINUTES.equalsIgnoreCase(field)) {
//			return get(DateTimeFields.MILLISECONDS, reference)
//					/ DateTimeFields.ONE_MINUTE;
//		} else if (DateTimeFields.MINUTE_OF_HOUR.equalsIgnoreCase(field)) {
//			return (get(DateTimeFields.MILLISECOND_OF_DAY, reference) / DateTimeFields.ONE_MINUTE)
//					% DateTimeFields.ONE_HOUR;
//		} else if (DateTimeFields.HOURS.equalsIgnoreCase(field)) {
//			return get(DateTimeFields.MILLISECONDS, reference)
//					/ DateTimeFields.ONE_HOUR;
//		} else if (DateTimeFields.HOUR_OF_DAY.equalsIgnoreCase(field)) {
//			return (get(DateTimeFields.MILLISECOND_OF_DAY, reference) / DateTimeFields.ONE_HOUR)
//					% DateTimeFields.ONE_DAY;
//		} else if (DateTimeFields.DAYS.equalsIgnoreCase(field)) {
//			return get(DateTimeFields.MILLISECONDS, reference)
//					/ DateTimeFields.ONE_DAY;
//		} else if (DateTimeFields.DAY_OF_MONTH.equalsIgnoreCase(field)) {
//			return reference.getDate();
//		}
//		// FIXME get noch erweiterung public static final String ERA="era";
//		// public static final String MONTH="month";
//		// public static final String YEAR="year";
//		// public static final String WEEK_OF_YEAR="week_of_year";
//		// public static final String WEEK_OF_MONTH="week_of_month";
//		// public static final String WEEK_OF_WEEK="week_of_week";
//		// public static final String DAY_OF_YEAR="day_of_year";
//		// public static final String AMPM="am/pm";
//		return 0;
//	}
//
//
//	/**
//	 * get the currentyear of the date GetYear
//	 * 
//	 * @return Year
//	 */
//	public int getYear() {
////		return super.getYear() + 1900;
//		long time = getTime();
//		int year=(int) (time/DateTimeEntity.ONE_YEAR_SJ)+1970;
//		int schaltjahre=((year-1)-1968)/4 - ((year-1)-1900)/100 + ((year-1)-1600)/400;
//		return (int) (((time-schaltjahre*DateTimeEntity.ONE_DAY)/DateTimeEntity.ONE_YEAR)+1970);
//	}
//
//	// /**
//	// * get the currentmonth of the date
//	// * @return Month
//	// */
//	public int getMonth() {
//		return super.getMonth() + 1;
//	}
//	
//	public void setHours(int hours){
//		super.setHours(hours);
//	}
//	
//	public void setMinutes(int minutes){
//		super.setMinutes(minutes);
//	}
//	
//	public void setSeconds(int seconds){
//		super.setSeconds(seconds);
//	}
//	
//	@Override
//	public int getHours() {
//		return super.getHours();
//	}
//	@Override
//	public int getMinutes() {
//		return super.getMinutes();
//	}
//	@Override
//	public int getSeconds() {
//		return super.getSeconds();
//	}
//
//	public int getMaxDays(int month, int year) {
//		return fields.getMonthLength(month, year);
//	}
//
//	/**
//	 * erase the time of the date
//	 */
//	public void setMidnight() {
//		long result = this.getTime() % ONE_DAY;
//		this.setTime(this.getTime() + ONE_DAY);
//		this.setTime(this.getTime() - result);
//	}
//
//	public int getDay() {
//		// Get the Weekday
//		// 01.01.70 is Tuersday
//		int weekday = (int) ((this.get(DateTimeFields.DAYS) - 3) % 7);
//		return weekday;
//	}
//	
//	public int getDate(){
//		return super.getDate();
//	}
//	
//	public void setDate(int dayOfMonth) {
//		super.setDate(dayOfMonth);
////		int year = this.getYear();
////		this.setNewDate(getYearSeconds(year)+get(DateTimeFields.SECOND_OF_YEAR));
////		System.out.println(get(DateTimeFields.SECOND_OF_YEAR));
//	}
//
//	public int getDayOfYear(int year, int month, int dayOfMonth) {
//		return (int) dayOfMonth
//				+ (fields.isLeapYear(year) ? DateTimeConstants.ACCUMULATED_DAYS_IN_MONTH_LEAP[month]
//						: DateTimeConstants.ACCUMULATED_DAYS_IN_MONTH[month]);
//	}
//	
//	public static boolean isSchaltjahr(int year) {
//		//FIXMEs
//		return false;
//	}
//
//	/**
//	 * Returns the date of Easter Sunday for a given year.
//	 * 
//	 * @param year
//	 *            > 1583
//	 * @return The date of Easter Sunday for a given year.
//	 */
//	public static DateTimeEntity getEasterSunday(int year) {
//		int i = year % 19;
//		int j = year / 100;
//		int k = year % 100;
//
//		int l = (19 * i + j - (j / 4) - ((j - ((j + 8) / 25) + 1) / 3) + 15) % 30;
//		int m = (32 + 2 * (j % 4) + 2 * (k / 4) - l - (k % 4)) % 7;
//		int n = l + m - 7 * ((i + 11 * l + 22 * m) / 451) + 114;
//
//		int month = n / 31;
//		int day = (n % 31) + 1;
//
//		return new DateTimeEntity(year, month - 1, day);
//	}
//
//	/**
//	 * format a date with the formatString
//	 * 
//	 * @param dateFormat
//	 * @return a String of Date
//	 */
//	public String toString(String dateFormat) {
//		StringBuilder sb = new StringBuilder();
//		String sub;
//		StringTokener tokener = (StringTokener) new StringTokener().withText(dateFormat);
//		do {
//			sub = tokener.nextString('"', true, true);
//			if (sub.length() > 0 && !tokener.isString()) {
//				// System.out.println(count++
//				// +": #"+sub+"# -- "+tokener.isString());
//				// Time
//				sub = sub.replace("HH", strZero(this.getHours(), 2));
//				sub = sub.replace("H", String.valueOf(this.getHours()));
//				sub = sub.replace("MM", strZero(this.getMinutes(), 2));
//				sub = sub.replace("M", String.valueOf(this.getMinutes()));
//				sub = sub.replace("SS", strZero(this.getSeconds(), 2));
//				sub = sub.replace("S", String.valueOf(this.getSeconds()));
//				// Date
//
//				sub = sub.replace("dddd", this.weekDays[this.getDay()]);
//				sub = sub.replace("ddd",
//						this.weekDays[this.getDay()].substring(0, 2));
//				sub = sub.replace("dd", strZero(this.getDate(), 2));
//				sub = sub.replace("d", String.valueOf(this.getDate()));
//				sub = sub.replace("mmmm", this.monthOfYear[this.getMonth()]);
//				sub = sub.replace("mmm",
//						this.monthOfYear[this.getMonth()].substring(0, 3));
//				sub = sub.replace("mm", strZero(this.getMonth(), 2));
//				sub = sub.replace("m", String.valueOf(this.getMonth()));
//				sub = sub.replace("yyyy", String.valueOf(this.getYear()));
//				sub = sub.replace("yyy", String.valueOf(this.getYear()));
//				sub = sub.replace("yy", strZero(this.getYear(), 2, 2));
//				sub = sub.replace("y", strZero(this.getYear(), 1, 2));
//			}
//			sb.append(sub);
//		} while (sub.length() > 0);
//
//		return sb.toString();
//	}
	//FIXME
	public String toString(String format) {
		SimpleDateFormat pattern=new SimpleDateFormat(format);
		return pattern.format(new Date());
	}

	// SETTER
	/**
	 * set a new year for the date
	 * 
	 * @param newYear
	 * @return 
	 */
	public DateTimeEntity withYear(int value) {
		this.fields.set(DateField.YEAR, value);
		return this;
	}
	
	public DateTimeEntity withMonth(int value) {
		this.fields.set(DateField.MONTH, value);
		return this;
	}

	public DateTimeEntity withDate(int value) {
		this.fields.set(DateField.DAY_OF_MONTH, value);
		return this;
	}

	
	// GETTER
	public long getTime(){
		return fields.getTime();
	}

	/**
	 * format a String with 0
	 * 
	 * @param value
	 * @param length
	 * @return a String of Value
	 */
	public String strZero(int value, int length) {
		String result = String.valueOf(value);
		while (result.length() < length) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * Format a date with 0
	 * 
	 * @param value
	 * @param length
	 * @param max
	 * @return a String of Value with max value
	 */
	public String strZero(int value, int length, int max) {
		String result = strZero(value, length);
		if (result.length() > max) {
			result = result.substring(0, max);
		}
		return result;
	}
}