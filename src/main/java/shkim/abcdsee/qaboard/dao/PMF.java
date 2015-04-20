package shkim.abcdsee.qaboard.dao;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PMF
{
	private static PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public static PersistenceManagerFactory get()
	{
		return pmfInstance;
	}
}
