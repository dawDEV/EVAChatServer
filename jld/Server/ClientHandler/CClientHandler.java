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
	private CClient mClient = null;

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
		mClient = new CClient("", mSocket.getInetAddress(), null);
		this.start();
	}
	
	public CServer getServer() {
		return mServer;
	}
	protected void setClient(CClient client) {
		mClient = client;
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
				
				// Packetstruktur pruefen
				if(!CClientPacket.checkPacket(msg)) continue;
				
				// Packet in Einzelteile zerlegen
				msg = msg.substring(0, length -2);
				
				CClientPacket packet = new CClientPacket(msg, this, mSocket.getInetAddress());
				packet.handlePacket();
				utils.debugMsg("Got message (FROM: " + mSocket.getInetAddress() + ") -> " + msg);
			}	
		} catch (IOException e) {
			notifyDisconnect();
		}
	}

	private void notifyDisconnect() {
		mClient.disconnect();
		mServer.clientDisconnected(this);
		try {
			mSocket.close();
		} catch (IOException e) {
			utils.errorMsg("Error when closing socket is disconnected client");
		}
	}
	
	protected void informRegisterFailed(){
		mOutput.print("0x0002");
		mOutput.flush();
	}
	
	public void sendMessage(String msg, CClient sender){
		// Siehe Packet Structure (MAX_PACKET_LENGTH - PacketHeader - Parameter1_laenge_laenge - Parameter1_laenge - Parameter2_laenge_laenge)
		final int MAX_MESSAGE_LENGTH = 245 - sender.getUsername().length();
		assert(msg.length() <= MAX_MESSAGE_LENGTH);
		mOutput.println("0x0004" + makeValidUserParameterLength(sender.getUsername().length()) + sender.getUsername() + makeValidMessageParameterLength(msg.length()) + msg);
		mOutput.flush();
	}
	
	public String makeValidMessageParameterLength(int length){
		assert(length < 256);
		if(length < 10)
			return "00" + length;
		else if(length < 100)
			return "0" + length;
		else
			return Integer.toString(length);
	}
	
	public String makeValidUserParameterLength(int length){
		assert(length < 64);
		if(length < 10)
			return "0" + length;
		else
			return Integer.toString(length);
	}
}

class ServerPacketHeaders{
	public final String LOGIN_REJECTED = "0x0000";
	public final String LOGIN_SUCCESSFUL = "0x0001";
	public final String REGISTER_REJECTED = "0x0002";
	public final String REGISTER_SUCCESSFUL = "0x0003";
	public final String MESSAGE = "0x0004";
}