package de.uniks.networkparser.json;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BufferItem;

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
