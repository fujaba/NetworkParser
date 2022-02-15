/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.uniks.networkparser.ext.io;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.uniks.networkparser.SimpleException;

/**
 * This class represents an entry in a Tar archive. It consists of the entry is
 * header, as well as the entry is File. Entries can be instantiated in one of
 * three ways, depending on how they are to be used.
 * <p>
 * TarEntries that are created from the header bytes read from an archive are
 * instantiated with the TarEntry( byte[] ) constructor. These entries will be
 * used when extracting from or listing the contents of an archive. These
 * entries have their header filled in using the header bytes. They also set the
 * File to null, since they reference an archive entry not a file.
 * <p>
 * TarEntries that are created from Files that are to be written into an archive
 * are instantiated with the TarEntry( File ) constructor. These entries have
 * their header filled in using the File is information. They also keep a
 * reference to the File for convenience when writing entries.
 * <p>
 * Finally, TarEntries can be constructed from nothing but a name. This allows
 * the programmer to construct the entry by hand, for instance when only an
 * InputStream is available for writing to the archive, and the header
 * information is constructed from other information. In this case the header
 * fields are set to defaults and the File is set to null.
 *
 * <p>
 * The C structure for a Tar Entry is header is:
 *
 * <pre>
 * struct header {
 * char name[100];     TarConstants.NAMELEN    - offset   0
 * char mode[8];       TarConstants.MODELEN    - offset 100
 * char uid[8];        TarConstants.UIDLEN     - offset 108
 * char gid[8];        TarConstants.GIDLEN     - offset 116
 * char size[12];      TarConstants.SIZELEN    - offset 124
 * char mtime[12];     TarConstants.MODTIMELEN - offset 136
 * char chksum[8];     TarConstants.CHKSUMLEN  - offset 148
 * char linkflag[1];                           - offset 156
 * char linkname[100]; TarConstants.NAMELEN    - offset 157
 * The following fields are only present in new-style POSIX tar archives:
 * char magic[6];      TarConstants.MAGICLEN   - offset 257
 * char version[2];    TarConstants.VERSIONLEN - offset 263
 * char uname[32];     TarConstants.UNAMELEN   - offset 265
 * char gname[32];     TarConstants.GNAMELEN   - offset 297
 * char devmajor[8];   TarConstants.DEVLEN     - offset 329
 * char devminor[8];   TarConstants.DEVLEN     - offset 337
 * char prefix[155];   TarConstants.PREFIXLEN  - offset 345
 * Used if "name" field is not long enough to hold the path
 * char pad[12];       NULs                    - offset 500
 * } header;
 * All unused bytes are set to null.
 * New-style GNU tar files are slightly different from the above.
 * For values of size larger than 077777777777L (11 7s)
 * or uid and gid larger than 07777777L (7 7s)
 * the sign bit of the first byte is set, and the rest of the
 * field is the binary representation of the number.
 * See TarUtils.parseOctalOrBinary.
 * </pre>
 *
 * <p>
 * The C structure for a old GNU Tar Entry is header is:
 *
 * <pre>
 * struct oldgnu_header {
 * char unused_pad1[345]; TarConstants.PAD1LEN_GNU       - offset 0
 * char atime[12];        TarConstants.ATIMELEN_GNU      - offset 345
 * char ctime[12];        TarConstants.CTIMELEN_GNU      - offset 357
 * char offset[12];       TarConstants.OFFSETLEN_GNU     - offset 369
 * char longnames[4];     TarConstants.LONGNAMESLEN_GNU  - offset 381
 * char unused_pad2;      TarConstants.PAD2LEN_GNU       - offset 385
 * struct sparse sp[4];   TarConstants.SPARSELEN_GNU     - offset 386
 * char isextended;       TarConstants.ISEXTENDEDLEN_GNU - offset 482
 * char realsize[12];     TarConstants.REALSIZELEN_GNU   - offset 483
 * char unused_pad[17];   TarConstants.PAD3LEN_GNU       - offset 495
 * };
 * </pre>
 *
 * Whereas, "struct sparse" is:
 *
 * <pre>
 * struct sparse {
 * char offset[12];   offset 0
 * char numbytes[12]; offset 12
 * };
 * </pre>
 *
 * <p>
 * The C structure for a xstar (Joerg Schilling star) Tar Entry is header is:
 *
 * <pre>
 * struct star_header {
 *  char name[100];		offset   0
 *  char mode[8];		offset 100
 *  char uid[8];		offset 108
 *  char gid[8];		offset 116
 *  char size[12];		offset 124
 *  char mtime[12];		offset 136
 *  char chksum[8];		offset 148
 *  char typeflag;		offset 156
 *  char linkname[100];	offset 157
 *  char magic[6];		offset 257
 *  char version[2];	offset 263
 *  char uname[32];		offset 265
 *  char gname[32];		offset 297
 *  char devmajor[8];	offset 329
 *  char devminor[8];	offset 337
 *  char prefix[131];	offset 345
 *  char atime[12];     offset 476
 *  char ctime[12];     offset 488
 *  char mfill[8];      offset 500
 *  char xmagic[4];     offset 508  "tar"
 * };
 * </pre>
 * <p>
 * which is identical to new-style POSIX up to the first 130 bytes of the
 * prefix.
 * </p>
 * @author Stefan
 */
