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
import java.util.HashMap;

import de.uniks.jism.DefaultTextItems;
import de.uniks.jism.TextItems;

public class DateTimeEntity  {
	private boolean dirty;
	private Long time;
	private DateTimeField timeZone = new DateTimeField(DateField.TIMEZONE).withMinMax(-12, 12).withDefault(1);
	private HashMap<DateField, DateTimeField> fields=new HashMap<DateField, DateTimeField>();
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

	public DateTimeEntity() {
//		add(new DateTimeField(ERA).withMin(0).withDefault(1));
		add(new DateTimeField(DateField.YEAR).withMin(0).withDefault(1970));
		add(new DateTimeField(DateField.MONTH).withMinMax(1, 12));
		add(new DateTimeField(DateField.WEEK_OF_YEAR).withMinMax(1, 53));
		add(new DateTimeField(DateField.WEEK_OF_MONTH).withMinMax(1, 5));
		add(new DateTimeField(DateField.DAY_OF_WEEK).withMinMax(1, 7));
		add(new DateTimeField(DateField.DAY_OF_MONTH).withMinMax(1, 31));
		add(new DateTimeField(DateField.DAY_OF_YEAR).withMinMax(1, 366));
		add(new DateTimeField(DateField.DAYS).withMin(1));
		add(new DateTimeField(DateField.AMPM).withMinMax(0, 1));
		add(new DateTimeField(DateField.HOUR_OF_DAY).withMinMax(0, 23));
		add(new DateTimeField(DateField.HOURS).withMin(0));
		add(new DateTimeField(DateField.MINUTES).withMin(0));
		add(new DateTimeField(DateField.MINUTE_OF_HOUR).withMinMax(0, 59));
		add(new DateTimeField(DateField.SECONDS).withMin(0));
		add(new DateTimeField(DateField.SECOND_OF_DAY).withMin(0));
		add(new DateTimeField(DateField.SECOND_OF_MINUTE).withMinMax(0, 59));
		add(new DateTimeField(DateField.SECOND_OF_YEAR).withMin(0));
		add(new DateTimeField(DateField.MILLISECOND).withMinMax(0,999));
		add(new DateTimeField(DateField.MILLISECONDS).withMin(0));
		add(new DateTimeField(DateField.MILLISECOND_OF_DAY).withMin(0));
		add(new DateTimeField(DateField.MILLISECOND_OF_YEAR).withMin(0));
	}

	public void add(DateTimeField field) {
		fields.put(field.getType(), field);
	}

	static final int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
			30, 31 }; // 0-based
	static final int MONTH_LENGTH_LP[] = { 31, 29, 31, 30, 31, 30, 31, 31,
			30, 31, 30, 31 }; // 0-based

	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = 60 * ONE_SECOND;
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_WEEK = 7 * ONE_DAY;
	public static final long ONE_YEAR=ONE_DAY*365;
	public static final long ONE_YEAR_LY=ONE_DAY*366;


	/**
	 * The normalized year of the gregorianCutover in Gregorian, with 0
	 * representing 1 BCE, -1 representing 2 BCE, etc.
	 */
	private transient int GREGORIANCUTOVERYEAR = 1582;

	// The default value of gregorianCutover.
