package jld.Server.ClientHandler;

public class CServerPacketHeaders{
	public static final String LOGIN_REJECTED = "0x0000";
	public static final String LOGIN_SUCCESSFUL = "0x0001";
	public static final String REGISTER_REJECTED = "0x0002";
	public static final String REGISTER_SUCCESSFUL = "0x0003";
	public static final String MESSAGE = "0x0004";
	
	public static boolean isValid(String header){
		switch(header){
		case LOGIN_REJECTED:
			return true;
		case LOGIN_SUCCESSFUL:
			return true;
		case REGISTER_REJECTED:
			return true;
		case REGISTER_SUCCESSFUL:
			return true;
		case MESSAGE:
			return true;
		default:
			return false;
		}
	}
}