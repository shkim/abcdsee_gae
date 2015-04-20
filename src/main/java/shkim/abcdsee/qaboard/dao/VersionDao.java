package shkim.abcdsee.qaboard.dao;

import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.domain.VersionInfo;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

@Component
public class VersionDao
{
	public VersionInfo get(Long versionId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			VersionInfo _td = pm.getObjectById(VersionInfo.class, versionId);
			return pm.detachCopy(_td);
		}
		catch (JDOObjectNotFoundException ex)
		{
			System.out.println("VersionInfo for " +versionId+ " not found.");
      		return null;
		}
		finally
		{
			pm.close();
		}
	}

	public VersionInfo getLatest()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			Query query = pm.newQuery(VersionInfo.class);
			query.setOrdering("id DESC");
			query.setRange(0, 1);
			List<VersionInfo> entries = (List<VersionInfo>) query.execute();
			return pm.detachCopy(entries.get(0));
		}
		finally
		{
			pm.close();
		}
	}

	public List<VersionInfo> getList(int numResults)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			Query query = pm.newQuery(VersionInfo.class);
			query.setOrdering("id DESC");
			query.setRange(0, numResults);
			List<VersionInfo> entries = (List<VersionInfo>) query.execute();
			return (List<VersionInfo>) pm.detachCopyAll(entries);
		}
		finally
		{
			pm.close();
		}
	}

	// maybe same with create()
	public void persist(VersionInfo entry)
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
			pm.deletePersistent(pm.getObjectById(VersionInfo.class, id));
		}
		finally
		{
			pm.close();
		}
	}
}