public class TarArchiveEntry {
	/** The entry is name. */
	private String name = "";

	/** Whether to allow leading slashes or drive names inside the name */
	private boolean preserveAbsolutePath;

	/** The entry is permission mode. */
	private int mode;

	/** The entry is user id. */
	private long userId = 0;

	/** The entry is group id. */
	private long groupId = 0;

	/** The entry is size. */
	private long size = 0;

	/** The entry is modification time. */
	private long modTime;

	/** If the header checksum is reasonably correct. */
	private boolean checkSumOK;

	/** The entry is link flag. */
	private byte linkFlag;

	/** The entry is link name. */
	private String linkName = "";

	/** The entry is magic tag. */
	private String magic = TarUtils.MAGIC_POSIX;
	/** The version of the format */
	private String version = TarUtils.VERSION_POSIX;

	/** The entry is user name. */
	private String userName;

	/** The entry is group name. */
	private String groupName = "";

	/** The entry is major device number. */
	private int devMajor = 0;

	/** The entry is minor device number. */
	private int devMinor = 0;

	/** If an extension sparse header follows. */
	private boolean isExtended;

	/** The entry is real size in case of a sparse file. */
	private long realSize;

	/** is this entry a GNU sparse entry using one of the PAX formats? */
	private boolean paxGNUSparse;

	/** is this entry a star sparse entry using the PAX header? */
	private boolean starSparse;

	/** Extra, user supplied pax headers */
	private final Map<String, String> extraPaxHeaders = new HashMap<String, String>();

    private File file;

	/**  Maximum length of a user is name in the tar file. */
	public static final int MAX_NAMELEN = 31;

	/**  Default permissions bits for directories. */
	public static final int DEFAULT_DIR_MODE = 040755;

	/**  Default permissions bits for files. */
	public static final int DEFAULT_FILE_MODE = 0100644;

	/**  Convert millis to seconds. */
	public static final int MILLIS_PER_SECOND = 1000;

	/**
	 * Construct an empty entry.
	 */
	public TarArchiveEntry() {
		String user = System.getProperty("user.name", "");

		if (user.length() > MAX_NAMELEN) {
			user = user.substring(0, MAX_NAMELEN);
		}
		this.userName = user;
	}
	
	/**
	 * With absolute path.
	 *
	 * @param value the value
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withAbsolutePath(boolean value) {
	    this.preserveAbsolutePath = value;
	    return this;
	}

	/**
	 * With link flag.
	 *
	 * @param linkFlag             the entry link flag.
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withLinkFlag(byte linkFlag) {
		this.linkFlag = linkFlag;
		if (linkFlag == TarUtils.LF_GNUTYPE_LONGNAME) {
			magic = TarUtils.MAGIC_GNU;
			version = TarUtils.VERSION_GNU_SPACE;
		}
		return this;
	}
	
	/**
	 * With file.
	 *
	 * @param value the value
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withFile(File value) {
	    this.file = value;
	    return this;                                                                                                
	}

	/**
	 * Determine if the two entries are equal. Equality is determined by the header
	 * names being equal.
	 *
	 * @param it Entry to be checked for equality.
	 * @return True if the entries are equal.
	 */
	public boolean equals(TarArchiveEntry it) {
		return it != null && getName().equals(it.getName());
	}

