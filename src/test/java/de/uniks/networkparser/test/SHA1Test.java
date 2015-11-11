package de.uniks.networkparser.test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.bytes.checksum.SHA1;

public class SHA1Test {
	@Test
	public void testSHA1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String text="Hallo Welt";
		Assert.assertEquals("28cbbc72d6a52617a7abbfff6756d04bbad0106a", SHA1.value(text).toString());
	}
}
