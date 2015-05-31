package jld.Server.ClientHandler;

import java.util.ArrayList;

import jld.Server.utils.utils;

public class CClientPacket {
	private static final String[] PACKET_TYPES =
		{ "0x0000", "0x0001", "0x0002" };
	private final byte LOGIN_TYPE = 0;
	private final byte REGISTER_TYPE = 1;
	private String mPacket = "";
	private String mPacketType = "";
	private ArrayList<String> mParameters = new ArrayList<String>();
	public static boolean checkPacket(String packet){
		for(int i = 0; i < PACKET_TYPES.length; i++){
			if(packet.startsWith(PACKET_TYPES[i])) return true;
		}
		return false;
	}
	
	public CClientPacket(String packet){
		assert(checkPacket(packet));
		this.mPacket = packet;
		setPacketType(packet.substring(0, 6));
		handlePacket();
	}

	public final String getPacketType() {
		return mPacketType;
	}

	private void handlePacket(){
		if(mPacketType.compareTo(PACKET_TYPES[0]) == 0){
			processRegisterOrLoginPacket(LOGIN_TYPE);
		} else if(mPacketType.compareTo(PACKET_TYPES[1]) == 0){
			
		} else if(mPacketType.compareTo(PACKET_TYPES[2]) == 0){
			
		}
	}
	
	private final void setPacketType(String packetType) {
		mPacketType = packetType;
	}

	public final String getParameter(int pos) {
		if(pos >= mParameters.size()) return "";
		return mParameters.get(pos);
	}
	/**
	 * Verarbeitet das aktuelle Objekt nach dem Login- oder Registerschema. Im Falle eines ungültigen Pakets wird das Paket verworfen.<br>
	 * Beispiel:<br>
	 * 		0x000003dwe0512345 =><br>
	 * 		Username (param1):	dwe<br>
	 * 		Passwort (param2):	12345
	 * @param type Gibt an ob es sich um ein Login- oder Registerpacket handelt. Die Auswertung des Packets ist bis zur Verwertung gleich, dort trennt sich das eine in Login, das andere in Registration.
	 */
	private final void processRegisterOrLoginPacket(final byte type){
		int MIN_LOGINPACKET_LENGTH = 10; 
		/*
		 * Pruefen ob der Packettyp + die Laenge des ersten und zweiten Packets vorhanden sind (6 Digits + 2 Digits + 2 Digits)
		 * Siehe Packet Structure
		 */
		if(mPacket.length() < MIN_LOGINPACKET_LENGTH) return;
		int posPointer = 6;
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
		 * 0x00003ABCXX
		 */
		MIN_LOGINPACKET_LENGTH += param1Length;
		if(mPacket.length() < MIN_LOGINPACKET_LENGTH) return;
		
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
		MIN_LOGINPACKET_LENGTH += param2Length;
		/*
		 * Pruefen der Gesamtlaenge, diesmal auf Gleichheit und falls alles stimmt auch den zweiten Parameter hinzufügen.
		 */
		if(mPacket.length() != MIN_LOGINPACKET_LENGTH) return;
		mParameters.add(mPacket.substring(posPointer+2, posPointer + 2 + param2Length));
		
		/**
		 * DONE:
		 * 	- Parameters of login read
		 * TODO:
		 * 	- Add login implementation
		 */
		switch(type){
			case LOGIN_TYPE:
				// Perform login
			break;
			
			case REGISTER_TYPE:
				// Perform register
			break;
		}
	}
}
