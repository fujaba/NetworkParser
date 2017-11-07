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

    NodeProxyFileSystem() {
        withOnline(true);
    }

    public NodeProxyFileSystem(String fileName) {
        this.fileName = fileName;
        withOnline(true);
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
            String data = this.space.convertMessage(msg);
            out.write(data);
            out.close();
            setSendTime(data.length());
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
