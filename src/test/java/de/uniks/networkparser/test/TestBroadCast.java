package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class TestBroadCast {
	@Test
	@Timeout(5)
	public void testBroadCast() throws IOException {
		Space space = Space.newInstance(NodeProxyTCP.createServer(5000), NodeProxyTCP.createServer(9876));
		Timer timer = new Timer();

		// Start in 10 Sekunden
		timer.schedule( new BroadCastClient(), 400 );
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("END");
		timer.cancel();
		space.close();
	}

	class BroadCastClient extends TimerTask{
		public void run() {
			NodeProxyTCP broasCast = new NodeProxyTCP().withServerType(NodeProxy.TYPE_OUT);
			DatagramPacket answer = broasCast.executeBroadCast(false);
			if(answer != null) {
				assertNotNull(answer.getData());
			}
//			String modifiedSentence = new String(answer.getData());
//			System.out.println("FROM SERVER:" + modifiedSentence);
			broasCast.close();
		}
	}
}
