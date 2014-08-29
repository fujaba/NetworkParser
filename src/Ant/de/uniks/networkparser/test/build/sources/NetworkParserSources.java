package de.uniks.networkparser.test.build.sources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.graph.GraphIdMap;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.YUMLConverter;

public class NetworkParserSources {
	private int change;
	private int skip;
	private int skipNotDir;
	private int ok;
	private int errors;
	private int lineofCode;
	private ArrayList<String> emptyMethods= new ArrayList<String>(); 
	private HashMap<String, String> customItems;
	private int thirdparty;

	public void copyFile(String sourcePath, String targetPath, boolean createDirectory, boolean createFiles){
		init();
		copiedFile(sourcePath, targetPath, createDirectory, createFiles);
		printResult();
	}
	
	public HashSet<SourceItem> getSources(String sourcePath){
		HashSet<SourceItem> items= new HashSet<SourceItem>(); 

		init();
		getSources(sourcePath, items);
		
		return items;
	}
	
	private void getSources(String sourcePath, HashSet<SourceItem> items){
		File path= new File(sourcePath);
		File[] listFiles = path.listFiles();
		for (File child : listFiles){
			if(child.isDirectory()){
				getSources(sourcePath+child.getName()+ "/", items);
			}else{
				if(!child.getAbsolutePath().endsWith(".java")){
					continue;
				}
				
				SourceItem source= new SourceItem(child);
				items.add(source);
//				copiedFile(child, targetPath, createFiles);
			}
		}
	}
	
	public String getPackageGraph(HashSet<SourceItem> items){
		GraphList list= new GraphList().withTyp(GraphIdMap.CLASS);
		for (SourceItem source : items){
			String packageName = source.getShortPackageName();
			for (String item : source.getImports().getPackages()){
				list.withEdge(packageName, item);
				list.toString();
			}
		}
		return YUMLConverter.URL+list.toString();
	}
	
	private void copiedFile(String sourcePath, String targetPath, boolean createDirectory, boolean createFiles){
		File path= new File(sourcePath);
		File[] listFiles = path.listFiles();
		for (File child : listFiles){
			if(child.isDirectory()){
				copiedFile(sourcePath+ "/" +child.getName(), targetPath+ "/" +child.getName(), createDirectory, createFiles);
			}else{
				copiedFile(child, targetPath, createDirectory, createFiles);
			}
		}
	}
	
	public void copiedFile(File file, String targetPath, boolean createDirectory, boolean createFiles){
		if(!file.getAbsolutePath().endsWith(".java")){
			return;
		}
		File targetFile= new File(targetPath+ "/" +file.getName());
		boolean created=false;
		if(!targetFile.exists()){
//			System.out.println("File not exists: " +targetFile.getAbsolutePath());
			if(!targetFile.getParentFile().exists() && !createDirectory){
//				System.out.println("Skip for Directory not exists");
				skip++;
				skipNotDir++;
				return;
			}
			if(!createFiles){
				skip++;
				return;
			}
			try {
				targetFile.getParentFile().mkdirs();
				targetFile.createNewFile();
				created =true;
			} catch (IOException e) {
//				e.printStackTrace();
				errors++;
				return;
			}
		}
		
		
		SourceItem source= new SourceItem(file);
		for (MethodItem m :  source.getMethods().getItems()){
			if(m.getLinesOfCode()<1 && m.getName().length()>0){
				System.out.println("Empty Method: " +m.getName()+ ":" +file.getName());
			}
		}
		SourceItem target= new SourceItem(targetFile);
		if(created){
			target.initFile(source);
		}
		lineofCode += source.getLineOfCode();
		if(target.changeBody(source)){
			System.out.println("File: " +source.getFileName()+ " does not match " +target.getFileName());
			target.write();
			change++;
		}else{
			ok++;
		}
	}

	private void init(){
		change = 0;
		skip = 0;
		skipNotDir = 0;
		ok=0;
		lineofCode = 0;
		thirdparty = 0;
		customItems= new HashMap<String, String>();
	}
	
	private void printResult(){
		System.out.println(skip+ " Uebersprungen");
		System.out.println(thirdparty+ " Andere Projekte");
		System.out.println(change+ " Aenderungen");
		System.out.println(errors+ " Dateien ignoriert, da der Pfad nicht erstellt war");
		System.out.println(skipNotDir+ " Skip for Directory not exists");
		System.out.println(ok+ " OK");
		System.out.println("Line of Code: " +lineofCode);
		if(customItems.size()>0){
			System.out.println("Custom Ids (" +customItems.size()+ "):");
			for (Iterator<Entry<String, String>> iterator = customItems.entrySet().iterator();iterator.hasNext();){
				Entry<String, String> item = iterator.next();
//				System.out.println(item.getKey()+ " (" +item.getValue()+ ")");
				System.out.println(item.getKey());
			}
		}
	}

	public void createComment(String commentPath, String sourcePath, String projectName){
		File path= new File(commentPath);
		SourceItem commentFile= new SourceItem(path);
		init();
		createdComment(sourcePath, commentFile, projectName);
		printResult();
	}
	private void createdComment(String sourcePath, SourceItem commentFile, String projectName){
		File path= new File(sourcePath);
		File[] listFiles = path.listFiles();
		for (File child : listFiles){
			if(child.isDirectory()){
				createdComment(sourcePath+child.getName()+ "/", commentFile, projectName);
			}else{
				if(!child.getAbsolutePath().endsWith(".java")){
					continue;
				}
 				SourceItem source= new SourceItem(child).withProjectName(projectName);
 				lineofCode += source.getLineOfCode();
 				String customComment = source.getCustomComment();
 				if(customComment.length()>0){
 					customItems.put(customComment, source.getFileName());
 					System.out.println("Thirdparty-Source: " +source.getFileName());
 					thirdparty++;
 					continue;
 				}
				if(source.skipComment()){
					skip++;
					continue;
				}
				if(source.changeComment(commentFile.getComment())){
					System.out.println(source.getFileName());
					change++;
					
					source.write();
				}else{
					ok++;
				}
			}
		}
	}
}
