package de.uniks.networkparser.converter;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphMetric;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SymTabEntry;

public class CodeCityConverter implements Converter {
	private NetworkParserLog logger;
/*Root := Document ?
	   Document := OPEN ElementNode \* CLOSE
			   ElementNode := OPEN NAME Serial ? AttributeNode \* CLOSE
			   Serial := OPEN ID INTEGER CLOSE
			   AttributeNode := OPEN Name ValueNode \* CLOSE
			   ValueNode := Primitive | Reference | ElementNode
			   Primitive := STRING | NUMBER | Boolean | Unlimited
			   Boolean := TRUE | FALSE
			   Unlimited := NIL
			   Reference := IntegerReference | NameReference
			   IntegerReference := OPEN REF INTEGER CLOSE
			   NameReference := OPEN REF NAME CLOSE
			   OPEN := "("
			   CLOSE := ")"
			   ID := "id:"
			   REF := "ref:"
			   TRUE := "true"
			   FALSE := "false"
			   NAME := letter ( letter | digit ) \* ( "." letter ( letter | digit ) ) \*
			   INTEGER := digit +
			   NUMBER := "-" ? digit + ( "." digit + ) ? ( ( "e" | "E" ) ( "-" | "+" ) ? digit + ) ?
			   STRING := ( "'" \[^'] \* "'" ) +
			   digit := \[0-9] 
			   letter := \[a-zA-Z_]
			Whitespace are the usual suspects, and comments are

			 comment := "\"" \[^"] \* "\""
*/
	@Override
	public String encode(BaseItem entity) {
		CharacterBuffer buffer=new CharacterBuffer();
		if(entity instanceof FileClassModel == false) {
			return buffer.toString();
		}
		FileClassModel model= (FileClassModel) entity;
		buffer.with("(Moose.Model (sourceLanguage 'Java') (entity ", BaseItem.CRLF); 
		
		SimpleKeyValueList<String, SimpleList<ParserEntity>> packageList = model.getPackageList();
		for(int i=0; i < packageList.size(); i++) {
			String packageName = packageList.get(i);
			buffer.with("(FAMIX.Namespace (id: "+(i+1)+") (name '"+packageName.replace(".", "::")+"'))", BaseItem.CRLF);
		}
		int index = ((packageList.size()+2) /100) + 100 ;
		SimpleKeyValueList<GraphMember, Integer> list=new SimpleKeyValueList<GraphMember, Integer>();

		for(int i=0; i < packageList.size(); i++) {
//			String packageName = packageList.get(i);
			SimpleList<ParserEntity> entities = packageList.getValueByIndex(i);
			for(int p = 0;p<entities.size();p++) {
				ParserEntity parserEntity = entities.get(p);
				index = addElement(buffer, parserEntity, i+1, index, list);
			}
		}
//		(FAMIX.InheritanceDefinition (id: 66938)(subclass (idref: 11679)) (superclass (idref: 12796))
		buffer.with("))");
		return buffer.toString();
	}

