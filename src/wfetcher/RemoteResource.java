/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

/**
 *
 * @author vanduir
 */
public class RemoteResource {

	private URL remoteLocation;

	public String getLocalLocation() {
		return localLocation;
	}

	public URL getRemoteLocation() {
		return remoteLocation;
	}
	private String localLocation;
	private static short UID_LEN = 16;

	public RemoteResource(URL remoteLocation) {
		this.remoteLocation = remoteLocation;

		byte[] ustr = null;
		try {
			ustr = remoteLocation.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			try {
				System.err.println("Warning: " + ex.getMessage());
				ustr = remoteLocation.toString().getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException ex1) {
				System.err.println("Unable to convert encoding: " + ex1.getMessage());
				System.exit(1);
			}
		}
		byte[] uid = new byte[UID_LEN];

		for (int i = 0; i < UID_LEN; i++) {
			uid[i % UID_LEN] = 0x0;
		}
		for (int i = 0; i < ustr.length; i++) {
			uid[i % UID_LEN] = (byte) ((uid[i % UID_LEN] ^ ustr[i]) & 0xff);
		}

		String uids = String.format("%x", new BigInteger(uid));

		File f = new File("data");
		f.mkdir();

		this.localLocation = "data/" + uids;
	}

	public RemoteResource(String remoteLocation) throws MalformedURLException {
		this(new URL(remoteLocation));
	}

	public void fetch() throws IOException {
		File f = new File(this.localLocation);
		if (!f.exists()) {
			FileWriter fstream = new FileWriter(this.localLocation);
			BufferedWriter out = new BufferedWriter(fstream);
			URLConnection connection = this.remoteLocation.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			do {
				String line = scanner.nextLine();
				//System.out.println(line);
				out.write(line + "\n");
			} while (scanner.hasNextLine());
			out.close();
			fstream.close();
		}
	}

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public String createXML() throws FileNotFoundException, IOException {
		this.fetch();
		String loc= this.localLocation + ".xml";
		File f = new File(this.localLocation + ".xml");
		if (!f.exists()) {

			Tidy t = new Tidy();
			t.setTrimEmptyElements(false);
			t.setWraplen(0);
			t.setIndentContent(false);
			t.setXmlOut(true);
			t.setXmlPi(true);
			t.setOutputEncoding("UTF-8");
			t.setQuoteNbsp(false);
			t.setQuiet(true);
			t.setUpperCaseTags(false);
			t.setUpperCaseAttrs(false);

			//t.getXHTML();

			FileReader fr = new FileReader(this.localLocation);
			FileWriter fw = new FileWriter(this.localLocation + ".xml");

			Node node = t.parse(fr, fw);
			fw.close();
		}
		return loc;
	}
	
	public Document getAsDom() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.parse(this.createXML() );
						
		return doc;
	}
}
