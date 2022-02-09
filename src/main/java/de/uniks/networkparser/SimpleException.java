package de.uniks.networkparser;

import de.uniks.networkparser.buffer.CharacterBuffer;

/**
 * Simple Exception
 * @author Stefan Lindel
 */
public class SimpleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Object[] datas;

    public SimpleException(String msg, Object... datas) {
        super(msg);
        this.datas = datas;
    }

    public Object[] getDatas() {
        return datas;
    }

    public SimpleException(Throwable e) {
        super(e);
        this.datas = null;
    }

    public Object getSource() {
        if (datas != null && datas.length > 0) {
            return datas[0];
        }
        return null;
    }
    
    public CharacterBuffer getErrorMessage() {
        if (datas != null && datas.length > 1 && datas[1] instanceof CharacterBuffer)  {
            return (CharacterBuffer) datas[1];
        }
        return null;
    }
}
