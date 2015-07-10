package jld.Server.ClientHandler;

import jld.Exceptions.InvalidPacketException;

public class CServerPacket {
	/* 
	 * Es ist nur der Header noetig, alle Serverpakete zum Client, ausser der Nachricht, die �ber eine gesonderte statische Methode
	 * benutzt wird, sind 0-parametrig.
	 */
	private String mPacketHeader = "";
	
	public CServerPacket(String packetHeader){
		assert(CServerPacketHeaders.isValid(packetHeader));
		mPacketHeader = packetHeader;
	}
	
	public void sendPacket(CClientHandler receiver){
		/* Der Header wird bereits bei Erzeugung des Objekts geprueft, deshalb waeren weitere Pruefungen redundant */
		receiver.getOutput().print(mPacketHeader);
		receiver.getOutput().flush();
	}
	
	@SuppressWarnings("static-access")
	public static void sendMessage(String message, CClientHandler receiver, CClient sender) throws InvalidPacketException{
		// Siehe Packet Structure (MAX_PACKET_LENGTH - PacketHeader - Parameter1_laenge_laenge - Parameter1_laenge - Parameter2_laenge_laenge)
		final int MAX_MESSAGE_LENGTH = 245 - sender.getUsername().length();
		if(message.length() > MAX_MESSAGE_LENGTH) throw new InvalidPacketException();
		receiver.getOutput().println("0x0004" + makeValidUserParameterLength(sender.getUsername().length()) + sender.getUsername() + makeValidMessageParameterLength(message.length()) + message);
		receiver.getOutput().flush();
		// TODO Remove
		/*try {
			// Damit die Nachrichten nicht zu schnell rausgeschickt werden.
			//receiver.sleep(100);
		} catch (InterruptedException e) {
		}*/
	}
	
	private static String makeValidUserParameterLength(int length){
		assert(length < 64);
		if(length < 10)
			return "0" + length;
		else
			return Integer.toString(length);
	}
	
	private static String makeValidMessageParameterLength(int length){
		assert(length < 256);
		if(length < 10)
			return "00" + length;
		else if(length < 100)
			return "0" + length;
		else
			return Integer.toString(length);
	}
}