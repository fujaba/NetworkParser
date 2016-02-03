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
			//pb = new ProcessBuilder("javadoc","-notree", "-noindex", "-nonavbar", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
			pb = new ProcessBuilder("javadoc", "-noindex", "-notree", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
//			pb.redirectOutput(Redirect.INHERIT);
//			pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();
			BufferedReader errorReader = new BufferedReader (new InputStreamReader(p.getErrorStream()));

			SimpleList<String> errors=new SimpleList<String>();
			String line;
			while ((line = errorReader.readLine ()) != null) {
				line = line.trim();
				if("^".equals(line) == false) {
					errors.add(line);
				}
			}
			errorReader.close();
			for(String item : errors) {
				System.err.println(item);
			}
			Assert.assertEquals(0, errors.size());
		}catch(Exception e) {
		}
	}
}
