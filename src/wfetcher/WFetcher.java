/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author vanduir
 */
public class WFetcher {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException, ParserConfigurationException, SAXException {

		RemoteResource r = new RemoteResource("http://www.microsoft.com/downloads/details.aspx?displaylang=pt-br&familyid=011acc24-6639-4c73-91af-7b88c65d1b8e");
		NodeFinder n = new NodeFinder(r.getAsDom());
		System.out.println(n.find("Windows Server 2008 Service Pack 2"));
		System.out.println(n.find("Windows6.0-KB2545698-x64.msu"));
		Node node = n.getNode("/html[0]/body[0]/table[0]/tr[0]/td[1]/div[1]/div[2]/div[2]/table[0]");

		Pattern sizeregex = Pattern.compile("([0-9]+(\\.[0-9]+)?)\\s+([A-Z])?B", Pattern.CASE_INSENSITIVE);
		Pattern dateregex = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)", 0);

		String filename = null;
		String language = null;
		Date pubDate = null;
		long size = -1;
		int kbArticle = -1;
		int platforms = 0;
		if (node.hasChildNodes()) {
			NodeList childNodes = node.getChildNodes();
			HashMap<String, String> h = new HashMap<>();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);

				if (item.getNodeName().compareToIgnoreCase("tr") != 0) {
					continue;
				}
				NodeFinder nodeFinder = new NodeFinder(item);
				Node node1 = nodeFinder.getNode("/td[0]");
				Node node2 = nodeFinder.getNode("/td[1]");
				System.out.println(node1.getTextContent().trim());
				try {
					h.put(
							node1.getTextContent().trim(),
							node2.getTextContent().trim());
				} catch (java.lang.NullPointerException ex) {
				}
			}
			System.out.println(h.get("Idioma:"));

			filename = h.get("Nome do arquivo:");
			language = h.get("Idioma:");

			Matcher matcher = sizeregex.matcher(h.get("Tamanho do Download:"));
			if (matcher.find()) {
				float fsize = Float.parseFloat(matcher.group(1));
				String mul = matcher.group(3);
				switch (mul) {
					case "T":
						fsize *= 1024;
					case "G":
						fsize *= 1024;
					case "M":
						fsize *= 1024;
					case "K":
						fsize *= 1024;
				}

				size = (int) Math.round(fsize);
			}
			//java.sql.Date d = new java.sql.Date

			matcher = dateregex.matcher(h.get("Data de Publicação:"));
			if (matcher.find()) {

				TimeZone t = TimeZone.getTimeZone("UDT");
				Calendar g = new GregorianCalendar(t);
				g.set(
						Integer.parseInt(matcher.group(3)),
						Integer.parseInt(matcher.group(2)),
						Integer.parseInt(matcher.group(1)),
						0,0,0
						);
				
				pubDate = new Date(g.getTimeInMillis());
			}


		}

		String description = n.getNode("/html[0]/body[0]/table[0]/tr[0]/td[1]/div[1]/div[0]/div[0]").getTextContent().trim();
		
		try {
			String platformsStr = n.getNode("/html[0]/body[0]/table[0]/tr[0]/td[1]/div[1]/div[3]/div[1]/ul[0]/li[0]/#text[1]").getTextContent();
			
			platformsStr = platformsStr.
					replace("Windows","").
					replace("Edition","").
					replace("Starter","").
					replace("Home","").
					replace("Basic","").
					replace("Premium","").
					replace("Professional","").
					replace("Enterprise","").
					replace("Ultimate","").
					replace("Standard","").
					replace("Embedded","").
					replace("SP4","").
					replace("SP3","").
					replace("SP2","").
					replace("SP1","").
					replace("Service Pack 4","").
					replace("Service Pack 3","").
					replace("Service Pack 2","").
					replace("Service Pack 1","").
					replace(" N "," ").
					replace(" KN "," ").
					replace(" K "," ");
			String[] split = platformsStr.split(";");
			for (int i = 0; i < split.length; i++){
				switch (split[i].trim() ){
					case "2000":
						platforms |= DownloadPage.PLAT_WIN2K;
						break;
					case "XP":
						platforms |= DownloadPage.PLAT_WINXP;
						break;
					case "Vista":
						platforms |= DownloadPage.PLAT_WINVISTA;
						break;
					case "7":
						platforms |= DownloadPage.PLAT_WIN7;
						break;
					case "8":
						platforms |= DownloadPage.PLAT_WIN8;
						break;
					case "Server 2003":
						platforms |= DownloadPage.PLAT_WS2003;
						break;
					case "Server 2008":
						platforms |= DownloadPage.PLAT_WS2008;
						break;
					case "Server 2008 R2":
						platforms |= DownloadPage.PLAT_WS2008R2;
						break;
					default:
						System.err.println("Unknown platform: " + split[i]);
				}
			}
			
		}
		catch(NullPointerException ex){}

		/*
		 * Class.forName("org.sqlite.JDBC"); Connection connection =
		 * DriverManager.getConnection("jdbc:sqlite:test.db" ); DataStore d = new
		 * DataStore(connection); d.addDownloadPageQueue("src/resources/updates.txt");
		 * d.processQueue();
		 */

	}
}
