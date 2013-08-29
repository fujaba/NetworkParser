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

public class DateTimeField {

	/** The field type. */
	private DateField typValue;
	private Long min;
	private Long max;
	private Long defaultValue;
	private Long value;
	
	/**
	 * Constructor
	 * 		DateTimeField must be have a typ
	 * @param typ
	 */
	public DateTimeField(DateField typ){
		this.typValue = typ;
	}
	public DateField getType() {
		return typValue;
	}
	
	public boolean isType(DateField typ) {
		return typValue.equals(typ);
	}

	public Long getMin() {
		return min;
	}
	
	public DateTimeField withMin(int value) {
		this.min = (long) value;
		this.defaultValue = this.min;
		return this;
	}
	public DateTimeField withMin(Long min) {
		this.min = min;
		return this;
	}
	
	public DateTimeField withMinMax(int min, int max) {
		this.min = (long) min;
		this.max = (long) max;
		this.defaultValue = this.min;
		return this;
	}

	public Long getMax() {
		return max;
	}

	public DateTimeField withMax(Long max) {
		this.max = max;
		return this;
	}

	public Long getDefaultValue() {
		return defaultValue;
	}
	
	public Long getValue() {
		return value;
	}

	public DateTimeField withDefault(int value) {
		this.defaultValue = (long) value;
		return this;
	}
	
	public DateTimeField withDefault(Long value) {
		this.defaultValue = value;
		return this;
	}
	
	public DateTimeField withValue(Long value) {
		this.value = value;
		return this;
	}

	public int validate(int value) {
		if (min != null && value < min) {
			return -1;
		}
		if (max != null && value > max) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * Supported Values are
	 * 	   MILLISECONDS, MILLISECOND_OF_YEAR
	 *     SECONDS, SECOND_OF_MINUTE, SECOND_OF_DAY, SECOND_OF_YEAR
	 *     MINUTES, MINUTE_OF_HOUR
	 *     HOURS, HOUR_OF_DAY
	 *     DAYS, DAY_OF_WEEK, DAY_OF_MONTH, DAY_OF_YEAR
	 *     AMPM,
	 *     WEEK_OF_MONTH, WEEK_OF_YEAR
	 *     YEAR
	 * 
	 * @return the Value As Milliseconds
	 */
	public long getValueInMillisecond(){
		if (isType(DateField.MILLISECONDS) || isType(DateField.MILLISECOND_OF_YEAR)) {
			return value;
		} else if (isType(DateField.SECONDS) ||  isType(DateField.SECOND_OF_MINUTE) || isType(DateField.SECOND_OF_DAY) || isType(DateField.SECOND_OF_YEAR)) {
				return value * DateTimeFields.ONE_SECOND;
		} else if (isType(DateField.MINUTES) || isType(DateField.MINUTE_OF_HOUR)) {
			return value * DateTimeFields.ONE_MINUTE;
		} else if (isType(DateField.HOURS) || isType(DateField.HOUR_OF_DAY)) {
			return value * DateTimeFields.ONE_HOUR;
		} else if (isType(DateField.DAYS) || isType(DateField.DAY_OF_WEEK) || isType(DateField.DAY_OF_MONTH)|| isType(DateField.DAY_OF_YEAR)) {
			return value * DateTimeFields.ONE_DAY;
		} else if (isType(DateField.AMPM)){
			return value * (DateTimeFields.ONE_DAY/2);
		} else if (isType(DateField.WEEK_OF_MONTH) || isType(DateField.WEEK_OF_YEAR)){
			return value * DateTimeFields.ONE_WEEK;
		} else if (isType(DateField.YEAR)){
			int year=Integer.valueOf(""+value);
			int schaltjahre=((year-1)-1968)/4 - ((year-1)-1900)/100 + ((year-1)-1600)/400;
			return (year-schaltjahre)*DateTimeFields.ONE_YEAR + (schaltjahre*DateTimeFields.ONE_YEAR_LY);
		}
		return 0;
	}
}
