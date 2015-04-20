package shkim.abcdsee.qaboard.dao;

import com.insose.gae.pager.GaeQueryPager;
import com.insose.gae.pager.PageDirection;
import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.domain.BugEntry;
import shkim.abcdsee.qaboard.domain.BugReply;
import shkim.abcdsee.qaboard.service.PagerInfo;

import javax.jdo.*;
import java.util.*;

@Component
public class BugEntryDao
{
	public Collection<BugEntry> getAll()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			List<BugEntry> messages = new ArrayList<BugEntry>();
			Extent<BugEntry> extent = pm.getExtent(BugEntry.class, false);
			for (BugEntry message : extent)
			{
				messages.add(message);
			}
			extent.closeAll();

			return messages;
		}
		finally
		{
			pm.close();
		}
	}

	public BugEntry get(Long bugId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			BugEntry _bug = pm.getObjectById(BugEntry.class, bugId);
			_bug.detachFields(pm);
			BugEntry bug = pm.detachCopy(_bug);

			return bug;
		}
		catch (JDOObjectNotFoundException ex)
		{
			System.out.println("bugEntry for " +bugId+ " not found.");
      		return null;
		}
		finally
		{
			pm.close();
		}
	}

	public List<BugEntry> getList(int section, int priority, int state, int ktype, String keyword, PagerInfo pi)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			Map<String, Object> params = new LinkedHashMap<String, Object>();
            Map<String, Object> bookmark = new LinkedHashMap<String, Object>();

			String query = String.format("where section == _section", section);

			if (priority > 0 && state > 0)
			{
				query += " && priority == _priority && state == _state parameters int _section, int _priority, int _state";
				params.put("_priority", priority);
				params.put("_state", state);
			}
			else if (priority > 0)
			{
				query += " && priority == _priority parameters int _section, int _priority";
				params.put("_priority", priority);
			}
			else if (state > 0)
			{
				query += " && state == _state parameters int _section, int _state";
				params.put("_state", state);
			}
			else
			{
				query += " parameters int _section";
			}

			query += " order by lastUpdate desc";
			params.put("_section", section);

			//PageDirection direction = pi.applyTo(bookmark);
			GaeQueryPager pager = new GaeQueryPager(
				pm, BugEntry.class,
				"id",
				pi.getRowsPerPage(),
				pi.applyTo(bookmark),//direction,
				query,
				params,
				bookmark);

			List<BugEntry> bugs = pager.performQueries();
			pi.reflectFrom(pager, bugs.size(), bookmark);

			return bugs;
		}
		finally
		{
			pm.close();
		}
	}

	public void create(BugEntry entry)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = null;

		try
		{
			tx = pm.currentTransaction();
        	tx.begin();
			pm.makePersistent(entry);
			tx.commit();
		}
		finally
		{
			if (tx != null && tx.isActive())
            	tx.rollback();

			pm.close();
		}
	}

	// maybe same with create()
	public void persist(BugEntry entry)
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
			pm.deletePersistent(pm.getObjectById(BugEntry.class, id));
		}
		finally
		{
			pm.close();
		}
	}

	public void updateLastState(Long bugId, byte state)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		try
		{
        	tx.begin();
			BugEntry entry = pm.getObjectById(BugEntry.class, bugId);
			entry.setLastUpdate(new Date());
			entry.setState(state);
			tx.commit();
		}
		finally
		{
			if (tx != null && tx.isActive())
            	tx.rollback();

			pm.close();
		}
	}
}
