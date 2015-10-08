package de.uniks.networkparser.test.ant.ikvm;

import java.util.StringTokenizer;

public class WildcardMatcher {
    /**
     * Posted by JemTek in JavaForum
     * @param text text to match (e.g. "WildcardMatcher.java")
     * @param pattern pattern to match (e.g. "*.java)
     * @return true if given text matches the pattern
     */
    public static boolean match (String text, String pattern) {
        if (text == null)
            throw new IllegalArgumentException ("null text");
        if (pattern == null)
            throw new IllegalArgumentException ("null pattern");

        int idx = 0;
        boolean wild = false;

        StringTokenizer tokens = new StringTokenizer (pattern, "*", true);
        while (tokens.hasMoreTokens ()) {
            String token = tokens.nextToken ();

            if (wild == true) {
                wild = false;
                if (text.indexOf (token, idx) > idx)
                    idx = text.indexOf (token, idx);
            }

            if (token.equals ("*"))
                wild = true;
            else
            if (text.indexOf (token, idx) == idx)
                idx += token.length ();
            else
                break;

            if (!tokens.hasMoreTokens ()) {
                if (token.equals ("*") || text.endsWith (token))
                    idx = text.length ();
            }
        }

        return idx == text.length();

    }
}
