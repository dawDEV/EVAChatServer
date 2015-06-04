package jld.Server.ClientHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

import jld.Server.utils.utils;

public class CClientPacket {
	private final byte LOGIN_TYPE = 0;
	private final byte REGISTER_TYPE = 1;
	private String mPacket = "";
	private String mPacketType = "";
	private CClientHandler mCaller = null;
	private InetAddress mIpOfClient = null;
	private ArrayList<String> mParameters = new ArrayList<String>();

	private final int PACKETHEADER_LENGTH = 6;
	
	public CClientPacket(String packet, CClientHandler callingHandler, InetAddress ipOfClient){
		this.mPacket = packet;
		this.mCaller = callingHandler;
		this.mIpOfClient = ipOfClient;
		setPacketType(packet.substring(0, 6));
	}

	public static boolean checkPacket(String packet){
		return ClientPacketHeaders.checkPacket(packet);
	}
	
	public final String getPacketType() {
		return mPacketType;
	}

	public void handlePacket(){
		if(mPacketType.equals(ClientPacketHeaders.LOGIN)){
			if(mCaller.getClient().isValid()) return;
			processRegisterOrLoginPacket(LOGIN_TYPE);
		} else if(mPacketType.equals(ClientPacketHeaders.REGISTER)){
			if(mCaller.getClient().isValid()) return;
			processRegisterOrLoginPacket(REGISTER_TYPE);
		} else if(mPacketType.equals(ClientPacketHeaders.MESSAGE)){
			if(!mCaller.getClient().isValid()) return;
			processMessage();
		}
	}
	
	private final void setPacketType(String packetType) {
		mPacketType = packetType;
	}

	public final String getParameter(int pos) {
		if(pos >= mParameters.size()) return "";
		return mParameters.get(pos);
	}
	
	private void processMessage(){
		int MIN_PACKET_LENGTH = 9;	// Da 1-parametrig. (6 Digits + 3 Digits)
		final int MESSAGE_LENGTH_LENGTH = 3;
		if(mPacket.length() < MIN_PACKET_LENGTH) return;
		/*
		 * Pruefen ob der Packettyp + die Laenge des ersten Parameters vorhanden sind (6 Digits + 2 Digits)
		 * Siehe Packet Structure
		 */
		if(mPacket.length() < MIN_PACKET_LENGTH) return;
		int posPointer = PACKETHEADER_LENGTH;
		/*
		 * Parameterlaenge aus Packet rausschneiden
		 */
		String tmp = mPacket.substring(posPointer, posPointer+MESSAGE_LENGTH_LENGTH);
		/*
		 * Pruefen ob die erste Laenge tatsaechlich eine Zahl ist und diese dann param1Length zuweisen
		 */
		if(!utils.isNumber(tmp)) return;
		int param1Length = Integer.valueOf(tmp);
		/*
		 * Pruefen ob das Paket der neuen Laenge (Packettyp [6] + Laenge Param1 [3] + Param1 [Laenge Param1])
		 * Beispiel:
		 * Nachricht mit 10 Stellen.
		 * 0x0002010Hallo!!!!!
		 */
		MIN_PACKET_LENGTH += param1Length;
		if(mPacket.length() < MIN_PACKET_LENGTH) return;
		/*
		 * Param1 zur Parameterliste hinzufügen
		 */
		mParameters.add(mPacket.substring(posPointer+MESSAGE_LENGTH_LENGTH, posPointer + MESSAGE_LENGTH_LENGTH + param1Length - 1));
		System.out.println("Nachricht: " + mParameters.get(0));
	}
	
