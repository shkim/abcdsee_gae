package shkim.abcdsee.qaboard.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.domain.BugReply;

@Component
public class BugReplyDao
{
	public Collection<BugReply> getAll()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			List<BugReply> messages = new ArrayList<BugReply>();
			Extent<BugReply> extent = pm.getExtent(BugReply.class, false);
			for (BugReply message : extent)
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

	public boolean persist(BugReply message)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			return (null != pm.makePersistent(message));
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
			pm.deletePersistent(pm.getObjectById(BugReply.class, id));
		}
		finally
		{
			pm.close();
		}
	}
}
