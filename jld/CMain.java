package jld;

import jld.Server.CServer;
import jld.Server.utils.utils;

/**
 * Created by Dorian on 23.04.2015.
 */
public class CMain {
	private static boolean debugMode = false;

	public static void main(String[] args) {
		if (args.length > 0)
			debugMode = Boolean.valueOf(args[0]);
		if(debugMode){
			utils.infoMsg("Debug mode active");
		}
		
		CConfigParser configData = new CConfigParser();
		
		// Start the main server process
		new CServer(1234);
		
		
	}

	public static boolean isOnDebug() {
		return debugMode;
	}
}
