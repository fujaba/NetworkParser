package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Server;

/**
 * Server for UPD.
 *
 * @author Stefan Lindel
 */
public class Server_UPD extends Thread implements Server {
	protected boolean run = true;
	protected boolean startReady = false;
	protected DatagramSocket socket;
	private NodeProxyTCP proxy;

	/**
	 * Instantiates a new server UPD.
	 *
	 * @param proxy the proxy
	 */
	public Server_UPD(NodeProxyTCP proxy) {
		this.proxy = proxy;
		if (init()) {
			startReady = true;
		} else {
			run = false;
		}
	}
	
	/**
	 * With start.
	 *
	 * @return the server UPD
	 */
	public Server_UPD withStart() {
		if(this.startReady) {
			startReady = false;
			this.start();
		}
		return this;
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	public boolean close() {
		this.run = false;
		if (socket != null) {
			socket.close();
			socket = null;
		}
		return true;
	}

	/**
	 * Checks if is run.
	 *
	 * @return true, if is run
	 */
	@Override
	public boolean isRun() {
		return socket != null && !socket.isClosed();
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (NodeProxy.isInput(proxy.getType())) {
			runServer();
		} else {
			DatagramPacket data = runClient();
			proxy.getSpace().firePropertyChange(BROADCAST, null, data);
		}
	}

	/**
	 * Creates the send packet.
	 *
	 * @param port the port
	 * @return the datagram packet
	 */
	public DatagramPacket createSendPacket(int port) {
		byte[] sendData = new byte[proxy.getBufferSize()];
		if (proxy.getSpace() != null) {
			sendData = proxy.getSpace().getReplicationInfo().toString().getBytes();
		}
		InetAddress IPAddress = null;
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
		}
		return new DatagramPacket(sendData, sendData.length, IPAddress, port);
	}

	/**
	 * Run client.
	 *
	 * @return the datagram packet
	 */
	public DatagramPacket runClient() {
		DatagramPacket message = createSendPacket(proxy.getReceivePort());
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

	/**
	 * Run server.
	 */
	public void runServer() {
		Thread.currentThread().setName(proxy.getPort() + " broadcast server");
		while (!isInterrupted() && this.run) {
			try {
				byte[] receiveData = new byte[proxy.getBufferSize()];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);

				int port = receivePacket.getPort();
				Entity answer = proxy.getSpace().getReplicationInfo();
				byte[] sendData = answer.toString().getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), port);
				socket.send(sendPacket);
			} catch (IOException e) { // Empty
			}
		}
	}

	private boolean init() {
		boolean success = true;
		try {
			/* Switch for Client / Server */
			if (proxy != null && NodeProxy.isInput(proxy.getType())) {
				int port = this.proxy.getPort();
				try {
					socket = new DatagramSocket(port);
				} catch (Exception e) {
					socket = new DatagramSocket();
					this.proxy.withPort(socket.getLocalPort());
					this.proxy.withReceivePort(port);
				}
			} else {
				socket = new DatagramSocket();
			}
		} catch (SocketException e) {
			success = false;
		}
		return success;
	}
}
