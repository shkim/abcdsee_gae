package shkim.abcdsee.qaboard.service;

import com.insose.gae.pager.GaeQueryPager;
import com.insose.gae.pager.PageDirection;
import shkim.abcdsee.qaboard.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public class PagerInfo
{
	private static final int DIR_INIT = 0;
	private static final int DIR_FORWARD = 1;
	private static final int DIR_BACKWARD = 2;

	private int rowsPerPage;
	private int curPage;	// first page = 1
	private int direction;
	
	private int itemBegin, itemEnd;
	private int totalCount;
	private boolean hasPrevPage;
	private boolean hasNextPage;
	private long idBackward;
	private long idForward;
	private long dateBackward;
	private long dateForward;

	public PagerInfo(int rpp)
	{
		this.rowsPerPage = rpp;
	}

	public void init(HttpServletRequest req)
	{
		String dir = req.getParameter("dir");
		if ("n".equals(dir))
		{
			direction = DIR_FORWARD;
			idForward = ParamUtil.getLong(req, "idf", 0);
			dateForward = ParamUtil.getLong(req, "dtf", 0);
			curPage = ParamUtil.getInt(req, "cp", 0) +1;
		}
		else if ("p".equals(dir))
		{
			direction = DIR_BACKWARD;
			idBackward = ParamUtil.getLong(req, "idb", 0);
			dateBackward = ParamUtil.getLong(req, "dtb", 0);
			curPage = ParamUtil.getInt(req, "cp", 0) -1;
		}
		else
		{
			direction = DIR_INIT;
			curPage = 1;
		}
	}

	public PageDirection applyTo(Map<String, Object> bookmark)
	{
		if (direction == DIR_BACKWARD)
		{
			bookmark.put("id.backward", idBackward);
			bookmark.put("lastUpdate.backward", new Date(dateBackward));
			return PageDirection.backward;
		}
		else if (direction == DIR_FORWARD)
		{
			bookmark.put("id.forward", idForward);
			bookmark.put("lastUpdate.forward", new Date(dateForward));
			return PageDirection.forward;
		}

		return PageDirection.forward;
	}

	private long convId(Map<String, Object> bookmark, String key)
	{
		Object a = bookmark.get(key);
		return (a == null) ? 0 : Long.parseLong(a.toString());
	}

	private long convDate(Map<String, Object> bookmark, String key)
	{
		Object a = bookmark.get(key);
		return (a == null) ? 0 : ((Date)a).getTime();
	}

	public void reflectFrom(GaeQueryPager pager, int itemCount, Map<String, Object> bookmark)
	{
		totalCount = (int) pager.performCount();

		if (totalCount < rowsPerPage)
		{
			curPage = 1;
			itemBegin = 1;
			itemEnd = totalCount;
		}
		else
		{
			itemBegin = (curPage -1) * rowsPerPage +1;
			itemEnd = itemBegin + itemCount -1;
		}

		hasPrevPage = (curPage > 1);
		hasNextPage = pager.hasNextPage();
		if (hasNextPage == false && curPage == 1 && rowsPerPage < totalCount)
			hasNextPage = true;

		idBackward = convId(bookmark, "id.backward");
		dateBackward = convDate(bookmark, "lastUpdate.backward");

		idForward = convId(bookmark, "id.forward");
		dateForward = convDate(bookmark, "lastUpdate.forward");

//		System.out.println("reflectFrom: bmk=" + bookmark);
	}

	public int getRowsPerPage()
	{
		return rowsPerPage;
	}

	public int getCurrentPage()
	{
		return curPage;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public int getItemBegin()
	{
		return itemBegin;
	}

	public int getItemEnd()
	{
		return itemEnd;
	}

	public boolean isHasPrevPage()
	{
		return hasPrevPage;
	}

	public boolean isHasNextPage()
	{
		return hasNextPage;
	}

	public long getIdBackward()
	{
		return idBackward;
	}

	public long getIdForward()
	{
		return idForward;
	}

	public long getDateBackward()
	{
		return dateBackward;
	}

	public long getDateForward()
	{
		return dateForward;
	}

}