	/**
	 * Determine if the two entries are equal. Equality is determined by the header
	 * names being equal.
	 *
	 * @param it Entry to be checked for equality.
	 * @return True if the entries are equal.
	 */
	@Override
	public boolean equals(Object it) {
		if (it == null || getClass() != it.getClass()) {
			return false;
		}
		return equals((TarArchiveEntry) it);
	}

	/**
	 * Hashcodes are based on entry names.
	 *
	 * @return the entry hashcode
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Determine if the given entry is a descendant of this entry. Descendancy is
	 * determined by the name of the descendant starting with this entry is name.
	 *
	 * @param desc Entry to be checked as a descendent of this.
	 * @return True if entry is a descendant of this.
	 */
	public boolean isDescendent(TarArchiveEntry desc) {
		if (desc == null) {
			return false;
		}
		if (this.name == null) {
            return false;
        }
		return name.startsWith(desc.getName());
	}

	/**
	 * Get this entry is name.
	 *
	 * <p>
	 * This method returns the raw name as it is stored inside of the archive.
	 * </p>
	 *
	 * @return This entry is name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set this entry is name.
	 *
	 * @param name This entry is new name.
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withName(String name) {
		this.name = normalizeFileName(name, this.preserveAbsolutePath);
		return this;
	}

	/**
	 * Set the mode for this entry.
	 *
	 * @param mode the mode for this entry
	 * @return ThisComponent
	 */
	public TarArchiveEntry withMode(int mode) {
		this.mode = mode;
		return this;
	}

	/**
	 * Get this entry is link name.
	 *
	 * @return This entry is link name.
	 */
	public String getLinkName() {
		return linkName;
	}

	/**
	 * Set this entry is link name.
	 *
	 * @param link the link name to use.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withLinkName(String link) {
		this.linkName = link;
		return this;
	}

	/**
	 * Get this entry is user id.
	 *
	 * @return This entry is user id.
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Set this entry is user id.
	 *
	 * @param userId This entry is new user id.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withUserId(long userId) {
		this.userId = userId;
		return this;
	}

	/**
	 * Get this entry is group id.
	 *
	 * @return This entry is group id.
	 */
	public long getGroupId() {
		return groupId;
	}

	/**
	 * Set this entry is group id.
	 *
	 * @param groupId This entry is new group id.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withGroupId(long groupId) {
		this.groupId = groupId;
		return this;
	}

	/**
	 * Get this entry is user name.
	 *
	 * @return This entry is user name.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set this entry is user name.
	 *
	 * @param userName This entry is new user name.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withUserName(String userName) {
		this.userName = userName;
		return this;
	}

	/**
	 * Get this entry is group name.
	 *
	 * @return This entry is group name.
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Set this entry is group name.
	 *
	 * @param groupName This entry is new group name.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	/**
	 * Convenience method to set this entry is group and user ids.
	 *
	 * @param userId  This entry is new user id.
	 * @param groupId This entry is new group id.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withIds(int userId, int groupId) {
		withUserId(userId);
		return withGroupId(groupId);
	}

	/**
	 * Convenience method to set this entry is group and user names.
	 *
	 * @param userName  This entry is new user name.
	 * @param groupName This entry is new group name.
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withNames(String userName, String groupName) {
		withUserName(userName);
		return withGroupName(groupName);
	}

	/**
	 * Set this entry is modification time. The parameter passed to this method is
	 * in "Java time".
	 *
	 * @param time This entry is new modification time.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withModTime(long time) {
		modTime = time / MILLIS_PER_SECOND;
		return this;
	}

	/**
	 * Set this entry is modification time.
	 *
	 * @param time This entry is new modification time.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withModTime(Date time) {
		if (time != null) {
			modTime = time.getTime() / MILLIS_PER_SECOND;
		}
		return this;
	}

	/**
	 * Set this entry is modification time.
	 *
	 * @return time This entry is new modification time.
	 */
	public Date getModTime() {
		return new Date(modTime * MILLIS_PER_SECOND);
	}

	/**
	 * Gets the last modified date.
	 *
	 * @return the last modified date
	 */
	public Date getLastModifiedDate() {
		return getModTime();
	}

	/**
	 * Get this entry is checksum status.
	 *
	 * @return if the header checksum is reasonably correct
	 * @see TarUtils#verifyCheckSum(byte[])
	 */
	public boolean isCheckSumOK() {
		return checkSumOK;
	}

