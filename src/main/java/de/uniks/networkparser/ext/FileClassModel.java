package de.uniks.networkparser.ext;

import java.io.File;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.GraphMetric;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SymTabEntry;

//String string = model.toString(new DotConverter().withShowAssocInfo(false).withShowSimpleNodeInfo(true));
//FileBuffer.writeFile("model2.data", string.getBytes());


public class FileClassModel extends ClassModel {
	private SimpleSet<ParserEntity> error = new SimpleSet<ParserEntity>();
	private SimpleSet<ParserEntity> list = new SimpleSet<ParserEntity>();
	private SimpleKeyValueList<String, SimpleList<ParserEntity>> packageList = new SimpleKeyValueList<String, SimpleList<ParserEntity>>();

	public FileClassModel(String packageName) {
		with(packageName);
	}

	public boolean readFiles(String path, ObjectCondition... conditions) {
		long currentTimeMillis = System.currentTimeMillis();
		ObjectCondition condition = null;
		if (conditions != null && conditions.length > 0) {
			condition = conditions[0];
		}
		String pkgName = this.getName().replace('.', '/');
		getFiles(new File(path + pkgName), condition);
		for (ParserEntity item : list) {
			analyse(item);
		}

		System.out.println(System.currentTimeMillis() - currentTimeMillis);
		//		System.out.println(error+" / "+files.size());
		return true;
	}

	public static ParserEntity createParserEntity(File file, ObjectCondition condition) {
		return new ParserEntity().withFile(file.getAbsolutePath()).withCondition(condition);
	}
	
	public ParserEntity analyse(ParserEntity entity) {
		CharacterBuffer content = FileBuffer.readFile(entity.getFileName());
		try {
			entity.parse(content);
		} catch (Exception e) {
			this.error.add(entity);
		}
		return entity;
	}

	public ClassModel analyseBounds(ClassModel model) {
		if(model == null) {
			model = this;
		}
		String search = "import " + model.getName();
		SimpleKeyValueList<Clazz, SimpleList<String>> assocs = new SimpleKeyValueList<Clazz, SimpleList<String>>();
		ClazzSet set = new ClazzSet();

		for (ParserEntity element : list) {
			Clazz clazz = element.getClazz();
			SimpleList<SymTabEntry> imports = element.getSymbolEntries(SymTabEntry.TYPE_IMPORT);
			SimpleList<String> assoc = new SimpleList<String>();
			for (SymTabEntry item : imports) {
				if (item.getValue().startsWith(search)) {
					String ref = item.getValue().substring(7);
					ref = ref.substring(ref.lastIndexOf('.') + 1);
					if (ref.equals("//") == false) {
						assoc.add(ref);
					}
				}
			}
			set.add(clazz);
			assocs.add(clazz, assoc);
		}
		for (int i = 0; i < assocs.size(); i++) {
			Clazz clazz = assocs.getKeyByIndex(i);
			SimpleList<String> assocValue = assocs.getValueByIndex(i);
			if (assocValue.size() > 0) {
				model.add(clazz);
			}
			for (String item : assocValue) {
				Clazz target = (Clazz) this.getChildByName(item, Clazz.class);
				if (target == null) {
					target = set.getClazz(item);
				}
				if (target == null) {
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

	private void getFiles(File directory, ObjectCondition condition) {
		if (directory.exists() && directory.isDirectory()) {
			File[] items = directory.listFiles();
			if (items == null) {
				return;
			}
			SimpleList<ParserEntity> packageEntity = new SimpleList<ParserEntity>();
			for (File file : items) {
				if (file.getName().endsWith(".java")) {
					ParserEntity element = createParserEntity(file, condition);
					list.add(element);
					packageEntity.add(element);
				} else if (file.getName().equalsIgnoreCase("test") == false && file.isDirectory()) {
					getFiles(file, condition);
				}
			}
			if (packageEntity.size() > 0) {
				packageList.put(directory.getName(), packageEntity);
			}
		}
	}
	public ClassModel analyseInBoundLinks(ClassModel model) {
		if(model == null) {
			model = this;
		}
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
		return model;
	}

	public SimpleSet<ParserEntity> getErros() {
		return error;
	}
	
	
	public int analyseMcCabe(Object item) {
		String methodBody = null;
		Method owner = null;
		if(item instanceof Method) {
			owner = (Method) item;
			methodBody = owner.getBody();
			methodBody = methodBody.toLowerCase();
			methodBody = methodBody.replace("\n", "");
			methodBody = methodBody.replaceAll("\t", "");
			methodBody = methodBody.replaceAll(" ", "");
			methodBody = methodBody.replace('<', '(');
			methodBody = methodBody.replace('>', ')');
		}else if(item instanceof String) {
			methodBody = (String)item;
		}
		int mcCabe = 1;
		if(methodBody != null) {
			mcCabe += check(methodBody, "if(");
			mcCabe += check(methodBody, "do{");
			mcCabe += check(methodBody, "while(");
			mcCabe += check(methodBody, "&&");
			if(owner != null) {
				GraphMetric metric = GraphMetric.create(owner);
				metric.withMcCabe(mcCabe);
			}
		}
		return mcCabe;
	}

	private int check(String body, String search) {
		int mcCabe=0;
		int index=0;
		while(index>=0) {
			index = body.indexOf(search, index);
			if (index == -1) {
				break;
			}
			if (checkQuotes(body, index)) {
				mcCabe++;
				index += 1;
			} else {
				index += 1;
			}
		}
		return mcCabe;
	}

	public boolean checkQuotes(String allText, int index) {
		int quote = 0;
		for (int i = 0; i < index; i++) {
			char nextChar = allText.charAt(i);
			if (nextChar == '\"')
				quote++;
		}

		if (quote % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public GraphMetric analyseLoC(Object item) {
		String methodBody = null;
		Method owner = null;
		if(item instanceof Method) {
			owner = (Method) item;
			methodBody = owner.getBody();
		}else if(item instanceof String) {
			methodBody = (String) item;
		}
		GraphMetric metric = GraphMetric.create(owner);
		if(methodBody == null) {
			return metric;
		}
		int emptyLine = 0, commentCount = 0, methodheader = 0,annotation = 0, linesOfCode = 0;
		String[] lines = methodBody.split("\n");
		for (String line : lines) {
			String simple = line.trim();
			if (simple.length() < 1) {
				emptyLine++;
				continue;
			}
			if (simple.indexOf("/*") >= 0 || simple.indexOf("*/") >= 0 || simple.indexOf("//") >= 0
					|| simple.startsWith("*")) {
				commentCount++;
				continue;
			}
			if ("{}".indexOf(simple) >= 0) {
				methodheader++;
				continue;
			}
			if (simple.startsWith("@")) {
				annotation++;
				continue;
			}
			linesOfCode++;
		}
		metric.withLoc(emptyLine, commentCount, methodheader, annotation, linesOfCode);
		return metric;
	}
}
