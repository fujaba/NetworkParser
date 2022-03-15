package de.uniks.networkparser.test;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.graph.Clazz;

public class FileModelTest {
    @Test
    public void testModel() {
        FileClassModel model = new FileClassModel("de.uniks.networkparser.calculator");
        model.readFiles("src/main/java/");
        model.fixClassModel();
//        for(Clazz clazz : model.getClazzes()) {
//            System.out.println(clazz.getName());
//        }
    }
    
    @Test
    public void testAnalyseJavaDoc() {
        FileClassModel model = new FileClassModel("de.uniks.networkparser.calculator");
        model.readFiles("src/main/java/");
        model.analyseJavaDoc(true);
        for(Clazz clazz : model.getClazzes()) {
            
          System.out.println(clazz.getName());
      }
    }
}
