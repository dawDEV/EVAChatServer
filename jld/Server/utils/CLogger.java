package jld.Server.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CLogger {
	private FileWriter mWriter;
	int mWriteActions = 0;
	
	public CLogger(File file){
		try {
			mWriter = new FileWriter(file);
		} catch (IOException e) {
			System.out.println("Critical file writing exception");
			System.exit(0);
		}
	}
	
	public synchronized void write(String msg){
		try {
			mWriter.write(msg);
			if(mWriteActions >= 10){
				mWriter.flush();
				mWriteActions = 0;
			}
		} catch (IOException e) {
			utils.errorMsg("Logging failed and might be corrupted! Check if the server still takes logs or not and contact your helpdesk");
		}
	}
}