	/**
	 * Get this entry is file.
	 *
	 * <p>
	 * This method is only useful for entries created from a {@code
	 * File} but not for entries read from an archive.
	 * </p>
	 *
	 * @return This entry is file.
	 */
	public File getFile() {
        return file;
	}

	/**
	 * Get this entry is mode.
	 *
	 * @return This entry is mode.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Get this entry is file size.
	 *
	 * @return This entry is file size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Set this entry is file size.
	 *
	 * @param size This entry is new file size.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withSize(long size) {
		if (size >= 0) {
			this.size = size;
		}
		return this;
	}

	/**
	 * Get this entry is major device number.
	 *
	 * @return This entry is major device number.
	 */
	public int getDevMajor() {
		return devMajor;
	}

	/**
	 * Set this entry is major device number.
	 *
	 * @param devNo This entry is major device number.
	 * @return the tar archive entry
	 */
	public TarArchiveEntry withDevMajor(int devNo) {
		if (devNo >= 0) {
			this.devMajor = devNo;
		}
		return this;
	}

	/**
	 * Get this entry is minor device number.
	 *
	 * @return This entry is minor device number.
	 */
	public int getDevMinor() {
		return devMinor;
	}

	/**
	 * Set this entry is minor device number.
	 *
	 * @param devNo This entry is minor device number.
	 * @return ThisComponent
	 */
	public TarArchiveEntry withDevMinor(int devNo) {
		if (devNo >= 0) {
			this.devMinor = devNo;
		}
		return this;
	}

	/**
	 * Indicates in case of an oldgnu sparse file if an extension sparse header
	 * follows.
	 *
	 * @return true if an extension oldgnu sparse header follows.
	 */
	public boolean isExtended() {
		return isExtended;
	}

	/**
	 * Get this entry is real file size in case of a sparse file.
	 *
	 * @return This entry is real file size.
	 */
	public long getRealSize() {
		return realSize;
	}

	/**
	 * Indicate if this entry is a GNU sparse block.
	 *
	 * @return true if this is a sparse extension provided by GNU tar
	 */
	public boolean isGNUSparse() {
		return isOldGNUSparse() || isPaxGNUSparse();
	}

	/**
	 * Indicate if this entry is a GNU or star sparse block using the oldgnu format.
	 *
	 * @return true if this is a sparse extension provided by GNU tar or star
	 */
	public boolean isOldGNUSparse() {
		return linkFlag == TarUtils.LF_GNUTYPE_SPARSE;
	}

	/**
	 * Indicate if this entry is a GNU sparse block using one of the PAX formats.
	 *
	 * @return true if this is a sparse extension provided by GNU tar
	 */
	public boolean isPaxGNUSparse() {
		return paxGNUSparse;
	}

	/**
	 * Indicate if this entry is a star sparse block using PAX headers.
	 *
	 * @return true if this is a sparse extension provided by star
	 */
	public boolean isStarSparse() {
		return starSparse;
	}

	/**
	 * Indicate if this entry is a GNU long linkname block.
	 *
	 * @return true if this is a long name extension provided by GNU tar
	 */
	public boolean isGNULongLinkEntry() {
		return linkFlag == TarUtils.LF_GNUTYPE_LONGLINK;
	}

	/**
	 * Indicate if this entry is a GNU long name block.
	 *
	 * @return true if this is a long name extension provided by GNU tar
	 */
	public boolean isGNULongNameEntry() {
		return linkFlag == TarUtils.LF_GNUTYPE_LONGNAME;
	}

	/**
	 * Check if this is a Pax header.
	 *
	 * @return if this is a Pax header.
	 *
	 */
	public boolean isPaxHeader() {
		return linkFlag == TarUtils.LF_PAX_EXTENDED_HEADER_LC || linkFlag == TarUtils.LF_PAX_EXTENDED_HEADER_UC;
	}

	/**
	 * Check if this is a Pax header.
	 *
	 * @return if this is a Pax header.
	 */
	public boolean isGlobalPaxHeader() {
		return linkFlag == TarUtils.LF_PAX_GLOBAL_EXTENDED_HEADER;
	}

	/**
	 * Return whether or not this entry represents a directory.
	 *
	 * @return True if this entry is a directory.
	 */
	public boolean isDirectory() {
		if (file != null) {
			return file.isDirectory();
		}

		if (linkFlag == TarUtils.LF_DIR) {
			return true;
		}

		return !isPaxHeader() && !isGlobalPaxHeader() && getName().endsWith("/");
	}

