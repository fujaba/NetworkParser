package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;

public class CharacterBufferTest {
    @Test
    public void testSimple() {
        String txt = "Stefan";
        CharacterBuffer buffer = new CharacterBuffer();
        buffer.with(txt);
        
        assertEquals(txt, buffer.toString());
    }
    
    @Test
    public void testText() {
        String txt = "\\Stefan\\";
        CharacterBuffer buffer = new CharacterBuffer();
        buffer.with(txt);
        
        assertEquals(txt, buffer.toString());
    }
    
    @Test
    public void testEscape() {
        String txt = "\\\"Stefan\\\"";
        CharacterBuffer buffer = new CharacterBuffer();
        buffer.with(txt);
        
        assertEquals(txt, buffer.toString());
    }
}
