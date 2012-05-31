/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wfetcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vanduir
 */
public class UrlQuery extends HashMap<String, String> {

	private static Pattern parser = Pattern.compile("&?([^=]*)(=([^&]*))?", 0x0);

	public UrlQuery(String str) {
		if (str == null) return;
		Matcher m = UrlQuery.parser.matcher(str);
		while (m.find()) {
			String key = m.group(1);
			if (key.length() > 0)
			super.put(m.group(1), m.group(3));
		}
	}

	@Override
	public String toString() {
		String out = "";
		Set<String> keySet = super.keySet();
		Object[] keys = keySet.toArray();
		Object[] values = super.values().toArray();
		//sorting
		int i;
		boolean reSort = false;
		
		do {
			reSort = false;
			for (i = 1; i < keys.length; i++){
				if (keys[i-1].toString().compareTo(keys[i].toString()) > 0){
					reSort = true;
					Object buf;
					
					buf       = keys[i-1];
					keys[i-1] = keys[i];
					keys[i]   = buf;
					
					buf         = values[i-1];
					values[i-1] = values[i];
					values[i]   = buf;
				}
			}
		} while (reSort);
		
		boolean start = false; 
		for (i = 0; i < keys.length; i++) {
			if (start) out += "&";
			else start = true;
			out += keys[i] + "=" + values[i];
		}
		return out;
	}
}
