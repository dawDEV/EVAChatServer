package jld.Server.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import jld.Server.CServer;
import jld.Server.utils.utils;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CClientHandler extends Thread {
	private CServer mServer;
	private Socket mSocket;
	private BufferedReader mInput;
	private PrintWriter mOutput;
	private CClient mClient;

	public CClientHandler(CServer server, Socket socket) {
		mServer = server;
		mSocket = socket;
		try {
			mSocket.setSoTimeout(100000000);
		} catch (SocketException se) {
			utils.errorMsg("Error when setting socket timeout of clientsocket");
		}
		try {
			mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mOutput = new PrintWriter(mSocket.getOutputStream());
			
		} catch (IOException e) {
			utils.errorMsg("Error when getting IO stream of new client");
		}
		mClient = new CClient("", mSocket.getInetAddress());
		this.start();
	}
	
	public CClient getClient() {
		return mClient;
	}

	@Override
	public void run() {
		/**
		 * Perform login in the following procedure:
		 * 1. get username and check if a userfile is available
		 * 2. get passwort and check if password in userfile is the same
		 * 3. a) if userfile is not available or password is wrong -> Deny login
		 * 3. b) else: Allow login
		 */
		try {
			while(true){
				char buffer[] = new char[256];
				int length = 0;
				length = mInput.read(buffer, 0, 256);
				
				/* length = -1 => Nutzer hat die Verbindung getrennt.
				 * length >= 1 => Nachrichten stehen an.
				 */
				if(length == -1){
					notifyDisconnect();
					return;
				}
				String msg = String.valueOf(buffer);
				
				// Packetstruktur prüfen
				if(!CClientPacket.checkPacket(msg)) continue;
				
				// Packet in Einzelteile zerlegen
				msg = msg.substring(0, length -2);
				
				CClientPacket packet = new CClientPacket(msg);
				
				utils.infoMsg("Got message -> " + msg);
			}	
		} catch (IOException e) {
			notifyDisconnect();
		}
	}

	private void notifyDisconnect() {
		mServer.clientDisconnected(this);
		try {
			mSocket.close();
		} catch (IOException e) {
			utils.errorMsg("Error when closing socket is disconnected client");
		}
	}
}
