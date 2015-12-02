package de.uniks.networkparser.gui.javafx.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
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
	private final String CRLF = "\r\n";
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
			} catch (Exception e) {
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
                                	sb.append(new String(buf, 0, read));
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
		try {
			FileOutputStream out = new FileOutputStream(target);
			byte[] bytes = content.getBytes();
			out.write(bytes, 0, bytes.length);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
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
					sb.append(new String(buffer, 0, count));
				}
				is.close();
			} catch (IOException e) {
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

		if (is != null) {
			final int BUFF_SIZE = 5 * 1024; // 5KB
			final byte[] buffer = new byte[BUFF_SIZE];

			try {
				if (!target.exists()) {
					target.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(target);

				while (true) {
					int count = is.read(buffer);
					if (count == -1)
						break;
					out.write(buffer, 0, count);
				}
				out.close();
				is.close();
			} catch (Exception e) {
				// e.printStackTrace();
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

	public class JavaApp {
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
		values.with(args);

		launch(values.toArray(new String[0]));
	}

	@Override
	public String getIcon() {
		if (this.logic != null) {
			return this.logic.getIcon();
		}
		return null;
	}
}
