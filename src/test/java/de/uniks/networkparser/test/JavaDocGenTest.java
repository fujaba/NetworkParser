package de.uniks.networkparser.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.list.SimpleList;


public class JavaDocGenTest {

	@Test
	public void testGenJavaDoc() {
		
		try{
			ProcessBuilder pb = new ProcessBuilder("javadoc");
			pb = new ProcessBuilder("javadoc","-notree", "-noindex", "-nohelp", "-nonavbar", "-subpackages", "de.uniks.networkparser", "-sourcepath","src/main/java", "-d", "build/testJavadoc");
//			pb.redirectOutput(Redirect.INHERIT);
//			pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();
			BufferedReader reader = new BufferedReader (new InputStreamReader(p.getInputStream()));
			SimpleList<String> item=new SimpleList<String>();
			String line;
			while ((line = reader.readLine ()) != null) {
				item.add(line);
			}
			String last = item.last();
			Assert.assertTrue(last.startsWith("Generating "));
		}catch(Exception e) {
		}
	}
}
