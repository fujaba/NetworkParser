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

import java.util.Date;
import java.util.HashMap;

public class DateTimeFields {
	private boolean dirty;

//FIXME	public static final String ERA = "era";
	private HashMap<DateField, DateTimeField> fields=new HashMap<DateField, DateTimeField>(); 

	public DateTimeFields() {
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
		add(new DateTimeField(DateField.SECOND_OF_MINUTE).withMinMax(0, 59));
		add(new DateTimeField(DateField.MILLISECONDS).withMin(0));
		add(new DateTimeField(DateField.MILLISECOND_OF_DAY).withMin(0));
		add(new DateTimeField(DateField.MILLISECOND_OF_YEAR).withMin(0));
		add(new DateTimeField(DateField.TIMEZONE).withMinMax(-12, 12).withDefault(1));
	}

	public void add(DateTimeField field) {
		fields.put(field.getType(), field);
	}

	static final int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
			30, 31 }; // 0-based
	static final int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31,
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
		return isLeapYear(year) ? LEAP_MONTH_LENGTH[month]
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
		
	}
	
	public DateTimeField get(DateField field){
		DateTimeField item = fields.get(field);
		if(item!=null){
			if(isDirty()){
				calculate();
			}
		}
		return item;
	}
	
	private DateTimeField getInternalField(DateField field){
		return fields.get(field);
	}

	
	public Long getTime() {
		DateTimeField item = get(DateField.MILLISECONDS);
		if(item.getValue()==null){
			item.withValue(new Date().getTime());
			this.dirty = true;
			return item.getValue();
		}
		return item.getValue();
	}
	
	/* Fix the TimeZone Offset so the Entity is a simpleCalendar item
	 * @see java.util.Date#getTime()
	 */
	public Long getTimeWithTimeZone() {
		Long value = get(DateField.MILLISECONDS).getValue();
		
		DateTimeField timezone = getInternalField(DateField.TIMEZONE);
		if(timezone.getValue()!=null){
			return value+(getTimezone()*ONE_HOUR);
		}
		return value;
	}

	public DateTimeFields withTime(Long value) {
		DateTimeField item = getInternalField(DateField.MILLISECONDS);
		if(value!=item.getValue()){
			item.withValue(value);
			this.dirty = true;
		}
		return this;
	}
	
	public DateTimeFields addTime(long value) {
		DateTimeField item = getInternalField(DateField.MILLISECONDS);
		if(value!=0){
			item.withValue(value);
			this.dirty = true;
		}
		return this;
	}

	public Long getTimezone() {
		return get(DateField.TIMEZONE).getValue();
	}

	public DateTimeFields withTimezone(Long value) {
		DateTimeField item = getInternalField(DateField.TIMEZONE);
		if(value!=item.getValue()){
			item.withValue(value);
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
}
