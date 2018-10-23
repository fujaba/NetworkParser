package de.uniks.networkparser.ext.generic;

import java.io.File;
import java.util.ArrayList;

import org.hamcrest.Condition;
import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.DotConverter;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.DebugCondition;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SymTabEntry;

public class ParserIncomming {
	@Test
	public void testTest() {
		ParserIncomming parserIncomming = new ParserIncomming();
		File file = null;
		parserIncomming.analyse("src/main/java/", "de.uniks.networkparser");
//		parserIncomming.analyseInBoundLinks("src/main/java/", "de.uniks.networkparser");
//		file = new File("src/main/java/de/uniks/networkparser/ext/io/TarArchiveEntry.java");
		if(parserIncomming.analyse(file, new DebugCondition()).isValid== false) {
			System.out.println("ERROR");
		}
	}
	
	public ClassModel analyseInBoundLinks(String path, String packageName) {
		SimpleSet<File> files = new SimpleSet<File>();
		String pkgName = packageName.replace('.', '/');
		getFiles(new File(path+pkgName), files);
		ClassModel model=new ClassModel().with("");
		int error=0;
		SimpleList<ParserEntity> list=new SimpleList<ParserEntity>();

		for(File item : files) {
			ParserEntity result = analyse(item); 
			if(result.isValid == false) {
				error++;
			}
			list.add(result);
		}
		System.out.println(error+" / "+files.size());
		for (int i = 0; i < list.size(); i++) {
			ParserEntity parserEntity = list.get(i);
			CharacterBuffer content = parserEntity.getCode().getContent();
			Clazz clazz = parserEntity.getClazz();
			model.add(clazz);
			for (int p = 0; p < list.size(); p++) {
				if(p==i) {
					continue;
				}
				
				Clazz targetClazz = list.get(p).getClazz();
				String targetName = targetClazz.getName();
				if(content.indexOf("protected "+targetName+" ")>0) {
					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					continue;
				}
				if(content.indexOf("public "+targetName+" ")>0) {
					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					continue;
				}
				MethodSet methods = clazz.getMethods();
				for(Method m : methods) {
					if(m.getModifier().has(Modifier.PROTECTED) || m.getModifier().has(Modifier.PUBLIC)) {
						for(Parameter param : methods.getParameters()) {
							if(param.getType().equals(targetClazz)) {
								clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
								break;
							}
						}
					}
				}
			}
		}
		String string = model.toString(new DotConverter().withShowAssocInfo(false).withShowSimpleNodeInfo(true));
		FileBuffer.writeFile("model2.data", string.getBytes());
		return model;
	}
	
	public ClassModel analyse(String path, String packageName) {
		SimpleSet<File> files = new SimpleSet<File>();
		String pkgName = packageName.replace('.', '/');
		getFiles(new File(path+pkgName), files);
		ArrayList<ParserEntity> list=new ArrayList<ParserEntity>();
		
		SimpleKeyValueList<String, SimpleList<ParserEntity>> packageList = new SimpleKeyValueList<String, SimpleList<ParserEntity>>();
		int error=0;
		for(File item : files) {
			ParserEntity result = analyse(item); 
			SimpleList<ParserEntity> packageEntity = packageList.get(item.getParent());
			if(packageEntity == null) {
				packageEntity = new SimpleList<ParserEntity>();
				packageList.put(item.getParent(), packageEntity);
				
			}
			if(result.isValid == false) {
				error++;
			}
			list.add(result);
			packageEntity.add(result);
		}
		System.out.println(error+" / "+files.size());
		ClassModel model=new ClassModel().with("");
		String search="import "+packageName;
		SimpleKeyValueList<Clazz, SimpleList<String>> assocs=new SimpleKeyValueList<Clazz, SimpleList<String>>();
		ClazzSet set=new ClazzSet();

		for(ParserEntity element : list) {
			Clazz clazz = element.getClazz();
			SimpleList<SymTabEntry> imports = element.getSymbolEntries(SymTabEntry.TYPE_IMPORT);
			SimpleList<String> assoc=new SimpleList<String>();
			for(SymTabEntry item : imports) {
				if(item.getValue().startsWith(search)) {
					String ref = item.getValue().substring(7);
					ref = ref.substring(ref.lastIndexOf('.')+1);
					if(ref.equals("//") == false) {
						assoc.add(ref);
					}
				}
			}
			set.add(clazz);
			assocs.add(clazz, assoc);
		}
		for(int i=0;i<assocs.size();i++) {
			Clazz clazz = assocs.getKeyByIndex(i);
			SimpleList<String> assocValue = assocs.getValueByIndex(i);
			if(assocValue.size()>0) {
				model.add(clazz);
			}
			for(String item : assocValue) {
				Clazz target = (Clazz)model.getChildByName(item, Clazz.class);
				if(target == null) {
					target = set.getClazz(item);
				}
				if(target == null) {
					target = model.createClazz(item);
				}
				clazz.createBidirectional(target, "use", Association.ONE, "use", Association.ONE);
			}
		}
		
		for (int i = 0; i < packageList.size(); i++) {
			SimpleList<ParserEntity> parserEntities = packageList.getValueByIndex(i);
			for (int p = 0; p < parserEntities.size(); p++) {
				ParserEntity parserEntity = parserEntities.get(p);
				CharacterBuffer content = parserEntity.getCode().getContent();
				Clazz clazz = parserEntity.getClazz();
//			String clazzName = clazz.getName();
//				System.out.print("Start:" + clazz.getAssociations().size() + " ->");
				for (int e = 0; e < parserEntities.size(); e++) {
					if (e == p) {
						continue;
					}
					Clazz targetClazz = parserEntities.get(e).getClazz();
					String targetName = targetClazz.getName();
					if (content.indexOf(targetName) > 0) {
						clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					}
//				if(content.indexOf("new "+targetName+"(")>0) {
//					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
////					System.out.println(clazzName+"--"+targetName);
//				}else if(content.indexOf("("+targetName)>0) {
//					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
////					System.out.println(clazzName+"--"+targetName);
//				}else if(content.indexOf(" "+targetName)>0) {
//					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
////					System.out.println(clazzName+"--"+targetName);
//				}else if(content.indexOf("\t"+targetName+" ")>0) {
//					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
////					System.out.println(clazzName+"--"+targetName);
//				}
				}
//				System.out.println(clazz.getAssociations().size());
			}
		}
//		String string = model.toString(new DotConverter().withShowAssocInfo(false).withShowNodeInfo(false));
//		FileBuffer.writeFile("model.data", string.getBytes());
//		System.out.println(string);
		return model;
	}
	public ParserEntity analyse(File file, ObjectCondition... condition) {
		ParserEntity parser = new ParserEntity();
		if(file == null) {
			return parser;
		}
		if(condition != null && condition.length>0) {
			parser.withCondition(condition[0]);
		}
//		parser.withCondition(new DebugCondition().withLine(4880));
		Clazz clazz = new Clazz(file.getName());
		CharacterBuffer content = FileBuffer.readFile(file);
		try {
			parser.parse(content, clazz, file.getName());
//			System.out.println(item.getName());
		}catch (Exception e) {
			parser.isValid = false;
//			System.out.println("Cant parse:"+item.getName());
//			e.printStackTrace();
		}
		return parser;
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
