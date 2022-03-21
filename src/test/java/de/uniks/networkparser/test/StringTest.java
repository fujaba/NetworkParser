package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Player;
import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.CharacterReader;
import de.uniks.networkparser.bytes.ByteConverterHex;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.StringCondition;

public class StringTest {

    @Test
    public void testgetString() {
        CharacterBuffer test = new CharacterBuffer();
        String ref = "Hallo World" + BaseItem.CRLF + "Stefan";

        test.with(ref);
        assertEquals(ref.length(), test.length());

        CharacterBuffer buffer = new CharacterBuffer().with(ref);
        Object item = buffer.getString(buffer.length() - buffer.position());

        String tokenerString = item.toString();
        assertEquals(ref.length(), tokenerString.length());
        assertEquals(ref, tokenerString);
    }

    @Test
    public void testObjectType() {
        NetworkParserLog logger = new NetworkParserLog();
        logger.info(StringUtil.getObjectType("byte"));
        logger.info(StringUtil.getObjectType("int"));
        logger.info(StringUtil.getObjectType("Integer"));
        logger.info(StringUtil.getObjectType("Room"));
    }

    @Test
    public void testUmlaute() {
        String uml = "\u00fcbung";
//		System.out.println(uml);
        byte[] umlBytes = uml.getBytes();

//		String newString = new String(umlBytes, 0, umlBytes.length);
//		for(int i=0;i<umlBytes.length;i++) {
//			System.out.println(umlBytes[i]+" ");
//		}
//		System.out.println(newString);
        assertNotNull(umlBytes);
    }

    @Test
    public void testStringReplace() {
        CharacterBuffer buffer = new CharacterBuffer().with("My %DEEP is not the %DEEP");
        buffer.replace("%DEEP", "1");
        assertEquals("My 1 is not the 1", buffer.toString());
    }

    @Test
    public void testStringReplaceLong() {
        CharacterBuffer buffer = new CharacterBuffer().with("My %ID is not the %ID");
        buffer.replace("%ID", "4223");
        assertEquals("My 4223 is not the 4223", buffer.toString());
    }

    @Test
    public void testString() {
        String simple = "<c id=\"C:\\\" />";
        ByteBuffer bytes = new ByteBuffer().with(simple.getBytes());
        String string = new ByteConverterHex().toString(bytes, 1);
        assertEquals("3C 63 20 69 64 3D 22 43 3A 5C 22 20 2F 3E ", string);
    }

    @Test
    public void testEscape() {
        String ref = "Hallo Welt";
        String temp = ref;
        for (int i = 0; i < 6; i++) {
            temp = StringUtil.quote(temp);
        }

        for (int i = 0; i < 6; i++) {
            temp = StringUtil.unQuote(temp);
        }
        assertEquals(ref, temp);
    }

    @Test
    public void testByteCode() throws UnsupportedEncodingException {
        String a = new String(new byte[] { 0x42 }, "UTF-8");
        assertEquals("B", a);
    }

    @Test
    public void testEscapeSimple() {
        String g = "\"\\\"Hallo Welt\\\"\"";
        String t = "\"\\\"\\\\\\\"Hallo Welt\\\\\\\"\\\"\"";
        assertEquals(g, StringUtil.unQuote(t));
    }

    @Test
    public void testEscapeSimpleHTML() {
        char[] txt = new char[] { 'H', 'a', 'l', 'l', 228 };
        String example = new String(txt);
        String encode = StringUtil.encode(example);
        assertEquals(example, StringUtil.decode(encode));
    }

    @Test
    public void testSomeString() {
        CharacterReader buffer = new CharacterReader().with("Hallo Welt");
        showString(buffer, "Hallo Welt");

        showString(buffer, "Hallo \"meine\" Welt");

        showString(buffer, "\"Hallo meine\" Welt");

        showString(buffer, "Hallo \"meine \\\"kleine\\\"\" Welt");

        DateTimeEntity dateTime = new DateTimeEntity();
        showString(buffer, "HH:MM:SS \"Sekunden\"");
        assertNotNull(dateTime.toString("HH:MM:SS \"Sekunden\""));
    }

