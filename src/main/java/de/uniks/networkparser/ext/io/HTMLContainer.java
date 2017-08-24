package de.uniks.networkparser.ext.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class HTMLContainer {
	public static int BUFFER=100*1024;

	public HTMLEntity fromUrl(String url) {
		byte[] messageArray = new byte[BUFFER];
		HTMLEntity rootItem=new HTMLEntity();
		try {
			URL remoteURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) remoteURL.openConnection();
			conn.setRequestMethod("GET");
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			while (true) {
				int bytesRead = is.read(messageArray, 0, BUFFER);
				if (bytesRead <= 0)
					break; // <======= no more data
				sb.append(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
			}
			rootItem.withEncoding(HTMLEntity.ENCODING_UTF8);
			IdMap idMap = new IdMap();
			MapEntity map = new MapEntity(idMap);
			XMLTokener tokener = new XMLTokener().withMap(idMap);
			System.out.println(sb.toString());
//			Entity child = idMap.decode(sb.toString(), tokener);
			Object child = idMap.decode(sb.toString());
			
			rootItem.with(child);
		} catch (IOException e) {
		}
		return rootItem;
	}
}
