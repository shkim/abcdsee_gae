package shkim.abcdsee.qaboard.controller;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import shkim.abcdsee.qaboard.domain.BugEntry;
import shkim.abcdsee.qaboard.domain.BugReply;
import shkim.abcdsee.qaboard.service.BugService;
import shkim.abcdsee.qaboard.service.PagerInfo;
import shkim.abcdsee.qaboard.util.AlertUtil;
import shkim.abcdsee.qaboard.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping("/bugs")
public class BugBbsController
{
	@Autowired
	private BugService bugService;

	private UserService userService = UserServiceFactory.getUserService();
	private BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();

	private static final int POST_ERROR_INVALID_PARAM = 0;
	private static final int POST_ERROR_NOT_AUTHORIZED = 1;
	private static final int POST_ERROR_REPORT_NOAUTH = 2;
	private static final int POST_ERROR_REPORT_CREATE = 3;
	private static final int POST_ERROR_REPLY_NOAUTH = 4;
	private static final int POST_ERROR_REPLY_CREATE = 5;
	private static final int POST_ERROR_REPLY_INCONSISTENT = 6;

	private static final int MASK_STATE_FOUND = 1 << (BugEntry.STATE_FOUND -1);
	private static final int MASK_STATE_ACCEPTED = 1 << (BugEntry.STATE_ACCEPTED -1);
	private static final int MASK_STATE_UNFIXED = 1 << (BugEntry.STATE_UNFIXED -1);
	private static final int MASK_STATE_FIXED = 1 << (BugEntry.STATE_FIXED -1);
	private static final int MASK_STATE_REJECTED = 1 << (BugEntry.STATE_REJECTED -1);
	private static final int MASK_STATE_CONFIRMED = 1 << (BugEntry.STATE_CONFIRMED -1);
	private static final int MASK_STATE_CLOSED = 1 << (BugEntry.STATE_CLOSED -1);

	private static int[] stateFlowMask = new int[] {
		0,
		MASK_STATE_ACCEPTED | MASK_STATE_REJECTED | MASK_STATE_FIXED,
		MASK_STATE_FIXED,
		MASK_STATE_FIXED | MASK_STATE_REJECTED,
		MASK_STATE_CONFIRMED | MASK_STATE_UNFIXED,
		MASK_STATE_UNFIXED,
		MASK_STATE_CLOSED,
		0
	};

	private ModelAndView makeList(HttpServletRequest req, int group)
	{
		int page = ParamUtil.getInt(req, "pg", 0);
		int priority = ParamUtil.getInt(req, "pr", 0);
		int state = ParamUtil.getInt(req, "st", 0);
		int ktype = ParamUtil.getInt(req, "kt", 0);
		String keyword = req.getParameter("q");

		PagerInfo pi = new PagerInfo(8);
		pi.init(req);

		List<BugEntry> bugs = bugService.getList(group, priority, state, ktype, keyword, pi);
		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("bugList", bugs);
		model.put("priority", priority);
		model.put("state", state);
		model.put("ktype", ktype);
		model.put("keyword", keyword);
		model.put("group", group);
		model.put("pagerInfo", pi);

		return new ModelAndView("bug_list", model);
	}

	@RequestMapping("/list")
	public ModelAndView _list(HttpServletRequest req)
    {
		return makeList(req, BugEntry.SECTION_CURRENT);
    }

	@RequestMapping("/prevlist")
	public ModelAndView _prevlist(HttpServletRequest req)
    {
		return makeList(req, BugEntry.SECTION_PREVIOUS);
	}

	@RequestMapping("/view/{bugId}")
	public ModelAndView _view(@PathVariable int bugId)
    {
		HashMap<String,Object> model = new HashMap<String,Object>();
		BugEntry bug = bugService.getBug(bugId);
		if (bug == null)
			return AlertUtil.forward("alert.invalid.param", "/bugs/list");

		model.put("bug", bug);
		if (bug.getSection() == 0 && userService.isUserLoggedIn() && userService.isUserAdmin())
			model.put("canMoveSection", true);

		return new ModelAndView("bug_view", model);
	}

