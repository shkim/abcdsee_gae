package shkim.abcdsee.qaboard.domain;

import java.io.Serializable;
import java.util.Date;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.blobstore.BlobKey;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BugReply implements Serializable
{
	private static final long serialVersionUID = 2L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent//(dependent = "true")
	private BugEntry entry;

	@Persistent
	private Date writeDate;

	@Persistent
	private Byte state;

	@Persistent
	private String nickname;

	@Persistent
	private String content;

	@Persistent
	private User user;

	@Persistent
	private BlobKey blobKey;

	@Persistent
	private String fileContentType;

	@Persistent
	private String fileName;

	@Persistent
	private Integer fileSize;

	@Override
	public int hashCode()
	{
		return key.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return hashCode() == o.hashCode();
	}

	public BugEntry getEntry() {
		return entry;
	}

	public void setEntry(BugEntry entry) {
		this.entry = entry;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
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

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize == null ? 0 : fileSize.intValue();
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public BlobKey getBlobKey() {
		return blobKey;
	}

	public String getBlobKeyString() {
		return blobKey.getKeyString();
	}

	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}

}
