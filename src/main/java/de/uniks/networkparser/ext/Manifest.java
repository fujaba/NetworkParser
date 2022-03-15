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
import de.uniks.networkparser.list.SimpleList;

/**
 * Representation for Manifest.
 *
 * @author Stefan Lindel
 */
public class Manifest extends SimpleKeyValueList<String, String> {
    private static char SPLITTER = ':';
	private static char[] CRLF = new char[] { '\r', '\n' };
	
	/** The Constant VERSION. */
	public static final String VERSION = "Implementation-Version";
	
	/** The Constant TITLE. */
	public static final String TITLE = "Specification-Title";
	
	/** The Constant BUILD. */
	public static final String BUILD = "Built-Time";
	
	/** The Constant HASH. */
	public static final String HASH = "Hash";
	
	/** The Constant LICENCE. */
	public static final String LICENCE = "Licence";
	
	/** The Constant HOMEPAGE. */
	public static final String HOMEPAGE = "Homepage";
	
	/** The Constant COVERAGE. */
	public static final String COVERAGE = "Coverage";
	private boolean empty = true;

	/**
	 * Creates the.
	 *
	 * @return the manifest
	 */
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
	
	/**
	 * Gets the global version.
	 *
	 * @return the global version
	 */
	public static String getGlobalVersion() {
        Manifest manifest = create();
        if (!manifest.isEmptyManifest()) {
            return manifest.getVersion();
        }
        return "";
	}
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		CharacterBuffer sb = new CharacterBuffer();
		sb.withLine("Title: " + getString(TITLE));
		sb.withLine("Version: " + getString(VERSION));
		sb.withLine("Time: " + getString(BUILD));
		sb.withLine("Hash: " + getString(HASH));
		sb.withLine("Licence: " + getString(LICENCE));
		sb.withLine("Homepage: " + getString(HOMEPAGE));
		sb.withLine("Coverage: " + getString(COVERAGE));
		return sb.toString();
	}
	
	/**
	 * Gets the full version.
	 *
	 * @param splitter the splitter
	 * @param excludes the excludes
	 * @return the full version
	 */
	public CharacterBuffer getFullVersion(String splitter, String... excludes) {
	    CharacterBuffer sb = new CharacterBuffer();
	    SimpleList<String> excludeList = new SimpleList<>();
	    excludeList.rawAdd(excludes);
	    if(splitter == null) {
	        splitter = "";
	    }
	    for(int i=0;i<this.size();i++) {
	        String key = this.getKeyByIndex(i);
	        if(!excludeList.containsKey(key) && !key.isEmpty()) {
	            String value = this.getValueByIndex(i);
	            if(!value.isEmpty()) {
	                sb.withLine(key.trim() + ": " +value.trim()+ splitter);
	            }
	        }
	    }
	    return sb;
	}

	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @return the manifest
	 */
	public static Manifest create(CharSequence value) {
		Manifest manifest = new Manifest();
		CharacterBuffer tokener = new CharacterBuffer().with(value);
		while (!tokener.isEnd()) {
			CharacterBuffer section = tokener.nextToken(SPLITTER);
			CharacterBuffer sectionheader = tokener.nextToken(CRLF);
			boolean isCoverage = section.toString().equals(COVERAGE);
			tokener.skipFor('\n');
			while (tokener.getCurrentChar() == ' ' || tokener.getCurrentChar() == '\t') {
				CharacterBuffer newLine = tokener.nextToken(CRLF);
				if (isCoverage) {
					sectionheader.trim().with(newLine);
				} else {
					sectionheader.with(newLine);
				}
				tokener.skipFor('\n');
			}
			String key = section.trim().toString();
			String valueKey = sectionheader.trim().toString();
			if(!key.isEmpty() && !valueKey.isEmpty()) {
			    manifest.add(key, valueKey);
			}
		}
		manifest.empty = !manifest.containsAll(VERSION, TITLE, BUILD);
		return manifest;
	}

	/**
	 * Checks if is empty manifest.
	 *
	 * @return true, if is empty manifest
	 */
	public boolean isEmptyManifest() {
		return empty;
	}
}
