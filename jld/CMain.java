package jld;

import jld.Exceptions.ConfigParserNotInitializedException;
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
		try {
			// Start the server
			new CServer(configData.getPort());
		} catch (ConfigParserNotInitializedException e) {
			utils.errorMsg("Fehler beim Auslesen der Konfigurationsdatei.");
			System.exit(0);
		}
}

	public static boolean isOnDebug() {
		return debugMode;
	}
}
