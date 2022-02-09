package de.uniks.networkparser.ext;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.SHA1;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphMetric;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SimpleReverseEngineering;
import de.uniks.networkparser.parser.SymTabEntry;
import de.uniks.networkparser.xml.HTMLEntity;

public class FileClassModel extends ClassModel {
	/**
	 * The suffix of a java file as constant for easer use
	 */
	private static final String JAVA_FILE_SUFFIX = ".java";
	public static final String RECURSIVE = "rekursive";
    private NetworkParserLog logger;
    private SortedSet<ParserEntity> packages = new SortedSet<>(ParserEntity.PROPERTY_FILENAME, new ParserEntity());
    private ObjectCondition reverseEngineering;

	public FileClassModel(String packageName) {
		with(packageName);
	}

	public ParserEntity readFiles(String path, ObjectCondition... conditions) {
	    String name = this.getName();
        String pkgName = name.replace('.', '/');
        if (path != null) {
            if (!path.endsWith("/") && !path.endsWith("\\")) {
                path += "/";
            }
        } else {
            path = "";
        }
		return readFiles(new File(path + pkgName), conditions);
	}
	
   public ParserEntity readFiles(File path, ObjectCondition... conditions) {
       ObjectCondition con = null;
       if(conditions != null && conditions.length>0) {
           con = conditions[0];
       }
       return getFiles(path, JAVA_FILE_SUFFIX, null, con);
    }

	public boolean finishReverseEngineering() {
		SimpleEvent event = new SimpleEvent(this, "reverseengineering", null, this.packages);
		boolean update = getReverseEngineering().update(event);
		this.fixClassModel();
		return update;
	}

	public SimpleList<String> analyseJavaDoc(boolean fullCheck) {
		SimpleList<String> errors = new SimpleList<>();
		for (ParserEntity item : getParserEntities()) {
			errors.addAll(analyseJavaDoc(item, fullCheck));
		}
		return errors;
	}
	
	public SortedSet<ParserEntity> getParserEntities() {
        return packages;
	}

	/**
	 * Validates a single java file for the java doc
	 * 
	 * @param entity    Analyse JavaDoc
	 * @param fullCheck FullCheck
	 * @return List f Warnings and Errors
	 */
	public SimpleList<String> analyseJavaDoc(ParserEntity entity, boolean fullCheck) {
	    CharacterBuffer content;
	    if(!entity.isContent()) {
	        content = FileBuffer.readFile(entity.getFileName());
        } else {
            content = new CharacterBuffer().with(entity.getCode().getContent());
        }
	    content.replace('\t', ' ');

		/* Create a string */
		SimpleList<String> lines = new SimpleList<>();
		while (!content.isEnd()) {
			lines.add(content.readLine().toString());
		}
		SimpleList<String> msg = new SimpleList<>();
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
		msg.addAll(checkClassJavaDoc(lines, fullCheck, currentPackage, currentFileName));

		/* Check all method java doc */
		msg.addAll(checkMethodsJavaDoc(lines, fullCheck, currentPackage, currentFileName));

		return msg;
	}

