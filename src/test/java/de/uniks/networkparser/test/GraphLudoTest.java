/*
   Copyright (c) 2012 zuendorf 
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package de.uniks.networkparser.test;
   
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphIdMap;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.ludo.Field;
import de.uniks.networkparser.test.model.ludo.Ludo;
import de.uniks.networkparser.test.model.ludo.LudoColor;
import de.uniks.networkparser.test.model.ludo.Pawn;
import de.uniks.networkparser.test.model.ludo.Player;
import de.uniks.networkparser.test.model.ludo.creator.DateCreator;
import de.uniks.networkparser.test.model.ludo.creator.DiceCreator;
import de.uniks.networkparser.test.model.ludo.creator.FieldCreator;
import de.uniks.networkparser.test.model.ludo.creator.LudoCreator;
import de.uniks.networkparser.test.model.ludo.creator.PawnCreator;
import de.uniks.networkparser.test.model.ludo.creator.PlayerCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
   
public class GraphLudoTest 
{
   private static final String RED = "red";


   @Test
   public void testLudoStoryboard()
   {
	  JsonIdMap jsonIdMap = new JsonIdMap();
	  jsonIdMap.with(new DateCreator())
		  .with(new DiceCreator())
		  .with(new FieldCreator())
		  .with(new LudoCreator())
		  .with(new PawnCreator())
		  .with(new PlayerCreator());
	  
	  // create a simple ludo storyboard
	  
	  Ludo ludo = new Ludo();
	  
	  Player tom = ludo.createPlayers().withName("Tom").withColor("blue").withEnumColor(LudoColor.blue);
	  
	  
	  Player sabine = ludo.createPlayers().withName("Sabine").withColor(RED).withEnumColor(LudoColor.red);
	  
	  tom.createDice().withValue(6);
	  
	  Pawn p2 = tom.createPawns().withColor("blue");
	  
	  Field tomStartField = tom.createStart().withColor("blue").withKind("start");
	  
	  sabine.createStart().withColor(RED).withKind("start");
	  
	  Field tmp = tomStartField;
	  for (int i = 0; i < 4; i++)
	  {
		 tmp = tmp.createNext();
	  }
	  
	  tom.createBase().withColor("blue").withKind("base").withPawns(p2);
	  
	  sabine.createPawns().withColor(RED).withPos(tomStartField);
	  
	  JsonArray jsonArray = jsonIdMap.toJsonArray(ludo);
	  GraphConverter graphConverter = new GraphConverter();
	  
	  // May be 8 Asssocs and write 11
	  JsonObject converter=graphConverter.convertToJson(GraphIdMap.CLASS, jsonArray, true);
	  showDebugInfos(converter, 3529, System.out);
   }
   private void showDebugInfos(JsonObject json, int len, PrintStream stream) {
	   if(stream != null) {
		   stream.println("###############################");
		   stream.println(json.toString(2));
		   stream.println("###############################");
	   }
	   Assert.assertEquals(len, json.toString(2).length());
   }

   @Test
 public void testSimpleGraph()
 {
	   SortedMsg root = new SortedMsg();
	   root.withMsg("Hallo Welt");
	   
	   root.setChild(new SortedMsg().withMsg("Child"));
	   
	   JsonIdMap map = new JsonIdMap();
	   map.with(new SortedMsgCreator());
	   
	   JsonArray jsonArray = map.toJsonArray(root, new Filter().withFull(true));
	   JsonObject item = jsonArray.get(map.getKey(root));
	   item.put(GraphConverter.HEAD, "map.png");
	   GraphConverter graphConverter = new GraphConverter();
	  JsonObject objectModel=graphConverter.convertToJson(GraphIdMap.OBJECT, jsonArray, true);
	  showDebugInfos(objectModel, 683, null);
	  
	  JsonObject clazzModel=graphConverter.convertToJson(GraphIdMap.CLASS, jsonArray, true);
	  showDebugInfos(clazzModel, 519, null);
 }
}

