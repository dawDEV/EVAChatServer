package jld;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import jld.Exceptions.ConfigParserNotInitializedException;
import jld.Server.utils.utils;

public class CConfigParser {
	File mConfigFile = new File("config.cfg");
	Scanner mReader;
	int mPort = -1;
	int mQueueLength = -1;
	
	public CConfigParser(){
		try {
			mReader = new Scanner(mConfigFile);
		} catch (FileNotFoundException e) {
			utils.errorMsg("Fehlerhafte Konfiguration. [config.cfg fehlt]");
		}
		readConfig();
	}
	
	private void readConfig(){
		while(mReader.hasNext()){
			if(mReader.next() == "Port"){
				mReader.next();
				mPort = mReader.nextInt();
			} else if(mReader.next() == "QueueLength"){
				mReader.next();
				mQueueLength = mReader.nextInt();
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
	
	public int queueLength() throws ConfigParserNotInitializedException{
		if(mQueueLength == -1) throw new ConfigParserNotInitializedException();
		return mQueueLength;
	}
}