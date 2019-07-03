package de.uniks.networkparser.ext;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.GraphMetric;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SimpleReverseEngineering;
import de.uniks.networkparser.parser.SymTabEntry;

/*String string = model.toString(new DotConverter().withShowAssocInfo(false).withShowSimpleNodeInfo(true));
  FileBuffer.writeFile("model2.data", string.getBytes()); */

public class FileClassModel extends ClassModel {
	/**
	 * The suffix of a java file as constant for easer use
	 */
	private static final String JAVA_FILE_SUFFIX = ".java";
	private SimpleSet<ParserEntity> error = new SimpleSet<ParserEntity>();
	private SimpleSet<ParserEntity> list = new SimpleSet<ParserEntity>();
	private SimpleKeyValueList<String, SimpleList<ParserEntity>> packageList = new SimpleKeyValueList<String, SimpleList<ParserEntity>>();
	private boolean parseFile = true;
	private ObjectCondition reverseEngineering;

	public FileClassModel(String packageName) {
		with(packageName);
	}

	public FileClassModel withParseFile(boolean value) {
		this.parseFile = value;
		return this;
	}

	public boolean readFiles(String path, ObjectCondition... conditions) {
		return readFiles(path, null, conditions);
	}

	public boolean readFiles(String path, String type, ObjectCondition... conditions) {
		ObjectCondition condition = null;
		if (conditions != null && conditions.length > 0) {
			condition = conditions[0];
		}
		String name = this.getName();
		String pkgName = name.replace('.', '/');
		String parent = "";
		if (name.indexOf('.') > 0) {
			parent = name.substring(0, name.lastIndexOf('.') + 1);
		}
		if (path != null) {
			if ((path.endsWith("/") || path.endsWith("\\")) == false) {
				path += "/";
			}
		} else {
			path = "";
		}

		getFiles(new File(path + pkgName), condition, parent);
		if (parseFile) {
			for (ParserEntity item : list) {
				analyse(item);
			}
		}
		return true;
	}

	public boolean finishReverseEngineering() {
		SimpleEvent event = new SimpleEvent(this, "reverseengineering", null, this.list);
		return getReverseEngineering().update(event);
	}

	public SimpleList<String> analyseJavaDoc(boolean fullCheck) {
		SimpleList<String> errors = new SimpleList<String>();
		for (ParserEntity item : list) {
			errors.addAll(analyseJavaDoc(item, fullCheck));
		}
		return errors;
	}

	/**
	 * Validates a single java file for the java doc
	 * 
	 * @param entity    Analyse JavaDoc
	 * @param fullCheck FullCheck
	 * @return List f Warnings and Errors
	 */
	public SimpleList<String> analyseJavaDoc(ParserEntity entity, boolean fullCheck) {
		CharacterBuffer content = FileBuffer.readFile(entity.getFileName());
		content.replace('\t', ' ');

		/* Create a string */
		SimpleList<String> lines = new SimpleList<String>();
		while (content.isEnd() == false) {
			lines.add(content.readLine().toString());
		}
		SimpleList<String> msg = new SimpleList<String>();
		String currentPackage = null;
		for (String s : lines) {
			if (s.contains("package")) {
				currentPackage = s.replace("package", "");
				break;
			}
		}
		if (currentPackage != null) {
			currentPackage = currentPackage.substring(0, currentPackage.length() - 1);
		}
		/* Set the current file name */
		String currentFileName = entity.getFileName();

		/* Check the class java doc */
		msg.addAll(checkClassJavaDoc(content, lines, fullCheck, currentPackage, currentFileName));

		/* Check all method java doc */
		msg.addAll(checkMethodsJavaDoc(content, lines, fullCheck, currentPackage, currentFileName));

		return msg;
	}

