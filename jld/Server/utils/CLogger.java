package jld.Server.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CLogger {
	private FileWriter mWriter;
	
	public CLogger(File file){
		try {
			mWriter = new FileWriter(file, true);
		} catch (IOException e) {
			System.out.println("Critical file writing exception");
			System.exit(0);
		}
	}
	
	public synchronized void write(String msg){
		try {
			mWriter.write(msg);
			mWriter.flush();
		} catch (IOException e) {
			utils.errorMsg("Logging failed and might be corrupted! Check if the server still takes logs or not and contact your helpdesk");
		}
	}
}
