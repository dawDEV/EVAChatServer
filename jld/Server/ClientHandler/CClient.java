package jld.Server.ClientHandler;

import java.net.InetAddress;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CClient {
	private String mUsername;
	private InetAddress mIp;
	private String mCurrentChannel;
	private boolean mValid = false;

	public boolean isValid() {
		return mValid;
	}

	public CClient(String username, InetAddress ip) {
		mUsername = username;
		mIp = ip;
		if(!mUsername.equals("")) mValid = true;
	}

	public String getUsername() {
		return mUsername;
	}

	public InetAddress getIp() {
		return mIp;
	}

	public String getCurrentChannel() {
		return mCurrentChannel;
	}

	public void setCurrentChannel(String currentChannel) {
		mCurrentChannel = currentChannel;
	}
}