	/**
	 * Checks if there is a java doc over the class declaration
	 *
	 * @param text            The text which should be checked
	 * @param lines           All lines of the current checked file
	 * @param fullCheck       Should founded warnings or error
	 * @param currentPackage  PackageName
	 * @param currentFileName FileName
	 * @return All Messages
	 */
	private SimpleList<String> checkClassJavaDoc(CharacterBuffer text, SimpleList<String> lines, boolean fullCheck,
			String currentPackage, String currentFileName) {
		SimpleList<String> msg = new SimpleList<String>();
		/*
		 * First check if the class has java doc, first get the index of "public class"
		 */
		int start = text.indexOf("public class");

		/* if the start -1 the class could be abstract */
		if (start == -1) {
			/* Get the index of "public abstract class" */
			start = text.indexOf("public abstract class");
		}

		/* If the start -1 the class is a enumeration */
		if (start == -1) {
			/* Get the index of "public enum" */
			start = text.indexOf("public enum");
		}

		/* If the start -1 the class is a interface */
		if (start == -1) {
			/* Get the index of "public interface" */
			start = text.indexOf("public interface");
		}

		/* Maybe there is just a class */
		if (start == -1) {
			/* Get the index of "class" */
			start = text.indexOf("class ");
		}

		/* Get the line of the class definition to jump there if there is no comment */
		int lineClass = -1;
		for (String s : lines) {
			if (s.contains("public class")) {
				lineClass = lines.indexOf(s) + 1;
				break;
			}
		}

		/* Check if line class is still -1, this means class could be abstract */
		for (String s : lines) {
			if (s.contains("public abstract class")) {
				lineClass = lines.indexOf(s) + 1;
				break;
			}
		}

		/* Check if line class is still -1, this means class is a interface */
		if (lineClass == -1) {
			for (String s : lines) {
				if (s.contains("public interface")) {
					lineClass = lines.indexOf(s) + 1;
					break;
				}
			}

		}

		/* Check if line class is still -1, this means class is a enumeration */
		if (lineClass == -1) {
			for (String s : lines) {
				if (s.contains("public enum")) {
					lineClass = lines.indexOf(s) + 1;
					break;
				}
			}
		}

		/* Maybe there is just a class */
		if (lineClass == -1) {
			for (String s : lines) {
				if (s.contains("class ")) {
					lineClass = lines.indexOf(s) + 1;
					break;
				}
			}
		}

		/* Check if the char before the method declaration is a end of a comment */
		if (lines.get(lineClass - 2).contains("*/")) {
			/* Get the java doc comment, 0 because the first comment is the class comment */
			String classDoc = extractJavaDocComment(text, start);

			/* There is no comment or a wrong one */
			if (classDoc.isEmpty() && fullCheck) {
				/* Put the missing java doc to the trace informations */
				msg.add("ERROR:" + currentPackage + ".missing.ClassDoc(" + currentFileName + ":" + lineClass + ")");
			} else {
				/* Check if there is a line like * some text */
				if (Pattern.compile("\\u002A \\w+").matcher(classDoc).find() == false) {
					/* There is no text, put a missing doc description error */
					msg.add("WARNING:" + currentPackage + ".missing.ClassDocText(" + currentFileName + ":" + lineClass
							+ ")");
				}
				/* Check if there is the @author tag with some text */
				if (classDoc.split("@author").length == 1) {
					/* There is no tag and no text */
					msg.add("ERROR:" + currentPackage + ".missing.AuthorTag(" + currentFileName + ":" + lineClass
							+ ")");
				} else if (classDoc.split("@author [a-zA-Z]+").length == 1) {
					/* There is no text after the tag, create warning */
					msg.add("WARNING:" + currentPackage + ".missing.AuthoTagText(" + currentFileName + ":" + lineClass
							+ ")");
				}
			}
			/* Return we found a doc or a incomplete doc */
			return msg;
		}

		/* Only if full check is enable */
		if (fullCheck) {
			/*
			 * There is no comment in any case over the class definition, therefore create
			 * error
			 */
			msg.add("ERROR:" + currentPackage + ".missing.ClassDoc(" + currentFileName + ":" + lineClass + ")");
		}
		return msg;
	}

