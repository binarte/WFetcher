/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

/**
 *
 * @author vanduir
 */
public class DataStore {
	
	private Connection conn;
	private Statement st;
	
	public Connection getConn() {
		return conn;
	}
	
	public DataStore(Connection conn) throws SQLException {
		this.conn = conn;
		this.st = conn.createStatement();
	}
	
	public void addDownloadPageQueue(String filename) throws FileNotFoundException, IOException, SQLException {
		this.addDownloadPageQueue(new File(filename));
	}
	
	public void addDownloadPageQueue(File file) throws FileNotFoundException, IOException, SQLException {
		FileReader f = new FileReader(file);
		char[] cbuf = new char[(int) file.length()];
		f.read(cbuf, 0, (int) file.length());
		String str = String.copyValueOf(cbuf);
		
		UrlGetter urlGetter = new UrlGetter(str);
		urlGetter.matchUrls();
		
		String sql;
		
		sql = "DELETE FROM \"Tmpdata\"";
		PreparedStatement clear = conn.prepareStatement(sql);
		clear.execute();
		
		sql = "insert into \"Tmpdata\" (\"data\") values (?)";
		PreparedStatement insert = conn.prepareStatement(sql);
		
		RemoteResource r;
		while (urlGetter.find()) {
			//System.out.println(urlGetter.get());
			r = new RemoteResource(urlGetter.get());
			r.fetch();
			
			file = new File(r.getLocalLocation());
			f = new FileReader(file);
			cbuf = new char[(int) file.length()];
			f.read(cbuf, 0, (int) file.length());
			str = String.copyValueOf(cbuf);
			UrlGetter durlGetter = new UrlGetter(str);
			durlGetter.matchDownloads();
			while (durlGetter.find()) {
				String url = durlGetter.get().toLowerCase();
				url = url.replace("&amp;", "&");
				url = url.replace("/en/", "/");
				URL urlp = new URL(url);
				UrlQuery urlQuery = null;
				try {
					urlQuery = new UrlQuery(urlp.getQuery());
				} catch (Exception ex) {
					System.out.println("::" + url);
					throw ex;
				}
				urlQuery.put("displaylang", "pt-br");
				
				url = urlp.getProtocol() + "://" + urlp.getHost() + urlp.getPath() + "?" + urlQuery.toString();
				
				
				insert.setString(1, url);
				insert.addBatch();
			}
			//prep.executeBatch();
		}
		
		conn.setAutoCommit(false);
		insert.executeBatch();
		conn.setAutoCommit(true);
		
		sql = "delete from \"Tmpdata\" "
				+ "WHERE \"data\" IN("
				+ "SELECT cast(\"url\" AS text )  FROM \"DownloadPageQueued\""
				+ ")";
		this.st.executeUpdate(sql);
		
		sql = "insert into\"DownloadPageQueued\"(\"url\")"
				+ "select distinct * from \"Tmpdata\"";
		this.st.executeUpdate(sql);
		
		clear.execute();
	}
	
	public void processQueue() throws SQLException, MalformedURLException, IOException {
		String sql = "SELECT \"url\",\"id\" FROM  \"DownloadPageQueued\" WHERE \"invalid\" = 0";
		ResultSet list = this.st.executeQuery(sql);
		sql = "UPDATE \"DownloadPageQueued\" SET \"invalid\" = 1 WHERE \"id\" = ?";
		PreparedStatement invalidate = this.conn.prepareStatement(sql);
		
		while (list.next()) {
			RemoteResource resource = new RemoteResource(list.getString(1));
			
			try {
				resource.fetch();
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
				invalidate.setInt(1, list.getInt(2));
				invalidate.addBatch();
			}
		}
		conn.setAutoCommit(false);
		invalidate.executeBatch();
		conn.setAutoCommit(true);
	}
}
