package de.uniks.jism.date;

public enum DateField {
	MILLISECONDS("milliseconds"), MILLISECOND_OF_DAY("millisecond_of_day"), MILLISECOND_OF_YEAR("millisecond_of_year"),
	SECONDS("seconds"), SECOND_OF_MINUTE("second_of_minute"), SECOND_OF_DAY("second_of_minute"), SECOND_OF_YEAR("second_of_year"),
	MINUTES("minutes"), MINUTE_OF_HOUR("minute_of_hour"),
	HOURS("hours"), HOUR_OF_DAY("hour_of_day"), AMPM("am/pm"), TIMEZONE("zone"),
	DAYS("days"), DAY_OF_WEEK("day_of_week"), DAY_OF_MONTH("day_of_month"), DAY_OF_YEAR("day_of_year"), 
	WEEK_OF_MONTH("week_of_month"), WEEK_OF_YEAR("week_of_year"),  
	MONTH("month"), YEAR("year");
	private String value;
	
	DateField(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}

}
