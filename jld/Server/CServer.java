package jld.Server;

import jld.Server.ClientHandler.CClientHandler;
import jld.Server.utils.utils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CServer extends Thread {
	private int mPort;
	private ServerSocket mServerSocket;
	private ArrayList<CClientHandler> mConnectedClients = new ArrayList<CClientHandler>();

	public CServer(int port) {
		mPort = port;
		this.start();
	}

	@Override
	public void run() {
		try {
			// Open port on defined port.
			mServerSocket = new ServerSocket(mPort, 100);
			utils.infoMsg("ServerSocket listening on port " + mPort);
			while (true) {
				// Accept a new client on the server
				clientConnected(mServerSocket.accept());
			}
		} catch (IOException e) {
			utils.errorMsg("Error occured when creating the ServerSocket.");
		}
	}
	
	public synchronized void clientConnected(Socket socket){
		CClientHandler currentClient = new CClientHandler(this, socket);
		utils.infoMsg("New connection from " + currentClient.getClient().getIp());
		mConnectedClients.add(currentClient);
		utils.debugMsg("New amount of clients: " + mConnectedClients.size());
	}
	
	public synchronized void clientDisconnected(CClientHandler client){
		utils.infoMsg("Client disconnected " + client.getClient().getIp());
		mConnectedClients.remove(client);
		utils.debugMsg("New amount of clients: " + mConnectedClients.size());
	}
}
