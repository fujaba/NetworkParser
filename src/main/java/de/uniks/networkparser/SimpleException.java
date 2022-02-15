package de.uniks.networkparser;

import de.uniks.networkparser.buffer.CharacterBuffer;

/**
 * Simple Exception.
 *
 * @author Stefan Lindel
 */
public class SimpleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Object[] datas;

    /**
     * Instantiates a new simple exception.
     *
     * @param msg the msg
     * @param datas the datas
     */
    public SimpleException(String msg, Object... datas) {
        super(msg);
        this.datas = datas;
    }

    /**
     * Gets the datas.
     *
     * @return the datas
     */
    public Object[] getDatas() {
        return datas;
    }

    /**
     * Instantiates a new simple exception.
     *
     * @param e the e
     */
    public SimpleException(Throwable e) {
        super(e);
        this.datas = null;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public Object getSource() {
        if (datas != null && datas.length > 0) {
            return datas[0];
        }
        return null;
    }
    
    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public CharacterBuffer getErrorMessage() {
        if (datas != null && datas.length > 1 && datas[1] instanceof CharacterBuffer)  {
            return (CharacterBuffer) datas[1];
        }
        return null;
    }
}
