package jld.Server.ClientHandler;

import java.net.InetAddress;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CClient {
	private String mUsername;
	private InetAddress mIp;
	private String mCurrentChannel;

	public CClient(String username, InetAddress ip) {
		mUsername = username;
		mIp = ip;
	}

	public String getUsername() {
		return mUsername;
	}

	public InetAddress getIp() {
		return mIp;
	}
}
