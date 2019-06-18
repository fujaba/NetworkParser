package de.uniks.networkparser.bytes;

public class SHA2 {
	int digestLength = 32;
	int blockSize = 64;

	/* SHA-256 constants */
	int[] K = new int[] { 0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4,
			0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
			0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da, 0x983e5152,
			0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138,
			0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b, 0xc24b8b70,
			0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
			0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa,
			0xa4506ceb, 0xbef9a3f7, 0xc67178f2 };

	public int hashBlocks(int[] w, int[] v, byte[] p, int pos, int len) {
		if(w == null || v == null || p == null) {
			return -1;
		}
		int a, b, c, d, e, f, g, h, u, i, j, t1, t2;
		while (len >= 64) {
			a = v[0];
			b = v[1];
			c = v[2];
			d = v[3];
			e = v[4];
			f = v[5];
			g = v[6];
			h = v[7];

			for (i = 0; i < 16; i++) {
				j = pos + i * 4;
				w[i] = (((p[j] & 0xff) << 24) | ((p[j + 1] & 0xff) << 16) | ((p[j + 2] & 0xff) << 8)
						| (p[j + 3] & 0xff));
			}

			for (i = 16; i < 64; i++) {
				u = w[i - 2];
				t1 = (u >>> 17 | u << (32 - 17)) ^ (u >>> 19 | u << (32 - 19)) ^ (u >>> 10);

				u = w[i - 15];
				t2 = (u >>> 7 | u << (32 - 7)) ^ (u >>> 18 | u << (32 - 18)) ^ (u >>> 3);

				w[i] = (t1 + w[i - 7] | 0) + (t2 + w[i - 16] | 0);
			}

			for (i = 0; i < 64; i++) {
				t1 = (((((e >>> 6 | e << (32 - 6)) ^ (e >>> 11 | e << (32 - 11)) ^ (e >>> 25 | e << (32 - 25)))
						+ ((e & f) ^ (~e & g))) | 0) + ((h + ((K[i] + w[i]) | 0)) | 0)) | 0;

				t2 = (((a >>> 2 | a << (32 - 2)) ^ (a >>> 13 | a << (32 - 13)) ^ (a >>> 22 | a << (32 - 22)))
						+ ((a & b) ^ (a & c) ^ (b & c))) | 0;

				h = g;
				g = f;
				f = e;
				e = (d + t1) | 0;
				d = c;
				c = b;
				b = a;
				a = (t1 + t2) | 0;
			}

			v[0] += a;
			v[1] += b;
			v[2] += c;
			v[3] += d;
			v[4] += e;
			v[5] += f;
			v[6] += g;
			v[7] += h;

			pos += 64;
			len -= 64;
		}
		return pos;
	}

	/* Note: Int32Array is used instead of Uint32Array for performance reasons. */
	private int[] state = new int[8]; /* hash state */
	private int[] temp = new int[64]; /* temporary state */
	private byte[] buffer = new byte[128]; /* buffer for data to hash */
	private int bufferLength = 0; /* number of bytes in buffer */
	private int bytesHashed = 0; /* number of total bytes hashed */

	boolean finished; /* indicates whether the hash was finalized */

	public SHA2() {
		this.reset();
	}

	/* Resets hash state making it possible to re-use this instance to hash other data. */
	public SHA2 reset() {
		this.state[0] = 0x6a09e667;
		this.state[1] = 0xbb67ae85;
		this.state[2] = 0x3c6ef372;
		this.state[3] = 0xa54ff53a;
		this.state[4] = 0x510e527f;
		this.state[5] = 0x9b05688c;
		this.state[6] = 0x1f83d9ab;
		this.state[7] = 0x5be0cd19;
		this.bufferLength = 0;
		this.bytesHashed = 0;
		this.finished = false;
		return this;
	}

	/* Cleans internal buffers and re-initializes hash state. */
	public void clean() {
		for (int i = 0; i < this.buffer.length; i++) {
			this.buffer[i] = 0;
		}
		for (int i = 0; i < this.temp.length; i++) {
			this.temp[i] = 0;
		}
		this.reset();
	}

	public SHA2 update(String data) {
		if (data != null) {
			byte[] bytes = data.getBytes();
			return update(bytes, bytes.length);
		}
		return this;
	}

