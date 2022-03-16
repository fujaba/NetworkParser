package de.uniks.networkparser.json;

import java.util.Collection;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.list.ObjectMapEntry;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class JsonTokener.
 *
 * @author Stefan
 */
public class SimpleJsonTokener extends JsonTokener {
    public static JsonObject parseEntity(Buffer buffer) {
        SimpleJsonTokener tokener = new SimpleJsonTokener();
        return (JsonObject) tokener.parsingEntity(tokener.newInstance(), buffer);
    }

    /**
     * Next value.
     *
     * @param buffer the buffer
     * @return the object
     */
    @Override
    public Object nextValue(Buffer buffer) {
        switch (buffer.nextClean()) {
        case BufferItem.QUOTES:
            buffer.skip();
            CharacterBuffer text = buffer.parseString(true, BufferItem.QUOTES);
            buffer.skip();
            return text;
        case '\\':
            /* Must be unquote */
            buffer.skip();
            buffer.skip();
            CharacterBuffer textResult = buffer.parseString(true, BufferItem.QUOTES);
            buffer.skip();
            return textResult;
        case JsonObject.START:
            return this.parsingEntity(newInstance(), buffer);
        case JsonArray.START:
            return this.parsingEntity(newInstanceList(), buffer);
        }
        return buffer.nextValue(STOPCHARS);
    }
}
