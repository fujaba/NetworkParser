package de.uniks.networkparser.ext;

import java.io.IOException;
import java.io.InputStream;

import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class Manifest extends SimpleKeyValueList<String, String>{
	public static char SPLITTER=':';
	public static char[] CRLF=new char[]{'\r', '\n'};
	public static final String VERSION="Implementation-Version";
	public static final String TITLE="Specification-Title";
	public static final String BUILD="Built-Time";
	public static final String HASH="Hash";
	public static final String LICENCE="Licence";
	public static final String HOMEPAGE="Homepage";
	public static final String COVERAGE="Coverage";
	private boolean empty = true;

	public static Manifest create() {
		String value = null;
		InputStream resources = Manifest.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		int len;
		try {
			len = resources.available();
			byte[] bytes = new byte[len];
			int read = resources.read(bytes, 0, len);
			value = new String(bytes, 0, read);
		} catch (IOException e) {
		} finally {
			if(resources != null) {
				try {
					resources.close();
				} catch (IOException e) {
				}
			}
		}
		return create(value);
	}

	public static void printVersion() {
		Manifest manifest = create();
		if(manifest.isEmptyManifest() == false) {
			System.out.println("Title: "+manifest.getString(TITLE));
			System.out.println("Version: "+manifest.getString(VERSION));
			System.out.println("Time: "+manifest.getString(BUILD));
			System.out.println("Hash: "+manifest.getString(HASH));
			System.out.println("Licence: "+manifest.getString(LICENCE));
			System.out.println("Homepage: "+manifest.getString(HOMEPAGE));
			System.out.println("Coverage: "+manifest.getString(COVERAGE));
		}
	}

	public static Manifest create(CharSequence value) {
		Manifest manifest = new Manifest();
		Tokener tokener=new Tokener().withBuffer(value);
		while(tokener.isEnd() == false) {
			CharacterBuffer section = tokener.nextToken(true, SPLITTER);
			CharacterBuffer sectionheader = tokener.nextToken(false, CRLF);
			boolean isCoverage= section.toString().equals(COVERAGE);
			tokener.skip();
			while(tokener.getCurrentChar()==' ' || tokener.getCurrentChar() == '\t') {
				//continue line
				CharacterBuffer newLine = tokener.nextToken(true, CRLF);
				if(isCoverage) {
					sectionheader.trim().with(newLine);
				} else {
					sectionheader.with(newLine);
				}
				tokener.skip();
			}
			String key = section.toString();
			manifest.add(key, sectionheader.trim().toString());
		}
		manifest.empty = manifest.containsAll(VERSION, TITLE, BUILD) == false;
		return manifest;
	}

	public boolean isEmptyManifest() {
		return empty;
	}
}
