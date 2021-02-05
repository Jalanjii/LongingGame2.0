/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Mytchel
 */
public class HostSockConnect extends Thread {
	
	public static int SERVER_CODE = 4;
	public static int JUST_CHECKING = 5;
	public static int PLAY = 6;
	public static int ACCEPTIING_PLAY = 7;
	public static int DECLINING_PLAY = 8;
	
	private AndroidRacer	an;
	private Socket			sock;
	
	public HostSockConnect(AndroidRacer a, Socket s) {
		an = a;
		sock = s;
	}
	
	@Override
	public void run() {
		try {
			sock.getOutputStream().write(SERVER_CODE);
			
			int b = sock.getInputStream().read();
			
			if (b == 0) {
				sock.close();
			} else if (b == JUST_CHECKING) {
				sock.getOutputStream().write(an.screenName().getBytes());
				sock.close();
			} else if (b == PLAY) {
				byte[] buffer = new byte[20];
				sock.getInputStream().read(buffer, 0, 20);
				String name = new String(buffer);
				
				Log.d("Host", "name = " + name);
				final String host = name.trim();
				Log.d("Host", "host = " + host);
				
				Log.d("Host", "getCanonicalHostName: " + sock.getInetAddress().getCanonicalHostName().toString());
				Log.d("Host", "getHostAddress: " + sock.getInetAddress().getHostAddress().toString());
				Log.d("Host", "getHostName: " + sock.getInetAddress().getHostName().toString());
				Log.d("Host", "toString: " + sock.getInetAddress().toString());
				Log.d("Host", "getAddress: " + sock.getInetAddress().getAddress().toString());
				
				an.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(an);
						builder
						.setTitle("Play Multiplayer Game")
						.setMessage("Play a multiplayer game with " + host + " (" + sock.getInetAddress().getHostAddress() + ")")
						.setCancelable(true)
						.setPositiveButton("Play", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								try {
									sock.getOutputStream().write(ACCEPTIING_PLAY);
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								
							    an.newGame(new MultiplayerGameView(an, null, sock));
								an.lockOrientation();
							}
						})
						.setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								try {
									
									sock.getOutputStream().write(DECLINING_PLAY);
									
									sock.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						})
						.create()
						.show();
					}
				});
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
