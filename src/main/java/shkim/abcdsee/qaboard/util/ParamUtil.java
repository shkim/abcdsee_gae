package shkim.abcdsee.qaboard.util;

import javax.servlet.http.HttpServletRequest;

public class ParamUtil
{
	public static int getInt(HttpServletRequest req, String varname, int defaultValue)
	{
		String value = req.getParameter(varname);
		if (value == null)
			return defaultValue;

		try
		{
			return Integer.parseInt(value);
		}
		catch(Exception ex)
		{
			return defaultValue;
		}
	}

	public static long getLong(HttpServletRequest req, String varname, long defaultValue)
	{
		String value = req.getParameter(varname);
		if (value == null)
			return defaultValue;

		try
		{
			return Long.parseLong(value);
		}
		catch(Exception ex)
		{
			return defaultValue;
		}
	}

	public static double getDouble(HttpServletRequest req, String varname, double defaultValue)
	{
		String value = req.getParameter(varname);
		if (value == null)
			return defaultValue;

		try
		{
			return Double.parseDouble(value);
		}
		catch(Exception ex)
		{
			return defaultValue;
		}
	}

	public static String getString(HttpServletRequest req, String varname, String defaultValue)
	{
		String value = req.getParameter(varname);
		if (value == null)
			return defaultValue;

		try
		{
			return  value;
		}
		catch(Exception ex)
		{
			return defaultValue;
		}
	}

	public static boolean getBool(HttpServletRequest req, String varname)
	{
		String value = req.getParameter(varname);
		if (value == null)
			return false;

		return value.equals("1") || value.equalsIgnoreCase("true");
	}
}