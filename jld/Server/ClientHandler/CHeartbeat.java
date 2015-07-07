package jld.Server.ClientHandler;

import jld.Server.utils.utils;

public class CHeartbeat extends Thread {
	private CClientHandler mParent;
	private boolean hbReceived = true;
	private boolean stopThread = false;
	
	public CHeartbeat(CClientHandler parent){
		mParent = parent;
		this.start();
	}
	
	public void run(){
		try {
			while(!stopThread){
				if(!hbReceived){
					utils.debugMsg("No HB from " + mParent.getClient().getUsername());
					mParent.notifyDisconnect();
					return;
				}
				hbReceived = false;
				sleep(6000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopThread(){
		stopThread = true;
	}
	
	public void beatReceived(){
		hbReceived = true;
	}
}