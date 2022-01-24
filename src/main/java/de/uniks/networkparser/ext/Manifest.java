package de.uniks.networkparser.ext;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.IOException;
import java.io.InputStream;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * Representation for Manifest
 * @author Stefan Lindel
 */
public class Manifest extends SimpleKeyValueList<String, String> {
	public static char SPLITTER = ':';
	public static char[] CRLF = new char[] { '\r', '\n' };
	public static final String VERSION = "Implementation-Version";
	public static final String TITLE = "Specification-Title";
	public static final String BUILD = "Built-Time";
	public static final String HASH = "Hash";
	public static final String LICENCE = "Licence";
	public static final String HOMEPAGE = "Homepage";
	public static final String COVERAGE = "Coverage";
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
			if (resources != null) {
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
		if (manifest.isEmptyManifest() == false) {
			CharacterBuffer sb = new CharacterBuffer();
			sb.withLine("Title: " + manifest.getString(TITLE));
			sb.withLine("Version: " + manifest.getString(VERSION));
			sb.withLine("Time: " + manifest.getString(BUILD));
			sb.withLine("Hash: " + manifest.getString(HASH));
			sb.withLine("Licence: " + manifest.getString(LICENCE));
			sb.withLine("Homepage: " + manifest.getString(HOMEPAGE));
			sb.withLine("Coverage: " + manifest.getString(COVERAGE));

			System.out.println(sb.toString());

		}
	}

	public static Manifest create(CharSequence value) {
		Manifest manifest = new Manifest();
		CharacterBuffer tokener = new CharacterBuffer().with(value);
		while (tokener.isEnd() == false) {
			CharacterBuffer section = tokener.nextToken(true, SPLITTER);
			CharacterBuffer sectionheader = tokener.nextToken(false, CRLF);
			boolean isCoverage = section.toString().equals(COVERAGE);
			tokener.skip();
			while (tokener.getCurrentChar() == ' ' || tokener.getCurrentChar() == '\t') {
				CharacterBuffer newLine = tokener.nextToken(true, CRLF);
				if (isCoverage) {
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
