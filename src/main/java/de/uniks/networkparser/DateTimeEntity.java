package de.uniks.networkparser;

import java.util.Date;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class DateTimeEntity implements SendableEntityCreatorNoIndex {
	public static final String W3CDTF_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private boolean dirty;
	private Long time;
	private Byte timeZone = 1;
	private SimpleKeyValueList<String, Long> fields = new SimpleKeyValueList<String, Long>();
	private TextItems items;

	/** Month of the Year Default is German */
	public String[] monthOfYear = new String[] { TextItems.DEFAULT.get("JANUARY"), TextItems.DEFAULT.get("FEBRUARY"),
			TextItems.DEFAULT.get("MARCH"), TextItems.DEFAULT.get("APRIL"), TextItems.DEFAULT.get("MAY"), TextItems.DEFAULT.get("JUNE"),
			TextItems.DEFAULT.get("JULY"), TextItems.DEFAULT.get("AUGUST"), TextItems.DEFAULT.get("SEPTEMBER"), TextItems.DEFAULT.get("OCTOBER"),
			TextItems.DEFAULT.get("NOVEMBER"), TextItems.DEFAULT.get("DECEMBER") };

	/** Days of the week */
	public String[] weekDays = new String[] { TextItems.DEFAULT.get("SUNDAY"), TextItems.DEFAULT.get("MONDAY"),
			TextItems.DEFAULT.get("TUESDAY"), TextItems.DEFAULT.get("WEDNESDAY"), TextItems.DEFAULT.get("THURSDAY"), TextItems.DEFAULT.get("FRIDAY"),
			TextItems.DEFAULT.get("SATURDAY")};
	private boolean isInitConstants = false;

	private static final int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }; // 0-based

	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = 60 * ONE_SECOND;
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_WEEK = 7 * ONE_DAY;
	public static final long ONE_YEAR = ONE_DAY * 365;
	public static final long ONE_YEAR_LY = ONE_DAY * 366;
	
	public static final String MILLISECOND="MILLISECOND";
	public static final String MILLISECONDS="MILLISECONDS";
	public static final String MILLISECONDSREAL="MILLISECONDSREAL";
	public static final String WINTERTIME="WINTERTIME";
	public static final String SUMMERTIME="SUMMERTIME";
	public static final String MILLISECOND_OF_DAY = "MILLISECOND_OF_DAY";
	public static final String MILLISECOND_OF_YEAR = "MILLISECOND_OF_YEAR";
	public static final String SECOND_OF_MINUTE = "SECOND_OF_MINUTE";
	public static final String MINUTE_OF_HOUR = "MINUTE_OF_HOUR";
	public static final String HOUR_OF_DAY = "HOUR_OF_DAY";
	public static final String AMPM = "AMPM";
	public static final String TIMEZONE = "TIMEZONE";
	public static final String DAY_OF_WEEK = "DAY_OF_WEEK";
	public static final String DAY_OF_MONTH = "DAY_OF_MONTH";
	public static final String DAY_OF_YEAR = "DAY_OF_YEAR";
	public static final String WEEK_OF_MONTH = "WEEK_OF_MONTH";
	public static final String WEEK_OF_YEAR = "WEEK_OF_YEAR";
	public static final String MONTH = "MONTH";
	public static final String YEAR = "YEAR";
	

	/**
	 * The normalized year of the gregorianCutover in Gregorian, with 0 representing
	 * 1 BCE, -1 representing 2 BCE, etc.
	 */
	private transient int GREGORIANCUTOVERYEAR = 1582;

	/**
	 * Returns the length of the specified month in the specified year. The year
	 * number must be normalized.
	 *
	 * @param month month as asking
	 * @param year  year of asking
	 * @return int day of Month
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
		if (month < 0 || month > 11) {
			return 0;
		}
		return MONTH_LENGTH[month];
	}

	/**
	 * Returns the length (in days) of the specified year. The year must be
	 * normalized.
	 *
	 * @param year year for asking
	 * @return day of fullyear
	 */
	public int getYearLength(int year) {
		return isLeapYear(year) ? 366 : 365;
	}

	/**
	 * Determines if the given year is a leap year. Returns <code>true</code> if the
	 * given year is a leap year. To specify BC year numbers,
	 * <code>1 - year number</code> must be given. For example, year BC 4 is
	 * specified as -3.
	 *
	 * @param year the given year.
	 * @return success: <code>true</code> if the given year is a leap year;
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

	private boolean internCalculate(long time, boolean calc) {
		long years = time / ONE_YEAR + 1970;
		long schaltjahre = ((years - 1) - 1968) / 4 - ((years - 1) - 1900) / 100 + ((years - 1) - 1600) / 400;
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
			this.fields.put(AMPM, 1L);
		} else {
			this.fields.put(AMPM, 0L);
		}
		long hour = daymillis / ONE_HOUR;

		// 01.01.70 is Tuersday
		long dayOfWeek = (time / ONE_DAY + 4) % 7;
		long leftDays = 31 - day;
		if (calc) {
			if (month > 3 && month < 10) {
				return true;
			} else if (month == 3 && leftDays < 7) {
				if ((7 - dayOfWeek) >= leftDays || (dayOfWeek == 7)) {
					return true;
				}
			} else if (month == 10 && leftDays < 7 || (dayOfWeek == 7)) {
				if ((7 - dayOfWeek) < leftDays) {
					return true;
				}
			}
		}
		this.fields.put(MILLISECOND_OF_DAY, daymillis);
		this.fields.put(HOUR_OF_DAY, hour);
		this.fields.put(MINUTE_OF_HOUR, (daymillis % ONE_HOUR) / ONE_MINUTE);
		this.fields.put(SECOND_OF_MINUTE, (daymillis % ONE_MINUTE) / ONE_SECOND);

		this.fields.put(MILLISECOND_OF_YEAR, yearMillis);

		long dayOfYear = yearMillis / ONE_DAY;
		this.fields.put(DAY_OF_YEAR, dayOfYear);
		this.fields.put(YEAR, (long) year);
		this.fields.put(MONTH, (long) month);
		this.fields.put(DAY_OF_MONTH, (long) day);

		this.fields.put(DAY_OF_WEEK, dayOfWeek);
		long week = dayOfYear / 7;
		if (dayOfYear % 7 > 0) {
			week++;
		}
//		week += 1;
		this.fields.put(WEEK_OF_YEAR, week);
		this.fields.put(WEEK_OF_MONTH, week - ((dayOfYear - day) / 7));
		this.fields.put(MILLISECONDSREAL, time);

		return false;
	}

	public void calculate() {
		if (this.dirty) {
			Long time = getTimeWithTimeZone();
			this.fields.put(MILLISECONDS, time);
			this.fields.put(MILLISECOND, time % ONE_SECOND);

			if (internCalculate(time, true)) {
				time += ONE_HOUR;
				internCalculate(time, false);
			}

			this.dirty = false;
		}
	}

	public long get(String field) {
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
	 * 
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
		if (value == null || value.equals(this.time) == false) {
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
		if ((this.timeZone == null && value != null) || (this.timeZone != null && !this.timeZone.equals(value))) {
			this.timeZone = value;
			this.dirty = true;
		}
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean add(String field, int value) {
		Long oldValue = get(field);
		return set(field, oldValue + value);
	}

	public boolean set(String field, int value) {
		return set(field, (long) value);
	}

	/**
	 * set to the date the amount value for the field
	 *
	 * @param field dateTimeField
	 * @param value value of changes
	 * @return success
	 */
	public boolean set(String field, long value) {
		if (field != null) {
			long oldValue = getValueInMillisecond(field);
			if(field.equals(MONTH)) {
				oldValue = getValueInMillisecond(MILLISECOND_OF_YEAR);
				fields.put(field, value);
				addTime(getValueInMillisecond(MILLISECOND_OF_YEAR) - oldValue);
			} else if(field.equals(TIMEZONE)) {
				withTimezone((byte) value);
			} else {
				fields.put(field, value);
				addTime(getValueInMillisecond(field) - oldValue);
			}
			return true;
		}
		return false;
	}

	/**
	 * Setter with milliseconds
	 *
	 * @param milliseconds milliseconds since 01.01.1970
	 * @return Itself
	 */
	public DateTimeEntity withValue(long milliseconds) {
		withTime(milliseconds);
		return this;
	}

	/**
	 * Setter with day, month and year
	 *
	 * @param year  year of the date
	 * @param month month of the date
	 * @param day   day of the date
	 * @return Itself
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
	 * @param date date as String
	 * @return Itself
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
	 * @param date with new date
	 * @return Itself
	 */
	public DateTimeEntity withValue(java.util.Date date) {
		withValue(date.getTime());
		return this;
	}

	/**
	 * Set new TimeStamp
	 *
	 * @param date a new Date
	 * @return Itself
	 */
	public DateTimeEntity withNewDate(long date) {
		withTime(date * ONE_SECOND);
		return this;
	}

	/**
	 * @param items The new TextItem for text
	 * @return Itself
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
	 * @param year xear that is greater then 1583
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
	 * @param format The Format
	 * @return a String of Date
	 */
	public String toString(String format) {
		initDate();
		calculate();
		CharacterBuffer sb = new CharacterBuffer();
		String sub;
		Tokener tokener = new Tokener();
		CharacterBuffer dateFormat = new CharacterBuffer().with(format);
		dateFormat.replace('\'', '\"');
		boolean isString = false;
		do {
			sub = tokener.nextString(dateFormat, new CharacterBuffer(), false, false, '"').toString();
			// FIXME Change String to StringContainter
			if (sub.length() > 0 && !isString) {
				sub = sub.replace("HZ", EntityUtil.strZero(get(HOUR_OF_DAY) - getTimezone(), 2));
				sub = sub.replace("HH", EntityUtil.strZero(get(HOUR_OF_DAY), 2));
				sub = sub.replace("H", String.valueOf(get(HOUR_OF_DAY)));
				sub = sub.replace("MM", EntityUtil.strZero(get(MINUTE_OF_HOUR), 2));
				sub = sub.replace("M", String.valueOf(get(MINUTE_OF_HOUR)));
				sub = sub.replace("SS", EntityUtil.strZero(get(SECOND_OF_MINUTE), 2));
				sub = sub.replace("S", String.valueOf(get(SECOND_OF_MINUTE)));
				// Date
				sub = sub.replace("dddd", this.weekDays[(int) get(DAY_OF_WEEK)]);
				sub = sub.replace("ddd", this.weekDays[(int) get(DAY_OF_WEEK)].substring(0, 3));
				sub = sub.replace("dd", EntityUtil.strZero(get(DAY_OF_MONTH), 2));
				sub = sub.replace("d", String.valueOf(get(DAY_OF_MONTH)));
				sub = sub.replace("mmmm", this.monthOfYear[(int) get(MONTH) - 1]);
				sub = sub.replace("mmm", this.monthOfYear[(int) get(MONTH) - 1].substring(0, 3));
				sub = sub.replace("mm", EntityUtil.strZero(get(MONTH), 2));
				sub = sub.replace("m", String.valueOf(get(MONTH)));
				sub = sub.replace("yyyy", String.valueOf(get(YEAR)));
				sub = sub.replace("yyy", String.valueOf(get(YEAR)));
				sub = sub.replace("yy", EntityUtil.strZero(get(YEAR), 2, 2));
				sub = sub.replace("y", EntityUtil.strZero(get(YEAR), 1, 2));
				if (this.timeZone > 0) {
					sub = sub.replace("Z", "+" + EntityUtil.strZero(this.timeZone, 2, 2) + "00");
				} else if (this.timeZone < 0) {
					sub = sub.replace("Z", "-" + EntityUtil.strZero(this.timeZone, 2, 2) + "00");
				} else {
					sub = sub.replace("Z", "0000");
				}
				sub = sub.replace("z", "CEST");
			}
			sb.with(sub);
			if (dateFormat.getCurrentChar() == '\"') {
				dateFormat.getChar();
			}
			isString = !isString;
		} while (sub.length() > 0);

		return sb.toString();
	}

	@Override
	public String toString() {
		if (this.isDirty()) {
			this.calculate();
		}
		return this.get(DAY_OF_MONTH) + "." + this.fields.get(MONTH) + "."
				+ this.fields.get(YEAR);
	}

	public String toGMTString() {
		// d MMM yyyy HH:mm:ss 'GMT'
		return this.toString("ddd, dd mmm yyyy HH:MM:SS 'GMT'");
	}

	// SETTER
	/**
	 * set a new year for the date
	 *
	 * @param value the newYear
	 * @return Itself
	 */
	public DateTimeEntity withYear(int value) {
		set(YEAR, value);
		return this;
	}

	/**
	 * set a new month for the Date
	 *
	 * @param value The new Month
	 * @return Itself
	 */
	public DateTimeEntity withMonth(int value) {
		set(MONTH, value);
		return this;
	}

	public DateTimeEntity withDate(int value) {
		set(DAY_OF_MONTH, value);
		return this;
	}

	public DateTimeEntity withHour(int value) {
		set(HOUR_OF_DAY, value);
		return this;
	}

	public DateTimeEntity withMinute(int value) {
		set(MINUTE_OF_HOUR, value);
		return this;
	}

	public DateTimeEntity withSecond(int value) {
		set(SECOND_OF_MINUTE, value);
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
	 * @param field The Field for get
	 * @return the Value As Milliseconds
	 */
	public long getValueInMillisecond(String field) {
		long value = fields.get(field);
		if (field.equals(MILLISECOND) || field.equals(MILLISECONDS) || field.equals(MILLISECOND_OF_YEAR)
				|| field.equals(MILLISECOND_OF_DAY) || field.equals(MILLISECONDSREAL)) {
			return value;
		} else if (field.equals(SECOND_OF_MINUTE)) {
			return value * DateTimeEntity.ONE_SECOND;
		} else if (field.equals(MINUTE_OF_HOUR)) {
			return value * DateTimeEntity.ONE_MINUTE;
		} else if (field.equals(HOUR_OF_DAY)) {
			return value * DateTimeEntity.ONE_HOUR;
		} else if (field.equals(DAY_OF_WEEK) || field.equals(DAY_OF_MONTH)
				|| field.equals(DAY_OF_YEAR)) {
			return value * DateTimeEntity.ONE_DAY;
		} else if (field.equals(AMPM)) {
			return value * (DateTimeEntity.ONE_DAY / 2);
		} else if (field.equals(WEEK_OF_MONTH) || field.equals(WEEK_OF_YEAR)) {
			return value * DateTimeEntity.ONE_WEEK;
		} else if (field.equals(YEAR)) {
			int year = Integer.parseInt("" + value);
			int schaltjahre = ((year - 1) - 1968) / 4 - ((year - 1) - 1900) / 100 + ((year - 1) - 1600) / 400;
			return (year - schaltjahre) * DateTimeEntity.ONE_YEAR + (schaltjahre * DateTimeEntity.ONE_YEAR_LY);
		}
		return 0;
	}
	
	/** The Constant VALUE. */
	public static final String VALUE = "value";

	/* return the Properties */
	@Override
	public String[] getProperties() {
		return new String[] { VALUE };
	}

	/* Create new Instance of Date */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new Date();
	}

	/* Getter for java.util.Date */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (VALUE.equals(attribute)) {
			return Long.valueOf(((Date) entity).getTime());
		}
		return null;
	}

	/* Setter for java.util.Date */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		if (VALUE.equals(attribute)) {
			((Date) entity).setTime((Long) value);
			return true;
		}
		return false;
	}
}
