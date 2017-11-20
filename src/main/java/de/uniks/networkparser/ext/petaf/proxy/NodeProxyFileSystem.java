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
import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class NodeProxyFileSystem extends NodeProxy {
	private String fileName;
	private FileWatcher nodeProxyFileWatcher;
	private boolean fullModell;

	public void enableGitFilter() {
		this.withFilter(new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				// Filter if it should be written on disk
				System.out.println(value);
//				if (value instanceof ChangeMessage) {
//					SimpleList<NodeProxy> receiver = ((ChangeMessage) value).getReceived();
//					if (receiver != null && receiver.contains(this) ) {
//						// already saved to disk
//						System.out.println("Already saved");
//						return false;
//					}
//				}else {
//					System.out.println("No Change");
//				}
				// eigener change?

				return true;
			}
		});
	}

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
//		if (!this.getFilter().update(msg)) {
//			// dont't update
//			return result;
//		}
		try {
			FileBuffer file = new FileBuffer();
			file.withFile(fileName);
			file.createFile();
			int len = 0;
			if (this.fullModell) {
				NodeProxyModel model = getSpace().getModel();
				Object modell = model.getModell();
				BaseItem value = this.space.encode(modell, null);
				String data = value.toString();
				len = data.length();
				file.write(data, false);
			} else if (msg != null) {
				String data = this.space.convertMessage(msg);
				len = data.length();
				file.write(data, true);
			}
			file.close();
			setSendTime(len);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public BaseItem load(Object root) {
		BaseItem readBaseFile = FileBuffer.readBaseFile(this.fileName);
		if (this.space != null && readBaseFile != null) {
			IdMap map = space.getMap();
			if (map != null) {
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