	/**
	 * Check if this is a "normal file".
	 *
	 * @return whether this is a "normal file"
	 */
	public boolean isFile() {
		if (file != null) {
			return file.isFile();
		}
		if (linkFlag == TarUtils.LF_OLDNORM || linkFlag == TarUtils.LF_NORMAL) {
			return true;
		}
		return !getName().endsWith("/");
	}

	/**
	 * Check if this is a symbolic link entry.
	 *
	 * @return whether this is a symbolic link
	 */
	public boolean isSymbolicLink() {
		return linkFlag == TarUtils.LF_SYMLINK;
	}

	/**
	 * Check if this is a link entry.
	 *
	 * @return whether this is a link entry
	 */
	public boolean isLink() {
		return linkFlag == TarUtils.LF_LINK;
	}

	/**
	 * Check if this is a character device entry.
	 *
	 * @return whether this is a character device
	 */
	public boolean isCharacterDevice() {
		return linkFlag == TarUtils.LF_CHR;
	}

	/**
	 * Check if this is a block device entry.
	 *
	 * @return whether this is a block device
	 */
	public boolean isBlockDevice() {
		return linkFlag == TarUtils.LF_BLK;
	}

	/**
	 * Check if this is a FIFO (pipe) entry.
	 *
	 * @return whether this is a FIFO entry
	 */
	public boolean isFIFO() {
		return linkFlag == TarUtils.LF_FIFO;
	}

	/**
	 * Check whether this is a sparse entry.
	 *
	 * @return whether this is a sparse entry
	 */
	public boolean isSparse() {
		return isGNUSparse() || isStarSparse();
	}

	/**
	 * get extra PAX Headers.
	 *
	 * @return read-only map containing any extra PAX Headers
	 */
	public Map<String, String> getExtraPaxHeaders() {
		return Collections.unmodifiableMap(extraPaxHeaders);
	}

	/**
	 * clear all extra PAX headers.
	 *
	 */
	public void clearExtraPaxHeaders() {
		extraPaxHeaders.clear();
	}

	/**
	 * add a PAX header to this entry. If the header corresponds to an existing
	 * field in the entry, that field will be set; otherwise the header will be
	 * added to the extraPaxHeaders Map
	 *
	 * @param name  The full name of the header to set.
	 * @param value value of header.
	 */
	public void addPaxHeader(String name, String value) {
		processPaxHeader(name, value);
	}

	/**
	 * get named extra PAX header.
	 *
	 * @param name The full name of an extended PAX header to retrieve
	 * @return The value of the header, if any.
	 */
	public String getExtraPaxHeader(String name) {
		return extraPaxHeaders.get(name);
	}

	/**
	 * Update the entry using a map of pax headers.
	 *
	 * @param headers
	 */
	void updateEntryFromPaxHeaders(Map<String, String> headers) {
		if (headers == null) {
			return;
		}
		for (Map.Entry<String, String> ent : headers.entrySet()) {
			final String key = ent.getKey();
			final String val = ent.getValue();
			processPaxHeader(key, val, headers);
		}
	}

	/**
	 * process one pax header, using the entries extraPaxHeaders map as source for
	 * extra headers used when handling entries for sparse files.
	 *
	 * @param key The Header key
	 * @param val The Header value
	 */
	private void processPaxHeader(String key, String val) {
		processPaxHeader(key, val, extraPaxHeaders);
	}

