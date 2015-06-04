package jld.Server;

import jld.Server.Channels.CChannel;
import jld.Server.Channels.CChannelParser;
import jld.Server.ClientHandler.CClient;
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
	private CChannel mChannels[];
	private String mDefaultChannelName;
	private int mDefaultChannelPosInArray;
	private CClient mServerClient = new CClient("System", null, null);	// Wird benutzt um Systemnachrichten zu senden
	
	public CClient getServerClient() {
		return mServerClient;
	}

	public CServer(int port, String defaultChannelName) {
		mPort = port;
		mDefaultChannelName = defaultChannelName;
		this.start();
	}

	@Override
	public void run() {
		try {
			// Open port on defined port.
			mServerSocket = new ServerSocket(mPort, 100);
			utils.infoMsg("ServerSocket listening on port " + mPort);
			mChannels = CChannelParser.readChannels(this);
			boolean hasDefaultChannel = false;
			utils.infoMsg("Available channels:");
			for(int i = 0; i < mChannels.length; i++){
				if(mChannels[i].getName().equals(mDefaultChannelName)){
					hasDefaultChannel = true;
					mDefaultChannelPosInArray = i;
				}
				utils.infoMsg("\t" + mChannels[i].getName());
			}
			if(hasDefaultChannel == false){
				utils.errorMsg("Fehlerhafte Konfiguration. [DefaultChannel aus config.cfg ist nicht in channels.lst]");
			}
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
	
	public ArrayList<CClientHandler> getClientsOfChannel(CChannel channel){
		ArrayList<CClientHandler> clients = new ArrayList<CClientHandler>();
		for(int i = 0; i < mConnectedClients.size(); i++){
			if(mConnectedClients.get(i).getClient().getCurrentChannel() != null && mConnectedClients.get(i).getClient().getCurrentChannel().equals(channel)){
				clients.add(mConnectedClients.get(i));
			}
		}
		return clients;
	}
	
	public CChannel getDefaultChannel(){
		assert(mChannels != null);
		return mChannels[mDefaultChannelPosInArray];
	}
}