	private int getPos(String text, String... search) {
	    int pos=-1;
	    if(text == null || search == null) {
	        return pos;
	    }
	    for (String item : search) {
    	    pos = text.indexOf(item);
    	    if(pos<0) {
    	        continue;
    	    }
    	    if(pos>0 && text.charAt(pos-1) != ' ') {
    	        pos=-1;
    	        continue;
    	    }
    	    return pos;
	    }
	    return pos;
	}
	
	
	/**
	 * Checks if there is a java doc over the class declaration
	 *
	 * @param lines           All lines of the current checked file
	 * @param fullCheck       Should founded warnings or error
	 * @param currentPackage  PackageName
	 * @param currentFileName FileName
	 * @return All Messages
	 */
	private SimpleList<String> checkClassJavaDoc(SimpleList<String> lines, boolean fullCheck,
			String currentPackage, String currentFileName) {
		SimpleList<String> msg = new SimpleList<>();
		int lineClass = 0;
		for (; lineClass <lines.size();lineClass++) {
            String trimLine = lines.get(lineClass).trim();
            if(trimLine.isEmpty()) {
                continue;
            }
            char first = trimLine.trim().charAt(0);
            if(first=='*' || first == '/') {
                continue;
            }
            int pos = getPos(trimLine, "class " , "enum ", "interface ");
            if(pos<0) {
                continue;
            }
            /* Get the java doc comment, 0 because the first comment is the class comment */
            String classDoc = extractJavaDocComment(lines, lineClass);
            if(classDoc.isEmpty()) {
                /* There is no comment or a wrong one */
                if(fullCheck) {
                    /* Put the missing java doc to the trace informations */
                    msg.add("ERROR:" + currentPackage + ".missing.ClassDoc(" + currentFileName + ":" + lineClass + ")");
                }
                continue;
            }
            /* Check if there is a line like * some text */
            if (!Pattern.compile("\\u002A \\w+").matcher(classDoc).find()) {
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
	 * @param lines           All lines of the current checked file
	 * @param fullCheck       Should founded warnings or error
	 * @param currentPackage  PackageName
	 * @param currentFileName FileName
	 * @return All Messages
	 */
	private SimpleList<String> checkMethodsJavaDoc(SimpleList<String> lines, boolean fullCheck, String currentPackage, String currentFileName) {
		SimpleList<String> msg = new SimpleList<>();
		/* Create a matcher to find all method declarations */
		// "((public|private|protected|static|final|native|synchronized|abstract|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+[\\$_\\w]+\\([^\\)]*\\)?\\s*\\{?"
		
	    /* Go through all matches */
		for(int lineMethod = 0; lineMethod <lines.size();lineMethod++) {
		    String line = lines.get(lineMethod);
		    CharacterBuffer buffer = new CharacterBuffer().with(line);
            buffer.trim();
            if (!buffer.skip("public")) {
                if (!buffer.skip("private")) {
                    buffer.skip("protected");
                }
            }
            buffer.skip("static", "final","native", "synchronized", "abstract", "transient");
            String name = buffer.nextString('(');
            /* Current Line is not a Methodheader */
            if(buffer.getCurrentChar()!='(') {
                continue;
            }
            if(!StringUtil.isText(name)) {
                continue;
            }
            // Method found
            /*
             * Check if there is a annotation over the method, if so go to the next method
             */
            if (lines.get(lineMethod - 2).contains("@")) {
                continue;
            }

            /* Check if the char before the method declaration is a end of a comment */
            if (lines.get(lineMethod - 2).contains("*/")) {
                /* Get the java doc comment */
                String methodDoc = extractJavaDocComment(lines, lineMethod);

                /* There is no comment or a wrong one */
                if (methodDoc.isEmpty() && fullCheck) {

                    /* Put the missing java doc to the trace informations */
                    msg.add("ERROR:" + currentPackage + ".missing.MethodDoc(" + currentFileName + ":" + lineMethod
                            + ")");
                } else {
                    /* Check if there is a line like * some text */
                    if (!Pattern.compile("\\u002A \\s*[\\w+<]").matcher(methodDoc).find()) {
                        if (line.contains(" get") || line.contains(" set") || line.contains(" is") || line.contains(" with")) {
                            /* no nessessary Comment for getter and Setter */
                            continue;
                        }
                        /* There is no text, put a missing doc description error */
                        msg.add("WARNING:" + currentPackage + ".missing.MethodDocText(" + currentFileName + ":"
                                + lineMethod + ")");
                    }

                    /* Check if there are parameters in the method declaration */
                    if (!line.contains("()")) {
                        /* Get all parameters from method declaration */
                        String temp = line.substring(line.indexOf("(") + 1, line.indexOf(")")).replaceAll("<[^\\)]*>", "");
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
                                msg.add("ERROR:" + currentPackage + ".missing.ParamTag(" + currentFileName + ":" + lineMethod + ")");
                            } else if (methodDoc.split("@param " + parameterName + "[ ]+[a-zA-Z*\r]+").length == 1) {
                                /* There is no text after the tag, create warning */
                                msg.add("WARNING:" + currentPackage + ".missing.ParamTagText(" + currentFileName + ":" + lineMethod + ")");
                            }
                        }
                    }

                    /* Check if there is a return type in the method */
                    if (!line.contains("void")) {
                        /* There is no tag and text */
                        if (methodDoc.split("@return").length == 1) {
                            /* There is no tag and no text */
                            msg.add("ERROR:" + currentPackage + ".missing.ReturnTag(" + currentFileName + ":" + lineMethod + ")");
                        } else if (methodDoc.split("@return[ \t]+[a-zA-Z]+").length == 1) {
                            /* There is no text after the tag, create warning */
                            msg.add("WARNING:" + currentPackage + ".missing.ReturnTagText(" + currentFileName + ":" + lineMethod + ")");
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
	private String extractJavaDocComment(List<String> lines, int searchIndex) {

		/* Initialize attribute which describe the range of the doc */
		int beginPos = -1;
		int beginLine = -1;
		int endPos = -1;
		int endLine = -1;

		/* Go back from the searchIndex */
		for (int i = searchIndex; i > 0; i--) {
		    String line = lines.get(i);
		    int pos;
		    if(endPos<0) {
		        pos = line.lastIndexOf("*/");
		        if(pos<0) {
		            continue;
		        }
		        endPos = pos + 2;
		        endLine = i;
		        pos = line.lastIndexOf("/**", endPos);
		    } else {
		        pos = line.lastIndexOf("/**");
		    }
		    if(pos>=0) {
		        beginPos = pos;
		        beginLine = i;
		        break;
		    }
		}
		/* Wrong comment just a /* or there is no comment */
		if(beginPos<0) {
		    return "";
		}
		if(beginLine == endLine) {
		    return lines.get(beginLine).substring(beginPos, endPos);
		}
		
		CharacterBuffer result = new CharacterBuffer();
		for(int i=beginLine;i<=endLine;i++) {
		    if(i == beginLine) {
		        result.with(lines.get(i).substring(beginPos), BaseItem.CRLF);
		    } else if(i == endLine) {
		        result.with(lines.get(i).substring(0, endPos), BaseItem.CRLF);
		    } else {
		        result.with(lines.get(i), BaseItem.CRLF);
		    }
		}
		return result.toString();
	}

	public static ParserEntity createParserEntity(File file, ObjectCondition condition) {
		return new ParserEntity().withFile(file.getAbsolutePath()).withCondition(condition);
	}

	public SimpleKeyValueList<String, SimpleList<ParserEntity>> getPackageList() {
	    SimpleKeyValueList<String, SimpleList<ParserEntity>> result = new SimpleKeyValueList<>();
	    String searchKey = null;
	    SimpleList<ParserEntity> items = null;
	    for (ParserEntity element : packages) {
	        String key = new File(element.getFileName()).getParent();
	        if(searchKey == null || !searchKey.equals(key)) {
	            items = new SimpleList<>();
	            items.add(element);
	            result.put(key, items);
	            searchKey = key;
	            continue;
	        }
	        items.add(element);
	    }
	    return result;
	}
	
	public FileClassModel analyseBounds() {
		String search = "import " + getName();
		SimpleKeyValueList<Clazz, SimpleList<String>> assocs = new SimpleKeyValueList<>();
		ClazzSet set = new ClazzSet();
		fixClassModel();

		for (ParserEntity element : packages) {
			Clazz clazz = element.getClazz();
			SimpleList<SymTabEntry> imports = element.getSymbolEntries(SymTabEntry.TYPE_IMPORT);
			SimpleList<String> assoc = new SimpleList<>();
			for (SymTabEntry item : imports) {
				if (item.getName().startsWith(search)) {
					String ref = item.getName().substring(7);
					ref = ref.substring(ref.lastIndexOf('.') + 1);
					if (!ref.equals("//")) {
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
			if (!assocValue.isEmpty()) {
				add(clazz);
			}
			for (String item : assocValue) {
				Clazz target = (Clazz) this.getChildByName(item, Clazz.class);
				if (target == null) {
					target = set.getClazz(item);
				}
				if (target == null) {
					target = createClazz(item);
				}
				clazz.createBidirectional(target, "use", Association.ONE, "use", Association.ONE);
			}
		}
		SimpleKeyValueList<String, SimpleList<ParserEntity>> packageList = getPackageList();
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
		return this;
	}

	private ParserEntity getFiles(File path, String type, String parent, ObjectCondition condition) {
	    if(path == null || !path.exists()) {
	        return null;
	    }
	    if(path.isFile() && path.getName().endsWith(JAVA_FILE_SUFFIX)) {
            ParserEntity element = createParserEntity(path, condition);
            this.packages.add(element);
            return element;
	    }
		if (path.isDirectory()) {
			File[] items = path.listFiles();
			if (items == null) {
				return null;
			}
			if(parent == null) {
			    parent = "";
			}
			ObjectCondition con = null;
			boolean isRekusive = true;
			if (condition instanceof FeatureCondition) {
				Feature feature = ((FeatureCondition) condition).getFeature(null);
				if (feature != null && RECURSIVE.equalsIgnoreCase(feature.getName())) {
					isRekusive = false;
				}
			}
			if (isRekusive) {
				con = condition;
			}
			for (File file : items) {
				if (file.getName().endsWith(type)) {
					packages.add(createParserEntity(file, con));
				} else if (!file.getName().equalsIgnoreCase("test") && file.isDirectory()) {
					if (!isRekusive) {
						continue;
					}
					getFiles(file, type, parent + path.getName() + ".", condition);
				}
			}
		}
		return null;
	}

	public ClassModel analyseInBoundLinks() {
	    fixClassModel();
		for (int i = 0; i < packages.size(); i++) {
			ParserEntity parserEntity = packages.get(i);
			CharacterBuffer content = parserEntity.getCode().getContent();
			Clazz clazz = parserEntity.getClazz();
			add(clazz);
			for (int p = 0; p < packages.size(); p++) {
				if (p == i) {
					continue;
				}

				Clazz targetClazz = packages.get(p).getClazz();
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
		return this;
	}

	public int analyseMcCabe(Object item) {
		String methodBody = null;
		Method owner = null;
		if (item instanceof Method) {
			owner = (Method) item;
			methodBody = owner.getBody();
			methodBody = methodBody.toLowerCase();
			methodBody = methodBody.replace("\n", "");
			methodBody = methodBody.replace("\t", "");
			methodBody = methodBody.replace(" ", "");
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
		GraphMember owner = null;
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
			Method m =(Method) item;
			owner = m;
			methodBody = m.getBody();
		} else if(item instanceof SourceCode) {
			SourceCode s = (SourceCode) item;
			owner = s;
			methodBody = s.getContent().toString();
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

	@Override
	public HTMLEntity dumpHTML(String diagramName, boolean... write) {
		finishReverseEngineering();
		return super.dumpHTML(diagramName, write);
	}

	public static final FeatureCondition createFeature(String key) {
		FeatureCondition condition = new FeatureCondition();
		Feature item = GraphUtil.createFeature(key);
		condition.withFeature(item);
		return condition;
	}
	
	@Override
	public boolean fixClassModel() {
	    boolean result = super.fixClassModel();
	    for(ParserEntity entity : packages) {
	        if(entity.isContent()) {
	           continue; 
	        }
	        CharacterBuffer content = FileBuffer.readFile(entity.getFileName());
	        try {
	            entity.parse(content);
	            add(entity.getClazz());
	        } catch (SimpleException e) {
	            result = false;
	            if (logger != null) {
	                logger.error(this, "parse error", e.getErrorMessage());
	            }
	        }
	    }
	    return result;
	}

	public SourceCode analyse(SourceCode code) {
		if(code == null) {
			return code;
		}
		this.analyseLoC(code);
		this.analyseCheckValue(code);
		return code;
	}

	private void analyseCheckValue(Object code) {
		if(code instanceof SourceCode) {
			SourceCode s = (SourceCode) code;
			GraphMetric metric = GraphMetric.create(s);
			metric.withCRC(SHA1.value(s.getContent().toBytes(true)).toString());
		}
	}

    public FileClassModel withLogger(NetworkParserLog value) {
        this.logger = value;
        return this;
    }
}
