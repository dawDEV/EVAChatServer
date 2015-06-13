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
			if(ch.getClient() == user) continue;
			ch.sendMessage("joinChannel " + user.getUsername(), mServer.getServerClient());
		}
	}
	
	public void leaveChannel(CClient user){
		ArrayList<CClientHandler> clienthandlers = mServer.getClientsOfChannel(this);
		for(CClientHandler ch : clienthandlers){
			if(ch.getClient() == user) continue;
			ch.sendMessage("leaveChannel " + user.getUsername(), mServer.getServerClient());
		}
	}
}
