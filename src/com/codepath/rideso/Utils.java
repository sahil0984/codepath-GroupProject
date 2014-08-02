package com.codepath.rideso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateUtils;
import android.util.Log;

public class Utils {   

	public static String[] stringToDateTime(String dateTime) {
		
		String date = "";
		String time = "";
		
		date = dateTime.replaceAll(" .*:.*", "");
		time = dateTime.replaceAll(".*/.*/.* ", "");

		//Toast.makeText(context, date, Toast.LENGTH_SHORT).show();
		
		if (date.equals("01/01/3001")) {
			date = "";
		}
		if (time.equals("25:25")) {
			time = "";
		} else {
			String hr = time.replaceAll(":.*", "");
			String min = time.replaceAll(".*:", "");
			if (Integer.parseInt(hr)>12) {
				time = (Integer.parseInt(hr)-12) + ":" + min + "pm";
			} else {
				time = hr + ":" + min + "am";
			}
		}
				
		//Toast.makeText(context, date, Toast.LENGTH_SHORT).show();

		String[] dateTimeQualified = new String[2];
		
		dateTimeQualified[0] = date;
		dateTimeQualified[1] = time;
		
		return dateTimeQualified;
	}
	
	
	public static String getDiffInDateTime(String dateTimeStart, String dateTimeEnd) {
		

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		Date dateStart;
		Date dateEnd;
		try {
			dateStart = format.parse(dateTimeStart);
			dateEnd = format.parse(dateTimeEnd);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		
		long difference = dateEnd.getTime() - dateStart.getTime();
		long seconds = difference / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		
		String diffTime = "";
		if (days!=0) {
			diffTime = days + "d " + hours%24 + "h " + minutes%60 + "m";
		} else if (hours!=0) {
			diffTime = hours%24 + "h " + minutes%60 + "m";
		} else if (minutes!=0) {
			diffTime = minutes%60 + "m";
		}
		
		//Log.d("timeFormattingDebug", difference + " " + seconds + " " + minutes);
		
		return diffTime;
	}
	
	//Do not use this function. It has bugs.
	public static String stringToDateTimeRelative(String dateTime) {
		
		long dateMillis;
		String relativeDate = "";
		
		// Add Recurring parameter.(repeat/or not) absolute or relative depending on Ans.
		try {
			dateMillis = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault()).parse(dateTime).getTime();
			//dateMillis = new SimpleDateFormat("MM/dd/yyyy'T'hh:mm", Locale.ENGLISH).parse(dateTime).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
			
			//relativeDate = (String) DateUtils.getRelativeDateTimeString(context, dateMillis, DateUtils.SECOND_IN_MILLIS,
			//			DateUtils.WEEK_IN_MILLIS, 0);
		} catch (java.text.ParseException e) {
			dateMillis = 0;
			e.printStackTrace();
		}
		
		return relativeDate;
	}

}  