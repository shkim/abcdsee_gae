package shkim.abcdsee.qaboard.controller;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import shkim.abcdsee.qaboard.domain.TodoEntry;
import shkim.abcdsee.qaboard.service.TodoService;
import shkim.abcdsee.qaboard.util.AlertUtil;
import shkim.abcdsee.qaboard.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/todo")
public class TodoController
{
	@Autowired
	private TodoService todoService;

	private UserService userService = UserServiceFactory.getUserService();

	@RequestMapping("/list")
	public ModelAndView _list(HttpServletRequest req)
    {
		List<TodoEntry> tds = todoService.getList();
		HashMap<String,Object> model = new HashMap<String,Object>();
		model.put("todoList", tds);

		return new ModelAndView("todo_list", model);
    }

	@RequestMapping("/form")
	public ModelAndView _form(HttpServletRequest req)
    {
		int id = ParamUtil.getInt(req, "id", 0);

		HashMap<String,Object> model = new HashMap<String,Object>();

		if (id > 0)
		{
			TodoEntry todo = todoService.get(id);
			System.out.println("todo "+id+"="+todo);
			model.put("priority", todo.getPriority());
			model.put("ordering", todo.getOrdering());
			model.put("subject", todo.getSubject());
			model.put("content", todo.getContent());
		}

		model.put("todoId", id);
		model.put("postUrl", "/todo/save");
		return new ModelAndView("todo_form", model);
	}

	@RequestMapping(value="save", method= RequestMethod.POST)
    public ModelAndView _save(HttpServletRequest req)
    {
		if (!userService.isUserAdmin())
		{
			return AlertUtil.back("alert.invalid.param");
		}

		int todoId = ParamUtil.getInt(req, "id", 0);
		int priority = ParamUtil.getInt(req, "priority", TodoEntry.PRIO_LOW);
		int ordering = ParamUtil.getInt(req, "ordering", 0);
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");

		TodoEntry todo;
		if (todoId > 0)
		{
			todo = todoService.get(todoId);
			if (todo == null)
				return AlertUtil.back("alert.invalid.param");

			todoService.update(todo, priority, ordering, subject, content);
		}
		else
		{
			todo = todoService.createNew(userService.getCurrentUser(), priority, ordering, subject, content);
		}

		return AlertUtil.forward("alert.postnew.success", "/todo/view/"+todo.getId());
	}

	@RequestMapping("/view/{todoId}")
	public ModelAndView _view(@PathVariable int todoId)
    {
		HashMap<String,Object> model = new HashMap<String,Object>();
		TodoEntry todo = todoService.get(todoId);
		if (todo == null)
			return AlertUtil.forward("alert.invalid.param", "/todo/list");

		model.put("todo", todo);
		model.put("isAdmin", userService.isUserLoggedIn() && userService.isUserAdmin());
		return new ModelAndView("todo_view", model);
	}
}
