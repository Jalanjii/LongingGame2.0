package com.sIlence.androidracer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.MotionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Mytchel
 */
public class MultiplayerGameView extends GameView implements Runnable {

    public static final String	CODE_SPAWNING = "spawning";
    public static final String	CODE_UPDATING = "xy";
	public static final String	CODE_DO_NOTHING = "nothing";
	public static final String	CODE_DIE = "die";

    Thread					network;
    Socket					sock;
    BufferedReader          in;
    PrintStream				out;
    boolean					connected;

	int						otherBoxWidth;
    int						otherBoxHeight;

    WebRacer                webRacer;
    LocalRacer              localRacer;
	WallRacer				wall1;
	WallRacer				wall2;

    public MultiplayerGameView(Context c, Game g, Socket s) {
    	super(c, AIRacer.DIFF_MEDI, g);
		sock = s;
    }

    public void run() {
    	try {
			connected = true;
		
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			localRacer = new LocalRacer(this, out);
			webRacer = new WebRacer(this, in);
			
			out.println(getWidth());
			out.println(getHeight());
						
			String input = in.readLine();
			int oWidth = Integer.parseInt(input);
			input = in.readLine();
			int oHeight = Integer.parseInt(input);
			
			setBoxsX(gcd(oWidth, getWidth()));
			setBoxsY(gcd(oHeight, getHeight()));
			
			while (boxsX() > 120) setBoxsX(boxsX() / 2);
			while (boxsY() > 200) setBoxsY(boxsY() / 2);
			
			setBoxWidth(getWidth() / boxsX());
			setBoxHeight(getHeight() / boxsY());

			connected = true;
		} catch (Exception ex) {
			final Exception e = ex;

			connected = false;
			
			post(new Runnable() {
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					builder
					.setTitle("Error Connecting")
					.setMessage(e.getMessage())
					.setCancelable(true)
					.setNeutralButton("Exit", new android.content.DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							((AndroidRacer) getContext()).menu();
						}
					})
					.create()
					.show();
					pauseGame();

					e.printStackTrace();
				}
			});	
		}
		
		
		wall1 = new WallRacer(this, boxWidth(), top() + boxHeight());
		wall2 = new WallRacer(this, boxWidth() * (boxsX() - 1), boxHeight() * (boxsY() - 1));
		
		int loops = (boxsX() + boxsY()) * 2;
		for (int i = 0; i < loops; i++) {
			wall1.preupdate();
			wall2.preupdate();
		}
		    			
		Part[] parts = new Part[] {webRacer, localRacer, wall1, wall2};
		localRacer.setOpps(parts);
		webRacer.setOpps(parts);
		
		localRacer.spawn(LightRacer.STANDARD_LENGTH);
    }


    @Override
    public boolean onTouchEvent (MotionEvent e) {
    	if (e.getAction() == MotionEvent.ACTION_DOWN) {
			if (!connected) {
				newGame();
				start();
			} else if ((isPaused()) && !gameOver) {
				vibrate();
				resumeGame();
				starting = false;
		    } else if (gameOver && endTime + 300 < System.currentTimeMillis()) {
				vibrate();
				stopGame();
				((AndroidRacer) getContext()).multiplayer();
		    }
		} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
		    xDiff = e.getX() - x;
		    yDiff = e.getY() - y;

		    int g = gratistDiff(xDiff, yDiff);
		    if (g == 0) {
		    	if (xDiff > 1 && localRacer.changeDirection(1)) {
					vibrate();
				}
				if (xDiff < -1 && localRacer.changeDirection(3)) {
					vibrate();
				}
		    } else if (g == 1) {
				if (yDiff < -1 && localRacer.changeDirection(0)) {
					vibrate();
				}
				if (yDiff > 1 && localRacer.changeDirection(2)) {
					vibrate();
				}
		    }
		}
		x = e.getX();
		y = e.getY();

		return true;
    }

    @Override
    protected void newGame() {
		game.otherDifficualty = AIRacer.DIFF_MEDI;
    	
		super.newGame();
		
    	network = new Thread(this);
		network.start();
    }


    @Override
    public synchronized void stopGame() {
    	super.stopGame();

    	try {
			connected = false;
			in.close();
			out.close();
			sock.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

    @Override
    public void render(Canvas c) {
		background(c);
		
		if (!starting) {
			localRacer.render(c);
			webRacer.render(c);
			wall1.render(c);
			wall2.render(c);
		}
		
		hud(c);
		
		if (!connected) {
		    drawMessageBox(c, new String[] {"Connection Lost"});
		} else {
		    messages(c);
		}
    }

	@Override
	public void messages(Canvas c) {
		if (gameOver) {
		    if (won) {
		        if (!highScore) {
					drawMessageBox(c, new String[] {"Game Over", "You Won"});
				}
		    } else {
		        drawMessageBox(c, new String[] {"Game Over", "You Lost"});
		    }
		} else if (starting) {
		    drawMessageBox(c, new String[] {"You Are Blue", "Swipe To Turn", "Make Yellow Crash", "Tap To Play"});
		} else if (loop.isPaused()) {
		    drawMessageBox(c, new String[] {"Touch Screen", "To Resume"});
		}
    }

	@Override
	public boolean checkScore() {
		if (game.kills >= 10 && game.kills - 2 >= game.deaths) {
		    won = true;
			gameOver();

			return true;
		} else if (game.deaths >= 10 && game.deaths - 2 >= game.kills) {
		    won = false;
			gameOver();

			return true;
		}

		return false;
    }
	
    @Override
    public void update() {
		if (starting) return;
		if (gameOver) {
			stopGame();
			return;
		}
		
		if (connected) {
			game.ticks++;
			game.time += loop.framePeriod;
			
			localRacer.update();
			wall1.update();
			wall2.update();
			
			try {
			    String input = in.readLine();
			    if (input == null) {
					connected = false;
			    } else {
					webRacer.update(input);
			    }
			} catch (IOException e) {}
			
			if (pausing) {
				pauseGame();
			}
		} else {
			pauseGame();
		}
    }
	
	

    private int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
	
	@Override
    public void pauseGame() {
		super.pauseGame();
		
		if (webRacer != null) webRacer.pause();
		if (localRacer != null) localRacer.pause();
    }
	
	@Override
    public synchronized void resumeGame() {
        super.resumeGame();
		
		if (webRacer != null) webRacer.resume();
		if (localRacer != null) localRacer.resume();
    }
}
