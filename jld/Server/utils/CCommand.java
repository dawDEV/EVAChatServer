package jld.Server.utils;

public class CCommand {
	private String mCommand;
	private String mHelpText;
	
	public CCommand(String command, String helpText){
		mCommand = command;
		mHelpText = helpText;
	}
	
	// Damit man keinen Command erstellen kann ohne die Parameter zu nutzen
	@SuppressWarnings("unused")
	private CCommand(){}
	
	public String getCommand(){
		return mCommand;
	}
	public String getHelpText(){
		return mHelpText;
	}
}
