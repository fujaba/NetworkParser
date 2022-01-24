package de.uniks.networkparser.ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.graph.DotConverter;
import de.uniks.networkparser.graph.GraphList;

public class BNDGraph {
    private HashSet<String> visited = new HashSet<String>();
    private ArrayList<String> todo = new ArrayList<String>();
    private StringBuilder sb = new StringBuilder();
    private StringBuilder common = new StringBuilder();
    private NetworkParserLog logger;
    private GraphList graph;

    public void anaylseProject(String path, String startproject, String fileName) {
        graph = new GraphList();
        File file = new File(path);
        file.listFiles();
        visited.add(startproject);
        path = path + "\\";
        readBND(path, startproject);
        readBDNRun(path, startproject, fileName);
        while (todo.size() > 0) {
            String id = todo.remove(0);
            if (!visited.contains(id)) {
                visited.add(id);
                readBND(path, id);
                readBDNRun(path, id, fileName);
            }
        }
        System.out.println("RESULT:");
        System.out.println(sb.toString());
        System.out.println("Common:");
        System.out.println(common.toString());

        File outputFile = new File("output.txt");
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            output.write(sb.toString().getBytes());
            output.write(10);
            output.write(13);
            output.write(10);
            output.write(13);
            output.write(10);
            output.write(13);
            output.write(common.toString().getBytes());
        } catch (Exception e) {
            if (logger != null) {
                logger.error(this, "anaylseProject", e);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    if (logger != null) {
                        logger.error(this, "anaylseProject", e);
                    }
                }
                output = null;
            }
        }
        try {
            output = new FileOutputStream("output.dot");
            DotConverter dotConverter = new DotConverter();
            dotConverter.withShowNodeInfo(false);
            dotConverter.withShowAssocInfo(false);
            dotConverter.replaceInvalidChars(graph, '.', '_', '-', '_');
            String string = graph.toString(dotConverter);
            output.write(string.getBytes());
            // Story story = new Story();
//         story.addDiagram(graph);
//         story.writeToFile("output.html");
        } catch (Exception e) {
            if (logger != null) {
                logger.error(this, "anaylseProject", e);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    if (logger != null) {
                        logger.error(this, "anaylseProject", e);
                    }
                }
                output = null;
            }
        }
    }

    private void readBDNRun(String path, String target, String fileName) {
        try {
            String allValue = readFile(path + target + "\\" + fileName);
            String[] split = allValue.split("\n");
            HashSet<String> found = new HashSet<String>();

            for (int z = 0; z < split.length; z++) {
                String line = split[z].trim();
                if (line.indexOf("-runbundles:") >= 0) {
                    while (line.length() > 0) {
                        line = cleanLine(line, "-runbundles:");
                        if (line.length() > 0) {
                            String subPackage = findProject(path, line);
                            if (subPackage.length() > 0) {
                                if (!visited.contains(subPackage) && !found.contains(subPackage)) {
                                    todo.add(subPackage);
                                    found.add(subPackage);
                                    String value = "{\"target\":\"" + target + "\", \"source\":\"" + subPackage
                                            + "\", \"type\": \"Generalisation\"},";
                                    common.append(value + "\r\n");
                                }
                            } else if (line.length() > 0 && !visited.contains(line) && !found.contains(line)) {
                                todo.add(line);
                                found.add(line);
                                String value = "{ \"target\":\"" + target + "\", \"source\":\"" + line
                                        + "\", \"type\": \"Generalisation\"},";
                                common.append(value + "\r\n");
                            }
                        }
                        line = split[++z].trim();
                    }
                }
            }
        } catch (FileNotFoundException e2) {
            System.out.println("External Libary: " + target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findProject(String path, String line) {
        if (new File(path + line).exists()) {
            return line;
        }
        while (line.indexOf(".") > 0) {
            line = line.substring(0, line.lastIndexOf("."));
            if (new File(path + line).exists()) {
                return line;
            }
        }
        return "";
    }

    private String readFile(String file) throws IOException {
        FileInputStream filestream = new FileInputStream(new File(file));
        int bytes = filestream.available();
        byte[] buffer = new byte[bytes];
        filestream.read(buffer);
        String allValue = new String(buffer);
        return allValue;
    }

    private String cleanLine(String line, String prefix) {
        line = line.replace(prefix, "");
        line = line.replace("\\", "");
        line = line.replace(",", "");
        int pos = line.indexOf(";");

        if (pos > 0) {
            line = line.substring(0, pos);
        }
        line = line.trim();
        return line;
    }

    private void readBND(String file, String target) {
        try {
            String allValue = readFile(file + target + "\\bnd.bnd");
            String[] split = allValue.split("\n");

            for (int z = 0; z < split.length; z++) {
                String line = split[z].trim();
                if (line.indexOf("-buildpath:") >= 0) {
                    while (line.length() > 0) {
                        line = cleanLine(line, "-buildpath:");
                        if (line.length() > 0 && !line.startsWith("${") && line.indexOf("/") < 1
                                && !visited.contains(line)) {
                            System.out.println(line + " - " + target);
                            todo.add(line);
                            graph.createAssociation(line, target);

                            String value = "{\"target\":\"" + target + "\", \"source\":\"" + line
                                    + "\", \"type\": \"Generalisation\"},";
                            sb.append(value + "\r\n");
                        }
                        if (z + 1 == split.length) {
                            break;
                        }
                        line = split[++z].trim();
                    }
                    break;
                }
            }
        } catch (FileNotFoundException e2) {
            System.out.println("External Libary: " + target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
