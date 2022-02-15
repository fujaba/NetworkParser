package de.uniks.networkparser.ext.http;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.Manifest;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class ImpressumService.
 *
 * @author Stefan
 */
public class ImpressumService implements Condition<HTTPRequest> {
    private HTTPRequest routing;
    private SimpleUpdateListener listener;
    private String tag = "Impressum";

    /**
     * Instantiates a new impressum service.
     */
    public ImpressumService() {
        routing = HTTPRequest.createRouting("/impressum");
        routing.withTag(tag);
        routing.withUpdateCondition(this);
    }
    
    /**
     * Update.
     *
     * @param value the value
     * @return true, if successful
     */
    @Override
    public boolean update(HTTPRequest value) {
        if(HTTPRequest.HTTP_TYPE_GET.equalsIgnoreCase(value.getHttp_Type())) {
            return this.showDefault(value);
        }
        return false;
    }

    /**
     * With tag.
     *
     * @param value the value
     * @return the impressum service
     */
    public ImpressumService withTag(String value) {
        this.tag = value;
        return this;
    }
    
    
    private boolean showDefault(HTTPRequest value) {
        try {
            SimpleKeyValueList<String, Manifest> manifests = new SimpleKeyValueList<String, Manifest>();
            Enumeration<URL> resources=getClass().getClassLoader().getResources("META-INF");
            while(resources.hasMoreElements()) {
                URL metaInf=resources.nextElement();
                if("jar".equals(metaInf.getProtocol())) {
                    visitManifestsJar(metaInf, manifests);
                }
            }

            // Create Report
            HTMLEntity entity = new HTMLEntity();
            XMLEntity headerTag = entity.createChild("div", "class", "header");
            XMLEntity backBtn = headerTag.createChild("a", "href", "/");
            backBtn.createChild("div", FileBuffer.readResource("back.svg", GraphUtil.class).toString());
            backBtn.createChild("div", "Zur&uuml;ck");

            entity.createChild("h1", "Dependencies");
            for(int i=0;i<manifests.size();i++) {
                entity.createChild("h2", manifests.getKeyByIndex(i));
                entity.createChild("div", manifests.getValueByIndex(i).getFullVersion("<br/>", 
                        "*-Package", "X-Compile-*", "*-Resource",
                        "Require-Capability", "Sealed", "Tool", "*-ClassPath").toString()).add("class",  "text");
            }
            value.withContent(entity);
            
            if(listener != null) {
                listener.update(new SimpleEvent(this, HTTPRequest.HTTP_TYPE_GET, value));
            }
            value.write(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void visitManifestsJar(URL url, SimpleKeyValueList<String, Manifest> manifests) {
        String name = url.getFile();
        if(name.indexOf("!")>0) {
            name = name.substring(0, name.indexOf("!"));
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(URI.create(name).toURL().getFile());
        } catch (IOException e) {
        }
        if(jarFile != null) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                
                if(entryName.endsWith(".MF") && entryName.startsWith("META-INF/MANIFEST")) {
                    try {
                        CharacterBuffer readFile = FileBuffer.readResource(jarFile.getInputStream(entry));
                        String fileName = "Libary";
                        if(entryName.equals("META-INF/MANIFEST.MF")) {
                            int pos = name.indexOf(".jar");
                            if(pos>0) {
                                int start = name.lastIndexOf("/", pos)+1;
                                fileName = name.substring(start, pos + 4); 
                            }
                        }else {
                            fileName = entryName.substring(18, entryName.length()-3);
                        }
                        Manifest manifest = Manifest.create(readFile);
                        manifests.put(fileName, manifest);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Gets the routing.
     *
     * @return the routing
     */
    public HTTPRequest getRouting() {
        return routing;
    }
    
    /**
     * With listener.
     *
     * @param updateListener the update listener
     * @return the impressum service
     */
    public ImpressumService withListener(SimpleUpdateListener updateListener) {
        this.listener = updateListener;
        return this;
    }

}
