package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.HashMap;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;

public class DateTimeEntity {
	public static final String W3CDTF_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private boolean dirty;
	private Long time;
	private Byte timeZone = 1;
	private HashMap<DateField, Long> fields = new HashMap<DateField, Long>();
	private TextItems items;

	/** Month of the Year Default is German */
	public String[] monthOfYear = new String[] {DefaultTextItems.JANUARY,
			DefaultTextItems.FEBRUARY, DefaultTextItems.MARCH,
			DefaultTextItems.APRIL, DefaultTextItems.MAY,
			DefaultTextItems.JUNE, DefaultTextItems.JULY,
			DefaultTextItems.AUGUST, DefaultTextItems.SEPTEMBER,
			DefaultTextItems.OCTOBER, DefaultTextItems.NOVEMBER,
			DefaultTextItems.DECEMBER };

	/** Days of the week */
	public String[] weekDays = new String[] {DefaultTextItems.SUNDAY,
			DefaultTextItems.MONDAY, DefaultTextItems.TUESDAY,
			DefaultTextItems.WEDNESDAY, DefaultTextItems.THURSDAY,
			DefaultTextItems.FRIDAY, DefaultTextItems.SATURDAY };
	private boolean isInitConstants = false;

	static final int MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
			30, 31 }; // 0-based

	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = 60 * ONE_SECOND;
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_WEEK = 7 * ONE_DAY;
	public static final long ONE_YEAR = ONE_DAY * 365;
	public static final long ONE_YEAR_LY = ONE_DAY * 366;

	/**
	 * The normalized year of the gregorianCutover in Gregorian, with 0
	 * representing 1 BCE, -1 representing 2 BCE, etc.
	 */
	private transient int GREGORIANCUTOVERYEAR = 1582;

	/**
	 * Returns the length of the specified month in the specified year. The year
	 * number must be normalized.
	 *
	 * @param month		month as asking
	 * @param year		year of asking
	 * @return 			int day of Month
	 *
	 * @see #isLeapYear(int)
	 */
	public int getMonthLength(int month, int year) {
		if (isLeapYear(year)) {
			return getMonthLengthLP(month);
		}
		return MONTH_LENGTH[month];
	}

	private int getMonthLengthLP(int month) {
		if (month == 1) {
			return MONTH_LENGTH[month] + 1;
		}
		if(month<0 || month>11) {
			return 0;
		}
		return MONTH_LENGTH[month];
	}

	/**
	 * Returns the length (in days) of the specified year. The year must be
	 * normalized.
	 *
	 * @param year		year for asking
	 * @return 			day of fullyear
	 */
	public int getYearLength(int year) {
		return isLeapYear(year) ? 366 : 365;
	}

	/**
	 * Determines if the given year is a leap year. Returns <code>true</code> if
	 * the given year is a leap year. To specify BC year numbers,
	 * <code>1 - year number</code> must be given. For example, year BC 4 is
	 * specified as -3.
	 *
	 * @param year		the given year.
	 * @return 			success: <code>true</code> if the given year is a leap year; <code>false</code> otherwise.
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

	private boolean internCalculate(long time, boolean calc) {
		long years = time / ONE_YEAR + 1970;
		long schaltjahre = ((years - 1) - 1968) / 4 - ((years - 1) - 1900)
				/ 100 + ((years - 1) - 1600) / 400;
		long yearMillis = (time - (schaltjahre - 1) * ONE_DAY) % ONE_YEAR;
		int year = (int) ((time - schaltjahre * ONE_DAY) / ONE_YEAR) + 1970;
		int month = 0;
		long temp = yearMillis;
		long day = 0;
		if (isLeapYear(year)) {
			while (temp > 0) {
				temp -= getMonthLengthLP(month++) * ONE_DAY;
			}
			day = (temp + getMonthLengthLP(month - 1) * ONE_DAY) / ONE_DAY;
			if (day == 0) {
				temp += getMonthLengthLP(--month) * ONE_DAY;
				day = (temp + getMonthLengthLP(month - 1) * ONE_DAY) / ONE_DAY;
			}
		} else {
			while (temp > 0) {
				temp -= MONTH_LENGTH[month++] * ONE_DAY;
			}
			day = (temp + MONTH_LENGTH[month - 1] * ONE_DAY) / ONE_DAY;
			if (day == 0) {
				temp += MONTH_LENGTH[--month] * ONE_DAY;
				day = (temp + MONTH_LENGTH[month - 1] * ONE_DAY) / ONE_DAY;
			}
		}

		long daymillis = time % ONE_DAY;
		if (daymillis > ONE_DAY / 2) {
			this.fields.put(DateField.AMPM, 1L);
		} else {
			this.fields.put(DateField.AMPM, 0L);
		}
		long hour = daymillis / ONE_HOUR;

		// 01.01.70 is Tuersday
		long dayOfWeek = (time / ONE_DAY + 4) % 7;
		long leftDays = 31 - day;
		if (calc) {
			if (month > 3 && month < 10) {
				return true;
			} else if (month == 3 && leftDays < 7) {
				if ((7 - dayOfWeek) >= leftDays
						|| (dayOfWeek == 7)) {
					return true;
				}
			} else if (month == 10 && leftDays < 7
					|| (dayOfWeek == 7)) {
				if ((7 - dayOfWeek) < leftDays) {
					return true;
				}
			}
		}
		this.fields.put(DateField.MILLISECOND_OF_DAY, daymillis);
		this.fields.put(DateField.HOUR_OF_DAY, hour);
		this.fields.put(DateField.MINUTE_OF_HOUR, (daymillis % ONE_HOUR)
				/ ONE_MINUTE);
		this.fields.put(DateField.SECOND_OF_MINUTE, (daymillis % ONE_MINUTE)
				/ ONE_SECOND);

		this.fields.put(DateField.MILLISECOND_OF_YEAR, yearMillis);

		long dayOfYear = yearMillis / ONE_DAY;
		this.fields.put(DateField.DAY_OF_YEAR, dayOfYear);
		this.fields.put(DateField.YEAR, (long) year);
		this.fields.put(DateField.MONTH, (long) month);
		this.fields.put(DateField.DAY_OF_MONTH, (long) day);

		this.fields.put(DateField.DAY_OF_WEEK, dayOfWeek);
		long week = dayOfYear / 7;
		if (dayOfYear % 7 > 0) {
			week++;
		}
//		week += 1;
		this.fields.put(DateField.WEEK_OF_YEAR, week );
		this.fields
				.put(DateField.WEEK_OF_MONTH, week - ((dayOfYear - day) / 7));
		this.fields.put(DateField.MILLISECONDSREAL, time);
		return false;
	}

	public void calculate() {
		if(this.dirty) {
			Long time = getTimeWithTimeZone();
			this.fields.put(DateField.MILLISECONDS, time);
			this.fields.put(DateField.MILLISECOND, time % ONE_SECOND);

			if (internCalculate(time, true)) {
				time += ONE_HOUR;
				internCalculate(time, false);
			}

			this.dirty = false;
		}
	}

	public long get(DateField field) {
		if (time == null) {
			time = System.currentTimeMillis();
			this.dirty = true;
		}
		if (isDirty()) {
			calculate();
		}
		return fields.get(field);
	}

	/**
	 * Fix the TimeZone Offset so the Entity is a simpleCalendar item
	 * @return The CurrentTime with TimeZone
	 * 
	 * @see java.util.Date#getTime()
	 */
	public Long getTimeWithTimeZone() {
		if (this.timeZone != null) {
			return time + (this.timeZone * ONE_HOUR);
		}
		return time;
	}

	public DateTimeEntity withTime(Long value) {
		if (value == null || value.equals(this.time) == false ) {
			this.time = value;
			this.dirty = true;
		}
		return this;
	}

	public DateTimeEntity addTime(long value) {
		if (value != 0) {
			this.time += value;
			this.dirty = true;
		}
		return this;
	}

	public byte getTimezone() {
		return this.timeZone;
	}

	public DateTimeEntity withTimezone(Byte value) {
		if ((this.timeZone == null && value != null)
				|| (this.timeZone != null && !this.timeZone.equals(value))) {
			this.timeZone = value;
			this.dirty = true;
		}
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void add(DateField field, int value) {
		Long oldValue = get(field);

		if (oldValue != null) {
			set(field, oldValue + value);
		}
	}

	public void set(DateField field, int value) {
		set(field, (long) value);
	}

	/**
	 * set to the date the amount value for the field
	 *
	 * @param field		dateTimeField
	 * @param value		value of changes
	 */
	public void set(DateField field, long value) {
		if (field != null) {
			long oldValue = getValueInMillisecond(field);
			switch (field) {
			case MONTH:
				oldValue = getValueInMillisecond(DateField.MILLISECOND_OF_YEAR);
				fields.put(field, value);
				addTime(getValueInMillisecond(DateField.MILLISECOND_OF_YEAR)
						- oldValue);
				break;
			case TIMEZONE:
				withTimezone((byte) value);
				break;
			default:
				fields.put(field, value);
				addTime(getValueInMillisecond(field) - oldValue);
				break;
			}
		}
	}

	/**
	 * Setter with milliseconds
	 *
	 * @param milliseconds	milliseconds since 01.01.1970
	 * @return 				Itself
	 */
	public DateTimeEntity withValue(long milliseconds) {
		withTime(milliseconds);
		return this;
	}

	/**
	 * Setter with day, month and year
	 *
	 * @param year		year of the date
	 * @param month		month of the date
	 * @param day		day of the date
	 * @return 			Itself
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
	 * @param date		date as String
	 * @return 			Itself
	 */
	public DateTimeEntity withValue(String date) {
		this.withYear(Integer.parseInt(date.substring(6, 9)));
		this.withMonth(Integer.parseInt(date.substring(3, 4)));
		this.withDate(Integer.parseInt(date.substring(0, 1)));
		return this;
	}

	/**
	 * Setter with date
	 *
	 * @param date		with new date
	 * @return 			Itself
	 */
	public DateTimeEntity withValue(java.util.Date date) {
		withValue(date.getTime());
		return this;
	}

	/**
	 * Set new TimeStamp
	 *
	 * @param date		a new Date
	 * @return 			Itself
	 */
	public DateTimeEntity withNewDate(long date) {
		withTime(date * ONE_SECOND);
		return this;
	}

	/**
	 * @param items		The new TextItem for text
	 * @return 			Itself
	 */
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
	 * @param year		xear that is greater then 1583
	 * @return 			The date of Easter Sunday for a given year.
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
	 * @param dateFormat	The Format
	 * @return 				a String of Date
	 */
	public String toString(String dateFormat) {
		initDate();
		calculate();
		StringBuilder sb = new StringBuilder();
		String sub;
		Tokener tokener = new Tokener();
		dateFormat = dateFormat.replaceAll("'", "\"");
		tokener.withBuffer(dateFormat);
		boolean isString = false;
		do {
			sub = tokener.nextString(new CharacterBuffer(), false, false, '"').toString();
			//FIXME Change String to StringContainter
			if (sub.length() > 0 && !isString) {
				// System.out.println(count++
				// + ": #" +sub+ "# -- " +tokener.isString());
				// Time
				sub = sub.replace("HZ", EntityUtil.strZero(get(DateField.HOUR_OF_DAY) - getTimezone(), 2));
				sub = sub.replace("HH", EntityUtil.strZero(get(DateField.HOUR_OF_DAY), 2));
				sub = sub.replace("H",
						String.valueOf(get(DateField.HOUR_OF_DAY)));
				sub = sub.replace("MM",
						EntityUtil.strZero(get(DateField.MINUTE_OF_HOUR), 2));
				sub = sub.replace("M",
						String.valueOf(get(DateField.MINUTE_OF_HOUR)));
				sub = sub.replace("SS",
						EntityUtil.strZero(get(DateField.SECOND_OF_MINUTE), 2));
				sub = sub.replace("S",
						String.valueOf(get(DateField.SECOND_OF_MINUTE)));
				// Date
				sub = sub.replace("dddd",
						this.weekDays[(int) get(DateField.DAY_OF_WEEK)]);
				sub = sub.replace("ddd",
						this.weekDays[(int) get(DateField.DAY_OF_WEEK)]
								.substring(0, 2));
				sub = sub
						.replace("dd", EntityUtil.strZero(get(DateField.DAY_OF_MONTH), 2));
				sub = sub.replace("d",
						String.valueOf(get(DateField.DAY_OF_MONTH)));
				sub = sub.replace("mmmm",
						this.monthOfYear[(int) get(DateField.MONTH) - 1]);
				sub = sub.replace("mmm",
						this.monthOfYear[(int) get(DateField.MONTH) - 1]
								.substring(0, 3));
				sub = sub.replace("mm", EntityUtil.strZero(get(DateField.MONTH), 2));
				sub = sub.replace("m", String.valueOf(get(DateField.MONTH)));
				sub = sub.replace("yyyy", String.valueOf(get(DateField.YEAR)));
				sub = sub.replace("yyy", String.valueOf(get(DateField.YEAR)));
				sub = sub.replace("yy", EntityUtil.strZero(get(DateField.YEAR), 2, 2));
				sub = sub.replace("y", EntityUtil.strZero(get(DateField.YEAR), 1, 2));
			}
			sb.append(sub);
			if(tokener.getCurrentChar()=='\"') {
				tokener.getChar();
			}
			isString = !isString;
		} while (sub.length() > 0);

		return sb.toString();
	}

	@Override
	public String toString() {
		return this.fields.get(DateField.DAY_OF_MONTH) + "."
				+ this.fields.get(DateField.MONTH) + "."
				+ this.fields.get(DateField.YEAR);
	}

	// SETTER
	/**
	 * set a new year for the date
	 *
	 * @param value		the newYear
	 * @return 			Itself
	 */
	public DateTimeEntity withYear(int value) {
		set(DateField.YEAR, value);
		return this;
	}

	/**
	 * set a new month for the Date
	 *
	 * @param value		The new Month
	 * @return 			Itself
	 */
	public DateTimeEntity withMonth(int value) {
		set(DateField.MONTH, value);
		return this;
	}

	public DateTimeEntity withDate(int value) {
		set(DateField.DAY_OF_MONTH, value);
		return this;
	}

	public DateTimeEntity withHour(int value) {
		set(DateField.HOUR_OF_DAY, value);
		return this;
	}

	public DateTimeEntity withMinute(int value) {
		set(DateField.MINUTE_OF_HOUR, value);
		return this;
	}

	public DateTimeEntity withSecond(int value) {
		set(DateField.SECOND_OF_MINUTE, value);
		return this;
	}

	// GETTER
	public long getTime() {
		if (time == null) {
			time = System.currentTimeMillis();
			this.dirty = true;
		}
		return time;
	}

	/**
	 * Supported Values are MILLISECOND, MILLISECONDS, MILLISECOND_OF_YEAR,
	 * MILLISECOND_OF_DAY,MILLISECONDSREAL SECOND_OF_MINUTE, SECOND_OF_DAY,
	 * SECOND_OF_YEAR MINUTE_OF_HOUR HOUR_OF_DAY DAY_OF_WEEK, DAY_OF_MONTH,
	 * DAY_OF_YEAR AMPM, WEEK_OF_MONTH, WEEK_OF_YEAR YEAR
	 *
	 * @param field		The Field for get
	 * @return the Value As Milliseconds
	 */
	public long getValueInMillisecond(DateField field) {
		long value = fields.get(field);
		if (field == DateField.MILLISECOND || field == DateField.MILLISECONDS
				|| field == DateField.MILLISECOND_OF_YEAR
				|| field == DateField.MILLISECOND_OF_DAY
				|| field == DateField.MILLISECONDSREAL) {
			return value;
		} else if (field == DateField.SECOND_OF_MINUTE) {
			return value * DateTimeEntity.ONE_SECOND;
		} else if (field == DateField.MINUTE_OF_HOUR) {
			return value * DateTimeEntity.ONE_MINUTE;
		} else if (field == DateField.HOUR_OF_DAY) {
			return value * DateTimeEntity.ONE_HOUR;
		} else if (field == DateField.DAY_OF_WEEK
				|| field == DateField.DAY_OF_MONTH
				|| field == DateField.DAY_OF_YEAR) {
			return value * DateTimeEntity.ONE_DAY;
		} else if (field == DateField.AMPM) {
			return value * (DateTimeEntity.ONE_DAY / 2);
		} else if (field == DateField.WEEK_OF_MONTH
				|| field == DateField.WEEK_OF_YEAR) {
			return value * DateTimeEntity.ONE_WEEK;
		} else if (field == DateField.YEAR) {
			int year = Integer.parseInt("" + value);
			int schaltjahre = ((year - 1) - 1968) / 4 - ((year - 1) - 1900)
					/ 100 + ((year - 1) - 1600) / 400;
			return (year - schaltjahre) * DateTimeEntity.ONE_YEAR
					+ (schaltjahre * DateTimeEntity.ONE_YEAR_LY);
		}
		return 0;
	}
}
