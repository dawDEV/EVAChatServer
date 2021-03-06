package jld;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import jld.Exceptions.ConfigParserNotInitializedException;
import jld.Server.utils.utils;

public class CConfigParser {
	private File mConfigFile = new File("config.cfg");
	private Scanner mReader;
	private int mPort = -1;
	private int mQueueLength = -1;
	private String mDefaultChannel = "";
	
	public CConfigParser(){
		try {
			mReader = new Scanner(mConfigFile);
		} catch (FileNotFoundException e) {
			utils.errorMsg("Fehlerhafte Konfiguration. [config.cfg fehlt]");
			System.exit(0);
		}
		readConfig();
	}
	
	public String getDefaultChannel() {
		return mDefaultChannel;
	}

	public void setDefaultChannel(String defaultChannel) {
		mDefaultChannel = defaultChannel;
	}

	private void readConfig(){
		while(mReader.hasNext()){
			String tag = mReader.next();
			if(tag.equalsIgnoreCase("Port")){
				mReader.next();
				mPort = mReader.nextInt();
			} else if(tag.equalsIgnoreCase("QueueLength")){
				mReader.next();
				mQueueLength = mReader.nextInt();
			} else if(tag.equalsIgnoreCase("DefaultChannel")){
				mReader.next();
				mDefaultChannel = mReader.next();
			}
		}
		if(mPort == -1 && mQueueLength == -1){
			utils.errorMsg("Konfigurationsdatei fehlerhaft, Dokumentation lesen / Helpdesk konsultieren.");
			System.exit(0);
		}
	}
	
	public int getPort() throws ConfigParserNotInitializedException{
		if(mPort == -1) throw new ConfigParserNotInitializedException();
		return mPort;
	}
	
	public int getQueueLength() throws ConfigParserNotInitializedException{
		if(mQueueLength == -1) throw new ConfigParserNotInitializedException();
		return mQueueLength;
	}
}