    @Test
    public void testStringSplit() {
        CharacterReader tokener = new CharacterReader().with("[1,\"2,3\",4]");
        if (tokener.charAt(0) == '[' && tokener.charAt(tokener.length() - 1) == ']') {
            tokener.withStartPosition(1);
            tokener.withBufferLength(tokener.length() - 1);
            int count = 0;
            CharacterBuffer sc;
            do {
                sc = tokener.nextString(true, ',');
                if (sc.length() > 0) {
                    assertNotNull(count);
                    output(count++ + ": #" + sc.toString() + "# -- " + tokener.isString(), null);
                }
            } while (sc.length() > 0);
        }
    }

    @Test
    public void testToday() {
        DateTimeEntity date = new DateTimeEntity();
        assertNotNull(date.toString("ddd. dd.mm.yyyy"));
    }

    public void showString(CharacterReader tokener, String value) {
        int count = 0;
        CharacterBuffer sub;
        PrintStream stream = null;

        output("zu parsen: " + value, stream);
        tokener.clear();
        tokener.with(value);
        do {
            sub = tokener.nextString(true, '"');
            if (sub.length() > 0) {
                assertNotNull(count);
                output(count++ + ": #" + sub + "# -- " + tokener.isString(), stream);
            }
        } while (sub.length() > 0);
        output("\n", stream);
    }

    void output(String str, PrintStream stream) {
        if (stream != null) {
            stream.print(str);
        }
    }

    @Test
    public void testSearchText() {
        CharacterBuffer stringTokener = new CharacterBuffer().with("-Harmonie -Illusion -\"E1 E2\"");
        Or condition = (Or) StringCondition.createSearchLogic(stringTokener);
        assertEquals(3, condition.size());
    }

    @Test
    public void testUTF8() {
        String test = new String(new byte[] { -61, -68 });
        byte[] bytes = test.getBytes();
        assertEquals(2, bytes.length);
        assertNotNull(test.charAt(0));
        assertNotNull(bytes[0]);
//		JsonTokener jsonTokener = (JsonTokener) new JsonTokener();
        CharacterBuffer buffer = new CharacterBuffer().with(test);
        assertNotNull(buffer.nextString(true, '\"'));
        assertNotNull(buffer.nextString(true, '\"'));
    }

    @Test
    public void testReplace() {
        CharacterBuffer buffer = new CharacterBuffer();
        buffer.with("apple, kiwi, cherry");

        assertEquals("apple, kiwi, cherry", buffer.toString()); // START

        buffer.replace(7, 11, "pear");

        assertEquals("apple, pear, cherry", buffer.toString()); // SAME LENGTH

        buffer.replace(7, 11, "orange");

        assertEquals("apple, orange, cherry", buffer.toString()); // LONGER LENGTH

        buffer.replace(7, 13, "grape");

        assertEquals("apple, grape, cherry", buffer.toString()); // SHORTER LENGTH
    }

    @Test
    public void testReplaceExtended() {
        CharacterBuffer test = new CharacterBuffer();
        test.with("Hallo x");
        test.replace(6, 7, "Welt");

        assertEquals("Hallo Welt", test.toString());
    }

    @Test
    public void testReplaceExtend() {
        CharacterBuffer test = new CharacterBuffer();
        test.with("\t\tIdMap map=new IdMap().withCreator(new HouseCreator()); //<2>");
        test.replace(57, 62, "<i class=\"conum\" data-value=\"2\" />");
        assertEquals(
                "\t\tIdMap map=new IdMap().withCreator(new HouseCreator()); <i class=\"conum\" data-value=\"2\" />",
                test.toString());
    }

    @Test
    public void testStringReplaceLess() {
        String value = "Apple, Pear, Cherry";
        CharacterBuffer sb = new CharacterBuffer();
        sb.with(value);
//		GRAPE

//		sb.replace(7, 11, "Grape");
        sb.replace(5, 11, "");
        assertEquals("Apple, Cherry", sb.toString());
    }

