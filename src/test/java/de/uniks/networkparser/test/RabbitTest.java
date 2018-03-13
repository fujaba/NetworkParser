package de.uniks.networkparser.test;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.io.RabbitMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyRabbit;
import de.uniks.networkparser.interfaces.ObjectCondition;

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
	

	public void connectRabbit() {
		NodeProxyRabbit proxy = new NodeProxyRabbit("localhost");
		proxy.connect();
		
		proxy.createChannel("hello", new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				System.out.println("RECEIVED: "+value);
				return false;
			}
		});
//		proxy.publish("hello", "Hello World!");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxy.close();
	}
	@Test
	public void connectRabbitDecoding() {
		RabbitMessage msg = new RabbitMessage();
		msg.withPayload(new byte[] {0, 10, 0, 10, 0, 9, 0, 0, 1, -56, 12, 99, 97, 112, 97, 98, 105, 108, 105, 116, 105, 101, 115, 70, 0, 0, 0, -57, 18, 112, 117, 98, 108, 105, 115, 104, 101, 114, 95, 99, 111, 110, 102, 105, 114, 109, 115, 116, 1, 26, 101, 120, 99, 104, 97, 110, 103, 101, 95, 101, 120, 99, 104, 97, 110, 103, 101, 95, 98, 105, 110, 100, 105, 110, 103, 115, 116, 1, 10, 98, 97, 115, 105, 99, 46, 110, 97, 99, 107, 116, 1, 22, 99, 111, 110, 115, 117, 109, 101, 114, 95, 99, 97, 110, 99, 101, 108, 95, 110, 111, 116, 105, 102, 121, 116, 1, 18, 99, 111, 110, 110, 101, 99, 116, 105, 111, 110, 46, 98, 108, 111, 99, 107, 101, 100, 116, 1, 19, 99, 111, 110, 115, 117, 109, 101, 114, 95, 112, 114, 105, 111, 114, 105, 116, 105, 101, 115, 116, 1, 28, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 102, 97, 105, 108, 117, 114, 101, 95, 99, 108, 111, 115, 101, 116, 1, 16, 112, 101, 114, 95, 99, 111, 110, 115, 117, 109, 101, 114, 95, 113, 111, 115, 116, 1, 15, 100, 105, 114, 101, 99, 116, 95, 114, 101, 112, 108, 121, 95, 116, 111, 116, 1, 12, 99, 108, 117, 115, 116, 101, 114, 95, 110, 97, 109, 101, 83, 0, 0, 0, 22, 114, 97, 98, 98, 105, 116, 64, 68, 69, 83, 75, 84, 79, 80, 45, 69, 52, 67, 66, 82, 74, 50, 9, 99, 111, 112, 121, 114, 105, 103, 104, 116, 83, 0, 0, 0, 46, 67, 111, 112, 121, 114, 105, 103, 104, 116, 32, 40, 67, 41, 32, 50, 48, 48, 55, 45, 50, 48, 49, 56, 32, 80, 105, 118, 111, 116, 97, 108, 32, 83, 111, 102, 116, 119, 97, 114, 101, 44, 32, 73, 110, 99, 46, 11, 105, 110, 102, 111, 114, 109, 97, 116, 105, 111, 110, 83, 0, 0, 0, 53, 76, 105, 99, 101, 110, 115, 101, 100, 32, 117, 110, 100, 101, 114, 32, 116, 104, 101, 32, 77, 80, 76, 46, 32, 32, 83, 101, 101, 32, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 114, 97, 98, 98, 105, 116, 109, 113, 46, 99, 111, 109, 47, 8, 112, 108, 97, 116, 102, 111, 114, 109, 83, 0, 0, 0, 15, 69, 114, 108, 97, 110, 103, 47, 79, 84, 80, 32, 50, 48, 46, 50, 7, 112, 114, 111, 100, 117, 99, 116, 83, 0, 0, 0, 8, 82, 97, 98, 98, 105, 116, 77, 81, 7, 118, 101, 114, 115, 105, 111, 110, 83, 0, 0, 0, 5, 51, 46, 55, 46, 51, 0, 0, 0, 14, 65, 77, 81, 80, 76, 65, 73, 78, 32, 80, 76, 65, 73, 78, 0, 0, 0, 5, 101, 110, 95, 85, 83});
		msg.analysePayLoad();
	}
	
}
