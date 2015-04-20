package shkim.abcdsee.qaboard.domain;

import shkim.abcdsee.qaboard.util.DateTimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Key;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Sequence(name = "jdo1", datastoreSequence = "jdothat", strategy = SequenceStrategy.NONTRANSACTIONAL,
	extensions = @Extension(vendorName = "datanucleus", key="key-cache-size", value="12"))
public class BugEntry implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int SECTION_CURRENT = 0;
	public static final int SECTION_PREVIOUS = 1;

	public static final byte PRI_CRITICAL =1;
	public static final byte PRI_HIGH =2;
	public static final byte PRI_NORMAL =3;
	public static final byte PRI_LOW =4;

	public static final byte STATE_FOUND =1;
	public static final byte STATE_ACCEPTED =2;
	public static final byte STATE_UNFIXED =3;
	public static final byte STATE_FIXED =4;
	public static final byte STATE_REJECTED =5;
	public static final byte STATE_CONFIRMED =6;
	public static final byte STATE_CLOSED =7;
		

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.SEQUENCE, sequence = "jdo1")
	private Long id;

	@Persistent(mappedBy = "entry")
	@Element(dependent = "true")
	@Order(extensions = @Extension(vendorName="datanucleus", key="list-ordering", value="writeDate asc"))
	private List<BugReply> replyList = new ArrayList<BugReply>();

	public void detachFields(PersistenceManager pm)
	{
		if (replyList == null)
		{	System.out.println("replyList is null"); return; }

		replyList = (List<BugReply>) pm.detachCopyAll(replyList);
	}

	@Persistent
	private Integer section;

	@Persistent
	private Date lastUpdate;

	@Persistent
	private Date regDate;

	@Persistent
	private Byte priority;

	@Persistent
	private Byte state;

	@Persistent
	private String nickname;

	@Persistent
	private String subject;

	@Persistent
	private User user;


	public Long getId() {
		return id;
	}
/*
	public void setId(Long id) {
		this.bugId = id;
	}
*/
	public int getSection() {
		return section == null ? 0 : section.intValue();
	}

	public void setSection(int section) {
		this.section = section;
	}

	public int getReplyCount() {
		return replyList.size();
	}

	public List<BugReply> getReplyList() {
		return replyList;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getRegDate() {
		return regDate;
	}

	public String getRegDateTillMinute() {
        return DateTimeUtil.formatTillMinute(lastUpdate);
    }

	public String getRegDateTillDay() {
        return DateTimeUtil.formatTillDay(lastUpdate);
    }

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public byte getPriority() {
		return priority == null ? 0 : priority.byteValue();
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public byte getState() {
		return state == null ? 0 : state.byteValue();
	}

	public void setState(byte state) {
		this.state = state;
	}

	public String getNickname() {
		return nickname;
	}

	public String getNicknameWithoutHostname()
	{
		if (nickname == null)
			return "";

		int atpos = nickname.indexOf('@');
		if (atpos > 0)
		{
			return nickname.substring(0, atpos);
		}
		else
		{
			return nickname;
		}
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}