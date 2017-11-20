package de.uniks.networkparser.ext.petaf.proxy;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.FileWatcher;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;
import de.uniks.networkparser.interfaces.BaseItem;

public class NodeProxyFileSystem extends NodeProxy {
    private String fileName;
    private FileWatcher nodeProxyFileWatcher;
    private boolean fullModell;

    NodeProxyFileSystem() {
        withOnline(true);
    }

    public NodeProxyFileSystem(String fileName) {
        this.fileName = fileName;
        withOnline(true);
    }
    
    public boolean isFullModell() {
		return fullModell;
	}
    
    public NodeProxyFileSystem withFullModell(boolean value) {
    	this.fullModell = value;
    	return this;
    }

    @Override
    protected boolean sending(Message msg) {
        boolean result = super.sending(msg);
//		String filepath="";
        try {
        	FileBuffer file = new FileBuffer();
        	file.withFile(fileName);
        	file.createFile();
            FileOutputStream networkFile = new FileOutputStream(String.valueOf(file));

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(networkFile));
            int len=0;
            if(this.fullModell) {
            	NodeProxyModel model = getSpace().getModel();
            	Object modell = model.getModell();
            	BaseItem value = this.space.encode(modell, null);
            	String data = value.toString();
            	len = data.length();
            	out.write(data);
            } else if(msg != null){
	            String data = this.space.convertMessage(msg);
	            len = data.length();
	            out.append(data);
            }
            out.close();
            setSendTime(len);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    
    public BaseItem load(Object root) {
    	BaseItem readBaseFile = FileBuffer.readBaseFile(this.fileName);
    	if(this.space != null && readBaseFile != null) {
    		IdMap map = space.getMap();
    		if(map != null) {
    			map.decode(readBaseFile, root, null);
    		}
    	}
    	return readBaseFile;
    }

    @Override
    public String getKey() {
        return fileName;
    }

    @Override
    public boolean close() {
        nodeProxyFileWatcher.close();
        return true;
    }

    @Override
    protected boolean initProxy() {
        withType(NodeProxyType.INOUT);
        nodeProxyFileWatcher = new FileWatcher().init(this, this.fileName);
        return true;
    }

    @Override
    public boolean isSendable() {
        return false;
    }

    @Override
    public Object getSendableInstance(boolean reference) {
        return new NodeProxyFileSystem(null);
    }
}
