package jld.Server.ClientHandler;

import java.util.ArrayList;

public class CClientPacket {
	private static final String[] PACKET_TYPES =
		{ "0x0000", "0x0001", "0x0002" };
	
	private String mPacketType;
	private ArrayList<String> mParameters;
	public static boolean checkPacket(String packet){
		for(int i = 0; i < PACKET_TYPES.length; i++){
			if(packet.startsWith(PACKET_TYPES[i])) return true;
		}
		return false;
	}
	
	public CClientPacket(String packet){
		assert(checkPacket(packet));
		
	}

	public final String getPacketType() {
		return mPacketType;
	}

	private final void setPacketType(String packetType) {
		mPacketType = packetType;
	}

	public final String getParameter(int pos) {
		if(pos >= mParameters.size()) return "";
		return mParameters.get(pos);
	}

	private final void setParameter(int pos, String value) {
		mParameters.set(pos, value);
	}
}
