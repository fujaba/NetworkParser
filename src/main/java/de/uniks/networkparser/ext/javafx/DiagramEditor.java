package de.uniks.networkparser.ext.javafx;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.gui.Editor;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class DiagramEditor extends SimpleShell implements Editor {
	private final static String CRLF = "\r\n";
	private WebView browser;
	private WebEngine webEngine;
	private Editor logic;

	@Override
	protected Parent createContents(FXStageController controller, Parameters args) {
		controller.withTitle("ClassdiagrammEditor");
		controller.withSize(900, 600);

		this.enableError("errors");
		String value = args.getNamed().get("logic");
		if(value != null) {
			try {
				Class<?> clazz = Class.forName(value);
				Object editor = clazz.newInstance();
				if(editor instanceof Editor) {
					this.logic = (Editor) editor;
				}
			} catch (RuntimeException e) {
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
		if(this.logic != null) {
			controller.withIcon(getIcon());
		}

		browser = new WebView();
		webEngine = browser.getEngine();

		SimpleKeyValueList<String, String> map = getParameterMap();
		StringBuilder content = new StringBuilder("<html><head>" + CRLF);

		String body = "</head><body>" + CRLF
				+ "<script language=\"Javascript\">classEditor = new ClassEditor(\"board\");</script></body></html>";
		if (map.containsKey("export")) {
			content.append("<script src=\"drawer.js\"></script>" + CRLF);
			content.append("<script src=\"graph.js\"></script>" + CRLF);
			content.append("<link href=\"diagramstyle.css\" rel=\"stylesheet\" type=\"text/css\">" + CRLF);
			content.append(body);
			copyFile("drawer.js");
			copyFile("graph.js");
			copyFile("diagramstyle.css");
			writeFile("Editor.html", content.toString());
			try {
				webEngine.load(new File("Editor.html").toURI().toURL().toString());
			} catch (MalformedURLException e) {
			}
		} else if (map.containsKey("exportall")) {
			// Add external Files
			content.append(readFile("drawer.js"));
			content.append(readFile("graph.js"));
			content.append(readFile("diagramstyle.css"));
			content.append(body);
			writeFile("Editor.html", content.toString());
			try {
				webEngine.load(new File("Editor.html").toURI().toURL().toString());
			} catch (MalformedURLException e) {
			}
		} else {
			// Add external Files
			content.append(readFile("drawer.js"));
			content.append(readFile("graph.js"));
			content.append(readFile("diagramstyle.css"));
			content.append(body);
			webEngine.loadContent(content.toString());
		}

		webEngine.setOnError(new EventHandler<WebErrorEvent>() {
			@Override
			public void handle(WebErrorEvent event) {
				System.err.println(event.getMessage());
			}
		});
		browser.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				webEngine.executeScript("classEditor.setBoardStyle(\"dragleave\");");
			}
		});
		browser.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if(db.hasFiles()){
					boolean error=true;
					for(File file:db.getFiles()){
						 String name = file.getName().toLowerCase();
						if(name.indexOf("json", name.length() - 4) >= 0) {
							error = false;
						}
					}
					if(!error) {
						event.acceptTransferModes(TransferMode.COPY);
						webEngine.executeScript("classEditor.setBoardStyle(\"Ok\");");
					}else {
						event.acceptTransferModes(TransferMode.NONE);
						webEngine.executeScript("classEditor.setBoardStyle(\"Error\");");
					}
				}

				event.consume();
			}
		});
		browser.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if(db.hasFiles()){
					for(File file:db.getFiles()){
						StringBuilder sb = new StringBuilder();
						byte buf[] = new byte[1024];
						int read;
						FileInputStream is = null;
						try {
							is=new FileInputStream(file);
							do {
								read = is.read(buf, 0, buf.length);
								if (read>0) {
									sb.append(new String(buf, 0, read, "UTF-8"));
								}
							} while (read>=0);
						} catch (IOException e) {
						}finally {
							if(is != null) {
								try {
									is.close();
								} catch (IOException e) {
								}
							}
						}
						webEngine.executeScript("classEditor.dropFile('"+sb.toString()+"', \""+file.getAbsolutePath()+"\");");
						   break;
					}
				}
			}
		});

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == Worker.State.SUCCEEDED) {
					System.out.println("called: " + webEngine.getLocation());
					JSObject win = (JSObject) webEngine.executeScript("window");
					win.setMember("java", new JavaApp(DiagramEditor.this));
				}
			}
		});
		return browser;
	}

	protected void writeFile(String file, String content) {
		File target = new File(file);
		if (!target.exists()) {
			try {
				target.createNewFile();
			} catch (IOException e) {
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(target);
			byte[] bytes = content.getBytes("UTF-8");
			out.write(bytes, 0, bytes.length);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected StringBuilder readFile(String file) {
		InputStream is = GraphList.class.getResourceAsStream(file);
		StringBuilder sb = new StringBuilder();
		if (file.endsWith(".js")) {
			sb.append("<script language=\"Javascript\">" + CRLF);
		} else if (file.endsWith(".css")) {
			sb.append("<style>" + CRLF);
		}
		if (is != null) {
			final int BUFF_SIZE = 5 * 1024; // 5KB
			final byte[] buffer = new byte[BUFF_SIZE];
			try {
				while (true) {
					int count;
					count = is.read(buffer);
					if (count == -1)
						break;
					sb.append(new String(buffer, 0, count, "UTF-8"));
				}
			} catch (IOException e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		if (file.endsWith(".js")) {
			sb.append("</script>" + CRLF);
		} else if (file.endsWith(".css")) {
			sb.append("</style>" + CRLF);
		}
		return sb;
	}

	protected void copyFile(String file) {
		File target = new File(file);

		InputStream is = GraphList.class.getResourceAsStream(file);
		FileOutputStream out = null;
		if (is != null) {
			final int BUFF_SIZE = 5 * 1024; // 5KB
			final byte[] buffer = new byte[BUFF_SIZE];

			try {
				if (!target.exists()) {
					if(target.createNewFile() == false) {
						return;
					}
				}
				out = new FileOutputStream(target);

				while (true) {
					int count = is.read(buffer);
					if (count == -1)
						break;
					out.write(buffer, 0, count);
				}

			} catch (IOException e) {
			} catch (Exception e) {
			} finally {
				if(out != null) {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public boolean generate(JsonObject model) {
		if(this.logic != null) {
			return this.logic.generate(model);
		}
		return false;
	}

	public final static class JavaApp {
		private DiagramEditor owner;

		public JavaApp(DiagramEditor owner) {
			this.owner = owner;
		}

		public void exit() {
			System.out.println("Exit");
			Platform.exit();
		}

		public void save(Object value) {
			this.owner.save(new JsonObject().withValue((String) value));
		}

		public String generate(String value) {
			try {
				Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						JavaApp.this.owner.saveException(e);
					}
				});
				this.owner.generate(new JsonObject().withValue(value));
			} catch (RuntimeException e) {
				this.owner.saveException(e);
			} catch (Exception e) {
				this.owner.saveException(e);
			} catch (Throwable e) {
				this.owner.saveException(e);
			}
			return "";
		}
	}

	public void save(JsonObject model) {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String name = model.getString("package");
		if (name == null || name.length() < 1) {
			name = "model";
		}
		name = name + "_" + formatter.format(new Date().getTime()) + ".json";
		writeFile(name, model.toString());
	}

	@Override
	public void open(Object logic, String... args) {
		SimpleList<String> values = new SimpleList<String>();
		if(logic != null) {
			values.add("--logic="+logic.getClass().getName());
		}
		if(args != null) {
			for(String item : args) {
				values.with(item);
			}
		}
		launch(values.toArray(new String[values.size()]));
	}

	@Override
	public String getIcon() {
		if (this.logic != null) {
			return this.logic.getIcon();
		}
		return null;
	}
}
