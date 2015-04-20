package shkim.abcdsee.qaboard.domain;

import javax.jdo.annotations.*;
import java.util.Date;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VersionInfo
{
	private static final long serialVersionUID = 4L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private Date releaseDate;

	@Persistent
	private String name;

	@Persistent
	private String downloadUrl;

	@Persistent
	private int fileSize;	// compressed file (zip) size

	@Persistent
	private String changeNotes;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getChangeNotes() {
		return changeNotes;
	}

	public void setChangeNotes(String changeNotes) {
		this.changeNotes = changeNotes;
	}
}
