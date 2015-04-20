package shkim.abcdsee.qaboard.tags;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/// PHP 의 nl2br() 과 같음: \n 을 <br/> 로 바꾸어 출력한다.
public class Nl2brTag extends BodyTagSupport
{
	private static final long serialVersionUID = 4950787121079431835L;

	public int doAfterBody() throws JspException
	{
		try
		{
			BodyContent bc = getBodyContent();
			String body = bc.getString();
			JspWriter out = bc.getEnclosingWriter();
			if (body != null)
			{
				out.print(body.replace("\n", "<br/>"));
			}
		}
		catch (IOException ioe)
		{
			throw new JspException("Error: " + ioe.getMessage());
		}
		
		return SKIP_BODY;
	}
}
