package de.uniks.networkparser.bytes;

/** 
 * @author Stefan Lindel
 * HMAC implements HMAC-SHA256 message authentication algorithm. */
public class HMAC {
	private SHA2 inner = new SHA2();
	private SHA2 outer = new SHA2();

	private int[] istate;
	private int[] ostate;

	public HMAC(String key) {
		if(key == null) {
			return;
		}
		byte[] keyBytes = key.getBytes();
		byte[] pad = new byte[this.inner.blockSize];
		if (keyBytes.length > this.inner.blockSize) {
			(new SHA2()).update(keyBytes, keyBytes.length).finish(pad).clean();
		} else {
			for (int i = 0; i < key.length(); i++) {
				pad[i] = keyBytes[i];
			}
		}
		for (int i = 0; i < pad.length; i++) {
			pad[i] ^= 0x36;
		}
		this.inner.update(pad, pad.length);

		for (int i = 0; i < pad.length; i++) {
			pad[i] ^= 0x36 ^ 0x5c;
		}
		this.outer.update(pad, pad.length);

		this.istate = new int[8];
		this.ostate = new int[8];

		this.inner._saveState(this.istate);
		this.outer._saveState(this.ostate);

		for (int i = 0; i < pad.length; i++) {
			pad[i] = 0;
		}
	}

	/** Returns HMAC state to the state initialized with key
	 * to make it possible to run HMAC over the other data with the same
	 *  key without creating a new instance.
	 * @return ThisComponent
	 */
	public HMAC reset() {
		this.inner._restoreState(this.istate, this.inner.blockSize);
		this.outer._restoreState(this.ostate, this.outer.blockSize);
		return this;
	}

	/* Cleans HMAC state. */
	public void clean() {
		if(this.istate != null) {
			for (int i = 0; i < this.istate.length; i++) {
				this.ostate[i] = this.istate[i] = 0;
			}
		}
		this.inner.clean();
		this.outer.clean();
	}

	/**
	 *  Updates state with provided data.
	 *  @param data new Data to Encode
	 *  @return ThisComponent
	 */
	public HMAC update(String data) {
		if(data != null) {
			update(data.getBytes());
		}
		return this;
	}
	
	public HMAC update(byte[] data) {
		if(data != null) {
			this.inner.update(data, data.length);
		}
		return this;
	}

	/**
	 *  Finalizes HMAC and puts the result in out.
	 *  @param out byteArray to set Encoding Data
	 *  @return ThisComponent
	 *  */
	public HMAC finish(byte[] out) {
		if (this.outer.finished) {
			this.outer.finish(out);
		} else {
			this.inner.finish(out);
			this.outer.update(out, this.inner.digestLength).finish(out);
		}
		return this;
	}

	/* Returns message authentication code. */
	public byte[] digest() {
		byte[] out = new byte[this.inner.digestLength];
		this.finish(out);
		return out;
	}
}