	@RequestMapping(value="error", method= RequestMethod.GET)
    public ModelAndView post_error(HttpServletRequest req)
    {
		int errCode = ParamUtil.getInt(req, "e", 0);
		String url;
		int bugId = ParamUtil.getInt(req, "id", 0);

		// FIXME: prefix with request.getContextPath()

		switch(errCode)
		{
			case POST_ERROR_INVALID_PARAM:
				return AlertUtil.forward("alert.invalid.param", "/bugs/report");

			case POST_ERROR_NOT_AUTHORIZED:
				return AlertUtil.forward("alert.invalid.nickname", "/bugs/list");

			case POST_ERROR_REPORT_NOAUTH:
				url = userService.createLoginURL("/bugs/report");
				return AlertUtil.forward("alert.needsign.towrite", url);

			case POST_ERROR_REPORT_CREATE:
				return AlertUtil.back("alert.postnew.fail");

			case POST_ERROR_REPLY_NOAUTH:
				url = userService.createLoginURL("/bugs/reply/"+bugId);
				return AlertUtil.forward("alert.needsign.towrite", url);

			case POST_ERROR_REPLY_CREATE:
				return AlertUtil.back("alert.postreply.fail");

			case POST_ERROR_REPLY_INCONSISTENT:
				return AlertUtil.back("alert.postreply.inconsist");
		}

		return AlertUtil.forward("alert.invalid.param", "/bugs/list");
	}

	@RequestMapping("/move2prev/{bugId}")
	public ModelAndView move_to_prev(@PathVariable int bugId)
    {
		BugEntry bug = bugService.getBug(bugId);
		if (bug != null && bug.getSection() == 0 && userService.isUserAdmin())
		{
			bugService.updateBugSection(bug, BugEntry.SECTION_PREVIOUS);
			return AlertUtil.forward("alert.done", "/bugs/prevlist");
		}

		return AlertUtil.forward("alert.invalid.param", "/bugs/list");

	}