	private int addElement(CharacterBuffer buffer, ParserEntity entity, int packageId, int index, SimpleKeyValueList<GraphMember, Integer> list) {
		Clazz clazz = entity.getClazz();
		int clazzId=index++;
		list.add(clazz, clazzId);
		SourceCode code = entity.getCode();

		buffer.withLine("(FAMIX.Class (id: "+clazzId+")");
		buffer.withLine("\t(name '"+clazz.getName(true)+"')");
		buffer.withLine("\t(belongsTo (idref: "+packageId+"))");
		buffer.withLine("\t(isAbstract "+GraphUtil.isAbstract(clazz)+")");
		buffer.withLine("\t(isInterface "+GraphUtil.isInterface(clazz)+")");
		buffer.withLine("\t(fileName 'FILE:"+entity.getFileName()+"')");
		buffer.withLine("\t(startLine "+code.getBodyStartLine()+")");
		buffer.withLine("\t(endLine "+code.getEndofBodyLine()+")");
		GraphMetric metric = getMetric(clazz);
		if(metric == null && logger != null) {
			logger.error(this, "addElement", "Clazz has no Metric: "+clazz.getName()+": "+metric);
		}
		if(metric != null) {
			buffer.withLine("\t(WLOC "+metric.getLinesOfCode()+")");
			buffer.withLine("\t(WNOCmts "+metric.getCommentCount()+")");
		}
/*
	(GodClass false)
	(DataClass false)
	(BrainClass false)
	(RefusedParentBequest false)
	(TraditionBreaker false)
	(WNOS 23.0)
	(WNOCond 3.0)
	(WOC 0.60)
	(ATFD 0.00)
	(WMC 7.00)
	(TCC 0.33)
	(CRIX 0.00)
	(NOAM 1.00)
	(NOPA 0.00)
	(BUR 1.00)
	(BOvR 0.00)
	(AMW 1.40)
	(NOM 5.00)
	(NAS 0.00)
	(PNAS -1.00)
	(LOC 56.00)
	(NProtM 0.00)

				*/
		buffer.with(")", BaseItem.CRLF, BaseItem.CRLF);
		
		// META MODEL SO METHODS SMALLER BECAUSE ATTRIBUTE AND ASSOCS METHODS
		AttributeSet attributes = clazz.getAttributes();
		for(Attribute attr : attributes) {
			index = addElement(buffer, entity, attr, index, list);
		}
		
		MethodSet methods = clazz.getMethods();
		for(Method method : methods) {
			index = addElement(buffer, entity, method, index, list);
		}
		return index;
	}
	private int addElement(CharacterBuffer buffer, ParserEntity entity, Attribute attribute, int index, SimpleKeyValueList<GraphMember, Integer> list) {
		int attriuteId=index++;
		list.add(attribute, attriuteId);
		
		buffer.withLine("(FAMIX.Attribute (id: "+attriuteId+")");
		buffer.withLine("\t(name '"+attribute.getName()+"')");
		buffer.withLine("\t(belongsTo (idref: "+list.get(attribute.getClazz())+"))");
		buffer.withLine("\t(hasClassScope true)");
		buffer.withLine("\t(accessControlQualifier "+GraphUtil.getVisible(attribute)+")");
//				(NMAV 4.0)
		buffer.with(")", BaseItem.CRLF, BaseItem.CRLF);
		
		return index;
	}
	private GraphMetric getMetric(GraphMember member) {
		GraphSimpleSet children = GraphUtil.getChildren(member);
		for(GraphMember item : children) {
			if(item instanceof GraphMetric) {
				return (GraphMetric) item;
			}
		}
		return null;
	}
	
	private int addElement(CharacterBuffer buffer, ParserEntity entity, Method method, int index, SimpleKeyValueList<GraphMember, Integer> list) {
		int methodId=index++;
		SourceCode code = entity.getCode();
		list.add(method, methodId);
		GraphMetric metric = getMetric(method);
		SymTabEntry symbolEntry = code.getSymbolEntry(SymTabEntry.TYPE_METHOD, method.getName());
		buffer.withLine("(FAMIX.Method (id: "+methodId+")");
		buffer.withLine("\t(fileName 'FILE:"+code.getFileName()+"')");
		if(symbolEntry != null) {
			buffer.withLine("\t(startLine "+symbolEntry.getStartLine()+")");
			buffer.withLine("\t(endLine "+symbolEntry.getEndLine()+")");
		}
		buffer.withLine("\t(name '"+method.getName()+"')");
		buffer.withLine("\t(belongsTo (idref: "+list.get(method.getClazz())+"))");
		buffer.withLine("\t(accessControlQualifier "+GraphUtil.getVisible(method)+")");
		buffer.withLine("\t(signature '"+method.getName(true, true)+"')");
		if(metric != null) {
			buffer.withLine("\t(LOC "+metric.getLinesOfCode()+")");
			buffer.withLine("\t(NOCmts "+metric.getCommentCount()+")");
		}
		
		
		buffer.with(")", BaseItem.CRLF, BaseItem.CRLF);
		return index;
//				(hasClassScope false)
//				(isAbstract false)
//				(isConstructor false)
//				(isPureAccessor false)
//				(FeatureEnvy false)
//				(BrainMethod false)
//				(IntensiveCoupling false)
//				(DispersedCoupling false)
//				(ShotgunSurgery false)
//				(NOS 2.0)
//				(NOCond 0.0)
//				(NMAA 1.0)
//				(NI 2.0)
//				(CYCLO 1.0)
//				(CINT 0.00)
//				(CDISP 0.00)
//				(CM 0.00)
//				(CC 0.00)
//				(ATFD 2.00)
//				(LAA 1.00)
//				(FDP 0.00)
//				(MAXNESTING 0.00)
//				(NOAV 1.00)
//			)
	}
}
