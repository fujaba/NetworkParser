package de.uniks.networkparser.test;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.io.MessageSession;
import de.uniks.networkparser.ext.io.RabbitMessage;

public class RabbitTest {
	@Test
	public void testStartOK() {
		RabbitMessage startOK = RabbitMessage.createStartOK();
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		startOK.write(byteBuffer);
		ByteBuffer buffer = new ByteBuffer();
		buffer.add(byteBuffer.toByteArray());
		System.out.println(buffer.toArrayString());
	}
	
	@Test
	public void connectRabbit() {
		MessageSession session = new MessageSession().withHost("localhost");
		session.connectAMQ(null, null);
	}
}