	/**
	 * Verarbeitet das aktuelle Objekt nach dem Login- oder Registerschema. Im Falle eines ungültigen Pakets wird das Paket verworfen.<br>
	 * Beispiel:<br>
	 * 		0x000003dwe0512345 =><br>
	 * 		Username (param1):	dwe<br>
	 * 		Passwort (param2):	12345
	 * @param type Gibt an ob es sich um ein Login- oder Registerpacket handelt. Die Auswertung des Packets ist bis zur Verwertung gleich, dort trennt sich das eine in Login, das andere in Registration.
	 */
	private void processRegisterOrLoginPacket(final byte type){
		int MIN_PACKET_LENGTH = 10; // Da 2-parametrig (6 Digits + 2 Digits)
		/*
		 * Pruefen ob der Packettyp + die Laenge des ersten und zweiten Parameters vorhanden sind (6 Digits + 2 Digits + 2 Digits)
		 * Siehe Packet Structure
		 */
		if(mPacket.length() < MIN_PACKET_LENGTH) return;
		int posPointer = PACKETHEADER_LENGTH;
		String tmp = mPacket.substring(posPointer, posPointer+2);
		/*
		 * Pruefen ob die erste Laenge tatsaechlich eine Zahl ist und diese dann param1Length zuweisen
		 */
		if(!utils.isNumber(tmp)) return;
		int param1Length = Integer.valueOf(tmp);
		/*
		 * Pruefen ob das Paket der neuen Laenge (Packettyp [6] + Laenge Param1 [2] + Param1 [Laenge Param1] + Laenge Param1 [2])
		 * Beispiel:
		 * Login mit Username ABC und einem XX stelligen Passwort (0 <= XX <= 64) 
		 * 0x000003ABCXX
		 */
		MIN_PACKET_LENGTH += param1Length;
		if(mPacket.length() < MIN_PACKET_LENGTH) return;
		
		/*
		 * Param1 zur Parameterliste hinzufügen
		 */
		mParameters.add(mPacket.substring(posPointer+2, posPointer + 2 + param1Length));
		
		/*
		 * Analog zum ersten Schritt für den zweiten Parameter
		 */
		posPointer += param1Length + 2;
		tmp = mPacket.substring(posPointer, posPointer+2);
		if(!utils.isNumber(tmp)) return;
		int param2Length = Integer.valueOf(tmp);
		MIN_PACKET_LENGTH += param2Length;
		/*
		 * Pruefen der Gesamtlaenge, diesmal auf Gleichheit und falls alles stimmt auch den zweiten Parameter hinzufügen.
		 */
		if(mPacket.length() != MIN_PACKET_LENGTH) return;
		mParameters.add(mPacket.substring(posPointer+2, posPointer + 2 + param2Length));
		
		switch(type){
			case LOGIN_TYPE:
				perform_login();
			break;
			
			case REGISTER_TYPE:
				perform_registration();
			break;
		}
	}
	
	private boolean check_login(String username, String password){
		assert(username != null && password != null && username.length() > 0 && password.length() > 0);
		File userfile = new File("logins/" + username + ".usr");
		if(userfile.exists()){
			try{
				Scanner userfileReader = new Scanner(userfile);				
				String pwdToCheck = userfileReader.nextLine();
				if(password.equals(pwdToCheck)){
					userfileReader.close();
					return true;
				} else{
					userfileReader.close();
					return false;
				}
			} catch(FileNotFoundException e){
				// Wird niemals erreicht, außer die Datei wird während des Lesevorgangs gelöscht.
				e.printStackTrace();
				return false;
			}
		} else return false;
	}
	
	private void perform_registration(){
		if(mParameters.get(0).length() > 0){
			// username ist größer als 0 Zeichen
			File userfile = new File("logins/" + mParameters.get(0) + ".usr");
			if(!userfile.exists()){
				// user gibt es noch nicht, Registration kann vollzogen werden
				try {
					/*
					 * Format des Datei Inhalts
					 * {password}
					 * {ggf. zukuenftig weitere Parameter wie z.B. Befugnisse etc.}
					 */
					userfile.createNewFile();
					PrintWriter userfileWriter = new PrintWriter(userfile);
					mCaller.setClient(new CClient(mParameters.get(0), mIpOfClient, mCaller));
					userfileWriter.println(mParameters.get(1));
					userfileWriter.flush();
					userfileWriter.close();
					utils.infoMsg("User " + mCaller.getClient().getUsername() + " just registered from " + mIpOfClient.toString() + "!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else{
				mCaller.informRegisterFailed();
			}
		}
	}
	private void perform_login(){
		if(check_login(mParameters.get(0), mParameters.get(1))){
			mCaller.setClient(new CClient(mParameters.get(0), mIpOfClient, mCaller));
			utils.infoMsg("User " + mCaller.getClient().getUsername() + " just logged in from " + mIpOfClient.toString() + "!");
		} else{
			utils.infoMsg("Login for " + mParameters.get(0) + " failed from " + mIpOfClient.toString());
		}
	}
}

class ClientPacketHeaders{
	public static final String LOGIN = "0x0000";
	public static final String REGISTER = "0x0001";
	public static final String MESSAGE = "0x0002";
	
	public static boolean checkPacket(String packet){
		if(packet.length() < 6) return false;
		packet = packet.substring(0, 6);
		switch(packet){
			case LOGIN:
				return true;
			case REGISTER:
				return true;
			case MESSAGE:
				return true;
		}
		return false;
	}
}