	/**
	 * Checks if there is a java doc over all method declarations
	 *
	 * @param text            The text which should be checked
	 * @param lines           All lines of the current checked file
	 * @param fullCheck       Should founded warnings or error
	 * @param currentPackage  PackageName
	 * @param currentFileName FileName
	 * @return All Messages
	 */
	private SimpleList<String> checkMethodsJavaDoc(CharacterBuffer text, SimpleList<String> lines, boolean fullCheck,
			String currentPackage, String currentFileName) {
		SimpleList<String> msg = new SimpleList<String>();
		/* Create a matcher to find all method declarations */
		Matcher methodPattern = Pattern.compile(
				"((public|private|protected|static|final|native|synchronized|abstract|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+[\\$_\\w]+\\([^\\)]*\\)?\\s*\\{?")
				.matcher(text);

		/* Go through all matches */
		while (methodPattern.find()) {
			/* Get the current match */
			String match = methodPattern.group();
			/* Get the index of the current match in the text */
			int start = methodPattern.start();

			/* Get the line of the method definition to jump there if there is no comment */
			int lineMethod = -1;
			for (String s : lines) {
				if (s.contains(match)) {
					lineMethod = lines.indexOf(s) + 1;
					break;
				}
			}

			/*
			 * If lineMethod still -1 the { is in the next row therefore cut at \n and try
			 * again
			 */
			if (lineMethod == -1) {
				for (String s : lines) {
					if (s.contains(match.split("\n")[0])) {
						lineMethod = lines.indexOf(s) + 1;
						break;
					}
				}
			}
			if (lineMethod == -1) {
				continue;
			}

			/*
			 * Check if there is a annotation over the method, if so go to the next method
			 */
			if (lines.get(lineMethod - 2).contains("@")) {
				continue;
			}

			/* Check if the char before the method declaration is a end of a comment */
			if (lines.get(lineMethod - 2).contains("*/")) {
				/* Get the java doc comment */
				String methodDoc = extractJavaDocComment(text, start);

				/* There is no comment or a wrong one */
				if (methodDoc.isEmpty() && fullCheck) {

					/* Put the missing java doc to the trace informations */
					msg.add("ERROR:" + currentPackage + ".missing.MethodDoc(" + currentFileName + ":" + lineMethod
							+ ")");
				} else {
					/* Check if there is a line like * some text */
					if (Pattern.compile("\\u002A \\s*[\\w+<]").matcher(methodDoc).find() == false) {
						if (match.contains(" get") || match.contains(" set") || match.contains(" is")
								|| match.contains(" with")) {
							/* no nessessary Comment for getter and Setter */
							continue;
						}
						/* There is no text, put a missing doc description error */
						msg.add("WARNING:" + currentPackage + ".missing.MethodDocText(" + currentFileName + ":"
								+ lineMethod + ")");
					}

					/* Check if there are parameters in the method declaration */
					if (match.contains("()") == false) {
						/* Get all parameters from method declaration */
						String temp = match.substring(match.indexOf("(") + 1, match.indexOf(")"))
								.replaceAll("<[^\\)]*>", "");
						String[] parameters = temp.split(",");

						/* Go through all parameters and check the @param tag */
						for (String s : parameters) {
							String[] param = s.trim().split(" ");

							String parameterName = null;
							if (param.length > 1) {
								parameterName = param[1];
							}

							/* Check if there is a @param tag with the current parameter name */
							if (methodDoc.split("@param " + parameterName).length == 1) {

								/* There is no tag and no text */
								msg.add("ERROR:" + currentPackage + ".missing.ParamTag(" + currentFileName + ":"
										+ lineMethod + ")");
							} else if (methodDoc.split("@param " + parameterName + "[ ]+[a-zA-Z*\r]+").length == 1) {
								/* There is no text after the tag, create warning */
								msg.add("WARNING:" + currentPackage + ".missing.ParamTagText(" + currentFileName + ":"
										+ lineMethod + ")");
							}
						}
					}

					/* Check if there is a return type in the method */
					if (match.contains("void") == false) {
						/* There is no tag and text */
						if (methodDoc.split("@return").length == 1) {
							/* There is no tag and no text */
							msg.add("ERROR:" + currentPackage + ".missing.ReturnTag(" + currentFileName + ":"
									+ lineMethod + ")");
						} else if (methodDoc.split("@return[ \t]+[a-zA-Z]+").length == 1) {
							/* There is no text after the tag, create warning */
							msg.add("WARNING:" + currentPackage + ".missing.ReturnTagText(" + currentFileName + ":"
									+ lineMethod + ")");
						}
					}
				}
				/* Look at the next match */
				continue;
			}

			/* Only if full check is enable */
			if (fullCheck) {
				/*
				 * There is no comment in any case over the method definition, therefore create
				 * error
				 */
				msg.add("ERROR:" + currentPackage + ".missing.MethodDoc(" + currentFileName + ":" + lineMethod + ")");
			}
		}
		return msg;
	}

