package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaSetMethod extends BasicGenerator {

	public JavaSetMethod() {
		createTemplate("Method", Template.METHOD,
				"{{#foreach {{parameter}}}}" +
						"{{#DEBUG}}",
				   "{{#if {{#AND}}{{item.typeValue.type}}==class}} {{#NOT}}{{file.member.name}}{{#ENDNOT}}{{ENDAND}}}}" +
				         "{{#import {{item.type(false)}}}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   {{visibility}} {{modifiers} }{{file.member.name}}Set {{name}}{{parameterName}}",
				"   {",
				"      return {{file.member.name}}Set.EMPTY_SET;",
				"   }","");
	}
	
//    if ("void".equals(type))
//    {
//       type =  clazz2.getName(true) + "Set";
//       body = "return "+type+".EMPTY_SET;";
//    }
//    else
//    {
//   	 String returnStat = "return this;";
//   	 if (EntityUtil.isNumericType(type)) {
//   		 type = "NumberList";
//            importType = NumberList.class.getName();
//   	 } else if ("String".indexOf(type) >= 0) {
//   		 type = "StringList";
//   		 importType = StringList.class.getName();
//   	 } else if ("boolean".indexOf(type) >= 0) {
//   		 type = BooleanList.class.getName();
//   		 importType = BooleanList.class.getSimpleName();
//   	 } else if ("Object".indexOf(type) >= 0) {
//          type = "LinkedHashSet<Object>";
//          importType = LinkedHashSet.class.getName();
//       }
//       else
//       {
//       	
//          type = type + "Set";
//          importType = model.getClazz().getName(false);
//          int dotpos = importType.lastIndexOf('.');
//          int typePos = type.lastIndexOf('.');
//          type = type.substring(typePos + 1);
//          importType = importType.substring(0, dotpos) + GenClassModel.UTILPATH + "." + type;
//       }
//   	 returnStat = "return result;";
//
//       parser.insertImport(importType);
//       if (model.getModifier().has(Modifier.STATIC))
//       {
//          returnStat = "";
//       }
////       if(model.getBody()!=null &&model.getBody().length()>0) {
//////       	body = model.getBody();
////       } else {
//       	body = 	"\n      returnSetCreate" +
//       			"\n      for (memberType obj : this)" +
//       			"\n      {" +
//       			"\n         returnSetAdd obj.methodName(actualParameter) returnSetEnd;" +
//       			"\n      }" +
//       			"\n      returnStat";
	
	@Override
	public Class<?> getTyp() {
		return Method.class;
	}
}
