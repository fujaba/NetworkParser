package de.uniks.networkparser.ext.io;

import java.util.Map;

import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.ext.GitRevision;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.graph.GraphMetric;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class Coveralls implements SendableEntityCreator{
	public String jacocoReportPath = "build/test-results/jacoco.xml";
    public String token;
    public String projectDir;
    public String sourceDir = "src/main/java/";
    public String service = "GitLab";;
    public String url = "https://coveralls.io/api/v1/jobs";
    public String reportFile;
    public Object git;
    private FileClassModel model;
	    
    public static final String REPORTPATH="jacocoReportPath";
    public static final String TOKEN="token";
    public static final String PROJECTDIR="projectDir";
    public static final String SOURCEDIR="sourceDir";
    public static final String SERVICE="servcie";
    public static final String REPORTFILE="reportFile";
    public static final String GIT="GIT";
    public static final String URL="url";
    public static final String[] properties = new String[] {REPORTPATH, TOKEN,PROJECTDIR, SOURCEDIR, SERVICE, REPORTFILE, GIT};
	
    @Override
    public Object getSendableInstance(boolean prototyp) {return new Coveralls();}
    
    @Override
    public String[] getProperties() {return properties;}

    @Override
    public Object getValue(Object entity, String attribute) {
        if(!(entity instanceof Coveralls)) {
            return null;
        }
        Coveralls params = (Coveralls) entity;
        if(REPORTPATH.equalsIgnoreCase(attribute)) return params.jacocoReportPath;
        if(TOKEN.equalsIgnoreCase(attribute)) return params.token;
        if(PROJECTDIR.equalsIgnoreCase(attribute)) return params.projectDir;
        if(SOURCEDIR.equalsIgnoreCase(attribute)) return params.sourceDir;
        if(SERVICE.equalsIgnoreCase(attribute)) return params.service;
        if(URL.equalsIgnoreCase(attribute)) return params.url;
        if(GIT.equalsIgnoreCase(attribute)) return params.git;
        if(REPORTFILE.equalsIgnoreCase(attribute)) {return params.reportFile;}
        return null;
    }

    @Override
    public boolean setValue(Object entity, String attribute, Object value, String type) {
        if(!(entity instanceof Coveralls)) {
            return false;
        }
        Coveralls params = (Coveralls) entity;
        if(REPORTPATH.equalsIgnoreCase(attribute)) {
            params.jacocoReportPath = "" + value;
            return true;
        }
        if(TOKEN.equalsIgnoreCase(attribute)) {
            params.token = "" + value;
            return true;
        }
        if(PROJECTDIR.equalsIgnoreCase(attribute)) {
            params.projectDir = "" + value;
            return true;
        }
        if(SOURCEDIR.equalsIgnoreCase(attribute)) {
            params.sourceDir = "" + value;
            return true;
        }
        if(SERVICE.equalsIgnoreCase(attribute)) {
        	params.service = "" + value;
            return true;
        }
        if(REPORTFILE.equalsIgnoreCase(attribute)) {
            params.reportFile = ""+value;
            return true;
        }
        if(URL.equalsIgnoreCase(attribute)) {
            params.url = ""+value;
            return true;
        }
        if(GIT.equalsIgnoreCase(attribute)) {
            params.git = (GitRevision) value;
            return true;
        }
        return false;
    }

    
    public Coveralls withMap(Map<?, ?> params) {
        if(params == null) {
            return this;
        }
        for(Object key : params.keySet()) {
            Object value = params.get(key);
            this.setValue(this, ""+key, value, SendableEntityCreator.NEW);
        }
        return this;
    }
	
    public void execute(Object param) {
    	withMap((Map<?, ?>) param);
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.put("repo_token", token);
        jsonObject.put("service_name", service);
        JsonArray files = new JsonArray();
        jsonObject.put("source_files", files);

        model = new FileClassModel("");
        
        XMLEntity report = new XMLEntity().withValue(new FileBuffer().withFile(jacocoReportPath));
        for(int i=0;i<report.sizeChildren();i++) {
        	XMLEntity packageEntry = (XMLEntity) report.getChild(i);
        	for(int s=0;s<packageEntry.sizeChildren();s++) {
        		XMLEntity sourcefile = (XMLEntity) packageEntry.getChild(s);
        		if("sourcefile".equalsIgnoreCase(sourcefile.getTag())) {
        			JsonObject element = parseSourceElement(sourceDir+packageEntry.getValue("name")+"/"+sourcefile.getValue("name"), sourcefile);
        			files.add(element);
        		}
        	}
        }
        addGitInfos(jsonObject);
        if( reportFile != null &&  reportFile.length()>0) {
        	FileBuffer.writeFile( reportFile, jsonObject.toString(2));
        }
        if( url != null) {
        	Map<String, Object> map = new SimpleKeyValueList<String, Object>();
        	map.put("json_file", jsonObject);
        	NodeProxyTCP.postMultiHTTP(url, map);
        }
    }
    
    private void addGitInfos(JsonObject result) {
    	if(this.git != null) {
    		JsonObject gitJson = result.createChild("git");
    		JsonObject headJson = gitJson.createChild("head");
    			
    		headJson.put("id", ReflectionLoader.getField(git, "hash"));
    		headJson.put("author_name", ReflectionLoader.getField(git, "author"));
    		headJson.put("author_email", ReflectionLoader.getField(git, "authorEMail"));
    		headJson.put("committer_name", ReflectionLoader.getField(git, "committer"));
    		headJson.put("committer_email", ReflectionLoader.getField(git, "committerEMail"));
    		headJson.put("message", ReflectionLoader.getField(git, "message"));
    		gitJson.put("branch", ReflectionLoader.getField(git, "currentBranch"));
    		JsonArray remotes = new JsonArray();
    		gitJson.put("remotes", remotes);
    		JsonObject remote = new JsonObject();
    		remotes.add(remote);
    		remote.put("name", "origin");
    		remote.put("url", ReflectionLoader.getField(git, "remote"));
    	}
    }
    
    public JsonObject parseSourceElement(String name, XMLEntity sourceFile) {
    	if (name == null) {
			return null;
		}
    	FileBuffer buffer = new FileBuffer().withFile(projectDir+name);
    	if(!buffer.exists()) {
    		return null;
    	}
    	SourceCode analyse = model.analyse(new SourceCode().withContent(buffer.readAll()));
    	GraphMetric metric = GraphMetric.create(analyse);
    	
    	JsonObject json = new JsonObject();
    	json.put("name", name);
    	json.put("source_digest", metric.getCRC());
    	
    	int line=metric.getFullLines();

    	Short[] coverages=new Short[line];
    	for(int i = 0;i<sourceFile.sizeChildren();i++) {
    		XMLEntity element = (XMLEntity) sourceFile.getChild(i);
    		if("line".equalsIgnoreCase(element.getTag())) {
    			int nr = element.getInt("nr");
    			coverages[nr -1] = element.getInt("ci") > 0 ? (short)1: (short)0;
    		}
    	}
    	json.put("coverage", coverages);
        return json;
    }
}