//	static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;

	/**
	 * Returns the length of the specified month in the specified year. The year
	 * number must be normalized.
	 * 
	 * @see #isLeapYear(int)
	 */
	public final int getMonthLength(int month, int year) {
		return isLeapYear(year) ? MONTH_LENGTH_LP[month]
				: MONTH_LENGTH[month];
	}

	/**
	 * Returns the length (in days) of the specified year. The year must be
	 * normalized.
	 */
	public final int getYearLength(int year) {
		return isLeapYear(year) ? 366 : 365;
	}

	/**
	 * Determines if the given year is a leap year. Returns <code>true</code> if
	 * the given year is a leap year. To specify BC year numbers,
	 * <code>1 - year number</code> must be given. For example, year BC 4 is
	 * specified as -3.
	 * 
	 * @param year
	 *            the given year.
	 * @return <code>true</code> if the given year is a leap year;
	 *         <code>false</code> otherwise.
	 */
	public boolean isLeapYear(int year) {
		if ((year & 3) != 0) {
			return false;
		}

		if (year > GREGORIANCUTOVERYEAR) {
			return (year % 100 != 0) || (year % 400 == 0); // Gregorian
		}
		return true; // Julian
	}

	public int validate(DateField field, int value) {
		DateTimeField fieldValue = get(field);
		if (fieldValue != null) {
			return fieldValue.validate(value);
		}
		return 0;
	}
	
	public void calculate(){
		Long time = getTimeWithTimeZone();
		this.fields.get(DateField.MILLISECONDS).withValue(time);
		this.fields.get(DateField.MILLISECOND).withValue(time%ONE_SECOND);
		this.fields.get(DateField.SECONDS).withValue(time/ONE_SECOND);
		this.fields.get(DateField.MINUTES).withValue(time/ONE_MINUTE);

		time += ONE_HOUR;
		this.fields.get(DateField.HOURS).withValue(time/ONE_HOUR);
		long days=time/ONE_DAY;
		this.fields.get(DateField.DAYS).withValue(days);
		
		long daymillis=time % ONE_DAY;
		if(daymillis>ONE_DAY/2){
			this.fields.get(DateField.AMPM).withValue(1L);
		}else{
			this.fields.get(DateField.AMPM).withValue(0L);
		}
		this.fields.get(DateField.MILLISECOND_OF_DAY).withValue(daymillis);
		this.fields.get(DateField.SECOND_OF_DAY).withValue(daymillis/ONE_SECOND);
		
		this.fields.get(DateField.HOUR_OF_DAY).withValue(daymillis/ONE_HOUR);
		this.fields.get(DateField.MINUTE_OF_HOUR).withValue((daymillis%ONE_HOUR)/ONE_MINUTE);
		this.fields.get(DateField.SECOND_OF_MINUTE).withValue((daymillis%ONE_MINUTE)/ONE_SECOND);
		
		long years = time/ONE_YEAR+1970;
		long schaltjahre=((years-1)-1968)/4 - ((years-1)-1900)/100 + ((years-1)-1600)/400;
		
		long yearMillis = (time-(schaltjahre-1)*ONE_DAY) % ONE_YEAR;
		int year=(int)((time-schaltjahre*ONE_DAY)/ONE_YEAR)+1970;
		this.fields.get(DateField.MILLISECOND_OF_YEAR).withValue(yearMillis);
		this.fields.get(DateField.SECOND_OF_YEAR).withValue(yearMillis/ONE_SECOND);
		long dayOfYear = yearMillis/ONE_DAY;
		this.fields.get(DateField.DAY_OF_YEAR).withValue(dayOfYear);

		this.fields.get(DateField.YEAR).withValue((long) year);
		int month=0;
		long temp=yearMillis;
		long day=0;
		if(isLeapYear(year)){
			while(temp>0){
				temp -= MONTH_LENGTH_LP[month++]*ONE_DAY;
			}
			day = (temp + MONTH_LENGTH_LP[month-1]*ONE_DAY)/ONE_DAY;
			if(day==0){
				temp += MONTH_LENGTH_LP[--month]*ONE_DAY;
				day = (temp + MONTH_LENGTH_LP[month-1]*ONE_DAY)/ONE_DAY;
			}
		}else{
			while(temp>0){
				temp -= MONTH_LENGTH[month++]*ONE_DAY;
			}
			day = (temp + MONTH_LENGTH[month-1]*ONE_DAY)/ONE_DAY;
			if(day==0){
				temp += MONTH_LENGTH[--month]*ONE_DAY;
				day = (temp + MONTH_LENGTH[month-1]*ONE_DAY)/ONE_DAY;
			}
		}
		this.fields.get(DateField.MONTH).withValue((long) month);
		this.fields.get(DateField.DAY_OF_MONTH).withValue((long) day);

		
		long dayOfWeek=(days - 3) % 7+1;
		this.fields.get(DateField.DAY_OF_WEEK).withValue(dayOfWeek);
		int dayOf0101 = (int) ((dayOfYear+dayOfWeek+2)%7);
		
		
//		this.fields.get(DateField.DAY_OF_WEEK).withValue((yearMillis - 3) % 7);
		
		//		this.fields.get(DateField.WEEK_OF_YEAR).withValue(yearMillis/ONE_WEEK);
//	case WEEK_OF_MONTH:

//		// 01.01.70 is Tuersday

		//TIMEZONE not set
		this.dirty=false;
	}
	
