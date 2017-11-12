package de.uniks.networkparser.ext.petaf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroadCast;
import de.uniks.networkparser.interfaces.Entity;

public class Server_UPD extends Thread{
	private static final String BROADCAST = "BROADCAST";
	protected boolean run=true;
	protected DatagramSocket socket;
	private NodeProxyBroadCast proxy;

	public Server_UPD(NodeProxyBroadCast proxy, boolean asyn) 
	{
		this.proxy = proxy;
		if(init() && asyn){
			start();
		}
	}
	
	public boolean closeServer(){
		this.run=false;
		if(socket!=null){
			socket.close();
			socket = null;
		}
		return true;
	}
	
	@Override
	public void run()
	{
		if(NodeProxyType.isInput(proxy.getType())) {
			runServer();
		} else {
			DatagramPacket data = runClient();
			if(proxy != null) {
				proxy.getSpace().firePropertyChange(BROADCAST, null, data);
			}
		}
	}
	
	public DatagramPacket createSendPacket() {
		byte[] sendData = new byte[proxy.getBufferSize()];
		if(proxy.getSpace() != null) {
			sendData = proxy.getSpace().getReplicationInfo().toString().getBytes();
		}
		InetAddress IPAddress = null;
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
		}
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, proxy.getPort());
		return sendPacket;
	}

	public DatagramPacket runClient() {
		DatagramPacket message = createSendPacket();
		DatagramPacket receivePacket;
		try {
			socket.send(message);
			
			byte[] receiveData = new byte[proxy.getBufferSize()];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
		} catch (IOException e) {
			return null;
		}
		return receivePacket;
	}
	
	public void runServer() 
	{
		Thread.currentThread().setName(proxy.getPort()+" broadcast server");
		while (!isInterrupted()&&this.run) 
		{
			try
			{
				byte[] receiveData = new byte[proxy.getBufferSize()];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				
	//			String sentence = new String(receivePacket.getData());
	//			System.out.println("RECEIVED: " + sentence);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				Entity answer = proxy.getSpace().getReplicationInfo();
				byte[] sendData = answer.toString().getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				socket.send(sendPacket);
			} 
			catch (IOException e) {
				
			}
		}
	}

	private boolean init() 
	{
		boolean success=true;
		try {
			// Switch for Client / Server
			if(proxy != null && NodeProxyType.isInput(proxy.getType())) {
				socket = new DatagramSocket(proxy.getPort());
			}else {
				socket = new DatagramSocket();
			}
		} catch (SocketException e) {
			success = false;
		}
		return success;
	}
}
