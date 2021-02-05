/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Mytchel
 */
public class HostThread extends Thread {	
	private AndroidRacer	an;
	private boolean			running;
	private ServerSocket	serv;
	
	private ArrayList<HostSockConnect>	connections;
	
	public HostThread(AndroidRacer a) {
		an = a;
		running = true;
		
		connections = new ArrayList<HostSockConnect>();
	}
	
	@Override
	public void run() {
		try {
			serv = new ServerSocket(4444);
			Socket socket;

			
			while (running) {
				socket = serv.accept();
				
				HostSockConnect host = new HostSockConnect(an, socket);
				host.start();
				connections.add(host);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		closeHost();
	}
	
	public void closeHost() {
		running = false;
		
		try {
			serv.close();
		} catch (Exception e ){
			e.printStackTrace();
		}
	}
	
	public boolean isRunning() {
		return running;
	}
}
