package jld.Server.Channels;

import java.util.ArrayList;

import jld.Server.CServer;
import jld.Server.ClientHandler.CClient;
import jld.Server.ClientHandler.CClientHandler;

public class CChannel {
	private CServer mServer;
	private String mName;
	
	public String getName() {
		return mName;
	}

	public CChannel(String channelname, CServer server){
		mServer = server;
		mName = channelname;
	}

	public void joinChannel(CClient user){
		ArrayList<CClientHandler> clienthandlers = mServer.getClientsOfChannel(this);
		for(CClientHandler ch : clienthandlers){
			ch.sendMessage("User " + user.getUsername() + " joined.", mServer.getServerClient());
		}
	}
	
	public void leaveChannel(CClient user){
		ArrayList<CClientHandler> clienthandlers = mServer.getClientsOfChannel(this);
		for(CClientHandler ch : clienthandlers){
			ch.sendMessage("User " + user.getUsername() + " left.", mServer.getServerClient());
		}
	}
}
