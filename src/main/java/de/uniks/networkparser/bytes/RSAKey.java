package de.uniks.networkparser.bytes;

/*
 * NetworkParser The MIT License Copyright (c) 2010-2016 Stefan Lindel
 * https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.math.BigInteger;
import java.util.Random;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.DERBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;

/**
 * Random sequential adsorption.
 *
 * @author Stefan Lindel
 */
public class RSAKey {
  
  /** The Constant BEGINPUBLICKEY. */
  public static final String BEGINPUBLICKEY = "-----BEGIN PUBLIC RSA KEY-----\n";
  
  /** The Constant ENDPUBLICKEY. */
  public static final String ENDPUBLICKEY = "-----END PUBLIC RSA KEY-----";
  
  /** The Constant BEGINPRIVATEKEY. */
  public static final String BEGINPRIVATEKEY = "-----BEGIN PRIVATE RSA KEY-----\n";
  
  /** The Constant ENDPRIVATEKEY. */
  public static final String ENDPRIVATEKEY = "-----END PRIVATE RSA KEY-----";

  /** The Constant RSABYTE. */
  public static final Byte RSABYTE = 48;
  
  /** The Constant SAFESIZE. */
  public static final int SAFESIZE = 1024;
  
  /** The Constant TAG. */
  public static final String TAG = "RSA";
  private BigInteger e;
  private BigInteger d;
  /* RSA-Modul */
  private BigInteger N;
  private static Random rand = new Random();

  /**
   * Instantiates a new RSA key.
   *
   * @param N the n
   */
  public RSAKey(BigInteger N) {
    this.N = N;
  }

  /**
   * With public key.
   *
   * @param value the value
   * @return the RSA key
   */
  public RSAKey withPublicKey(BigInteger value) {
    this.e = value;
    return this;
  }

  /**
   * Gets the public key.
   *
   * @return the public key
   */
  public BigInteger getPublicKey() {
    return e;
  }

  /**
   * With private key.
   *
   * @param value the value
   * @return the RSA key
   */
  public RSAKey withPrivateKey(BigInteger value) {
    this.d = value;
    return this;
  }

  /**
   * Gets the private key.
   *
   * @return the private key
   */
  public BigInteger getPrivateKey() {
    return d;
  }

  /**
   * Gets the modulus.
   *
   * @return the modulus
   */
  public BigInteger getModulus() {
    return N;
  }

  /**
   * Sets the public exponent.
   * 
   * @param value The the Public Exponent
   * @return ThisComponent
   */

  public RSAKey withPubExp(BigInteger value) {
    e = weedOut(value);
    return this;
  }

  /**
   * Sets the public exponent.
   * 
   * @param value The the Public Exponent
   * @return ThisComponent
   */
  public RSAKey withPubExp(int value) {
    BigInteger newValue = BigInteger.valueOf(value);
    e = weedOut(newValue);
    return this;
  }

  /**
   * Performs the classical RSA computation.
   * 
   * @param message Encrypt a Message
   * @return Encoded Message
   */
  public BigInteger encrypt(BigInteger message) {
    BigInteger modulus = getModulus();
    if (modulus == null) {
      return null;
    }
    if (message.divide(modulus).intValue() > 0) {
      // "WARNUNG MODULUS MUST BIGGER (HASH-VALUE)"
    }
    return message.modPow(getPublicKey(), getModulus());
  }

  /**
   * Performs the classical RSA computation.
   * 
   * @param value Enscript the Value
   * @return the enscripted Message
   */
  public CharacterBuffer encrypt(String value) {
    if (value == null) {
      return null;
    }
    return encrypt(value, value.length());
  }

  /**
   * Decrypt.
   *
   * @param message the message
   * @return the character buffer
   */
  public CharacterBuffer decrypt(String message) {
    if (message != null) {
      try {
        return decrypt(new BigInteger(message));
      } catch (Exception ex) {
          // Catch Error
      }
    }
    return null;
  }

  /**
   * Performs the classical RSA computation.
   * 
   * @param message Message to descrypt
   * @return the descrypted Message
   **/
  public CharacterBuffer decrypt(BigInteger message) {
    if (message == null) {
      return null;
    }
    BigInteger privateKey = getPrivateKey();
    BigInteger modulus = getModulus();
    if (privateKey == null || modulus == null) {
      return null;
    }
    BigInteger text = message.modPow(privateKey, modulus);
    BigInteger divider = BigInteger.valueOf(1000);
    int bitCount = text.bitCount();
    CharacterBuffer sb = new CharacterBuffer().withBufferLength(bitCount);
    while (bitCount >= 0) {
      BigInteger character = text.remainder(divider);
      sb.setCharAt(bitCount, (char) character.intValue());
      text = text.divide(divider);
      bitCount--;
    }
    return sb;
  }

  /**
   * Sign.
   *
   * @param value the value
   * @return the entity
   */
  public Entity sign(Entity value) {
    if (value != null) {
      String string = value.toString();
      CharacterBuffer hashCode = encrypt(string, string.length());
      /* CHECK FOR HASHCODE ONLY */
      value.put(TAG, hashCode);
      return value;
    }
    return null;
  }

  /**
   * Encrypt.
   *
   * @param value the value
   * @param group the group
   * @return the character buffer
   */
  public CharacterBuffer encrypt(String value, int group) {
    if (value == null) {
      return null;
    }
    CharacterBuffer sb = new CharacterBuffer();
    CharacterBuffer item = new CharacterBuffer();

    int c = 0;
    for (int i = 0; i < value.length(); i++) {
      if (c == 0) {
        item = new CharacterBuffer();
      }
      char character = value.charAt(i);
      if (character < 10) {
        item.append("00" + (int) character);
      } else if (character < 100) {
        item.append("0" + (int) character);
      } else {
        item.with(((int) character));
      }
      c++;
      if (c == group) {
        sb.append(encoding(item.toString()));
        item = new CharacterBuffer();
        c = 0;
      }
    }
    if (c > 0) {
      sb.append(encoding(item.toString()));
    }
    return sb;
  }

