package de.uniks.networkparser.test;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.SimpleJsonTokener;

public class SimpleJson {

    @Test
    public void testSimple() {
        ByteBuffer buffer = FileBuffer.readBinaryResource("citysmall2.json", SimpleJson.class);
//        String content = buffer.toString();
//        System.out.println(content);
        SimpleJsonTokener tokener = new SimpleJsonTokener();
        JsonObject result = new JsonObject();
        tokener.parsingEntity(result, buffer);
        System.out.println(result.toString(2));
    }
}
