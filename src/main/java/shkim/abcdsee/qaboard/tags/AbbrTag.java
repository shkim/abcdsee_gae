package shkim.abcdsee.qaboard.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class AbbrTag extends BodyTagSupport
{
	private static final long serialVersionUID = 1040377321944565028L;
	
	private int abbrLimitLength;
	private String abbrDots = "..";

	public void setLimit(String limit)
	{
		abbrLimitLength = Integer.parseInt(limit);
	}
	
	public void setDots(String dots)
	{
		abbrDots = dots;
	}

	public int doAfterBody() throws JspException
	{
		try
		{
			BodyContent bc = getBodyContent();
			String body = bc.getString();
			JspWriter out = bc.getEnclosingWriter();
			if (body != null)
			{
				out.print( abbreviate(body, abbrLimitLength) );
			}
		}
		catch (IOException ioe)
		{
			throw new JspException("Error: " + ioe.getMessage());
		}
		
		return SKIP_BODY;
	}

	private String abbreviate(String str, int maxWidth)
	{
		if (str == null)
			return null;

		int strLen = str.length();
		if (strLen <= maxWidth)
			return str;

		final int dotWidth = 1;//abbrDots.length();
		return (str.substring(0, maxWidth - dotWidth) + abbrDots);
	}
}
