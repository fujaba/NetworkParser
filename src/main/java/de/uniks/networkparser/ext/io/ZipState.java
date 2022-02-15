package de.uniks.networkparser.ext.io;

/**
 * ZipState .
 *
 * @author Stefan Lindel
 */
public enum ZipState {
    /** The signature. */
    SIGNATURE,
    /** The flags. */
    FLAGS,
    /** The compressed size. */
    COMPRESSED_SIZE,
    /** The fn length. */
    FN_LENGTH,
    /** The ef length. */
    EF_LENGTH,
    /** The header. */
    HEADER,
    /** The data. */
    DATA,
    /** The tail. */
    TAIL
}
