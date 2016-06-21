package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.GXLTokener;
import de.uniks.networkparser.xml.XMLContainer;

public class GXLTest {
	@Test
	public void testSimple() {
		XMLContainer xmlContainer = new XMLContainer();

		StringBuilder reference = new StringBuilder();
		xmlContainer.withPrefix("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlContainer.withPrefix("<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">");
		reference.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(BaseItem.CRLF); 
		reference.append("<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">").append(BaseItem.CRLF);
		reference.append("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">").append(BaseItem.CRLF);
		reference.append("</gxl>");
		  
		  University uni = new University();
		  uni.withName("University of Kassel");
		  Student student = new Student();
		  student.withName("Stefan");
		  uni.withStudents(student);
		  GXLTokener tokener = new GXLTokener();
		  IdMap map = new IdMap();
		  map.with(new UniversityCreator());
		  map.with(new StudentCreator());
		  xmlContainer.with(map.encode(uni, tokener));
		  
//		  System.out.println(xmlContainer.toString(2));
	}
}