	/**
	 * Extract a java doc comment from a given string
	 *
	 * @param extractFrom The text where the comment should be extracted from
	 * @param searchIndex The index from which the search will start
	 * @return The extracted comment as string
	 */
	private String extractJavaDocComment(CharacterBuffer extractFrom, int searchIndex) {

		/* Initialize attribute which describe the range of the doc */
		int begin = -1;
		int end = -1;

		/* Go back from the searchIndex */
		for (int i = searchIndex; i > 0; i--) {

			/* Found the start of the java doc */
			if (extractFrom.charAt(i) == '*' && extractFrom.charAt(i - 1) == '*' && extractFrom.charAt(i - 2) == '/') {

				/* The start of the comment */
				begin = i - 2;

				/* Found the end of the java doc */
			} else if (extractFrom.charAt(i) == '/' && extractFrom.charAt(i - 1) == '*') {

				/* The end of the comment */
				end = i;

			}
			/* If both values found break */
			if (begin != -1 && end != -1) {
				break;
			}
		}

		/* Wrong comment just a /* or there is no comment */
		if (begin == -1) {
			return "";
		}
		return extractFrom.substring(begin, end + 2);
	}

	public SimpleKeyValueList<String, SimpleList<ParserEntity>> getPackageList() {
		return packageList;
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

	public ClassModel analyseSymTabEntry(ClassModel model) {
		if (model == null) {
			model = this;
		}
		for (ParserEntity element : list) {
			this.add(element.getClazz());
			element.addMemberToModel(false);
		}
		return model;
	}

	public ClassModel analyseBounds(ClassModel model) {
		if (model == null) {
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
				if (item.getName().startsWith(search)) {
					String ref = item.getName().substring(7);
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
				for (int e = 0; e < parserEntities.size(); e++) {
					if (e == p) {
						continue;
					}
					Clazz targetClazz = parserEntities.get(e).getClazz();
					String targetName = targetClazz.getName();
					if (content.indexOf(targetName) > 0) {
						clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					}
				}
			}
		}
		return model;
	}

	private void getFiles(File directory, ObjectCondition condition, String parent) {
		if (directory.exists() && directory.isDirectory()) {
			File[] items = directory.listFiles();
			if (items == null) {
				return;
			}
			SimpleList<ParserEntity> packageEntity = new SimpleList<ParserEntity>();
			for (File file : items) {
				if (file.getName().endsWith(JAVA_FILE_SUFFIX)) {
					ParserEntity element = createParserEntity(file, condition);
					list.add(element);
					packageEntity.add(element);
				} else if (file.getName().equalsIgnoreCase("test") == false && file.isDirectory()) {
					getFiles(file, condition, parent + directory.getName() + ".");
				}
			}
			if (packageEntity.size() > 0) {
				packageList.put(parent + directory.getName(), packageEntity);
			}
		}
	}

	public ClassModel analyseInBoundLinks(ClassModel model) {
		if (model == null) {
			model = this;
		}
		for (int i = 0; i < list.size(); i++) {
			ParserEntity parserEntity = list.get(i);
			CharacterBuffer content = parserEntity.getCode().getContent();
			Clazz clazz = parserEntity.getClazz();
			model.add(clazz);
			for (int p = 0; p < list.size(); p++) {
				if (p == i) {
					continue;
				}

				Clazz targetClazz = list.get(p).getClazz();
				String targetName = targetClazz.getName();
				if (content.indexOf("protected " + targetName + " ") > 0) {
					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					continue;
				}
				if (content.indexOf("public " + targetName + " ") > 0) {
					clazz.createBidirectional(targetClazz, "use", Association.ONE, "use", Association.ONE);
					continue;
				}
				MethodSet methods = clazz.getMethods();
				for (Method m : methods) {
					if (m.getModifier().has(Modifier.PROTECTED) || m.getModifier().has(Modifier.PUBLIC)) {
						for (Parameter param : methods.getParameters()) {
							if (param.getType().equals(targetClazz)) {
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
		if (item instanceof Method) {
			owner = (Method) item;
			methodBody = owner.getBody();
			methodBody = methodBody.toLowerCase();
			methodBody = methodBody.replace("\n", "");
			methodBody = methodBody.replaceAll("\t", "");
			methodBody = methodBody.replaceAll(" ", "");
			methodBody = methodBody.replace('<', '(');
			methodBody = methodBody.replace('>', ')');
		} else if (item instanceof String) {
			methodBody = (String) item;
		}
		int mcCabe = 1;
		if (methodBody != null) {
			mcCabe += check(methodBody, "if(");
			mcCabe += check(methodBody, "do{");
			mcCabe += check(methodBody, "while(");
			mcCabe += check(methodBody, "&&");
			if (owner != null) {
				GraphMetric metric = GraphMetric.create(owner);
				metric.withMcCabe(mcCabe);
			}
		}
		return mcCabe;
	}

	private int check(String body, String search) {
		int mcCabe = 0;
		int index = 0;
		while (index >= 0) {
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
		if (item instanceof GraphModel) {
			GraphModel model = (GraphModel) item;
			GraphMetric modelMetric = GraphMetric.create(model);
			ClazzSet clazzes = model.getClazzes();
			for (Clazz clazz : clazzes) {
				GraphMetric clazzMetric = GraphMetric.create(clazz);
				MethodSet methods = clazz.getMethods();
				for (Method m : methods) {
					GraphMetric methodMetric = analyseLoC(m);
					clazzMetric.merge(methodMetric);
				}
				modelMetric.merge(clazzMetric);
			}
			return modelMetric;
		}
		if (item instanceof Method) {
			owner = (Method) item;
			methodBody = owner.getBody();
		} else if (item instanceof String) {
			methodBody = (String) item;
		}
		GraphMetric metric = GraphMetric.create(owner);
		if (methodBody == null) {
			return metric;
		}
		int emptyLine = 0, commentCount = 0, methodheader = 0, annotation = 0, linesOfCode = 0;
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

	public ParserEntity readFile(String fileName, ObjectCondition condition) {
		File file = new File(fileName);
		if (file.getName().endsWith(".java")) {
			ParserEntity element = createParserEntity(file, condition);
			SimpleList<ParserEntity> packageEntity = new SimpleList<ParserEntity>();
			list.add(element);
			packageEntity.add(element);
			if (packageEntity.size() > 0) {
				packageList.put(file.getParent(), packageEntity);
			}
			return analyse(element);
		}
		return null;
	}

	public ObjectCondition getReverseEngineering() {
		if (reverseEngineering == null) {
			reverseEngineering = new SimpleReverseEngineering();
		}
		return reverseEngineering;
	}

	public FileClassModel withReverseEngineering(ObjectCondition reverseEngineering) {
		this.reverseEngineering = reverseEngineering;
		return this;
	}

	@Override
	public Object getValue(String attribute) {
		if (PROPERTY_FILETYPE.equalsIgnoreCase(attribute)) {
			return getClass().getSuperclass().getSimpleName().toLowerCase();
		}
		return super.getValue(attribute);
	}
}
