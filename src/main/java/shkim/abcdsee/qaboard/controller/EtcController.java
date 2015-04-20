package shkim.abcdsee.qaboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import shkim.abcdsee.qaboard.domain.VersionInfo;
import shkim.abcdsee.qaboard.service.VersionService;
import shkim.abcdsee.qaboard.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class EtcController
{
	@Autowired
	private VersionService versionService;

	@RequestMapping("/etc/chlang")
	public ModelAndView _chlang(HttpServletRequest req)
    {
		int langId = ParamUtil.getInt(req, "lang", 0);

		// pageContext.request.locale.language == 'ko'
		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("lang", langId == 1 ? "ko_KR" : "en_US");
		model.put("url", req.getParameter("url"));
		return new ModelAndView("etc_chlang", model);
	}

	@RequestMapping("/etc/about")
	public ModelAndView _about()
    {
		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("lver", versionService.getLatestVersion());

		return new ModelAndView("etc_about", model);
	}

	@RequestMapping("/etc/relnotes")
	public ModelAndView _version()
    {
		HashMap<String,Object> model = new HashMap<String,Object>();
		List<VersionInfo> vl = versionService.getList(10);
		for(VersionInfo ver : vl)
		{
			String[] lines = ver.getChangeNotes().split("\n");
			StringBuilder sb = new StringBuilder();
			for (String line : lines)
			{
				if (line.trim().isEmpty())
					continue;
				
				sb.append("<li>");
				sb.append(line);
				sb.append("</li>\n");
			}
			ver.setChangeNotes(sb.toString());
		}

		model.put("verList", vl);

		return new ModelAndView("etc_relnote", model);
	}
}
