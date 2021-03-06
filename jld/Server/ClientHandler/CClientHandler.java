package jld.Server.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import jld.Exceptions.InvalidPacketException;
import jld.Server.CServer;
import jld.Server.Channels.CChannel;
import jld.Server.utils.CCommand;
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
	private CHeartbeat mHBThread;
	private static LinkedList<CCommand> mCommands = new LinkedList<CCommand>();
	
	private boolean stopThread = false;
	
	// Packet rest if two packets are received
	private String mRestMessage = "";

	public CClientHandler(CServer server, Socket socket) {
		if(mCommands.isEmpty()){
			// Belegt die Liste erst, wenn der erste Nutzer verbindet. Damit spart man minimal RAM.
			registerCommand("/join", "/join #channelname um einem Channel beizutreten.");
			registerCommand("/mychannel", "Gibt den aktuellen Channel zurück.");
			registerCommand("/help", "Zeigt die Hilfe.");
		}
		mServer = server;
		mSocket = socket;
		try {
			mSocket.setSoTimeout(300);
			mSocket.setTcpNoDelay(false);
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
		mHBThread = new CHeartbeat(this);
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
		while(!stopThread){
			try {
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
					// receiving beats
					mHBThread.beatReceived();
					// Packet auf richtige Laenge schneiden
					length = 0;
					if(!mRestMessage.equals("")){
						StringBuilder sb = new StringBuilder(mRestMessage);
						sb.append(msg);
						msg = sb.toString();
						mRestMessage = "";
					}
					while(length < msg.length() && (int)msg.charAt(length) != 0){
						length++;
					}
					// Packetstruktur pruefen
					if(!CClientPacket.checkPacket(msg)) continue;
					msg = msg.substring(0, length);
					CClientPacket packet = new CClientPacket(msg, this, mSocket.getInetAddress());
					packet.handlePacket();
					mOutput.print(1);
					mOutput.flush();
					utils.debugMsg("Got message (FROM: " + mSocket.getInetAddress() + ") -> " + msg);
			} catch (IOException e) {
				// Pruefen ob die Exception geworfen wurde weil der Timeout einfach erreicht wurde oder weil die Verbindung tot ist.
				mOutput.print(1);
				mOutput.flush();
				if(mOutput.checkError()){
					notifyDisconnect();
					try {
						mSocket.close();
					} catch (IOException e1) {
					}
					return;
				}
			}
		}
		try {
			mSocket.close();
		} catch (IOException e) {
		}
	}

	protected void notifyDisconnect() {
		stopThread = true;
		mHBThread.stopThread();
		mClient.disconnect();
		mServer.clientDisconnected(this);
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
				 * Dieser Fall kann nur eintreten wenn der Server eine Nachricht senden moechte, die laenger als
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
	
	private void registerCommand(String command, String helpText){
		mCommands.add(new CCommand(command, helpText));
	}
	
	public void onMessageReceived(String message){
		if(message.startsWith("/join")){
			/*
			 * Fall: /join zum Betreten eines anderen Channels wird ausgef�hrt.
			 */
			Scanner sc = new Scanner(message.substring(5));
			if(sc.hasNext() ){
				String channelName = sc.next();
				CChannel channelToJoin = mServer.getChannel(channelName);
				if(channelToJoin != null){
					mClient.setCurrentChannel(channelToJoin);
				} else {
					sendMessage("Channel existiert nicht.", mServer.getServerClient());
				}
			} else{
				sendMessage("Benutzung: /join [Channelname]", mServer.getServerClient());
			}
			sc.close();
			
		} else if(message.startsWith("/mychannel")){
			/*
			 * Fall: /mychannel zum erfragen des eigenen Channels
			 */
			sendMessage(mClient.getCurrentChannel().getName(), mServer.getServerClient());
		} else if(message.startsWith("/help")){
			/*
			 * Fall: /mychannel zum erfragen des eigenen Channels
			 */
			String helpText = "Folgende Befehle stehen Ihnen zur Verfuegung:";
			sendMessage(helpText, mServer.getServerClient());
			for(CCommand command : mCommands){
				sendMessage(command.getCommand() + ": " + command.getHelpText(), mServer.getServerClient());
			}
			
		} else{
			/*
			 * Fall: Kein Befehl wird ausgef�hrt.
			 * Aktion: Alle Clients im Channel holen die Nachricht senden mit dem Absender des eigenen Users 
			 */
			ArrayList<CClientHandler> clientsInSameChannel = mServer.getClientsOfChannel(mClient.getCurrentChannel());
			for(int i = 0; i < clientsInSameChannel.size(); i++){
				if(clientsInSameChannel.get(i).equals(this)) continue;
				clientsInSameChannel.get(i).sendMessage(message, mClient);
			}
		}
		
		
	}
	
	public String getRestMessage(){
		return mRestMessage;
	}
	
	public void setRestMessage(String restMessage){
		assert(restMessage != null);
		mRestMessage = restMessage;
	}

}

