package de.uniks.networkparser.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import de.uniks.networkparser.ext.petaf.network.BasicSpace;
import de.uniks.networkparser.ext.petaf.network.NodeProxyType;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroadCast;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class TestBroadCast {
	@Test
	public void testBroadCast() throws IOException {
		BasicSpace space = BasicSpace.newInstance(NodeProxyTCP.createServer(5000), NodeProxyBroadCast.createServer(9876));
		Timer timer = new Timer();

	    // Start in 10 Sekunden
	    timer.schedule( new BroadCastClient(), 400 );
	    try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("END");
		
	}
	
	class BroadCastClient extends TimerTask{
		public void run() {
			NodeProxyBroadCast broasCast = new NodeProxyBroadCast(NodeProxyType.OUT);
			DatagramPacket answer = broasCast.executeBroadCast(false);
			String modifiedSentence = new String(answer.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			broasCast.close();
		}
	}
}
