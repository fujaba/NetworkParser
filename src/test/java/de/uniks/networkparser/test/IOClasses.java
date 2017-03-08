package de.uniks.networkparser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IOClasses {
public static String CRLF="\r\n";
	public String getAbsolutePath(String file){
		file = "de/uniks/networkparser/test/"+file;
		String path = IOClasses.class.getResource("IOClasses.class").getPath();
		String root = new File("").toURI().getPath().replace(" ", "%20");

		if(path.startsWith(root)) {
			path = path.substring(root.length());
		}

		int pos = path.lastIndexOf("bin/");
		if(pos>=0){
			path = path.substring(0, pos)+"src/test/resources/" ;
		}else{
			pos = path.lastIndexOf("build/classes");
			if(pos>=0){
				path = path.substring(0, pos + 6)+"resources/test/";
			}
		}
		return path+file;
	}
	public StringBuffer readFile(String file){
		BufferedReader bufferedReader;
		String fullPath = getAbsolutePath(file);
		try {
			bufferedReader = new BufferedReader(new FileReader(fullPath));
			StringBuffer indexText = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null)
			{
				indexText.append(line).append(CRLF);
				line = bufferedReader.readLine();
			}

			bufferedReader.close();
			return indexText;
		} catch (FileNotFoundException e) {
			System.out.println(file+": " + e.getMessage());
		} catch (IOException e) {
		}
		return null;
	}
	
//	public StringBuffer writeFile(String file){
//		try {
//			throw new Exception("Path");
//		}
//	}
	
}
