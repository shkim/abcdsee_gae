package shkim.abcdsee.qaboard.dao;

import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.domain.TodoEntry;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

@Component
public class TodoEntryDao
{
	public TodoEntry get(Long todoId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			TodoEntry _td = pm.getObjectById(TodoEntry.class, todoId);
			return pm.detachCopy(_td);
		}
		catch (JDOObjectNotFoundException ex)
		{
			System.out.println("TodoEntry for " +todoId+ " not found.");
      		return null;
		}
		finally
		{
			pm.close();
		}
	}
	
	public List<TodoEntry> getList()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			Query query = pm.newQuery(TodoEntry.class);
			query.setOrdering("priority ASC, ordering ASC");
			List<TodoEntry> entries = (List<TodoEntry>) query.execute();
			return (List<TodoEntry>) pm.detachCopyAll(entries);
		}
		finally
		{
			pm.close();
		}
	}
	
	// maybe same with create()
	public void persist(TodoEntry entry)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			pm.makePersistent(entry);
		}
		finally
		{
			pm.close();
		}
	}

	public void deleteById(Long id)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			pm.deletePersistent(pm.getObjectById(TodoEntry.class, id));
		}
		finally
		{
			pm.close();
		}
	}
}
