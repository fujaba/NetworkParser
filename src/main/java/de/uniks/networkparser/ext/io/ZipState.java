package de.uniks.networkparser.ext.io;

/**
 * ZipState .
 *
 * @author Stefan Lindel
 */
public enum ZipState {
	SIGNATURE, FLAGS, COMPRESSED_SIZE, FN_LENGTH, EF_LENGTH, HEADER, DATA, TAIL
}
