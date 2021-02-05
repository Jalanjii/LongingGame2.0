package com.sIlence.androidracer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SoundPool.OnLoadCompleteListener {

    public static int		INCREASE_DEATHS = 0;
    public static int		INCREASE_KILLS = 1;
    public static int		INCREASE_NULL = -1;

    protected boolean		pausing = false;
    protected boolean		starting = true;
    protected boolean		gameOver = false;
    protected boolean		won = false;
    protected boolean		highScore = false;

    protected GameLoop		loop;
    private Paint			brush;

    public Vibrator			vibrate;

    public Game				game;

    private boolean			load;
    protected int			score;

    protected long			endTime;

    private int				boxWidth;
    private int				boxHeight;
    private int				boxsX;
    private int				boxsY;
    private int				top;
    private int				rotation;

    protected float			x;
    protected float			y;
    protected float			xDiff;
    protected float			yDiff;

    private Rect			bounds;
    private String			textString;

    private Typeface		font;
    private Rect			menu;
	private int				fromRight;

    private Database		database;

    private AlertDialog		highScoresBox;

	private SoundPool		sound;
	private int				silence;
	private int				cycleSound;
	private int				crashSound;

    public GameView(Context context, int o, Game g) {
		super(context);
		getHolder().addCallback(this);
		setFocusable(true);

		vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		game = g;
		load = true;
		if (g == null) {
		    load = false;
		    game = new Game();
		    game.otherDifficualty = o;
		}

		loop = new GameLoop(this);
    }



    protected void newGame() {
		score = 0;
		bounds = new Rect();
		textString = "";

		database = new Database(getContext());

		if (!load) {
		    game.kills = 0;
		    game.deaths = 0;
		    game.time = 0;
		    endTime = System.currentTimeMillis();

		    game.other = new AIRacer(this, game.otherDifficualty, INCREASE_KILLS);
		    game.local = new LightRacer(this, 0xC003CCF1, INCREASE_DEATHS);
		    game.wall1 = new WallRacer(this, boxWidth, top + boxHeight);
		    game.wall2 = new WallRacer(this, boxWidth * (boxsX - 1), boxHeight * (boxsY - 1));


			int loops = (boxsX() + boxsY()) * 2;
			for (int i = 0; i < loops; i++) {
				game.wall1.preupdate();
				game.wall2.preupdate();
			}

			Part[] parts = new Part[] {game.other, game.local, game.wall1, game.wall2};

			game.local.setOpps(parts);
		    game.other.setOpps(parts);

		    game.other.spawn(LightRacer.STANDARD_LENGTH);
		    game.local.spawn(LightRacer.STANDARD_LENGTH);

		    game.ticks = 0;

		    pausing = false;
		    starting = true;
		    gameOver = false;
		    won = false;
		    highScore = false;
		} else {
		    game.newOrientation(this);
		    load = false;

			pausing = false;
		    starting = false;
		    gameOver = false;
		    won = false;
		    highScore = false;
		    checkScore();
		}
    }

    public void update() {
    	if (starting || gameOver) return;

		game.ticks++;
		game.time += loop.framePeriod;

		game.local.update();
		game.other.update();
		game.wall1.update();
		game.wall2.update();

		if (pausing) {
		    pauseGame();
		}
    }

    public void render(Canvas c) {
		background(c);
		drawRacers(c);
		hud(c);
		messages(c);
    }

    public void drawRacers(Canvas c) {
		game.local.render(c);
		game.other.render(c);
		game.wall1.render(c);
		game.wall2.render(c);
    }

    public void hud(Canvas c) {
		brush.setColor(0xffffffff);
		c.drawText("Time: " + (game.time / 1000), 10, brush.getFontSpacing(), brush);

		textString = game.kills + " :";
		c.drawText(game.kills + " : " + game.deaths, getWidth() - fromRight - halfWidth(textString), brush.getFontSpacing(), brush);
    }

    public void messages(Canvas c) {
		if (gameOver) {
		    if (won) {
		        if (!highScore) {
			    drawMessageBox(c, new String[] {"Game Over", "You Won", "Not A High Score"});
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

    public void drawMessageBox(Canvas c, String[] lines) {
		brush.setStyle(Paint.Style.FILL);
		brush.setColor(0x50000000);
		c.drawRect(menu, brush);

		brush.setColor(0xffffffff);
		brush.setStyle(Paint.Style.STROKE);
		c.drawRect(menu, brush);

		float lineY;
		if (lines.length % 2 == 0) {
		    lineY = getHeight() / 2 + brush.getFontSpacing() - ((lines.length / 2) * brush.getFontSpacing());
		} else {
		    lineY = getHeight() / 2 + brush.getFontSpacing() / 2 - ((lines.length / 2) * brush.getFontSpacing());
		}

		for (int i = 0; i < lines.length; i++) {
		    c.drawText(lines[i], getWidth() / 2 - halfWidth(lines[i]), lineY, brush);
		    lineY += brush.getFontSpacing();
		}
    }

    public boolean checkScore() {
		if (game.kills >= 10 && game.kills - 2 >= game.deaths) {
		    if (game.deaths > 0) {
				score = (int) (10000 / (game.time / 1000) * ((float) game.kills / game.deaths));
		    } else {
				score = (int) (10000 / (game.time / 1000) * 15);
		    }

			highScore = database.isHighScore(game.otherDifficualty, score);

			if (highScore) {
				post(new Runnable() {
				    public void run() {

						LayoutInflater factory = LayoutInflater.from(getContext());
						View textEntryView = factory.inflate(R.layout.high_score_box, null);

						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						highScoresBox = builder
						.setView(textEntryView)
						.setCancelable(false)
						.create();
						highScoresBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
						highScoresBox.setCanceledOnTouchOutside(false);
						highScoresBox.show();

						TextView title = (TextView) highScoresBox.findViewById(R.id.high_score_title);
						title.setText("High Score " + score);
						title.setTypeface(font);

						EditText text = (EditText) highScoresBox.findViewById(R.id.high_score_name);
						text.setTypeface(font);

						Button done = (Button) highScoresBox.findViewById(R.id.done);
						done.setTypeface(font);
						done.setOnClickListener(new OnClickListener() {

						    public void onClick(View v) {
						    	closeHighscore();
							}
						});
				    }
				});
		    }

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

	public void gameOver() {
		gameOver = true;
		endTime = System.currentTimeMillis();
		
		game.local.pause();
		game.other.pause();
		game.wall1.pause();
		game.wall2.pause();
	}

    public void closeHighscore() {
		String name = "Unknown";

		EditText text = (EditText) highScoresBox.findViewById(R.id.high_score_name);
		String t = text.getText().toString();
		if (t.length() > 0) {
		    name = t;
		}

		database.insertHighScore(name, game.otherDifficualty, score);

		highScoresBox.dismiss();
		highScoresBox = null;

		newGame();
		start();
    }

    public void changeScore(int scoreType) {
		if (scoreType == INCREASE_DEATHS) {
			game.deaths++;
		} else if (scoreType == INCREASE_KILLS) {
			game.kills++;
		}
    }

    public int halfWidth(String text) {
		brush.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width() / 2;
    }

    public void background(Canvas c) {
		brush.setColor(0xFF000308);
        brush.setStyle(Paint.Style.FILL);
		c.drawRect(0, 0, getWidth(), getHeight(), brush);

		brush.setColor(0x206FC0DF);
		brush.setStyle(Paint.Style.STROKE);

		for (int x = 0; x < getWidth(); x += 70) {
			c.drawLine(x, top, x, getHeight(), brush);
		}
		for (int y = top; y < getHeight(); y += 70) {
		    c.drawLine(0, y, getWidth(), y, brush);
		}
    }

    @Override
    public boolean onTouchEvent (MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			if ((loop.isPaused() || starting) && !gameOver) {
				vibrate();
				resumeGame();
				starting = false;
			} else if (gameOver && endTime + 300 < System.currentTimeMillis()) {
				vibrate();
				stopGame();
			    newGame();
			    start();
			}
			x = e.getX();
			y = e.getY();
		} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
		    xDiff = e.getX() - x;
		    yDiff = e.getY() - y;
		    x = e.getX();
		    y = e.getY();

		    int g = gratistDiff(xDiff, yDiff);
		    if (g == 0) {
		        if (xDiff > 1 && game.local.changeDirection(1)) {
					vibrate();
		        }
		        if (xDiff < -1 && game.local.changeDirection(3)) {
					vibrate();
				}
			} else if (g == 1) {
				if (yDiff < -1 && game.local.changeDirection(0)) {
					vibrate();
				}
				if (yDiff > 1 && game.local.changeDirection(2)) {
					vibrate();
				}
		    }
		}
		return true;
    }

    protected int gratistDiff(float x, float y) {
        if (x < 0) x = -x;
        if (y < 0) y = -y;

        if (x > y) return 0;
		if (y > x) return 1;
		return -1;
    }

    public void pause() {
        pausing = true;
    }

    public void pauseGame() {
        loop.pauseGame();
		pausing = false;

		game.local.pause();
		game.other.pause();
		game.wall1.pause();
		game.wall2.pause();
    }

    public synchronized void resumeGame() {
        if (gameOver) return;
        loop.resumeGame();

		game.local.resume();
		game.other.resume();
		game.wall1.resume();
		game.wall2.resume();
    }

    public synchronized void stopGame() {
        if (loop == null) return;
        loop.stopGame();
    }

    public synchronized void start() {
        if (loop != null) {
		    loop.stopGame();
		}
		pausing = false;
		notify();

		loop = new GameLoop(this);
		loop.start();

		pauseGame();
    }

    public boolean isPaused() {
        if (loop == null) return true;
		if (gameOver) return true;
        return loop.isPaused();
    }

    public int boxsX() {
        return boxsX;
    }

    public int boxsY() {
        return boxsY;
    }
	
	protected void setBoxsX(int n) {
		boxsX = n;
	}

	
	protected void setBoxsY(int n) {
		boxsY = n;
	}
	
    public int boxWidth() {
        return boxWidth;
    }

    public int boxHeight() {
        return boxHeight;
    }
	
	protected void setBoxWidth(int n) {
		boxWidth = n;
	}
	
	protected void setBoxHeight(int n) {
		boxHeight = n;
	}

    public int top() {
        return top;
    }

    public int rotation() {
        return rotation;
    }

    public void vibrate(int time) {
        if (!AndroidRacer.vibrateAlowed) return;

        vibrate.vibrate(time);
    }

	public void vibrate() {
		vibrate(loop.framePeriod);
	}

    public int gratestLengthInSegments() {
        if (boxsX > boxsY) return boxsX;
        return boxsY;
    }

    public void surfaceCreated(SurfaceHolder arg0) {
		sound = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
		sound.setOnLoadCompleteListener(this);

		silence = sound.load(getContext(), R.raw.silence, 0);
		cycleSound = sound.load(getContext(), R.raw.cycle, 0);
		crashSound = sound.load(getContext(), R.raw.crash, 0);

		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		rotation = display.getRotation();

		font = Typeface.createFromAsset(getContext().getAssets(), "bank_gothic.ttf");
		brush = new Paint(Paint.ANTI_ALIAS_FLAG);
		brush.setTypeface(font);

		// Screen dependent stuff
		
		int size = getWidth();
		if (getHeight() < getWidth()) size = getHeight();

		if (size < 350) { // small

		    menu = new Rect(getWidth() / 2 - 100, getHeight() / 2 - 40, getWidth() / 2 + 100, getHeight() / 2 + 40);
		    brush.setTextSize(12);

		    boxWidth = 3;
		    boxHeight = 3;
		    top = (15 / boxHeight) * boxHeight;
			fromRight = 40;
		} else { // normal

		    menu = new Rect(getWidth() / 2 - 220, getHeight() / 2 - 80, getWidth() / 2 + 220, getHeight() / 2 + 80);
		    brush.setTextSize(26);

			boxWidth = 5;
		    boxHeight = 5;
			top = (40 / boxHeight) * boxHeight;
			fromRight = 80;
		}

	    boxsX = getWidth() / boxWidth;
        boxsY = getHeight() / boxHeight;

        if (starting) newGame();
        start();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (highScoresBox != null) highScoresBox.dismiss();

		game.local.stop();
		game.other.stop();
		game.wall1.stop();
		game.wall2.stop();

		sound.unload(cycleSound);
		sound.unload(crashSound);
		sound.unload(silence);

		if (sound != null) sound.release();
		sound = null;

		stopGame();
    }

	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if (sound != null && sampleId == silence) {
			sound.play(silence, 0f, 0f, 0, -1, 1f);
		}
	}

	public synchronized void pauseSound(int streamId) {
		if (sound != null) sound.pause(streamId);
	}

	public synchronized void resumeSound(int streamId) {
		if (sound != null) sound.resume(streamId);
	}

	public synchronized void stopSound(int streamId) {
		if (sound != null) sound.stop(streamId);
	}

	public synchronized void setRate(int streamId, float rate) {
		if (sound != null) sound.setRate(streamId, rate);
	}

	public synchronized int play(int soundId, float volume, int loop, float rate) {
		if (sound == null) return 0;
		if (!AndroidRacer.soundEffects) return -1;

		int stream = sound.play(soundId, volume, volume, 0, loop, rate);

		return stream;
	}

	public int cycleSoundId() {
		return cycleSound;
	}

	public int crashSoundId() {
		return crashSound;
	}
}