	/** Updates hash state with the given data.
	*
	* Optionally, length of the data can be specified to hash
	* fewer bytes than data.length.
	*
	* Throws error when trying to update already finalized hash:
	* instance must be reset to use it again.
	* @param data Data to Ecnoding
	* @param dataLength length of Data
	* @return ThisComponent
	*/
	public SHA2 update(byte[] data, int dataLength) {
		if (this.finished || data == null || dataLength>data.length) {
			return null;
		}
		int dataPos = 0;
		this.bytesHashed += dataLength;
		if (this.bufferLength > 0 && this.bufferLength<this.buffer.length) {
			while (this.bufferLength < 64 && dataLength > 0) {
				this.buffer[this.bufferLength++] = data[dataPos++];
				dataLength--;
			}
			if (this.bufferLength == 64) {
				hashBlocks(this.temp, this.state, this.buffer, 0, 64);
				this.bufferLength = 0;
			}
		}
		if (dataLength >= 64) {
			dataPos = hashBlocks(this.temp, this.state, data, dataPos, dataLength);
			dataLength %= 64;
		}
		while (dataLength > 0) {
			this.buffer[this.bufferLength++] = data[dataPos++];
			dataLength--;
		}
		return this;
	}

	/* Finalizes hash state and puts hash into out.
	  If hash was already finalized, puts the same value. */
	public SHA2 finish(byte[] out) {
		if (!this.finished) {
			int bytesHashed = this.bytesHashed;
			int left = this.bufferLength;
			int bitLenHi = (bytesHashed / 0x20000000) | 0;
			int bitLenLo = bytesHashed << 3;
			int padLength = (bytesHashed % 64 < 56) ? 64 : 128;

			this.buffer[left] = (byte) 0x80;
			for (int i = left + 1; i < padLength - 8; i++) {
				this.buffer[i] = 0;
			}
			this.buffer[padLength - 8] = (byte) ((bitLenHi >>> 24) & 0xff);
			this.buffer[padLength - 7] = (byte) ((byte) (bitLenHi >>> 16) & 0xff);
			this.buffer[padLength - 6] = (byte) ((byte) (bitLenHi >>> 8) & 0xff);
			this.buffer[padLength - 5] = (byte) ((byte) (bitLenHi >>> 0) & 0xff);
			this.buffer[padLength - 4] = (byte) ((byte) (bitLenLo >>> 24) & 0xff);
			this.buffer[padLength - 3] = (byte) ((byte) (bitLenLo >>> 16) & 0xff);
			this.buffer[padLength - 2] = (byte) ((byte) (bitLenLo >>> 8) & 0xff);
			this.buffer[padLength - 1] = (byte) ((byte) (bitLenLo >>> 0) & 0xff);

			hashBlocks(this.temp, this.state, this.buffer, 0, padLength);

			this.finished = true;
		}

		if(out != null && out.length>=31) {
			for (int i = 0; i < 8; i++) {
				out[i * 4 + 0] = (byte) ((this.state[i] >>> 24) & 0xff);
				out[i * 4 + 1] = (byte) ((this.state[i] >>> 16) & 0xff);
				out[i * 4 + 2] = (byte) ((this.state[i] >>> 8) & 0xff);
				out[i * 4 + 3] = (byte) ((this.state[i] >>> 0) & 0xff);
			}
		}

		return this;
	}

	/* Returns the final hash digest. */
	public byte[] digest() {
		byte[] out = new byte[this.digestLength];
		this.finish(out);
		return out;
	}

	/* Internal function for use in HMAC for optimization. */
	public void _saveState(int[] out) {
		if(out != null && out.length>=this.state.length) {
			for (int i = 0; i < this.state.length; i++) {
				out[i] = this.state[i];
			}
		}
	}

	/* Internal function for use in HMAC for optimization. */
	public void _restoreState(int[] from, int bytesHashed) {
		if(from != null && from.length>=this.state.length) {
			for (int i = 0; i < this.state.length; i++) {
				this.state[i] = from[i];
			}
		}
		this.bytesHashed = bytesHashed;
		this.finished = false;
		this.bufferLength = 0;
	}
}