	/**
	 * Process one pax header, using the supplied map as source for extra headers to
	 * be used when handling entries for sparse files
	 *
	 * @param key     the header name.
	 * @param val     the header value.
	 * @param headers map of headers used for dealing with sparse file.
	 * @return success
	 */
	private TarArchiveEntry processPaxHeader(String key, String val, Map<String, String> headers) {
		if ("path".equals(key)) {
			return withName(val);
		}
		if ("linkpath".equals(key)) {
			return withLinkName(val);
		}
		if ("gid".equals(key)) {
			return withGroupId(Long.parseLong(val));
		}
		if ("gname".equals(key)) {
			return withGroupName(val);
		}
		if ("uid".equals(key)) {
			return withUserId(Long.parseLong(val));
		}
		if ("uname".equals(key)) {
		    return withUserName(val);
		}
		if ("size".equals(key)) {
			return withSize(Long.parseLong(val));
		}
		if ("mtime".equals(key)) {
			return withModTime((long) (Double.parseDouble(val) * 1000));
		}
		if ("SCHILY.devminor".equals(key)) {
			return withDevMinor(Integer.parseInt(val));
		}
		if ("SCHILY.devmajor".equals(key)) {
			return withDevMajor(Integer.parseInt(val));
		}
		if ("GNU.sparse.size".equals(key)) {
			fillGNUSparse0xData(headers);
			return this;
		}
		if ("GNU.sparse.realsize".equals(key)) {
			fillGNUSparse1xData(headers);
			return this;
		}
		if ("SCHILY.filetype".equals(key)) {
			if ("sparse".equals(val)) {
				fillStarSparseData(headers);
			}
			return this;
		}
		extraPaxHeaders.put(key, val);
		return this;
	}

	/**
	 * Write an entry is header information to a header buffer.
	 *
	 * <p>
	 * This method does not use the star/GNU tar/BSD tar extensions.
	 * </p>
	 *
	 * @param outbuf The tar entry header buffer to fill in.
	 */
	public void writeEntryHeader(byte[] outbuf) {
		writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
	}

