package de.uniks.networkparser.ext.petaf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class Server_TCP extends Thread {
	protected boolean run=true;
	protected ServerSocket serverSocket;
	private NodeProxyTCP proxy;

	public Server_TCP(NodeProxyTCP proxy) 
	{
		this.proxy = proxy;
		if(init()){
			start();
		}
	}
	
	public void closeServer(){
		this.run=false;
		try {
			if(serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		if(proxy.getUrl() != null) {
			Thread.currentThread().setName(proxy.getUrl()+":"+proxy.getPort()+" com server");
		}else {
			Thread.currentThread().setName("localhost:"+proxy.getPort()+" com server");
		}
		while (!isInterrupted()&&this.run) 
		{
			Socket requestSocket = null;
			try 
			{
				requestSocket = serverSocket.accept();
				MessageRequest.execute(this.proxy, requestSocket);
			} 
			catch (IOException e) 
			{
			}finally{
				try {
					if(requestSocket!=null && !requestSocket.isClosed()){
						requestSocket.close();
					}
				} catch (IOException e) {
				}
			}
		}
	}

	private boolean init() 
	{
		boolean success=true;
		try 
		{
			serverSocket = new ServerSocket(proxy.getPort(), 10, null);
		} 
		catch (UnknownHostException e) 
		{
			success=false;
		} 
		catch (IOException e) 
		{
			success=false;
		}
		return success;
	}
}
