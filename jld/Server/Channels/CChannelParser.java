package jld.Server.Channels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import jld.Server.CServer;
import jld.Server.utils.utils;

public abstract class CChannelParser {
	public static CChannel[] readChannels(CServer server){
		File channelFile = new File("channels.lst");
		try {
			ArrayList<CChannel> channels = new ArrayList<CChannel>();
			Scanner channelReader = new Scanner(channelFile);
			while(channelReader.hasNextLine()){
				channels.add(new CChannel(channelReader.nextLine().toLowerCase(), server));
			}
			channelReader.close();
			CChannel[] channelsArr = new CChannel[channels.size()];
			for(int i = 0; i < channels.size(); i++){
				channelsArr[i] = channels.get(i);
			}
			return channelsArr;
		} catch (FileNotFoundException e) {
			utils.errorMsg("Fehlerhafte Konfiguration. [channels.lst fehlt]");
			System.exit(0);
			return null;
		}
	}
}