//	public int GetWeekOfYear(DateTime todayDate)
//	{
//	    int days = todayDate.DayOfYear;
//	    float result = days / 7;
//	    result=result+1;
//	    return Convert.ToInt32(result);
//	}
	
	public DateTimeField get(DateField field){
		DateTimeField item = fields.get(field);
		if(item!=null){
			if(time==null){
				time = System.currentTimeMillis()+ONE_HOUR;
				this.dirty = true;
			}
			if(isDirty()){
				calculate();
			}
		}
		return item;
	}
	
	private DateTimeField getInternalField(DateField field){
		return fields.get(field);
	}
	
	/* Fix the TimeZone Offset so the Entity is a simpleCalendar item
	 * @see java.util.Date#getTime()
	 */
	public Long getTimeWithTimeZone() {
		if(this.timeZone.getValue()!=null){
			return time+(this.timeZone.getValue()*ONE_HOUR);
		}
		return time;
	}

	public DateTimeEntity withTime(Long value) {
		if(value!=this.time){
			this.time = value;
			this.dirty = true;
		}
		return this;
	}
	
	public DateTimeEntity addTime(long value) {
		if(value!=0){
			this.time += value;
			this.dirty = true;
		}
		return this;
	}

	public Long getTimezone() {
		return this.timeZone.getValue();
	}

	public DateTimeEntity withTimezone(Long value) {
		if(value!=this.timeZone.getValue()){
			timeZone.withValue(value);
			this.dirty = true;
		}
		return this;
	}
	
	public boolean isDirty(){
		return dirty;
	}

	public void set(DateField field, int value) {
		DateTimeField dateTimeField = get(field);
		
		set(dateTimeField, value);
	}
	
	
	public void add(DateField field, int value) {
		DateTimeField dateTimeField = get(field);
		
		if(dateTimeField!=null){
			set(dateTimeField, dateTimeField.getValue() + value);
		}
	}
	
	
	
	/**
	 * set to the date the amount value for the field
	 * 
	 * @param dateTimeField
	 *            dateTimeField
	 * @param value
	 *            value of changes
	 */
	public void set(DateTimeField dateTimeField, long value) {
		if(dateTimeField!=null){
			long oldValue = dateTimeField.getValueInMillisecond();
			switch(dateTimeField.getType()){
				case MONTH:
					oldValue = getInternalField(DateField.MILLISECOND_OF_YEAR).getValueInMillisecond();
					dateTimeField.withValue(value);
					addTime(dateTimeField.getValueInMillisecond() - oldValue );
					break;
				case TIMEZONE:
					withTimezone(value);
					break;
				default:
					dateTimeField.withValue(value);
					addTime(dateTimeField.getValueInMillisecond() - oldValue );
					break;
			}
		}
	}

	
	
	/**
	 * Setter with milliseconds
	 * 
	 * @param milliseconds
	 *            milliseconds since 01.01.1970
	 */
	public DateTimeEntity withValue(long milliseconds){
		withTime(milliseconds);
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
		this.withYear( Integer.valueOf( date.substring(6, 9) ) );
		this.withMonth( Integer.valueOf( date.substring(3, 4) ) );
		this.withDate( Integer.valueOf( date.substring(0, 1) ) );
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
		withTime(date * ONE_SECOND);
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

	/**
	 * erase the time of the date
	 */
	public void setMidnight() {
		long result = this.getTime() % ONE_DAY;
		this.withTime(this.getTime() + ONE_DAY - result);
	}

	
	/**
	 * Returns the date of Easter Sunday for a given year.
	 * 
	 * @param year
	 *            > 1583
	 * @return The date of Easter Sunday for a given year.
	 */
	public static DateTimeEntity getEasterSunday(int year) {
		int i = year % 19;
		int j = year / 100;
		int k = year % 100;

		int l = (19 * i + j - (j / 4) - ((j - ((j + 8) / 25) + 1) / 3) + 15) % 30;
		int m = (32 + 2 * (j % 4) + 2 * (k / 4) - l - (k % 4)) % 7;
		int n = l + m - 7 * ((i + 11 * l + 22 * m) / 451) + 114;

		int month = n / 31;
		int day = (n % 31) + 1;

		return new DateTimeEntity().withValue(year, month - 1, day);
	}

	/**
	 * format a date with the formatString
	 * 
	 * @param dateFormat
	 * @return a String of Date
	 */
	//FIXME
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
	
	public String toString(){
		return this.fields.get(DateField.DAY_OF_MONTH).getValue()+"."+this.fields.get(DateField.MONTH).getValue()+"."+this.fields.get(DateField.YEAR).getValue();
	}

	// SETTER
	/**
	 * set a new year for the date
	 * 
	 * @param newYear
	 * @return 
	 */
	public DateTimeEntity withYear(int value) {
		set(DateField.YEAR, value);
		return this;
	}
	
	public DateTimeEntity withMonth(int value) {
		set(DateField.MONTH, value);
		return this;
	}

	public DateTimeEntity withDate(int value) {
		set(DateField.DAY_OF_MONTH, value);
		return this;
	}

	
	// GETTER
	public long getTime(){
		if(time==null){
			time = System.currentTimeMillis();
			this.dirty = true;
		}
		return time;
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