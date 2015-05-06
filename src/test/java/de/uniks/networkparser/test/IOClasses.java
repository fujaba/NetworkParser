package de.uniks.networkparser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IOClasses {
public static String CRLF="\r\n";
	
	public StringBuffer readFile(String file){
		BufferedReader bufferedReader;
		File path = new File(IOClasses.class.getResource("IOClasses.class").getPath());
		path = path.getParentFile();
		try {
			bufferedReader = new BufferedReader(new FileReader(path+File.separator+file));
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
		} catch (IOException e) {
		}
		return null;
	}
}