	@RequestMapping("/report")
    public ModelAndView _report(HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn())
		{
			// FIXME: prefix with request.getContextPath()
			String url = userService.createLoginURL("/bugs/report");
			return AlertUtil.forward("alert.needsign.towrite", url);
		}

		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("realnick", userService.getCurrentUser().getNickname());
		model.put("postUrl", blobService.createUploadUrl("/bugs/save_new"));
		return new ModelAndView("bug_form", model);
	}

	@RequestMapping("/reply/{bugId}")
	public ModelAndView _reply(@PathVariable int bugId, HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn())
		{
			String url = userService.createLoginURL("/bugs/reply/"+bugId);
			return AlertUtil.forward("alert.needsign.towrite", url);
		}

		BugEntry bug = bugService.getBug(bugId);
		if (bug == null)
		{
			return AlertUtil.back("alert.invalid.param");
		}

		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("bug", bug);
		model.put("showform", true);
		model.put("realnick", userService.getCurrentUser().getNickname());
		model.put("flowmask", stateFlowMask[bug.getState()]);
		model.put("postUrl", blobService.createUploadUrl("/bugs/save_re"));

		return new ModelAndView("bug_view", model);
	}

	@RequestMapping("/edit/{bugId}/{rIdx}")
	public ModelAndView _edit(@PathVariable int bugId, @PathVariable int rIdx, HttpServletRequest req)
    {
		if (!userService.isUserLoggedIn())
		{
			String url = userService.createLoginURL("/bugs/reply/"+bugId);
			return AlertUtil.forward("alert.needsign.towrite", url);
		}

		BugEntry bug = bugService.getBug(bugId);
		if (bug != null && rIdx > 0 && rIdx <= bug.getReplyCount())
		{
			BugReply reply = bug.getReplyList().get(rIdx -1);
			if (reply.getUser().equals(userService.getCurrentUser()))
			{
				HashMap<String,Object> model = new HashMap<String,Object>();
				model.put("bug", bug);
				model.put("reply", reply);
				model.put("rIndex", rIdx);
				model.put("postUrl", "/bugs/update_re");//blobService.createUploadUrl("/bugs/update_re"));

				return new ModelAndView("bug_edit", model);
			}
		}

		return AlertUtil.back("alert.invalid.param");
	}

	@RequestMapping(value="save_new", method= RequestMethod.POST)
    public void save_new(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
		boolean success = false;

		Map<String, BlobKey> blobs = blobService.getUploadedBlobs(req);
		BlobKey blobKey;
		if (blobs.keySet().isEmpty())
		{
			blobKey = null;
		}
		else
		{
			Iterator<String> names = blobs.keySet().iterator();
			String blobName = names.next();
			blobKey = blobs.get(blobName);
		}
		
		if (!userService.isUserLoggedIn())
		{
			resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPORT_NOAUTH);
		}
		else
		{
			byte prio = (byte) ParamUtil.getInt(req, "priority", 0);
			if (prio < BugEntry.PRI_CRITICAL || prio > BugEntry.PRI_LOW)
			{
				resp.sendRedirect("/bugs/error?e="+POST_ERROR_INVALID_PARAM);
			}
			else
			{
				HashMap<String,Object> model = new HashMap<String,Object>();
				User user = userService.getCurrentUser();
				String writer = req.getParameter("nickname");
				String subject = req.getParameter("subject");
				String content = req.getParameter("content");

				if ("abcdsee".equalsIgnoreCase(writer) && !userService.isUserAdmin())
				{
					resp.sendRedirect("/bugs/error?e="+POST_ERROR_NOT_AUTHORIZED);
				}
				else
				{
					BugEntry bug = bugService.createNewEntry(user, prio, writer, subject, content, blobKey);
					if(null != bug)
					{
						success = true;
						resp.sendRedirect("/bugs/view/"+bug.getId());
						//return AlertUtil.forward("alert.postnew.success", "/bugs/view/"+bug.getId());
					}
					else
					{
						resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPORT_CREATE);
					}
				}
			}
		}

		if (success == false)
		{
			blobService.delete(blobKey);
		}
	}

	@RequestMapping(value="save_re", method= RequestMethod.POST)
    public void save_reply(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
		boolean success = false;
		int bugId = ParamUtil.getInt(req, "bugId", 0);

		Map<String, BlobKey> blobs = blobService.getUploadedBlobs(req);
		BlobKey blobKey;
		if (blobs.keySet().isEmpty())
		{
			blobKey = null;
		}
		else
		{
			Iterator<String> names = blobs.keySet().iterator();
			String blobName = names.next();
			blobKey = blobs.get(blobName);
		}

		if (!userService.isUserLoggedIn())
		{
			resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPLY_NOAUTH+"&id="+bugId);
		}
		else
		{
			BugEntry bug = bugService.getBug(bugId);
			if (bug == null)
			{
				resp.sendRedirect("/bugs/error?e="+POST_ERROR_INVALID_PARAM);
			}
			else
			{
				int replyCount = ParamUtil.getInt(req, "replyCount", 0);
				if (bug.getReplyCount() != replyCount)
				{
					resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPLY_INCONSISTENT+"&id="+bugId);
				}
				else
				{
					User user = userService.getCurrentUser();
					byte state = (byte) ParamUtil.getInt(req, "state", 0);
					String writer = req.getParameter("nickname");
					String content = req.getParameter("content");

					if ("abcdsee".equalsIgnoreCase(writer) && !userService.isUserAdmin())
					{
						resp.sendRedirect("/bugs/error?e="+POST_ERROR_NOT_AUTHORIZED);
					}
					else
					{
						if(bugService.addBugReply(bug, user, state, writer, content, blobKey))
						{
							success = true;
							resp.sendRedirect("/bugs/view/"+bugId);
						}
						else
						{
							resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPLY_CREATE+"&id="+bugId);
						}
					}
				}
			}
		}

		if (success == false)
		{
			blobService.delete(blobKey);
		}
	}

	@RequestMapping(value="update_re", method= RequestMethod.POST)
    public void update_reply(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
		int bugId = ParamUtil.getInt(req, "bugId", 0);
		int rIndex = ParamUtil.getInt(req, "rIndex", 0);

		if (!userService.isUserLoggedIn())
		{
			resp.sendRedirect("/bugs/error?e="+POST_ERROR_REPLY_NOAUTH+"&id="+bugId);
		}
		else
		{
			BugEntry bug = bugService.getBug(bugId);
			if (bug != null && rIndex > 0 && rIndex <= bug.getReplyCount())
			{
				BugReply reply = bug.getReplyList().get(rIndex -1);
				if (reply.getUser().equals(userService.getCurrentUser()))
				{
					String writer = req.getParameter("nickname");
					String content = req.getParameter("content");

					if (rIndex == 1)
					{
						String subject = req.getParameter("subject");
						bugService.updateBugSubject(bug, subject, writer);
					}

					if(bugService.updateReply(reply, writer, content))
					{
						resp.sendRedirect("/bugs/view/"+bugId);
						return;
					}
				}
			}
		}

		resp.sendRedirect("/bugs/error?e="+POST_ERROR_INVALID_PARAM);
	}

	@RequestMapping("/getfile/{blobKeyStr}")
	public void _getfile(@PathVariable String blobKeyStr, HttpServletResponse resp) throws IOException
	{
		BlobKey blobKey = new BlobKey(blobKeyStr);
		blobService.serve(blobKey, resp);
	}
}
