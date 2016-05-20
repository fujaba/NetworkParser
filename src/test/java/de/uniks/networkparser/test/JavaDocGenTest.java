package de.uniks.networkparser.test;

import java.io.File;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.test.javadoc.JavaDocValidator;

public class JavaDocGenTest {
	public static final String PREFIX="src.main.java.";
	@Test(timeout=60000)
	public void testGenJavaDoc() {
//		long time = System.currentTimeMillis();
		JavaDocValidator javaDoc=new JavaDocValidator();
		ArrayList<String> validateFileTree = javaDoc.validateFileTree(new File("src/main/java"), true, false);
//		Assert.assertEquals(0, validateFileTree.size());
//		System.out.println("Time: " +(System.currentTimeMillis()- time));
		
		
		
//		try{
//			time = System.currentTimeMillis();
//			ProcessBuilder pb = new ProcessBuilder("javadoc","-notree", "-noindex", "-nonavbar", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
////			pb = new ProcessBuilder("javadoc", "-noindex", "-notree", "-quiet", "-subpackages", "de.uniks.networkparser", "-sourcepath", "src/main/java", "-d", "build/testJavadoc");
////			pb.redirectOutput(Redirect.INHERIT);
////			pb.redirectError(Redirect.INHERIT);
//
//			Process p = pb.start();
//			BufferedReader errorReader = new BufferedReader (new InputStreamReader(p.getErrorStream()));
//
//			
//			
//			SimpleList<String> errors=new SimpleList<String>();
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
//			Assert.assertEquals(0, errors.size());
//			System.out.println("Time: "+(System.currentTimeMillis()- time));
//		}catch(Exception e) {
//			Assert.fail();
//		}
	}
}