  private String encoding(String value) {
    if (value == null) {
      return null;
    }
    BigInteger encrypt;
    try {
      encrypt = encrypt(new BigInteger(value));
    } catch (Exception e) {
      return null;
    }
    if (encrypt == null) {
      return null;
    }
    String string = encrypt.toString();
    int rest = string.length() % 3;
    if (rest == 1) {
      return "0" + string;
    } else if (rest == 2) {
      return "00" + string;
    }
    return string;
  }

  /**
   * Weeds out bad inputs.
   * 
   * @param value The Value for Check
   * @return the checked Value
   */
  private final BigInteger weedOut(BigInteger value) {
    if (!isNull(value) && isPositive(value)) {
      return value;
    } else {
      return null;
    }
  }

  /**
   * Returns true when the argument is greater than zero.
   * 
   * @param number Number for Check
   * @return if number is Positive
   */
  private final boolean isPositive(BigInteger number) {
    return number != null && (number.compareTo(BigInteger.ZERO) > 0);
  }

  /**
   * Returns true when the argument is null.
   * 
   * @param value Value for Check
   * @return if Value is Null
   */
  private final boolean isNull(Object value) {
    return (value == null);
  }

  /**
   * Generate key.
   *
   * @param p the p
   * @param q the q
   * @param max the max
   * @return the RSA key
   */
  public static RSAKey generateKey(int p, int q, int max) {
    return generateKey(BigInteger.valueOf(p), BigInteger.valueOf(q), max);
  }

  /**
   * Generate key.
   *
   * @return the RSA key
   */
  public static RSAKey generateKey() {
    return generateKey(SAFESIZE);
  }

  /**
   * Generate key.
   *
   * @param max the max
   * @return the RSA key
   */
  public static RSAKey generateKey(int max) {
    return generateKey(BigInteger.ZERO, BigInteger.ZERO, max);
  }

  /**
   * Generate key.
   *
   * @param p the p
   * @param q the q
   * @param max the max
   * @return the RSA key
   */
  public static RSAKey generateKey(BigInteger p, BigInteger q, int max) {
    if (p == null || q == null) {
      return null;
    }
    if (p.longValue() < 1) {
      try {
        p = BigInteger.probablePrime(75 * max / 100, rand);
        q = BigInteger.probablePrime(25 * max / 100, rand);
      } catch (Exception e) {
        return null;
      }
    }
    RSAKey key = new RSAKey(p.multiply(q));
    /* n is the modulus for the public key and the private keys */

    BigInteger i;
    BigInteger phi = computePhi(p, q);

    for (i = BigInteger.probablePrime((max / 10), rand); i.compareTo(key.getModulus()) < 0; i = i
        .nextProbablePrime()) {
      if (i.gcd(phi).equals(BigInteger.ONE)) {
        break;
      }
    }
    key.withPubExp(i);
    return key;
  }

  /**
   * Computes the LCM of the primes.
   * 
   * @param p first prime
   * @param q second prime
   * @return Phi
   */
  private static BigInteger computePhi(BigInteger p, BigInteger q) {
    if (p == null || q == null) {
      return null;
    }
    return lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
  }

  /**
   * Computes the least common multiple.
   * 
   * @param a first value
   * @param b second value
   * @return the multiply of a,b
   */
  private static BigInteger lcm(BigInteger a, BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    try {
      return (a.multiply(b).divide(a.gcd(b)));
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Gets the decrypt key.
   *
   * @param n the n
   * @param privateKey the private key
   * @return the decrypt key
   */
  public static RSAKey getDecryptKey(BigInteger n, BigInteger privateKey) {
    RSAKey key = new RSAKey(n);
    key.withPrivateKey(privateKey);
    return key;
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    CharacterBuffer sb = new CharacterBuffer();
    if (e != null) {
      sb.with(BEGINPUBLICKEY + BaseItem.CRLF);
      sb.with(getPublicStream().toString() + BaseItem.CRLF);
      sb.with(ENDPUBLICKEY + BaseItem.CRLF);
    }
    if (d != null) {
      sb.with(BEGINPRIVATEKEY + BaseItem.CRLF);
      sb.with(getPrivateStream().toString() + BaseItem.CRLF);
      sb.with(ENDPRIVATEKEY + BaseItem.CRLF);
    }
    return sb.toString();
  }

  /**
   * Gets the public stream.
   *
   * @return the public stream
   */
  public DERBuffer getPublicStream() {
    return getStream(e);
  }

  /**
   * Gets the private stream.
   *
   * @return the private stream
   */
  public DERBuffer getPrivateStream() {
    return getStream(d);
  }

  /**
   * Gets the stream.
   *
   * @param key the key
   * @return the stream
   */
  public DERBuffer getStream(BigInteger key) {
    DERBuffer bitString = new DERBuffer();

    bitString.addGroup(RSABYTE, new Object[] {N, key});
    DERBuffer derBuffer = new DERBuffer();
    derBuffer.addGroup(RSABYTE, new Object[] {RSABYTE,
        new Object[] {DERBuffer.OBJECTID, new Byte[] {42, -122, 72, -122, -9, 13, 1, 1, 1}, DERBuffer.NULL},
        DERBuffer.BITSTRING, bitString.toBytes()});
    /* 48 l:92[48 l:13 [ 6 l:9 [42, -122, 72, -122, -9, 13, 1, 1, 1],5 l:0] 3 */
    /* l:75[n,e]] */
    return derBuffer;
  }
}
