package de.uniks.networkparser.ext.generic;

import java.io.File;

import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.DebugCondition;
import de.uniks.networkparser.parser.ParserEntity;

public class ParserIncomming {
	@Test
	public void testTest() {
		ParserIncomming parserIncomming = new ParserIncomming();
//		parserIncomming.analyse("src/main/java/", "de.uniks.networkparser");
		File file = new File("src/main/java/de/uniks/networkparser/bytes/qr/BitArray.java");
		if(parserIncomming.analyse(file) == false) {
			System.out.println("ERROR");
		}
	}
	
	public boolean analyse(String path, String packageName) {
		SimpleSet<File> files = new SimpleSet<File>();
		packageName = packageName.replace('.', '/');
		getFiles(new File(path+packageName), files);
		
//		SimpleList<Class<?>> classes = ReflectionLoader.getClassesForPackage(packageName);
		int error=0;
		for(File item : files) {
			if(analyse(item) == false) {
				error++;
			}
		}
		System.out.println(error+" / "+files.size());
		return true;
	}
	public boolean analyse(File file) {
		ParserEntity parser = new ParserEntity();
		parser.withCondition(new DebugCondition().withLine(10173));
		Clazz clazz = new Clazz(file.getName());
		CharacterBuffer content = FileBuffer.readFile(file);
		try {
			parser.parse(content, clazz, file.getName());
//			System.out.println(item.getName());
		}catch (Exception e) {
//			System.out.println("Cant parse:"+item.getName());
//			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void getFiles(File directory, SimpleSet<File> filesNames) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files == null) {
				return;
			}
			for (File file : files) {
				if (file.getName().endsWith(".java")) {
					filesNames.add(file);
				} else if (file.getName().equalsIgnoreCase("test")== false && file.isDirectory()) {
					getFiles(file, filesNames);
				}
			}
		}
	}
}
