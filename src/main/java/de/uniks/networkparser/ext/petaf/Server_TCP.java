package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Server;

public class Server_TCP extends Thread  implements Server {
	protected boolean run=true;
	private boolean searchFreePort=true;
	protected ServerSocket serverSocket;
	private NodeProxyTCP proxy;

	/**
	 * Fallback for simple Creating a Server without proxy
	 *
	 * @param port Port of TCP-Server
	 */
	public Server_TCP(int port) {
		this(NodeProxyTCP.createServer(port));
	}

	public Server_TCP(NodeProxyTCP proxy)
	{
		this.proxy = proxy;
		if(init()){
			start();
		}
	}

	public boolean isRun() {
		return run;
	}

	public boolean close(){
		this.run=false;
		try {
			if(serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
				MessageRequest.executeTask(this.proxy, requestSocket);
			}
			catch (IOException e)
			{
			}finally{
//				try {
//					if(requestSocket!=null && !requestSocket.isClosed()){
//						requestSocket.close();
//					}
//				} catch (IOException e) {
//				}
			}
		}
	}

	private boolean init()
	{
		try
		{
			serverSocket = new ServerSocket(proxy.getPort(), 10, null);
			return true;
		}
		catch (UnknownHostException e)
		{
			return false;
		}
		catch (IOException e)
		{
			if(searchFreePort) {
				// Wrong PORT
				try {
					serverSocket = new ServerSocket(0, 10, null);
					proxy.withPort(serverSocket.getLocalPort());
					return true;
				}catch (Exception exception) {
				}
			}
		}
		return false;
	}

	/**
	 * @return the searchFreePort
	 */
	public boolean isSearchFreePort() {
		return searchFreePort;
	}

	/**
	 * @param searchFreePort the searchFreePort to set
	 * @return ThisComponent
	 */
	public Server_TCP withSearchFreePort(boolean searchFreePort) {
		this.searchFreePort = searchFreePort;
		return this;
	}
}
