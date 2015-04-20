package shkim.abcdsee.qaboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shkim.abcdsee.qaboard.dao.VersionDao;
import shkim.abcdsee.qaboard.domain.VersionInfo;

import java.util.Date;
import java.util.List;

@Component
public class VersionService
{
	@Autowired
	private VersionDao versionDao;

	public VersionInfo createNew(String name, String downUrl, int fileSize, String notes)
	{
		VersionInfo ver = new VersionInfo();

		ver.setReleaseDate(new Date());
		update(ver, name, downUrl, fileSize, notes);

		return ver;
	}

	public void update(VersionInfo ver, String name, String downUrl, int fileSize, String notes)
	{
		ver.setName(name);
		ver.setDownloadUrl(downUrl);
		ver.setFileSize(fileSize);
		ver.setChangeNotes(notes);

		versionDao.persist(ver);
	}

	public VersionInfo get(int verId)
	{
		return versionDao.get(Long.valueOf(verId));
	}

	public List<VersionInfo> getList(int numResults)
	{
		return versionDao.getList(numResults);
	}

	public VersionInfo getLatestVersion()
	{
		return versionDao.getLatest();
	}
}
