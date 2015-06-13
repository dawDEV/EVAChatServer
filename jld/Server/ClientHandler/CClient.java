package jld.Server.ClientHandler;

import java.net.InetAddress;
import java.util.ArrayList;

import jld.Server.Channels.CChannel;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CClient {
	private String mUsername;
	private InetAddress mIp;
	private CChannel mCurrentChannel;
	private boolean mValid = false;
	private CClientHandler mHandler;

	public boolean isValid() {
		return mValid;
	}

	public CClient(String username, InetAddress ip, CClientHandler clienthandler) {
		mUsername = username;
		mIp = ip;
		mHandler = clienthandler;
		if((username != null) && (ip != null) && clienthandler != null){
			mValid = true;
			setCurrentChannel(clienthandler.getServer().getDefaultChannel());
		}
	}

	public String getUsername() {
		return mUsername;
	}

	public InetAddress getIp() {
		return mIp;
	}

	public CChannel getCurrentChannel() {
		return mCurrentChannel;
	}

	public void setCurrentChannel(CChannel channel) {
		if(mCurrentChannel != null){
			mCurrentChannel.leaveChannel(this);
			mCurrentChannel = channel;
			channel.joinChannel(this);
		} else{
			mCurrentChannel = channel;
			channel.joinChannel(this);
		}
		ArrayList<CClientHandler> clients = mHandler.getServer().getClientsOfChannel(mCurrentChannel);
		StringBuilder users = new StringBuilder("");
		for(int i = 0; i < clients.size(); i++){
			final int MAX_LENGTH = 230;
			// eigenen Client NICHT mit uebertragen
			if(clients.get(i).getClient() == this) continue;
			if(users.length() + clients.get(i).getClient().getUsername().length() >= MAX_LENGTH){
				mHandler.sendMessage("inChannel" + users.toString(), mHandler.getServer().getServerClient());
				users = new StringBuilder("");
			}
			users.append(" " + clients.get(i).getClient().getUsername());
		}
		mHandler.sendMessage("inChannel" + users.toString(), mHandler.getServer().getServerClient());
	}
	
	public void disconnect(){
		if(mValid){
			mCurrentChannel.leaveChannel(this);
		}
	}
}
