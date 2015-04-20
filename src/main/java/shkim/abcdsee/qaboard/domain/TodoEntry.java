package shkim.abcdsee.qaboard.domain;

import com.google.appengine.api.users.User;

import javax.jdo.annotations.*;
import java.util.Date;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TodoEntry
{
	private static final long serialVersionUID = 3L;

	public static final byte PRIO_HIGH = 1;
	public static final byte PRIO_MIDDLE = 2;
	public static final byte PRIO_LOW = 3;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private User user;

	@Persistent
	private Byte priority;

	@Persistent
	private Date lastUpdate;

	@Persistent
	private Integer ordering;

	@Persistent
	private String subject;
	
	@Persistent
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public byte getPriority() {
		return priority == null ? 0 : priority.byteValue();
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getOrdering() {
		return ordering == null ? 0 : ordering.intValue();
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
