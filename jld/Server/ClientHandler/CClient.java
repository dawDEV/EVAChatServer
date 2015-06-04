package jld.Server.ClientHandler;

import java.net.InetAddress;

import jld.Server.Channels.CChannel;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CClient {
	private String mUsername;
	private InetAddress mIp;
	private CChannel mCurrentChannel;
	private boolean mValid = false;

	public boolean isValid() {
		return mValid;
	}

	public CClient(String username, InetAddress ip, CClientHandler clienthandler) {
		mUsername = username;
		mIp = ip;
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
		}
		mCurrentChannel = channel;
		channel.joinChannel(this);
	}
	
	public void disconnect(){
		mCurrentChannel.leaveChannel(this);
	}
}
