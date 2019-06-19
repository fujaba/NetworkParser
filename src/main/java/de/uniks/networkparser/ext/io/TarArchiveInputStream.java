/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package de.uniks.networkparser.ext.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import de.uniks.networkparser.NetworkParserLog;

public class TarArchiveInputStream extends InputStream {
	private final byte[] single = new byte[1];
	private static final int BYTE_MASK = 0xFF;

	/** holds the number of bytes read in this stream */
	private long bytesRead = 0;

	private static final int SMALL_BUFFER_SIZE = 256;
	private final byte[] smallBuf = new byte[SMALL_BUFFER_SIZE];

	/** The size the TAR header */
	private final int recordSize;

	/** The size of a block */
	private final int blockSize;

	/** True if file has hit EOF */
	private boolean hasHitEOF;

	/** Size of the current entry */
	private long entrySize;

	/** How far into the entry the stream is at */
	private long entryOffset;

	/** An input stream to read from */
	private final InputStream is;

	/** The meta-data about the current entry */
	private TarArchiveEntry currEntry;

	/** The encoding of the file */
	private final NioZipEncoding zipEncoding;

	/** the global PAX header */
	private Map<String, String> globalPaxHeaders = new HashMap<String, String>();

	private NetworkParserLog logger;

	/**
	 * Method to decompress a gzip file
	 * 
	 * @param gZippedFile mFileName
	 * @return a TarArchiveInputStream
	 */
	public static TarArchiveInputStream create(String gZippedFile) {
		File file = new File(gZippedFile);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			int pos = file.getName().lastIndexOf(".");
			String tarName = file.getName() + ".tar";
			if (pos > 0) {
				tarName = file.getName().substring(0, pos) + ".tar";
			}
			File tarFile = new File(file.getParentFile().getPath() + "/" + tarName);
			GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);
			FileOutputStream fos = new FileOutputStream(tarFile);
			if (FileBuffer.copy(gZIPInputStream, fos) > 0) {
				return new TarArchiveInputStream(new FileInputStream(tarFile));
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Constructor for TarInputStream.
	 * 
	 * @param is the input stream to use
	 */
	public TarArchiveInputStream(final InputStream is) {
		this(is, TarUtils.DEFAULT_BLKSIZE, TarUtils.DEFAULT_RCDSIZE);
	}

	/**
	 * Constructor for TarInputStream.
	 * 
	 * @param is         the input stream to use
	 * @param blockSize  the block size to use
	 * @param recordSize the record size to use
	 */
	public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize) {
		this(is, blockSize, recordSize, null);
	}

