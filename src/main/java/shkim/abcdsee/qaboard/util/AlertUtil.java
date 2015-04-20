package shkim.abcdsee.qaboard.util;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


public class AlertUtil
{
	public static ModelAndView forward(String msg, String href)
	{
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("msg", msg);
		model.put("href", href);
		model.put("back", false);
		return new ModelAndView("alert_then_move", model);
	}

	public static ModelAndView back(String msg)
	{
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("msg", msg);
		model.put("back", true);
		return new ModelAndView("alert_then_move", model);
	}
}
