/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Finds nodes inside DOM documents.
 * This class provides easy methods to fetch and find data in DOM documents, allowing to 
 * fetch data with fewer lines of code.
 * @author vanduir
 */
public class NodeFinder {

	/**
	 * The node that the finder will search nodes from.
	 */
	private Node doc;
	/**
	 * Regex pattern used to parse node paths.
	 */
	private Pattern pathPattern;
	/**
	 * Regex matcher used to parse node paths.
	 */
	private Matcher matcher;

	/**
	 * Instantiates a NodeFinder for the given DOM document.
	 * @param doc The DOM Document to look for the nodes from.
	 */
	public NodeFinder(Node doc) {
		this.doc = doc;
		this.pathPattern = Pattern.compile("/(.*?)\\[([0-9]+)](\\{(.*?)\\})?", 0);
		this.matcher = pathPattern.matcher("");
	}

	/**
	 * Gets a node from a given path. 
	 * The path is in the format "/XXX[YYY]{ZZZ}..." in which X is the node name, Y is 
	 * the occurence number of the node and Z is an optional attribute. If the attribute 
	 * is defined, the method will stop and return said attribute.
	 * 
	 * Also note that this method does not perform any error handling. Bad path strings 
	 * might yield unpredictable results.
	 * 
	 * @param path The path of the node.
	 * @return The corresponding node or <b>null</b> if the node was not found.
	 */
	public Node getNode(String path) {
		this.matcher.reset(path);
		Node curNode = this.doc;
		while (matcher.find()) {
			String tagName = this.matcher.group(1);
			int tagNum = Integer.parseInt(this.matcher.group(2));

			if (!curNode.hasChildNodes()) {
				return null;
			}
			NodeList childNodes = curNode.getChildNodes();
			short ncount = 0;
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if (item.getNodeName().compareTo(tagName) == 0) {
					if (ncount == tagNum) {
						curNode = item;
						ncount = -1;
						break;
					} else {
						ncount++;
					}
				}
			}
			if (ncount != -1) {
				return null;
			}

			String attribute = this.matcher.group(4);
			if (attribute != null) {
				if (!curNode.hasAttributes()) {
					return null;
				}
				return curNode.getAttributes().getNamedItem(attribute);
			}

		}
		return curNode;
	}

	/**
	 * Finds a piece of data on the document.
	 * This will search the document for nodes which content match the provided string.
	 * The search is case-insensitive, and ignores leading and trailing spaces.
	 * @param str The string te search.
	 * @uses _find
	 * @return 
	 */
	public String find(String str) {
		str = str.trim().toLowerCase();

		return _find(this.doc, str, "");
	}

	/**
	 * Executes a search.
	 * @param node the node the search is being done on
	 * @param str the string being searched
	 * @param prefix centains the ancestry of the node
	 * @return the nodes that were found
	 */
	private String _find(Node node, String str, String prefix) {
		String out = "";
		String cont = null;
		try {
			cont = node.getTextContent().trim().toLowerCase();

			if (cont.compareTo(str) == 0) {
				out += prefix + "\n";
			}
		} catch (NullPointerException n) {
		}
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				cont = item.getTextContent().trim().toLowerCase();
				if (cont.compareTo(str) == 0) {
					out += prefix + "{" + item.getNodeName() + "}\n";
				}
			}
		}
		if (node.hasChildNodes()) {
			HashMap<String, Integer> tCount = new HashMap<>();
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				String nodeName = item.getNodeName();
				Integer count = tCount.get(nodeName);

				if (count == null) {
					count = new Integer(0);
				} else {
					count = new Integer(count.intValue() + 1);
				}
				tCount.put(nodeName, count);

				out += this._find(item, str, prefix + "/" + nodeName + "[" + count + "]");
			}

		}
		return out;
	}
}
