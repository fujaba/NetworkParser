package de.uniks.networkparser.ext.io;

import java.io.IOException;
import java.io.InputStream;

import de.uniks.networkparser.bytes.CRC;

/**
 * Add InputStream for ZipFile with Password
 * @author Stefan Lindel
 *
 */
public class ZipDecryptInputStream extends InputStream {
    private static final int DECRYPT_HEADER_SIZE = 12;
    private static final int[] LFH_SIGNATURE = {0x50, 0x4b, 0x03, 0x04};
 
    private final InputStream delegate;
    private final String password;
    private final int keys[] = new int[3];
 
    private ZipState state = ZipState.SIGNATURE;
    private int skipBytes;
    private int compressedSize;
    private int value;
    private int valuePos;
    private int valueInc;
    private CRC crcCheck = new CRC().withCRC(32);
 
    public ZipDecryptInputStream(InputStream stream, String password) {
        this.delegate = stream;
        this.password = password;
    }
 
    @Override
    public int read() throws IOException {
        int result = delegate.read();
        if(this.password == null) {
        	return result;
        }
        if (skipBytes == 0) {
            switch (state) {
                case SIGNATURE:
                    if (result != LFH_SIGNATURE[valuePos]) {
                        state = ZipState.TAIL;
                    } else {
                        valuePos++;
                        if (valuePos >= LFH_SIGNATURE.length) {
                            skipBytes = 2;
                            state = ZipState.FLAGS;
                        }
                    }
                    break;
                case FLAGS:
                    if ((result & 1) == 0) {
                        throw new IllegalStateException("ZIP not password protected.");
                    }
                    if ((result & 64) == 64) {
                        throw new IllegalStateException("Strong encryption used.");
                    }
                    if ((result & 8) == 8) {
                        throw new IllegalStateException("Unsupported ZIP format.");
                    }
                    result -= 1;
                    compressedSize = 0;
                    valuePos = 0;
                    valueInc = DECRYPT_HEADER_SIZE;
                    state = ZipState.COMPRESSED_SIZE;
                    skipBytes = 11;
                    break;
                case COMPRESSED_SIZE:
                    compressedSize += result << (8 * valuePos);
                    result -= valueInc;
                    if (result < 0) {
                        valueInc = 1;
                        result += 256;
                    } else {
                        valueInc = 0;
                    }
                    valuePos++;
                    if (valuePos > 3) {
                        valuePos = 0;
                        value = 0;
                        state = ZipState.FN_LENGTH;
                        skipBytes = 4;
                    }
                    break;
                case FN_LENGTH:
                case EF_LENGTH:
                    value += result << 8 * valuePos;
                    if (valuePos == 1) {
                        valuePos = 0;
                        if (state == ZipState.FN_LENGTH) {
                            state = ZipState.EF_LENGTH;
                        } else {
                            state = ZipState.HEADER;
                            skipBytes = value;
                        }
                    } else {
                        valuePos = 1;
                    }
                    break;
                case HEADER:
                    initKeys(password);
                    for (int i = 0; i < DECRYPT_HEADER_SIZE; i++) {
                        updateKeys((byte) (result ^ decryptByte()));
                        result = delegate.read();
                    }
                    compressedSize -= DECRYPT_HEADER_SIZE;
                    state = ZipState.DATA;
                    // intentionally no break
                case DATA:
                    result = (result ^ decryptByte()) & 0xff;
                    updateKeys((byte) result);
                    compressedSize--;
                    if (compressedSize == 0) {
                        valuePos = 0;
                        state = ZipState.SIGNATURE;
                    }
                    break;
                case TAIL:
                    // do nothing
            }
        } else {
            skipBytes--;
        }
        return result;
    }
 
    @Override
    public void close() throws IOException {
        delegate.close();
        super.close();
    }
 
    private void initKeys(String password) {
        keys[0] = 305419896;
        keys[1] = 591751049;
        keys[2] = 878082192;
        for (int i = 0; i < password.length(); i++) {
            updateKeys((byte) (password.charAt(i) & 0xff));
        }
    }
 
    private void updateKeys(byte charAt) {
        keys[0] = this.crcCheck.crc32(keys[0], charAt);
        keys[1] += keys[0] & 0xff;
        keys[1] = keys[1] * 134775813 + 1;
        keys[2] = this.crcCheck.crc32(keys[2], (byte) (keys[1] >> 24));
    }
 
    private byte decryptByte() {
        int temp = keys[2] | 2;
        return (byte) ((temp * (temp ^ 1)) >>> 8);
    }
}