package shkim.abcdsee.qaboard.util;

import java.util.Date;

public class DateTimeUtil
{
	public static String formatTillDay(Date dt)
	{
		return String.format("%04d-%02d-%02d",
			1900 + dt.getYear(), 1 + dt.getMonth(), dt.getDate());
	}

	public static String formatTillMinute(Date dt)
	{
		return String.format("%04d-%02d-%02d %02d:%02d",
			1900 + dt.getYear(), 1 + dt.getMonth(), dt.getDate(),
			dt.getHours(), dt.getMinutes());
	}
}
