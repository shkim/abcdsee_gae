package shkim.abcdsee.qaboard.controller;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import shkim.abcdsee.qaboard.domain.VersionInfo;
import shkim.abcdsee.qaboard.service.VersionService;
import shkim.abcdsee.qaboard.util.AlertUtil;
import shkim.abcdsee.qaboard.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/version")
public class VersionController
{
	@Autowired
	private VersionService versionService;

	private UserService userService = UserServiceFactory.getUserService();

	@RequestMapping("/list")
	public ModelAndView _list(HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn() || !userService.isUserAdmin())
		{
			return AlertUtil.back("alert.invalid.param");
		}

		HashMap<String,Object> model = new HashMap<String,Object>();
		List<VersionInfo> vl = versionService.getList(100);
		model.put("verList", vl);

		return new ModelAndView("ver_list", model);
	}

	@RequestMapping("/form")
	public ModelAndView _form(HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn() || !userService.isUserAdmin())
		{
			return AlertUtil.back("alert.invalid.param");
		}
		
		HashMap<String,Object> model = new HashMap<String,Object>();

		int id = ParamUtil.getInt(req, "id", 0);
		if (id > 0)
		{
			VersionInfo version = versionService.get(id);
			System.out.println("version "+id+"="+version);
			model.put("ver", version);
		}

		model.put("verId", id);
		return new ModelAndView("ver_form", model);
	}

	@RequestMapping(value="save", method= RequestMethod.POST)
    public ModelAndView _save(HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn() || !userService.isUserAdmin())
		{
			return AlertUtil.back("alert.invalid.param");
		}

		int verId = ParamUtil.getInt(req, "id", 0);
		int fileSize = ParamUtil.getInt(req, "filesize", 0);
		String name = req.getParameter("name");
		String downUrl = req.getParameter("url");
		String notes = req.getParameter("notes");

		VersionInfo ver;
		if (verId > 0)
		{
			ver = versionService.get(verId);
			if (ver == null)
				return AlertUtil.back("alert.invalid.param");

			versionService.update(ver, name, downUrl, fileSize, notes);
		}
		else
		{
			ver = versionService.createNew(name, downUrl, fileSize, notes);
		}

		return AlertUtil.forward("alert.postnew.success", "/version/form?id="+ver.getId());
	}

	@RequestMapping("/latest")
	public ModelAndView _latest(HttpServletRequest req)
    {
		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("ver", versionService.getLatestVersion());
		return new ModelAndView("ver_latest", model);
	}
}
