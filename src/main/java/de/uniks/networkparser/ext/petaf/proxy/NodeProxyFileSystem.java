package de.uniks.networkparser.ext.petaf.proxy;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.FileWatcher;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class NodeProxyFileSystem extends NodeProxy implements ObjectCondition {
	private String fileName;
	private FileWatcher nodeProxyFileWatcher;
	private boolean fullModell;

	public void enableGitFilter() {
		this.withFilter(this);
	}

	NodeProxyFileSystem() {
		withOnline(true);
	}

	public NodeProxyFileSystem(String fileName) {
		if (fileName != null) {
			this.fileName = fileName;
			withOnline(true);
		}
	}

	public boolean startModelDistribution() {
		this.withFullModell(false);
		return this.space.startModelDistribution(true);
	}

	public boolean isFullModell() {
		return fullModell;
	}

	public NodeProxyFileSystem withFullModell(boolean value) {
		this.fullModell = value;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	protected boolean sending(Message msg) {
		boolean result = super.sending(msg);
		if (this.space == null) {
			return false;
		}
		try {
			FileBuffer file = new FileBuffer();
			file.withFile(fileName);
			file.createFile();
			int len = 0;
			if (this.fullModell) {
				NodeProxyModel model = getSpace().getModel();
				Object modell = model.getModel();
				BaseItem value = this.space.encode(modell, null);
				if (value == null) {
					return false;
				}
				String data = value.toString();
				len = data.length();
				file.write(FileBuffer.OVERRIDE, data);
			} else if (msg != null) {
				String data = this.space.convertMessage(msg) + BaseItem.CRLF;
				len = data.length();
				file.write(FileBuffer.APPEND, data);
			}
			setSendTime(len);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public BaseItem load(Object root) {
		BaseItem readBaseFile = FileBuffer.readBaseFile(this.fileName);
		if (this.space != null && readBaseFile != null) {
			IdMap map = space.getMap();
			if (map != null) {
				if (this.isFullModell()) {
					this.space.withInit(false);
					Object model = map.decode(readBaseFile, root, null);
					this.space.withInit(true);
					/* Check if NodeProxyModel exists */
					this.space.createModel(model);
					return readBaseFile;
				}
				/* Maybe ChangeMessages */
				if (readBaseFile instanceof EntityList) {
					try {
						EntityList list = (EntityList) readBaseFile;
						for (int i = 0; i < list.sizeChildren(); i++) {
							BaseItem singleMessage = list.getChild(i);
							Object message = map.decode(singleMessage);
							if (message instanceof ChangeMessage) {
								ChangeMessage changeMsg = (ChangeMessage) message;
								if (map.getObject(changeMsg.getId()) == null) {
									/* Try to Use old Root */
									Object entity = changeMsg.getEntity();
									if (entity != null) {
										if (entity instanceof String || entity.getClass().equals(root.getClass())) {
											map.put(changeMsg.getId(), root, false);
										} else {
											map.put(changeMsg.getId(), changeMsg.getEntity(), false);
										}
									} else {
										map.put(changeMsg.getId(), changeMsg.getEntity(), false);
									}
								}
								changeMsg.withSpace(this.space);
								changeMsg.runTask();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return readBaseFile;
				}
				/* So May be a SingleChange or FullDatamodel and wrong Config */
				this.space.withInit(false);
				Object model = map.decode(readBaseFile, root, null);
				this.space.withInit(true);
				/* Check if NodeProxyModel exists */
				this.space.createModel(model);
				return readBaseFile;
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
	protected boolean startProxy() {
		withType(NodeProxy.TYPE_INOUT);
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

	@Override
	public boolean update(Object value) {
		return true;
	}
}
