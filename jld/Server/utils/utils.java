package jld.Server.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jld.CMain;

/**
 * Created by Dorian on 23.04.2015.
 */
public class utils {
	static CLogger logger = new CLogger(new File("log.txt"));
	static SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
	public static void debugMsg(String msg) {
		if (CMain.isOnDebug())
			msg = String.format("%s %-10s%s%n", simpleDateFormatter.format(new Date()), "[DEBUG]:", msg);
			System.out.print(msg);
			logger.write(msg);
	}

	public static void errorMsg(String msg) {
		msg = String.format("%s %-10s%s%n", simpleDateFormatter.format(new Date()), "[ERROR]:", msg);
		System.out.print(msg);
		logger.write(msg);
	}

	public static void infoMsg(String msg) {
		msg = String.format("%s %-10s%s%n", simpleDateFormatter.format(new Date()), "[INFO]:", msg);
		System.out.print(msg);
		logger.write(msg);
	}
	
	public static boolean isNumber(String toCheck){
		for(int i = 0; i < toCheck.length(); ++i){
			char c = toCheck.charAt(i);
			if(c != '0' && c != '1' && c != '2' && c != '3' && c != '4' && c != '5' && c != '6' && c != '7' && c != '8' && c != '9') return false;
		}
		return true;
	}
}