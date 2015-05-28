package jld.Server.ClientHandler;

import java.util.ArrayList;

import jld.Server.utils.utils;

public class CClientPacket {
	private static final String[] PACKET_TYPES =
		{ "0x0000", "0x0001", "0x0002" };
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
			int MIN_LOGINPACKET_LENGTH = 10; 
			/*
			 * Pruefen ob der Packettyp + die Laenge des ersten und zweiten Packets vorhanden sind (6 Digits + 2 Digits + 2 Digits)
			 * Siehe Packet Structure
			 */
			if(mPacket.length() < MIN_LOGINPACKET_LENGTH) return;
			int posPointer = 6;
			String tmp = mPacket.substring(posPointer, posPointer+2);
			if(!utils.isNumber(tmp)) return;
			int param1Length = Integer.valueOf(tmp);
			mParameters.add(mPacket.substring(posPointer+2, posPointer + 2 + param1Length));
			MIN_LOGINPACKET_LENGTH += param1Length;
			
			/*
			 * Pruefen ob auch noch zusätzlich der Parameter drin ist
			 */
			if(mPacket.length() < MIN_LOGINPACKET_LENGTH) return;
			posPointer += param1Length + 2;
			tmp = mPacket.substring(posPointer, posPointer+2);
			if(!utils.isNumber(tmp)) return;
			int param2Length = Integer.valueOf(tmp);
			mParameters.add(mPacket.substring(posPointer+2, posPointer + 2 + param2Length));
			System.out.println(mParameters.get(1));
			/*
			 * Pruefen ob die Gesamtlaenge stimmt.
			 */
			if(mPacket.length() > posPointer + 2 + param2Length) return;
			
			/**
			 * DONE:
			 * 	- Parameters of login read
			 * TODO:
			 * 	- Add login implementation
			 */
			System.out.println(param1Length);
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
}
