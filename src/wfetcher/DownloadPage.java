/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author vanduir
 */
public class DownloadPage {

	static int PLAT_WIN2K = 0x1;
	static int PLAT_WINXP = 0x2;
	static int PLAT_WINVISTA = 0x4;
	static int PLAT_WIN7 = 0x8;
	static int PLAT_WIN8 = 0x10;
	static int PLAT_WS2003 = 0x1000;
	static int PLAT_WS2008 = 0x2000;
	static int PLAT_WS2008R2 = 0x4000;
	static int ARCH_X86 = 0x1;
	static int ARCH_AMD64 = 0x2;
	static int ARCH_IA32 = 0x4;
	static int ARCH_ARM = 0x8;
	private int id;
	private int kbArticle;
	private String bulletin;
	private URL url;
	private String name;
	private String description;
	private String platformText;
	private String filename;
	private URL downloadLink;
	private int platform;
	private int arch;

	public int getArch() {
		return arch;
	}
	private long size;
	private String language;
	private Date pubDate;
	private boolean mustValidate;

	public static DownloadPage fromResource(RemoteResource r) throws
			ParserConfigurationException, FileNotFoundException, SAXException,
			IOException {
		NodeFinder n = new NodeFinder(r.getAsDom());
		//n.find("Windows6.0-KB2545698-x64.msu");
		return new DownloadPage(-1, -1, null, r.getRemoteLocation(), null, null, null, null, null, 0, 0, -1, null, null, true);
	}

	public String getBulletin() {
		return bulletin;
	}

	public String getDescription() {
		return description;
	}

	public URL getDownloadLink() {
		return downloadLink;
	}

	public String getFilename() {
		return filename;
	}

	public int getId() {
		return id;
	}

	public int getKbArticle() {
		return kbArticle;
	}

	public String getLanguage() {
		return language;
	}

	public boolean isMustValidate() {
		return mustValidate;
	}

	public String getName() {
		return name;
	}

	public int getPlatform() {
		return platform;
	}

	public String getPlatformText() {
		return platformText;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public long getSize() {
		return size;
	}

	public URL getUrl() {
		return url;
	}

	private DownloadPage(
			int id, int kbArticle, String bulletin, URL url, String name,
			String description, String platformText, String filename, URL downloadLink,
			int platform, int arch, long size, String language, Date pubDate,
			boolean mustValidate) {
		this.id = id;
		this.kbArticle = kbArticle;
		this.bulletin = bulletin;
		this.url = url;
		this.name = name;
		this.description = description;
		this.platformText = platformText;
		this.filename = filename;
		this.downloadLink = downloadLink;
		this.platform = platform;
		this.arch = arch;
		this.size = size;
		this.language = language;
		this.pubDate = pubDate;
		this.mustValidate = mustValidate;
	}
}
