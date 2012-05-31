/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vanduir
 */
public class UrlGetter {
	private String textContent;
	private Matcher matcher;
	private Pattern urlPattern;
	private Pattern downloadUrlPattern;

	public UrlGetter(String textContent) {
		this.textContent = textContent;
		this.urlPattern = Pattern.compile("http://[^\\s\"']*", 0);
		this.downloadUrlPattern = Pattern.compile("http://(www\\.microsoft\\.com/downloads?|downloads?\\.microsoft\\.com)/([^'\"]*?)details[^\\s\"'\\)]*", 0);
	}
	
	public void matchUrls() {
		this.matcher = this.urlPattern.matcher(this.textContent);
	}
	
	public void matchDownloads() {
		this.matcher = this.downloadUrlPattern.matcher(this.textContent);
	}
	
	public boolean find(){
		return this.matcher.find();
	}
	
	public String get(){
		return this.matcher.group(0);
	}
	
	
	
	
	

}
