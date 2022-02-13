package de.uniks.networkparser.ext.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.Manifest;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.xml.HTMLEntity;

public class ImpressumService implements Condition<HTTPRequest> {
    private HTTPRequest routing;
    private SimpleUpdateListener listener;

    public ImpressumService() {
        routing = HTTPRequest.createRouting("/impressum");
        routing.withUpdateCondition(this);
    }
    @Override
    public boolean update(HTTPRequest value) {
        if(HTTPRequest.HTTP_TYPE_GET.equalsIgnoreCase(value.getHttp_Type())) {
            return this.showDefault(value);
        }
        return false;
    }

    private boolean showDefault(HTTPRequest value) {
        try {
            Enumeration<URL> resources = IdMap.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            HTMLEntity entity = new HTMLEntity();
            entity.createTag("h1", "Dependencies");
            
            while(resources.hasMoreElements()) {
                URL manifestURL = resources.nextElement();
                CharacterBuffer readFile = FileBuffer.readResource((InputStream)manifestURL.getContent());
                String fileName = "Libary";
                String temp = manifestURL.toString();
                int pos = temp.indexOf(".jar");
                if(pos>0) {
                    int start = temp.lastIndexOf("/", pos)+1;
                    fileName = temp.substring(start, pos + 4); 
                }
                entity.createTag("h2", fileName);
                Manifest manifest = Manifest.create(readFile);
                entity.createTag("div", manifest.getFullVersion("<br/>", 
                        "*-Package", "X-Compile-*", "*-Resource",
                        "Require-Capability", "Sealed", "Tool").toString()).add("class",  "text");
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

    public HTTPRequest getRouting() {
        return routing;
    }
    public ImpressumService withListener(SimpleUpdateListener updateListener) {
        this.listener = updateListener;
        return this;
    }

}
