package de.uniks.networkparser.test.javafx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestRunner {
 public static void main(String[] args) {

	 String[] classpath = System.getProperty("java.class.path").split(";");
	 System.out.println(classpath);
}
 public void execute(){
	 String[] classpath = System.getProperty("java.class.path").split(";");
	 File config=new File(".classpath");
	 if(config.exists()){
		 try {
				BufferedReader in = new BufferedReader(new FileReader(config));
				String zeile = null;
				while ((zeile = in.readLine()) != null) {
					System.out.println("Gelesene Zeile: " + zeile);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
	 for(String path : classpath){
		 System.out.println(path);
	 }
//	 System.out.println(classpath);
 }

}
