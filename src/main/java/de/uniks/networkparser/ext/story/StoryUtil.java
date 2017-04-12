package de.uniks.networkparser.ext.story;

public class StoryUtil {
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwException(Throwable exception, Object dummy) throws T
    {
        throw (T) exception;
    }

    public static void throwException(Throwable exception)
    {
    	StoryUtil.<RuntimeException>throwException(exception, null);
    }
    
    public static Story withBreakOnAssert(Story story, boolean value) {
    	story.withBreakOnAssert(value);
    	return story;
    }
}
