package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.list.SimpleList;

public class JavaDocGenTest {
	public static final String PREFIX="src.main.java.";
	@Test
	@Timeout(180)
	public void testGenJavaDoc() {
//		long time = System.currentTimeMillis();
		FileClassModel model = new FileClassModel("de.uniks.networkparser");
		model.readFiles("src/main/java/");

		SimpleList<String> errors = model.analyseJavaDoc(false);
//		ArrayList<String> validateFileTree = javaDoc.validateFileTree(new File("src/main/java"), true, false);
		for(String item : errors) {
			System.out.println(item);
		}
		assertEquals(0, errors.size());
//		try{
//			ProcessBuilder pb = new ProcessBuilder("javadoc","-notree", "-noindex", "-nonavbar", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
//			pb = new ProcessBuilder("javadoc", "-noindex", "-notree", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
//			pb.redirectOutput(Redirect.INHERIT);
//			pb.redirectError(Redirect.INHERIT);
//
//			Process p = pb.start();
//			BufferedReader errorReader = new BufferedReader (new InputStreamReader(p.getErrorStream()));
//			String line;
//			while ((line = errorReader.readLine ()) != null) {
//				line = line.trim();
//				if("^".equals(line) == false) {
//					errors.add(line);
//				}
//			}
//			errorReader.close();
//			for(String item : errors) {
//				int pos= item.indexOf(":");
//				if(item.startsWith("src\\main") && pos>0) {
//					int end = item.indexOf(":", pos+1);
//					String path = item.substring(0, end);
//					path = path.replaceAll("\\\\", ".");
//
//					if(path.startsWith("src.main.java.")) {
//						path = path.substring(PREFIX.length());
//					}
//					int start = path.lastIndexOf(".", end);
//					end = path.indexOf(":", start);
//					start = path.lastIndexOf(".", start - 1);
//					System.out.println(item +" "+ path.substring(0, end) + "("+path.substring(start+1 )+")");
//
//				}else {
//					System.err.println(item);
//				}
//			}
//			assertEquals(0, errors.size());
//		}catch(Exception e) {
//			fail();
//		}
	}
}
