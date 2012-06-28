package de.uni.kassel.peermessage.json;

import de.uni.kassel.peermessage.BaseEntity;
import de.uni.kassel.peermessage.Entity;
import de.uni.kassel.peermessage.EntityList;
import de.uni.kassel.peermessage.Tokener;
import de.uni.kassel.peermessage.xml.XMLEntity;

/*
Copyright (c) 2012, Stefan Lindel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
   This product includes software developed by Stefan Lindel.
4. Neither the name of contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
public class JsonTokener extends Tokener{
	public JsonTokener() {
		super();
	}
	public JsonTokener(String s) {
		super(s);
	}
	 public Object nextValue(BaseEntity creator)  {
		char c = nextClean();

        switch (c) {
            case '"':
            case '\'':
                return nextString(c);
            case '{':
                back();
                BaseEntity element = creator.getNewObject();
                element.setTokener(this);
                return element; 
            case '[':
                back();
                EntityList elementList = creator.getNewArray();
                elementList.setTokener(this);
                return elementList;
        }
    	back();
        return super.nextValue(creator);
	 }
	 
	 public Entity parseEntity(Entity newValue){
		 return parseEntity(new JsonObject(), newValue);
	 }
	 public Entity parseEntity(JsonObject parent, Entity newValue){
		 if(newValue instanceof XMLEntity){
		 		XMLEntity xmlEntity=(XMLEntity) newValue;
		 		String[] names = Entity.getNames(xmlEntity);
		 		parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
		 		JsonObject props=new JsonObject();
		 		for(String prop : names){
		 			Object propValue=xmlEntity.get(prop);
		 			if(propValue instanceof XMLEntity){
		 				props.put(prop, parseEntity((XMLEntity)propValue));
		 			}else{
		 				props.put(prop, propValue);
		 			}
		 		}
		 		parent.put(JsonIdMap.JSON_PROPS, props);
		 }
		 return parent;
	 }
}
