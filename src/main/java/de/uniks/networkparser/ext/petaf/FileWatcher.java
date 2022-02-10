package de.uniks.networkparser.ext.petaf;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.io.FileBuffer;
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
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyFileSystem;

/**
 * FileWatcher for Changes on Storage 
 * @author Stefan Lindel
 */
public class FileWatcher implements Runnable {
	protected NodeProxy proxy;
	protected String fileName;
	private boolean runTask = true;
	private WatchService watcher;
	private NetworkParserLog logger;
	private long lastChange = -1;
	private Space space;

	public FileWatcher init(NodeProxyFileSystem owner, String fileName) {
		this.proxy = owner;
		this.fileName = fileName;

		return this;
	}

	public void run() {
		if (space == null) {
			return;
		}
		if (Os.isReflectionTest()) {
			return;
		}
		while (this.runTask) {
			if (watcher != null) {
				searchNIO();
			} else {
				File file = new File(this.fileName);
				long last = file.lastModified();
				if (this.lastChange < 1) {
					this.lastChange = last;
				} else {
					if (last != lastChange) {
						this.lastChange = last;
						if(logger != null) {
						    logger.info(this, "run", "New (version of) file " + fileName + " detected");
						}
						CharacterBuffer buffer = FileBuffer.readFile(fileName);
						space.getMap().decode(buffer);
					}
				}
			}
		}
	}

	private boolean searchNIO() {
		WatchKey watchKey = null;
		if (Os.isReflectionTest()) {
			return false;
		}
		try {
			watchKey = watcher.take();
		} catch (InterruptedException e) {
		}
		if (watchKey == null) {
			return true;
		}
		for (WatchEvent<?> event : watchKey.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			if (kind == OVERFLOW) {
				continue;
			}
			if (kind == ENTRY_CREATE) {
				/* if its a new json file, read it */
				Path filepath = (Path) event.context();
				if(logger != null) {
                    logger.info(this, "run", "New (version of) file " + filepath.toFile() + " detected");
                }
				CharacterBuffer buffer = FileBuffer.readFile(filepath.toFile());
				space.getMap().decode(buffer);
			}
			if (kind == ENTRY_MODIFY) {
				/* do I have a buf for this one, then read */
				Path filepath = (Path) event.context();
				CharacterBuffer buffer = FileBuffer.readFile(filepath.toFile());
				space.getMap().decode(buffer);
			}
			if (kind == ENTRY_DELETE) {
				continue;
			}
		}
		return true;
	}

	public boolean initNIOFileWatcher() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			File file = new File(this.fileName);
			if(file.exists() == false || file.isDirectory() == false) {
				return false;
			}
			Path dirPath = file.toPath();
			dirPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void close() {
		this.runTask = false;
	}
}