	/**
	 * Constructor for TarInputStream.
	 * 
	 * @param is         the input stream to use
	 * @param blockSize  the block size to use
	 * @param recordSize the record size to use
	 * @param encoding   name of the encoding to use for file names
	 * @since 1.4
	 */
	public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize,
			final String encoding) {
		this.is = is;
		this.hasHitEOF = false;
		this.zipEncoding = TarUtils.getZipEncoding(encoding);
		this.recordSize = recordSize;
		this.blockSize = blockSize;
	}

	/**
	 * Closes this stream. Calls the TarBuffer's close() method.
	 */
	@Override
	public void close() {
		try {
			is.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Get the record size being used by this stream's buffer.
	 *
	 * @return The TarBuffer record size.
	 */
	public int getRecordSize() {
		return recordSize;
	}

	/**
	 * Get the available data that can be read from the current entry in the
	 * archive. This does not indicate how much data is left in the entire archive,
	 * only in the current entry. This value is determined from the entry's size
	 * header field and the amount of data already read from the current entry.
	 * Integer.MAX_VALUE is returned in case more than Integer.MAX_VALUE bytes are
	 * left in the current entry in the archive.
	 *
	 * @return The number of available bytes for the current entry.
	 */
	@Override
	public int available() {
		if (isDirectory()) {
			return 0;
		}
		if (entrySize - entryOffset > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) (entrySize - entryOffset);
	}

	/**
	 * Skips over and discards <code>n</code> bytes of data from this input stream.
	 * The <code>skip</code> method may, for a variety of reasons, end up skipping
	 * over some smaller number of bytes, possibly <code>0</code>. This may result
	 * from any of a number of conditions; reaching end of file or end of entry
	 * before <code>n</code> bytes have been skipped; are only two possibilities.
	 * The actual number of bytes skipped is returned. If <code>n</code> is
	 * negative, no bytes are skipped.
	 *
	 *
	 * @param n the number of bytes to be skipped.
	 * @return the actual number of bytes skipped.
	 */
	@Override
	public long skip(final long n) {
		if (n <= 0 || isDirectory()) {
			return 0;
		}

		final long available = entrySize - entryOffset;
		final long skipped = FileBuffer.skip(is, Math.min(n, available));
		count(skipped);
		entryOffset += skipped;
		return skipped;
	}

	/**
	 * Since we do not support marking just yet, we return false.
	 *
	 * @return False.
	 */
	@Override
	public boolean markSupported() {
		return false;
	}

	/**
	 * Since we do not support marking just yet, we do nothing.
	 *
	 * @param markLimit The limit to mark.
	 */
	@Override
	public void mark(final int markLimit) {
	}

	/**
	 * Since we do not support marking just yet, we do nothing.
	 */
	@Override
	public synchronized void reset() {
	}

	/**
	 * Get the next entry in this tar archive. This will skip over any remaining
	 * data in the current entry, if there is one, and place the input stream at the
	 * header of the next entry, and read the header and instantiate a new TarEntry
	 * from the header bytes and return that entry. If there are no more entries in
	 * the archive, null will be returned to indicate that the end of the archive
	 * has been reached.
	 *
	 * @return The next TarEntry in the archive, or null.
	 */
	public TarArchiveEntry getNextTarEntry() {
		if (isAtEOF()) {
			return null;
		}

		if (currEntry != null) {
			/* Skip will only go to the end of the current entry */
			FileBuffer.skip(this, Long.MAX_VALUE);

			/* skip to the end of the last record */
			skipRecordPadding();
		}

		try {
			final byte[] headerBuf = getRecord();
			if (headerBuf == null) {
				/* hit EOF */
				currEntry = null;
				return null;
			}
			currEntry = new TarArchiveEntry(headerBuf, zipEncoding);
		} catch (Exception e) {
			return null;
		}

		entryOffset = 0;
		entrySize = currEntry.getSize();

		if (currEntry.isGNULongLinkEntry()) {
			final byte[] longLinkData = getLongNameData();
			if (longLinkData == null) {
				return null;
			}
			try {
				currEntry.setLinkName(zipEncoding.decode(longLinkData));
			} catch (Exception e) {
				return null;
			}
		}

		if (currEntry.isGNULongNameEntry()) {
			final byte[] longNameData = getLongNameData();
			if (longNameData == null) {
				return null;
			}
			try {
				currEntry.setName(zipEncoding.decode(longNameData));
			} catch (Exception e) {
				return null;
			}
		}

		if (currEntry.isGlobalPaxHeader()) { /* Process Global Pax headers */
			readGlobalPaxHeaders();
		}

		if (currEntry.isPaxHeader()) { /* Process Pax headers */
			paxHeaders();
		} else if (!globalPaxHeaders.isEmpty()) {
			applyPaxHeadersToCurrentEntry(globalPaxHeaders);
		}

		if (currEntry.isOldGNUSparse()) { /* Process sparse files */
			if (logger != null) {
				logger.error(this, "getNextTarEntry", "ERROR readOldGNUSparse");
			}
		}

		/*
		 * If the size of the next element in the archive has changed due to a new size
		 * being reported in the posix header information, we update entrySize here so
		 * that it contains the correct value.
		 */
		entrySize = currEntry.getSize();

		return currEntry;
	}

	/**
	 * The last record block should be written at the full size, so skip any
	 * additional space used to fill a record after an entry
	 */
	private void skipRecordPadding() {
		if (!isDirectory() && this.entrySize > 0 && this.entrySize % this.recordSize != 0) {
			final long numRecords = (this.entrySize / this.recordSize) + 1;
			final long padding = (numRecords * this.recordSize) - this.entrySize;
			final long skipped = FileBuffer.skip(is, padding);
			count(skipped);
		}
	}

	/**
	 * Get the next entry in this tar archive as longname data.
	 *
	 * @return The next entry in the archive as longname data, or null.
	 */
	protected byte[] getLongNameData() {
		/* read in the name */
		final ByteArrayOutputStream longName = new ByteArrayOutputStream();
		int length = 0;
		try {
			while ((length = read(smallBuf)) >= 0) {
				longName.write(smallBuf, 0, length);
			}
		} catch (Exception e) {
			return null;
		}
		getNextEntry();
		if (currEntry == null) {
			return null;
		}
		byte[] longNameData = longName.toByteArray();
		/* remove trailing null terminator(s) */
		length = longNameData.length;
		while (length > 0 && longNameData[length - 1] == 0) {
			--length;
		}
		if (length != longNameData.length) {
			final byte[] l = new byte[length];
			System.arraycopy(longNameData, 0, l, 0, length);
			longNameData = l;
		}
		return longNameData;
	}

	/**
	 * Get the next record in this tar archive. This will skip over any remaining
	 * data in the current entry, if there is one, and place the input stream at the
	 * header of the next entry.
	 *
	 * <p>
	 * If there are no more entries in the archive, null will be returned to
	 * indicate that the end of the archive has been reached. At the same time the
	 * {@code hasHitEOF} marker will be set to true.
	 * </p>
	 *
	 * @return The next header in the archive, or null.
	 */
	private byte[] getRecord() {
		byte[] headerBuf = readRecord();
		setAtEOF(isEOFRecord(headerBuf));
		if (isAtEOF() && headerBuf != null) {
			tryToConsumeSecondEOFRecord();
			consumeRemainderOfLastBlock();
			headerBuf = null;
		}
		return headerBuf;
	}

	/**
	 * Determine if an archive record indicate End of Archive. End of archive is
	 * indicated by a record that consists entirely of null bytes.
	 *
	 * @param record The record data to check.
	 * @return true if the record data is an End of Archive
	 */
	protected boolean isEOFRecord(byte[] record) {
		return record == null || TarUtils.isArrayZero(record, recordSize);
	}

	/**
	 * Read a record from the input stream and return the data.
	 *
	 * @return The record data or null if EOF has been hit.
	 */
	protected byte[] readRecord() {

		final byte[] record = new byte[recordSize];

		final int readNow = FileBuffer.readFully(is, record);
		count(readNow);
		if (readNow != recordSize) {
			return null;
		}

		return record;
	}

	private void readGlobalPaxHeaders() {
		globalPaxHeaders = parsePaxHeaders(this);
		getNextEntry(); /* Get the actual file entry */
	}

	private void paxHeaders() {
		final Map<String, String> headers = parsePaxHeaders(this);
		getNextEntry(); /* Get the actual file entry */
		applyPaxHeadersToCurrentEntry(headers);
	}

	/**
	 * NOTE, using a Map here makes it impossible to ever support GNU sparse files
	 * using the PAX Format 0.0
	 * 
	 * @see https://www.gnu.org/software/tar/manual/html_section/tar_92.html#SEC188
	 */
	Map<String, String> parsePaxHeaders(final InputStream i) {
		final Map<String, String> headers = new HashMap<String, String>(globalPaxHeaders);
		/* Format is length keyword=value */
		int ch = 0;
		do {
			int len = 0;
			int read = 0;
			try {
				while ((ch = i.read()) != -1) {
					read++;
					if (ch == '\n') { /* blank line in header */
						break;
					} else if (ch == ' ') { /* End of length string */
						/* Get keyword */
						final ByteArrayOutputStream coll = new ByteArrayOutputStream();
						while ((ch = i.read()) != -1) {
							read++;
							if (ch == '=') { /* end of keyword */
								final String keyword = coll.toString("UTF_8");
								/* Get rest of entry */
								final int restLen = len - read;
								if (restLen == 1) { /* only NL */
									headers.remove(keyword);
								} else {
									final byte[] rest = new byte[restLen];
									final int got = FileBuffer.readFully(i, rest);
									if (got != restLen) {
										return null;
									}
									/* Drop trailing NL */
									final String value = new String(rest, 0, restLen - 1, Charset.forName("UTF_8"));
									headers.put(keyword, value);
								}
								break;
							}
							coll.write((byte) ch);
						}
						break; /* Processed single header */
					}
					len *= 10;
					len += ch - '0';
				}
			} catch (Exception e) {
				return null;
			}
			if (ch == -1) { /* EOF */
				break;
			}
		} while (ch != -1);
		return headers;
	}

	private void applyPaxHeadersToCurrentEntry(final Map<String, String> headers) {
		currEntry.updateEntryFromPaxHeaders(headers);

	}

	private boolean isDirectory() {
		return currEntry != null && currEntry.isDirectory();
	}

	/**
	 * Returns the next Archive Entry in this Stream.
	 *
	 * @return the next entry, or {@code null} if there are no more entries
	 */
	public TarArchiveEntry getNextEntry() {
		return getNextTarEntry();
	}

	/**
	 * Tries to read the next record rewinding the stream if it is not a EOF record.
	 *
	 * <p>
	 * This is meant to protect against cases where a tar implementation has written
	 * only one EOF record when two are expected. Actually this won't help since a
	 * non-conforming implementation likely won't fill full blocks consisting of -
	 * by default - ten records either so we probably have already read beyond the
	 * archive anyway.
	 * 
	 * @return Success
	 *         </p>
	 */
	private boolean tryToConsumeSecondEOFRecord() {
		boolean shouldReset = true;
		final boolean marked = is.markSupported();
		if (marked) {
			is.mark(recordSize);
		}
		try {
			shouldReset = !isEOFRecord(readRecord());
		} finally {
			if (shouldReset && marked) {
				pushedBackBytes(recordSize);
				try {

					is.reset();
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Reads bytes from the current tar archive entry.
	 *
	 * This method is aware of the boundaries of the current entry in the archive
	 * and will deal with them as if they were this stream's start and EOF.
	 *
	 * @param buf       The buffer into which to place bytes read.
	 * @param offset    The offset at which to place bytes read.
	 * @param numToRead The number of bytes to read.
	 * @return The number of bytes read, or -1 at EOF.
	 */
	@Override
	public int read(byte[] buf, final int offset, int numToRead) {
		int totalRead = 0;

		if (isAtEOF() || isDirectory() || entryOffset >= entrySize) {
			return -1;
		}

		if (currEntry == null) {
			return -1;
		}

		try {
			numToRead = Math.min(numToRead, available());
			totalRead = is.read(buf, offset, numToRead);
		} catch (IOException e) {
			return -1;
		}

		if (totalRead == -1) {
			if (numToRead > 0) {
				return -1;
			}
			setAtEOF(true);
		} else {
			count(totalRead);
			entryOffset += totalRead;
		}
		return totalRead;
	}

	/**
	 * Whether this class is able to read the given entry.
	 *
	 * @param ae The TarArchiveEntry
	 * @return success
	 */
	public boolean canReadEntryData(TarArchiveEntry ae) {
		if (ae instanceof TarArchiveEntry) {
			final TarArchiveEntry te = (TarArchiveEntry) ae;
			return !te.isSparse();
		}
		return false;
	}

	/**
	 * Get the current TAR Archive Entry that this input stream is processing
	 *
	 * @return The current Archive Entry
	 */
	public TarArchiveEntry getCurrentEntry() {
		return currEntry;
	}

	protected final void setCurrentEntry(final TarArchiveEntry e) {
		currEntry = e;
	}

	protected final boolean isAtEOF() {
		return hasHitEOF;
	}

	protected final void setAtEOF(final boolean b) {
		hasHitEOF = b;
	}

	/**
	 * This method is invoked once the end of the archive is hit, it tries to
	 * consume the remaining bytes under the assumption that the tool creating this
	 * archive has padded the last block.
	 */
	private void consumeRemainderOfLastBlock() {
		final long bytesReadOfLastBlock = getBytesRead() % blockSize;
		if (bytesReadOfLastBlock > 0) {
			final long skipped = FileBuffer.skip(is, blockSize - bytesReadOfLastBlock);
			count(skipped);
		}
	}

	/**
	 * Checks if the signature matches what is expected for a tar file.
	 *
	 * @param signature the bytes to check
	 * @param length    the number of bytes to check
	 * @return true, if this stream is a tar archive stream, false otherwise
	 */
	public static boolean matches(byte[] signature, int length) {
		if (length < TarUtils.VERSION_OFFSET + TarUtils.VERSIONLEN) {
			return false;
		}

		if (TarUtils.matchAsciiBuffer(TarUtils.MAGIC_POSIX, signature, TarUtils.MAGIC_OFFSET, TarUtils.MAGICLEN)
				&& TarUtils.matchAsciiBuffer(TarUtils.VERSION_POSIX, signature, TarUtils.VERSION_OFFSET,
						TarUtils.VERSIONLEN)) {
			return true;
		}
		if (TarUtils.matchAsciiBuffer(TarUtils.MAGIC_GNU, signature, TarUtils.MAGIC_OFFSET, TarUtils.MAGICLEN)
				&& (TarUtils.matchAsciiBuffer(TarUtils.VERSION_GNU_SPACE, signature, TarUtils.VERSION_OFFSET,
						TarUtils.VERSIONLEN)
						|| TarUtils.matchAsciiBuffer(TarUtils.VERSION_GNU_ZERO, signature, TarUtils.VERSION_OFFSET,
								TarUtils.VERSIONLEN))) {
			return true;
		}
		/* COMPRESS-107 - recognise Ant tar files */
		return TarUtils.matchAsciiBuffer(TarUtils.MAGIC_ANT, signature, TarUtils.MAGIC_OFFSET, TarUtils.MAGICLEN)
				&& TarUtils.matchAsciiBuffer(TarUtils.VERSION_ANT, signature, TarUtils.VERSION_OFFSET,
						TarUtils.VERSIONLEN);
	}

	/**
	 * Increments the counter of already read bytes. Doesn't increment if the EOF
	 * has been hit (read == -1)
	 *
	 * @param read the number of bytes read
	 */
	protected void count(int read) {
		count((long) read);
	}

	/**
	 * Decrements the counter of already read bytes.
	 *
	 * @param pushedBack the number of bytes pushed back.
	 * @since 1.1
	 */
	protected void pushedBackBytes(long pushedBack) {
		bytesRead -= pushedBack;
	}

	/**
	 * Increments the counter of already read bytes. Doesn't increment if the EOF
	 * has been hit (read == -1)
	 *
	 * @param read the number of bytes read
	 * @since 1.1
	 */
	protected void count(long read) {
		if (read != -1) {
			bytesRead = bytesRead + read;
		}
	}

	/**
	 * Returns the current number of bytes read from this stream.
	 * 
	 * @return the number of read bytes
	 * @since 1.1
	 */
	public long getBytesRead() {
		return bytesRead;
	}

	/**
	 * Reads a byte of data. This method will block until enough input is available.
	 *
	 * Simply calls the {@link #read(byte[], int, int)} method.
	 *
	 * MUST be overridden if the {@link #read(byte[], int, int)} method is not
	 * overridden; may be overridden otherwise.
	 *
	 * @return the byte read, or -1 if end of input is reached
	 */
	@Override
	public int read() {
		final int num = read(single, 0, 1);
		return num == -1 ? -1 : single[0] & BYTE_MASK;
	}
}
