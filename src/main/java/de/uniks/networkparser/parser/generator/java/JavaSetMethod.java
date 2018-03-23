package de.uniks.networkparser.parser.generator.java;

/*
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
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaSetMethod extends BasicGenerator {
	public JavaSetMethod() {
		createTemplate("Method", Template.METHOD,
				"{{#foreach {{parameter}}}}" +
				   "{{#if {{#AND}}{{item.typeClazz.type}}==class {{file.member.name}}{{#ENDAND}}}}" +
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
