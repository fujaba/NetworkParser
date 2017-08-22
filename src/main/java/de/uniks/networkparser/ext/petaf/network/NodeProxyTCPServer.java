package de.uniks.networkparser.ext.petaf.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class NodeProxyTCPServer extends Thread{
	protected boolean run=true;
	protected ServerSocket serverSocket;
	private NodeProxyTCP proxy;

	public NodeProxyTCPServer(NodeProxyTCP proxy) 
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
		Thread.currentThread().setName(proxy.getUrl()+":"+proxy.getPort()+" com server");
		while (!isInterrupted()&&this.run) 
		{
			Socket requestSocket = null;
			try 
			{
				requestSocket = serverSocket.accept();
				readFromCommunication(requestSocket);
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

	private void readFromCommunication(final Socket requestSocket) 
	{
		MessageRequest request = new MessageRequest(this.proxy, requestSocket);
		this.proxy.getSpace().execute(request);
		//FIXME
		// execute request synchronously. Albert 
//		
//		request.run();
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