    @Test
    public void testLeventaion() {

        assertEquals(0, new CharacterBuffer().equalsLevenshtein(null), 0.001, "NULL");

        assertEquals(0, new CharacterBuffer().equalsLevenshtein(new CharacterBuffer().with("")), 0.001, "EMPTY");

        assertEquals(1, new CharacterBuffer().equalsLevenshtein(new CharacterBuffer().with("a")), 0.001, "a");

        assertEquals(7, new CharacterBuffer().with("aaapppp").equalsLevenshtein(new CharacterBuffer().with("")), 0.001, "aaapppp");

        assertEquals(1, new CharacterBuffer().with("frog").equalsLevenshtein(new CharacterBuffer().with("fog")),0.001, "fog");

        assertEquals(3, new CharacterBuffer().with("fly").equalsLevenshtein(new CharacterBuffer().with("ant")),0.001, "fly");

        assertEquals(7, new CharacterBuffer().with("elephant").equalsLevenshtein(new CharacterBuffer().with("hippo")), 0.001, "elephant");

        assertEquals(7, new CharacterBuffer().with("hippo").equalsLevenshtein(new CharacterBuffer().with("elephant")), 0.001, "hippo");

        assertEquals(8, new CharacterBuffer().with("hippo").equalsLevenshtein(new CharacterBuffer().with("zzzzzzzz")), 0.001, "hippo");

        assertEquals(1, new CharacterBuffer().with("hello").equalsLevenshtein(new CharacterBuffer().with("hallo")), 0.001, "hello");

        assertEquals(0.01, new CharacterBuffer().with("hello").equalsLevenshtein(new CharacterBuffer().with("Hello")), 0.001, "hello");

        assertEquals(4, new CharacterBuffer().with("hippo").equalsLevenshtein(new CharacterBuffer().with("hippofant")), 0.001, "hippofant");

        assertEquals(-7, new CharacterBuffer().with("hippofant").equalsLevenshtein(new CharacterBuffer().with("po")), 0.001, "hippofant");
    }

    @Test
    public void testModelType() {
        assertTrue(StringUtil.isNumericTypeContainer("long", "Long"));
        assertTrue(StringUtil.isNumericTypeContainer("boolean", "Boolean"));
        assertTrue(StringUtil.isNumericTypeContainer("int", "Integer"));
        assertTrue(StringUtil.isNumericTypeContainer("double", "Double"));
        assertTrue(StringUtil.isNumericTypeContainer("Boolean", "boolean"));
        assertTrue(StringUtil.isNumericTypeContainer("Integer", "int"));
        assertTrue(StringUtil.isNumericTypeContainer("Double", "double"));
        assertFalse(StringUtil.isNumericTypeContainer("int", "Short"));
        assertFalse(StringUtil.isNumericTypeContainer("int", "byte"));
        assertFalse(StringUtil.isNumericTypeContainer("int", "Byte"));
    }

    @Test
    public void testStringCompare() {
        NetworkParserLog logger = new NetworkParserLog();

        String text = "01 Maier Rothunde Montag 09:00";
        assertNotNull(text);
        String search = "Rothunde -Dienstag";

        ObjectCondition condition = StringCondition.createSearchLogic(CharacterBuffer.create(search));
        logger.info(condition.toString());

        Ludo ludo = new Ludo();
        Player alice = ludo.createPlayers().withName("Alice");
        Player bob = ludo.createPlayers().withName("Bob");
        assertNotNull(alice);
        assertNotNull(bob);
        alice.createMeeple();

//		SimpleList<String> stringList = stringTokener.getStringList();
//		ArrayList<String> searchList = new ArrayList<String>();
//		for (int i = 0; i < stringList.size(); i++) {
//			if (stringList.get(i).endsWith("-") && i < stringList.size() - 1) {
//				String temp = stringList.get(i);
//				temp = temp.substring(0, temp.length() - 1);
//				searchList.addAll(stringTokener.splitStrings(temp.trim()));
//				searchList.add("-" + stringList.get(++i).trim());
//			} else {
//				searchList.addAll(stringTokener.splitStrings(stringList.get(i)));
//			}
//		}
//		lastSearchCriteriaItems = searchList.toArray(new String[searchList
//				.size()]);

    }

}
