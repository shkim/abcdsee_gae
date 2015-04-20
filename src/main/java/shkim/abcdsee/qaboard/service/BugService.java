package shkim.abcdsee.qaboard.service;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import shkim.abcdsee.qaboard.dao.BugEntryDao;
import shkim.abcdsee.qaboard.dao.BugReplyDao;
import shkim.abcdsee.qaboard.domain.BugEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.domain.BugReply;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.users.User;

// http://localhost:8080/_ah/admin

@Component
public class BugService
{
	@Autowired
	private BugReplyDao replyDao;

	@Autowired
	private BugEntryDao entryDao;

	private void setBlobInfo(BugReply reply, BlobKey bk)
	{
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(bk);

		reply.setFileContentType(blobInfo.getContentType());
		reply.setFileSize((int)blobInfo.getSize());
		//Date creation = blobInfo.getCreation();
		reply.setFileName(blobInfo.getFilename());
		reply.setBlobKey(bk);
	}

	public BugEntry createNewEntry(User user, byte priority, String nickname, String subject, String content, BlobKey blobKey)
	{
		BugEntry entry = new BugEntry();

		entry.setRegDate(new Date());
		entry.setLastUpdate(new Date());
		entry.setState(BugEntry.STATE_FOUND);
		entry.setPriority(priority);
		entry.setNickname(nickname);
		entry.setSubject(subject);
		entry.setUser(user);
		entry.setSection(BugEntry.SECTION_CURRENT);

		BugReply reply = new BugReply();
		reply.setWriteDate(new Date());
		reply.setState(entry.getState());
		reply.setNickname(nickname);
		reply.setContent(content);
		reply.setUser(user);

		if (blobKey != null)
			setBlobInfo(reply, blobKey);

		reply.setEntry(entry);
		entry.getReplyList().add(reply);

		entryDao.create(entry);
		return entry;
	}

	public boolean addBugReply(BugEntry entry, User user, byte state, String nickname, String content, BlobKey blobKey)
	{
		BugReply reply = new BugReply();
		reply.setWriteDate(new Date());
		reply.setState(state);
		reply.setNickname(nickname);
		reply.setContent(content);
		reply.setUser(user);

		if (blobKey != null)
			setBlobInfo(reply, blobKey);

		reply.setEntry(entry);
		entry.getReplyList().add(reply);
		if (state != 0)
			entry.setState(state);
		entry.setLastUpdate(reply.getWriteDate());

		return replyDao.persist(reply);
	}

	public BugEntry getBug(int bugId)
	{
		return entryDao.get(Long.valueOf(bugId));
	}

	public List<BugEntry> getList(int group, int priority, int state, int ktype, String keyword, PagerInfo pi)
	{
		return entryDao.getList(group, priority, state, ktype, keyword, pi);
	}


	public void updateBugSection(BugEntry bug, int section)
	{
		bug.setSection(section);
		entryDao.persist(bug);
	}

	public void updateBugSubject(BugEntry bug, String subject, String nickname)
	{
		bug.setSubject(subject);
		bug.setNickname(nickname);
		entryDao.persist(bug);
	}

	public boolean updateReply(BugReply reply, String writer, String content)
	{
		reply.setWriteDate(new Date());
		reply.setNickname(writer);
		reply.setContent(content);

		return replyDao.persist(reply);
	}
}