	/**
	 * Write an entry is header information to a header buffer.
	 *
	 * @param outbuf   The tar entry header buffer to fill in.
	 * @param encoding encoding to use when writing the file name.
	 * @param starMode whether to use the star/GNU tar/BSD tar extension for numeric
	 *                 fields if their value does not fit in the maximum size of
	 *                 standard tar archives
	 */
	public void writeEntryHeader(byte[] outbuf, NioZipEncoding encoding, boolean starMode) {
		int offset = 0;

		offset = TarUtils.formatNameBytes(name, outbuf, offset, TarUtils.NAMELEN, encoding);
		offset = writeEntryHeaderField(mode, outbuf, offset, TarUtils.MODELEN, starMode);
		offset = writeEntryHeaderField(userId, outbuf, offset, TarUtils.UIDLEN, starMode);
		offset = writeEntryHeaderField(groupId, outbuf, offset, TarUtils.GIDLEN, starMode);
		offset = writeEntryHeaderField(size, outbuf, offset, TarUtils.SIZELEN, starMode);
		offset = writeEntryHeaderField(modTime, outbuf, offset, TarUtils.MODTIMELEN, starMode);

		final int csOffset = offset;
		if(outbuf == null || offset<0 || offset>=outbuf.length)  {
			return;
		}
		for (int c = 0; c < TarUtils.CHKSUMLEN; ++c) {
			outbuf[offset++] = (byte) ' ';
		}

		outbuf[offset++] = linkFlag;
		offset = TarUtils.formatNameBytes(linkName, outbuf, offset, TarUtils.NAMELEN, encoding);
		offset = TarUtils.formatNameBytes(magic, outbuf, offset, TarUtils.MAGICLEN);
		offset = TarUtils.formatNameBytes(version, outbuf, offset, TarUtils.VERSIONLEN);
		offset = TarUtils.formatNameBytes(userName, outbuf, offset, TarUtils.UNAMELEN, encoding);
		offset = TarUtils.formatNameBytes(groupName, outbuf, offset, TarUtils.GNAMELEN, encoding);
		offset = writeEntryHeaderField(devMajor, outbuf, offset, TarUtils.DEVLEN, starMode);
		offset = writeEntryHeaderField(devMinor, outbuf, offset, TarUtils.DEVLEN, starMode);

		while (offset < outbuf.length) {
			outbuf[offset++] = 0;
		}

		final long chk = TarUtils.computeCheckSum(outbuf);

		TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, TarUtils.CHKSUMLEN);
	}

	private int writeEntryHeaderField(long value, byte[] outbuf, final int offset, final int length,
			final boolean starMode) {
		if (starMode == false && (value < 0 || value >= 1L << 3 * (length - 1))) {
			/*
			 * value does not fit into field when written as octal number, will be written
			 * to PAX header or causes an error
			 */
			return TarUtils.formatLongOctalBytes(0, outbuf, offset, length);
		}
		return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
	}

	/**
	 * Parse an entry is header information from a header buffer.
	 *
	 * @param header The tar entry header buffer to get information from.
	 * @return the tar archive entry
	 */
	public TarArchiveEntry parseTarHeader(byte[] header) {
		try {
			parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
		} catch (final SimpleException ex) {
			parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
		}
		return this;
	}

	/**
	 * Parse an entry is header information from a header buffer.
	 *
	 * @param header   The tar entry header buffer to get information from.
	 * @param encoding encoding to use for file names
	 * @return the tar archive entry
	 */
	public TarArchiveEntry parseTarHeader(byte[] header, NioZipEncoding encoding) {
		parseTarHeader(header, encoding, false);
		return this;
	}

	private boolean parseTarHeader(byte[] header, NioZipEncoding encoding, boolean oldStyle) {
		int offset = 0;

		name = oldStyle ? TarUtils.parseName(header, offset, TarUtils.NAMELEN)
				: TarUtils.parseName(header, offset, TarUtils.NAMELEN, encoding);
		if (name == null) {
			return false;
		}
		offset += TarUtils.NAMELEN;
		mode = (int) TarUtils.parseOctalOrBinary(header, offset, TarUtils.MODELEN);
		offset += TarUtils.MODELEN;
		userId = (int) TarUtils.parseOctalOrBinary(header, offset, TarUtils.UIDLEN);
		offset += TarUtils.UIDLEN;
		groupId = (int) TarUtils.parseOctalOrBinary(header, offset, TarUtils.GIDLEN);
		offset += TarUtils.GIDLEN;
		size = TarUtils.parseOctalOrBinary(header, offset, TarUtils.SIZELEN);
		offset += TarUtils.SIZELEN;
		modTime = TarUtils.parseOctalOrBinary(header, offset, TarUtils.MODTIMELEN);
		offset += TarUtils.MODTIMELEN;
		checkSumOK = TarUtils.verifyCheckSum(header);
		offset += TarUtils.CHKSUMLEN;
		linkFlag = header[offset++];
		linkName = oldStyle ? TarUtils.parseName(header, offset, TarUtils.NAMELEN)
				: TarUtils.parseName(header, offset, TarUtils.NAMELEN, encoding);
		if (linkName == null) {
			return false;
		}
		offset += TarUtils.NAMELEN;
		magic = TarUtils.parseName(header, offset, TarUtils.MAGICLEN);
		if (magic == null) {
			return false;
		}
		offset += TarUtils.MAGICLEN;
		version = TarUtils.parseName(header, offset, TarUtils.VERSIONLEN);
		offset += TarUtils.VERSIONLEN;
		userName = oldStyle ? TarUtils.parseName(header, offset, TarUtils.UNAMELEN)
				: TarUtils.parseName(header, offset, TarUtils.UNAMELEN, encoding);
		if (userName == null) {
			return false;
		}
		offset += TarUtils.UNAMELEN;
		groupName = oldStyle ? TarUtils.parseName(header, offset, TarUtils.GNAMELEN)
				: TarUtils.parseName(header, offset, TarUtils.GNAMELEN, encoding);
		if (groupName == null) {
			return false;
		}
		offset += TarUtils.GNAMELEN;
		if (linkFlag == TarUtils.LF_CHR || linkFlag == TarUtils.LF_BLK) {
			devMajor = (int) TarUtils.parseOctalOrBinary(header, offset, TarUtils.DEVLEN);
			offset += TarUtils.DEVLEN;
			devMinor = (int) TarUtils.parseOctalOrBinary(header, offset, TarUtils.DEVLEN);
			offset += TarUtils.DEVLEN;
		} else {
			offset += 2 * TarUtils.DEVLEN;
		}

		final int type = evaluateType(header);
		switch (type) {
		case TarUtils.FORMAT_OLDGNU: {
			offset += TarUtils.ATIMELEN_GNU;
			offset += TarUtils.CTIMELEN_GNU;
			offset += TarUtils.OFFSETLEN_GNU;
			offset += TarUtils.LONGNAMESLEN_GNU;
			offset += TarUtils.PAD2LEN_GNU;
			offset += TarUtils.SPARSELEN_GNU;
			isExtended = TarUtils.parseBoolean(header, offset);
			offset += TarUtils.ISEXTENDEDLEN_GNU;
			realSize = TarUtils.parseOctal(header, offset, TarUtils.REALSIZELEN_GNU);
			offset += TarUtils.REALSIZELEN_GNU; /* NOSONAR - assignment as documentation */
			break;
		}
		case TarUtils.FORMAT_XSTAR: {
			final String xstarPrefix = oldStyle ? TarUtils.parseName(header, offset, TarUtils.PREFIXLEN_XSTAR)
					: TarUtils.parseName(header, offset, TarUtils.PREFIXLEN_XSTAR, encoding);
			if (xstarPrefix.length() > 0) {
				name = xstarPrefix + "/" + name;
			}
			break;
		}
		case TarUtils.FORMAT_POSIX:
		default: {
			final String prefix = oldStyle ? TarUtils.parseName(header, offset, TarUtils.PREFIXLEN)
					: TarUtils.parseName(header, offset, TarUtils.PREFIXLEN, encoding);
			/* SunOS tar -E does not add / to directory names, so fix up to be consistent */
			if (isDirectory() && !name.endsWith("/")) {
				name = name + "/";
			}
			if (prefix.length() > 0) {
				name = prefix + "/" + name;
			}
		}
		}
		return true;
	}

	/**
	 * Strips Windows drive letter as well as any leading slashes, turns path
	 * separators into forward slahes.
	 *
	 * @param fileName             FileName
	 * @param preserveAbsolutePath if absolutePath
	 * @return the new FileName
	 */
	private static String normalizeFileName(String fileName, boolean preserveAbsolutePath) {
		if (preserveAbsolutePath == false) {
			final String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

			if (osname != null) {

				/* Strip off drive letters! */
				/* REVIEW Would a better check be "(File.separator == \\)"? */

				if (osname.startsWith("windows")) {
					if (fileName != null && fileName.length() > 2) {
						final char ch1 = fileName.charAt(0);
						final char ch2 = fileName.charAt(1);

						if (ch2 == ':' && (ch1 >= 'a' && ch1 <= 'z' || ch1 >= 'A' && ch1 <= 'Z')) {
							fileName = fileName.substring(2);
						}
					}
				} else if (osname.contains("netware")) {
					final int colon = fileName.indexOf(':');
					if (colon != -1) {
						fileName = fileName.substring(colon + 1);
					}
				}
			}
		}

		if (fileName != null) {
			fileName = fileName.replace(File.separatorChar, '/');

			/*
			 * No absolute pathnames Windows (and Posix?) paths can start with
			 * "\\NetworkDrive\", so we loop on starting / s.
			 */
			while (preserveAbsolutePath == false && fileName.startsWith("/")) {
				fileName = fileName.substring(1);
			}
		}
		return fileName;
	}

	/**
	 * Evaluate an entry is header format from a header buffer.
	 *
	 * @param header The tar entry header buffer to evaluate the format for.
	 * @return format type
	 */
	private int evaluateType(byte[] header) {
		if (TarUtils.matchAsciiBuffer(TarUtils.MAGIC_GNU, header, TarUtils.MAGIC_OFFSET, TarUtils.MAGICLEN)) {
			return TarUtils.FORMAT_OLDGNU;
		}
		if (TarUtils.matchAsciiBuffer(TarUtils.MAGIC_POSIX, header, TarUtils.MAGIC_OFFSET, TarUtils.MAGICLEN)) {
			if (TarUtils.matchAsciiBuffer(TarUtils.MAGIC_XSTAR, header, TarUtils.XSTAR_MAGIC_OFFSET,
					TarUtils.XSTAR_MAGIC_LEN)) {
				return TarUtils.FORMAT_XSTAR;
			}
			return TarUtils.FORMAT_POSIX;
		}
		return 0;
	}

	void fillGNUSparse0xData(final Map<String, String> headers) {
		paxGNUSparse = true;
		if (headers == null) {
			return;
		}
		realSize = Integer.parseInt(headers.get("GNU.sparse.size"));
		if (headers.containsKey("GNU.sparse.name")) {
			/* version 0.1 */
			name = headers.get("GNU.sparse.name");
		}
	}

	void fillGNUSparse1xData(final Map<String, String> headers) {
		paxGNUSparse = true;
		if (headers != null) {
			realSize = Integer.parseInt(headers.get("GNU.sparse.realsize"));
			name = headers.get("GNU.sparse.name");
		}
	}

	void fillStarSparseData(final Map<String, String> headers) {
		starSparse = true;
		if (headers != null && headers.containsKey("SCHILY.realsize")) {
			realSize = Long.parseLong(headers.get("SCHILY.realsize"));
		}
	}
}
