package de.uniks.networkparser.ext.petaf;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Date;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyServer;
import de.uniks.networkparser.interfaces.Server;

public class Server_Time extends Thread implements Server{
	protected boolean run=true;
	private int port = 37;
	private NodeProxyServer proxy;
	private DatagramChannel channel;

	public Server_Time(NodeProxyServer proxy, boolean asyn) 
	{
		this.proxy = proxy;
		if(init() && asyn){
			start();
		}
	}
	
	public Server_Time(boolean asyn) 
	{
		if(init() && asyn){
			start();
		}
	}
	
	public boolean close(){
		this.run=false;
		if(channel!=null){
			try {
				channel.close();
			} catch (IOException e) {
			}
			channel = null;
		}
		return true;
	}
	
	@Override
	public boolean isRun() {
		return run;
	}
	
	@Override
	public void run()
	{
		runServer();
	}

	public void runServer() 
	{
		Thread.currentThread().setName(this.port+" time server");
		ByteBuffer in = ByteBuffer.allocate(8192);
	    ByteBuffer out = ByteBuffer.allocate(8);
	    out.order(ByteOrder.BIG_ENDIAN);
	    
		while (!isInterrupted()&&this.run) 
		{
			try
			{
				
				in.clear();
		        SocketAddress client = channel.receive(in);
		        System.err.println(client);
				 out.clear();
				 long secondsSince1900 = getTime();
		        out.putLong(secondsSince1900);
		        out.flip();
		        // skip over the first four bytes to make this an unsigned int
		        out.position(4);
		        channel.send(out, client);
			} 
			catch (IOException e) {
				
			}
		}
	}

	public Server_Time withPort(int value) {
		this.port = value;
		return this;
	}
	
	private boolean init() 
	{
		boolean success=true;
		try {
			if(proxy !=  null && NodeProxyType.isInput(proxy.getType())) {
				this.port = proxy.getPort();
			}
			SocketAddress address = new InetSocketAddress(this.port);
		    channel = DatagramChannel.open();
		    DatagramSocket socket = channel.socket();
		    socket.bind(address);
		} catch (SocketException e) {
			success = false;
		} catch (IOException e) {
			success = false;
		}
		return success;
	}
	  
	private static long getTime() {
		long differenceBetweenEpochs = 2208988800L;
		Date now = new Date();
		long secondsSince1970 = now.getTime() / 1000;
		long secondsSince1900 = secondsSince1970 + differenceBetweenEpochs;
		return secondsSince1900;
	}
}
