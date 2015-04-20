package shkim.abcdsee.qaboard.service;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.dao.TodoEntryDao;
import shkim.abcdsee.qaboard.domain.TodoEntry;

import java.util.Date;
import java.util.List;

@Component
public class TodoService
{
	@Autowired
	private TodoEntryDao entryDao;

	public TodoEntry createNew(User user, int priority, int ordering, String subject, String content)
	{
		TodoEntry entry = new TodoEntry();

		entry.setUser(user);
		entry.setLastUpdate(new Date());

		entry.setPriority((byte)priority);
		entry.setOrdering(ordering);
		entry.setSubject(subject);
		entry.setContent(content);

		entryDao.persist(entry);
		return entry;
	}

	public void update(TodoEntry entry, int priority, int ordering, String subject, String content)
	{
		entry.setLastUpdate(new Date());

		entry.setPriority((byte)priority);
		entry.setOrdering(ordering);
		entry.setSubject(subject);
		entry.setContent(content);

		entryDao.persist(entry);
	}

	public TodoEntry get(int todoId)
	{
		return entryDao.get(Long.valueOf(todoId));
	}
	
	public List<TodoEntry> getList()
	{
		return entryDao.getList();
	}
}
