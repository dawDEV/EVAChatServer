package jld.Server.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import jld.Exceptions.InvalidPacketException;
import jld.Exceptions.InvalidPacketHeaderException;
import jld.Server.CServer;
import jld.Server.Channels.CChannel;
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
	
	
	public PrintWriter getOutput() {
		return mOutput;
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
	
	public void sendMessage(String msg, CClient sender){
		try {
			final int MAX_MESSAGE_LENGTH = 245 - sender.getUsername().length();
			if(msg.length() < MAX_MESSAGE_LENGTH){
				/*
				 * Im Falle, dass ein User eine Nachricht sendet, tritt immer dieser Fall ein, da bereits beim auswerten eines eingegangenen
				 * Packets nur max. 256 Zeichen ausgewertet werden!
				 */
				CServerPacket.sendMessage(msg, this, sender);
			} else {
				/*
				 * Dieser Fall kann nur eintreten wenn der Server eine Nachricht senden möchte, die länger als
				 * (256 Zeichen - Laenge der Nachrichtenlaenge [3] - Laenge der Laenge des Senderusernames [2] - Laenge des Senderusernames) ist
				 */
				while(msg.length() > MAX_MESSAGE_LENGTH){
					CServerPacket.sendMessage(msg.substring(0, MAX_MESSAGE_LENGTH-1), this, sender);
					msg = msg.substring(MAX_MESSAGE_LENGTH - 1, msg.length());
				}
			}
		} catch (InvalidPacketException e) {
			/* Wird im Prinzip nie erreicht, da die Nachricht zur Not auf mehrere Paket aufgesplittet wird */
			e.printStackTrace();
		}
	}
	
	public void onMessageReceived(String message){
		if(message.startsWith("/join ")){
			/*
			 * Fall: /join zum Betreten eines anderen Channels wird ausgeführt.
			 */
			Scanner sc = new Scanner(message.substring(5));
			if(sc.hasNext() ){
				String channelName = sc.next();
				System.out.println(channelName);
				CChannel channelToJoin = mServer.getChannel(channelName);
				if(channelToJoin != null){
					mClient.setCurrentChannel(channelToJoin);
				} else {
					sendMessage("Channel existiert nicht.", mServer.getServerClient());
				}
			} else{
				sendMessage("Benutzung: /join [Channelname]", mServer.getServerClient());
			}
		} else{
			/*
			 * Fall: Kein Befehl wird ausgeführt.
			 * Aktion: Alle Clients im Channel holen die Nachricht senden mit dem Absender des eigenen Users 
			 */
			ArrayList<CClientHandler> clientsInSameChannel = mServer.getClientsOfChannel(mClient.getCurrentChannel());
			for(int i = 0; i < clientsInSameChannel.size(); i++){
				if(clientsInSameChannel.get(i).equals(this)) continue;
				clientsInSameChannel.get(i).sendMessage(message, mClient);
			}
		}
		
		
	}

}

