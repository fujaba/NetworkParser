package de.uniks.networkparser.test.ant.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SourceItem {
	public static final String CRLF="\r\n";
	private String packageString;
	private String header;
	private String comment;
	private Imports imports= new Imports();
//	private String body;
	private File file;
	private String definePart;
	private enum PART {PACKAGE, COMMENT, IMPORTS, HEADER, DEFINE, BODY};
	public static final String STARTCOMMENT="/*" +CRLF;
	public static final String STARTCOMMENTEXT="/**" +CRLF;
	private String projectName;
	private Body body= new Body();

	public SourceItem(File file){
		this.file=file;
		if(this.file.exists()){
			readFile();
		}
	}
	private void readFile() {
		BufferedReader in = null;
		try {
			FileInputStream networkFile = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(networkFile, "UTF-8");
			in = new BufferedReader(isr);

			MyStringBuilder packageBuilder= new MyStringBuilder();
			MyStringBuilder headerBBuilder= new MyStringBuilder();
			MyStringBuilder commentBuilder= new MyStringBuilder();
			PART typ=PART.HEADER;
			FilePart activ=headerBBuilder;
			String line=in.readLine();

			while (line!=null){
				if(line.startsWith("package ")){
					packageBuilder.append(line+CRLF);
					line=in.readLine();
					continue;
				}
				if(line.startsWith("import ")){
					imports.append(line+CRLF);
					line=in.readLine();
					continue;
				}
				if(typ==PART.HEADER && line.indexOf('{')>=0){
					definePart = line+CRLF;
					line=in.readLine();
					typ = PART.BODY;
					activ = body;
					continue;
				}
				if(typ==PART.HEADER){
					if(line.indexOf("/*")>=0 && commentBuilder.length()<1){
						typ = PART.COMMENT;
						activ = commentBuilder;
					}
				}

				activ.append(line+CRLF);

				if(typ!=PART.BODY){
					if(line.indexOf("*/")>=0){
						typ = PART.HEADER;
						activ = headerBBuilder;
					}
				}
				line=in.readLine();
			}
			this.body.finish();
			this.packageString=packageBuilder.toString();
			this.imports.finish();
			this.header=headerBBuilder.toString().trim();
			this.comment=commentBuilder.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public SourceItem withProjectName(String value){
		projectName = value;
		return this;
	}

	public String getCustomComment(){
		String custom = "";
		if(comment.length()<1){
			int pos =header.indexOf("//");
			if(pos>0){
				custom = comment.substring(pos);
			}
		} else if(comment.startsWith(STARTCOMMENT)){
			custom = comment.substring(STARTCOMMENT.length());
		} else if(comment.startsWith(STARTCOMMENTEXT)){
			custom = comment.substring(STARTCOMMENTEXT.length());
		}

		if(custom.length()>0){
			int pos = custom.indexOf("\n");
			if(pos>0){
				custom = custom.substring(0, pos-1).trim();
				if(custom.equals(projectName)){
					return "";
				}
			}
			return custom;
		}
		return custom;
	}

	public void write(){
		OutputStreamWriter writer = null;
		try {
			FileOutputStream networkFile = new FileOutputStream(file);
			writer = new OutputStreamWriter(networkFile, "UTF-8");
//			PrintWriter pw = new PrintWriter(ps);
//			String template = "%packageString%" +CRLF+CRLF+ "%comment%" +CRLF+CRLF+ "%header%%body%";
//			template.replaceAll("%packageString%", packageString);
//			template.replaceAll("%comment%", comment);
//			template.replaceAll("%header%", header);
//			template.replaceAll("%body%", body);

			writer.write(packageString);
			if(packageString.length()>0){
				writer.write(CRLF);
			}
			writer.write(comment.trim());
			if(comment.length()>0){
				writer.write(CRLF);
			}
			writer.write(imports.toString().trim());
			if(imports.length()>0){
				writer.write(CRLF);
			}
			writer.write(header.trim());
			if(header.length()>0){
				writer.write(CRLF);
			}

			writer.write(CRLF);
			writer.write(definePart);
			writer.write("\t" +body.toString().trim()+CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	public String getBody(){
		return body.toString();
	}

	public Body getMethods(){
		return body;
	}

	public String getFileName(){
		return file.getAbsolutePath();
	}
	public String getComment(){
		return comment;
	}
	public boolean changeComment(String value){
		if(!value.equals(comment)){
			this.comment = value;
			return true;
		}
		return false;
	}

	public boolean skipComment() {
		if(comment.length()<1){
			return false;
		}
		return !comment.startsWith(STARTCOMMENT+ " " +projectName+CRLF);
	}
	public int getLineOfCode() {
		return body.getLinesOfCode();
	}
	public boolean changeBody(SourceItem source) {
		boolean changed=false;
		String newBody = source.getBody();
		if(body==null||!body.toString().trim().equals(newBody.trim())){
			this.body = new Body().allAll(newBody);
			changed = true;
		}
		if(definePart==null||!definePart.trim().equals(source.getDefinePart().trim())){
			this.definePart = source.getDefinePart();
			changed = true;
		}
		String newComment = source.getComment();
		if(comment==null||!comment.equals(newComment)){
			this.comment = newComment;
			changed = true;
		}
		return changed;
	}
	public String get(PART property){
		switch (property) {
		case PACKAGE:
			return packageString;
		case COMMENT:
			return comment;
		case HEADER:
			return header;
		case IMPORTS:
			return imports.toString();
		case DEFINE:
			return definePart;
		default:
			break;
		}
		return null;
	}

	public String getDefinePart(){
		return definePart;
	}

	public Imports getImports(){
		return imports;
	}

	public String getShortPackageName(){
		String simple = packageString.substring(packageString.indexOf(" ")+1).trim();
		if(simple.endsWith(";")){
			return simple.substring(0, simple.length()-1);
		}
		return simple;
	}

	public void initFile(SourceItem source) {
		this.packageString = source.get(PART.PACKAGE);
		this.header = source.get(PART.HEADER);
		this.comment = source.get(PART.COMMENT);
		this.imports = source.getImports();
		this.definePart = source.get(PART.DEFINE);
	}
}
